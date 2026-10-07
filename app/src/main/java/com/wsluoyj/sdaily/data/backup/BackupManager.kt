package com.wsluoyj.sdaily.data.backup

import com.wsluoyj.sdaily.data.PartnerRepository
import com.wsluoyj.sdaily.data.RecordRepository
import com.wsluoyj.sdaily.data.local.SdailyDatabase
import com.wsluoyj.sdaily.data.local.entity.EjacRecord
import com.wsluoyj.sdaily.data.local.entity.Partner
import com.wsluoyj.sdaily.data.local.entity.RecordMethod
import androidx.room.withTransaction
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream
import java.io.OutputStream
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

/**
 * 加密备份 / 恢复。
 *
 * .mbak 文件布局（小端顺序写入的字节序列）：
 *   MAGIC(7B "SDAILY1") | SALT(16B) | IV(12B) | AES-GCM 密文(内含 16B 认证标签)
 *
 * 密钥由用户口令经 PBKDF2-HMAC-SHA256（20 万次）派生，salt 每文件随机；
 * 明文为 JSON，含 partners 与 records。口令错误时 GCM 认证失败直接抛异常。
 */
class BackupManager(
    private val database: SdailyDatabase,
    private val recordRepository: RecordRepository,
    private val partnerRepository: PartnerRepository
) {

    suspend fun export(output: OutputStream, password: CharArray) {
        val json = JSONObject().apply {
            put("app", APP)
            put("schemaVersion", SCHEMA)
            put("exportedAt", System.currentTimeMillis())
            put("partners", JSONArray().apply {
                partnerRepository.listAll().forEach { put(partnerToJson(it)) }
            })
            put("records", JSONArray().apply {
                // 全量导出：复用区间查询（取一个足够大的时间窗）
                val all = recordRepository.listBetween(0L, Long.MAX_VALUE)
                all.forEach { put(recordToJson(it)) }
            })
        }
        val payload = encrypt(json.toString().toByteArray(Charsets.UTF_8), password)
        output.use {
            it.write(payload)
            it.flush()
        }
    }

    /** 第一步：仅解密并解析，不写库（口令错误在此抛 AEADBadTagException） */
    suspend fun parse(input: InputStream, password: CharArray): BackupData {
        val bytes = input.use { it.readBytes() }
        val plain = decrypt(bytes, password)
        val json = JSONObject(String(plain, Charsets.UTF_8))

        require(json.optString("app") == APP) { "不是有效的 Sdaily 备份文件" }

        val partnersJson = json.optJSONArray("partners") ?: JSONArray()
        val recordsJson = json.optJSONArray("records") ?: JSONArray()

        val partners = buildList {
            for (i in 0 until partnersJson.length()) {
                add(jsonToPartner(partnersJson.getJSONObject(i)))
            }
        }
        val records = buildList {
            for (i in 0 until recordsJson.length()) {
                jsonToRecord(recordsJson.getJSONObject(i))?.let { add(it) }
            }
        }
        return BackupData(partners, records)
    }

    /** 第二步：用户确认后，在一个事务内整体替换现有数据 */
    suspend fun applyRestore(data: BackupData): BackupResult {
        database.withTransaction {
            partnerRepository.replaceAll(data.partners)
            recordRepository.replaceAll(data.records)
        }
        return BackupResult(partners = data.partners.size, records = data.records.size)
    }

    data class BackupData(val partners: List<Partner>, val records: List<EjacRecord>)
    data class BackupResult(val partners: Int, val records: Int)

    // ---------------- 加密原语 ----------------

    private fun deriveKey(password: CharArray, salt: ByteArray): SecretKeySpec {
        val spec = PBEKeySpec(password, salt, PBKDF2_ITERATIONS, 256)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        return SecretKeySpec(factory.generateSecret(spec).encoded, "AES")
    }

    private fun encrypt(plain: ByteArray, password: CharArray): ByteArray {
        val salt = ByteArray(SALT_LEN).also { SecureRandom().nextBytes(it) }
        val iv = ByteArray(IV_LEN).also { SecureRandom().nextBytes(it) }
        val cipher = Cipher.getInstance(TRANSFORM)
        cipher.init(Cipher.ENCRYPT_MODE, deriveKey(password, salt), GCMParameterSpec(TAG_BITS, iv))
        val ct = cipher.doFinal(plain)
        return ByteArray(MAGIC.size + SALT_LEN + IV_LEN + ct.size).also { out ->
            var pos = 0
            MAGIC.copyInto(out, pos); pos += MAGIC.size
            salt.copyInto(out, pos); pos += SALT_LEN
            iv.copyInto(out, pos); pos += IV_LEN
            ct.copyInto(out, pos)
        }
    }

    private fun decrypt(file: ByteArray, password: CharArray): ByteArray {
        val headerLen = MAGIC.size + SALT_LEN + IV_LEN
        require(file.size > headerLen + TAG_BYTES) { "备份文件已损坏或不完整" }
        val magic = file.copyOfRange(0, MAGIC.size)
        require(magic.contentEquals(MAGIC)) { "不是 Sdaily 备份文件" }

        var pos = MAGIC.size
        val salt = file.copyOfRange(pos, pos + SALT_LEN); pos += SALT_LEN
        val iv = file.copyOfRange(pos, pos + IV_LEN); pos += IV_LEN
        val ct = file.copyOfRange(pos, file.size)

        val cipher = Cipher.getInstance(TRANSFORM)
        cipher.init(Cipher.DECRYPT_MODE, deriveKey(password, salt), GCMParameterSpec(TAG_BITS, iv))
        return cipher.doFinal(ct) // 口令错误：AEADBadTagException
    }

    // ---------------- JSON 映射 ----------------

    private fun partnerToJson(p: Partner) = JSONObject().apply {
        put("id", p.id); put("name", p.name); put("note", p.note ?: JSONObject.NULL)
        put("archived", p.archived); put("createdAt", p.createdAt)
    }

    private fun jsonToPartner(o: JSONObject) = Partner(
        id = o.getLong("id"),
        name = o.getString("name"),
        note = if (o.isNull("note")) null else o.optString("note").ifBlank { null },
        archived = o.optBoolean("archived", false),
        createdAt = o.optLong("createdAt", System.currentTimeMillis())
    )

    private fun recordToJson(r: EjacRecord) = JSONObject().apply {
        put("id", r.id); put("timeMs", r.timeMs); put("method", r.method.name)
        put("durationMin", r.durationMin ?: JSONObject.NULL)
        put("rating", r.rating ?: JSONObject.NULL)
        put("partnerId", r.partnerId ?: JSONObject.NULL)
        put("location", r.location ?: JSONObject.NULL)
        put("internalEjac", r.internalEjac ?: JSONObject.NULL)
        put("protectionUsed", r.protectionUsed ?: JSONObject.NULL)
        put("usedToy", r.usedToy ?: JSONObject.NULL)
        put("note", r.note ?: JSONObject.NULL)
        put("createdAt", r.createdAt); put("updatedAt", r.updatedAt)
    }

    private fun jsonToRecord(o: JSONObject): EjacRecord? {
        val method = runCatching {
            RecordMethod.valueOf(o.optString("method"))
        }.getOrNull() ?: return null
        return EjacRecord(
            id = o.getLong("id"),
            timeMs = o.getLong("timeMs"),
            method = method,
            durationMin = if (o.isNull("durationMin")) null else o.optInt("durationMin"),
            rating = if (o.isNull("rating")) null else o.optInt("rating"),
            partnerId = if (o.isNull("partnerId")) null else o.optLong("partnerId"),
            location = if (o.isNull("location")) null else o.optString("location").ifBlank { null },
            internalEjac = if (o.isNull("internalEjac")) null else o.optBoolean("internalEjac"),
            protectionUsed = if (o.isNull("protectionUsed")) null else o.optBoolean("protectionUsed"),
            usedToy = if (o.isNull("usedToy")) null else o.optBoolean("usedToy"),
            note = if (o.isNull("note")) null else o.optString("note").ifBlank { null },
            createdAt = o.optLong("createdAt", System.currentTimeMillis()),
            updatedAt = o.optLong("updatedAt", System.currentTimeMillis())
        )
    }

    private companion object {
        const val APP = "Sdaily"
        const val SCHEMA = 2
        val MAGIC = "SDAILY1".toByteArray(Charsets.US_ASCII)
        const val SALT_LEN = 16
        const val IV_LEN = 12
        const val TAG_BITS = 128
        const val TAG_BYTES = 16
        const val PBKDF2_ITERATIONS = 200_000
        const val TRANSFORM = "AES/GCM/NoPadding"
    }
}
