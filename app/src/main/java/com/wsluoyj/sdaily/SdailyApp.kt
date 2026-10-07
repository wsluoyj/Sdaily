package com.wsluoyj.sdaily

import android.app.Application
import android.util.Log
import com.wsluoyj.sdaily.data.PartnerRepository
import com.wsluoyj.sdaily.data.RecordRepository
import com.wsluoyj.sdaily.data.local.AppDatabaseProvider
import com.wsluoyj.sdaily.data.local.SdailyDatabase
import com.wsluoyj.sdaily.data.local.entity.EjacRecord
import com.wsluoyj.sdaily.data.local.entity.Partner
import com.wsluoyj.sdaily.data.local.entity.RecordMethod
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.File

class SdailyApp : Application() {

    lateinit var database: SdailyDatabase
        private set
    lateinit var recordRepository: RecordRepository
        private set
    lateinit var partnerRepository: PartnerRepository
        private set
    lateinit var backupManager: com.wsluoyj.sdaily.data.backup.BackupManager
        private set

    /** 应用级轻量设置（应用锁开关等，非敏感项） */
    val settings get() = getSharedPreferences("sdaily_settings", MODE_PRIVATE)

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        database = AppDatabaseProvider.get(this)
        recordRepository = RecordRepository(database.recordDao())
        partnerRepository = PartnerRepository(database.partnerDao())
        backupManager = com.wsluoyj.sdaily.data.backup.BackupManager(
            database, recordRepository, partnerRepository
        )

        if (BuildConfig.DEBUG) {
            appScope.launch {
                runStorageSelfTest()
                runBackupRoundTripTest()
            }
        }
    }

    /**
     * 开发期存储自检：写入 → 查询 → 删除，并校验数据库文件不是明文 SQLite。
     */
    private suspend fun runStorageSelfTest() {
        val tag = "SdailySelfTest"
        try {
            val partnerId = partnerRepository.save(Partner(name = "__自检伴侣__"))
            val now = System.currentTimeMillis()
            val recordId = recordRepository.save(
                EjacRecord(
                    timeMs = now,
                    method = RecordMethod.SEX,
                    durationMin = 13,
                    rating = 4,
                    partnerId = partnerId,
                    location = "家",
                    internalEjac = false,
                    protectionUsed = true,
                    note = "__self_test__"
                )
            )
            val loaded = recordRepository.get(recordId)
            check(loaded != null && loaded.method == RecordMethod.SEX && loaded.durationMin == 13) {
                "记录回读校验失败"
            }

            // 明文 SQLite 文件头为 "SQLite format 3\0"；SQLCipher 文件应为随机字节
            val dbFile: File = getDatabasePath(SdailyDatabase.DB_NAME)
            val header = ByteArray(16)
            dbFile.inputStream().use { it.read(header) }
            val plainHeader = "SQLite format 3\u0000".toByteArray(Charsets.US_ASCII)
            val isEncrypted = !header.contentEquals(plainHeader)
            check(isEncrypted) { "数据库文件为明文，SQLCipher 未生效！" }

            recordRepository.delete(loaded)
            partnerRepository.delete(Partner(id = partnerId, name = "__自检伴侣__"))

            Log.i(tag, "PASS: Room 写入/查询正常，SQLCipher 加密已生效（${dbFile.length()} bytes）")
        } catch (t: Throwable) {
            Log.e(tag, "FAIL: 存储自检失败", t)
        }
    }

    /**
     * 开发期备份链路自检（不写库、不落盘）：
     * 内存中导出 → 正确口令解析并比对数据 → 错误口令必须认证失败 → 密文不含明文标记。
     */
    private suspend fun runBackupRoundTripTest() {
        val tag = "SdailyBackupTest"
        try {
            val pwd = "roundtrip_pwd_123".toCharArray()
            val raw = java.io.ByteArrayOutputStream()
            backupManager.export(raw, pwd)
            val bytes = raw.toByteArray()
            check(bytes.copyOfRange(0, 7).contentEquals("SDAILY1".toByteArray())) { "文件头 magic 错误" }

            // 密文中不得出现明文 JSON / 数据文本
            val asText = String(bytes, Charsets.ISO_8859_1)
            check(!asText.contains("\"records\"") && !asText.contains("__self_test__")) {
                "备份内容疑似未加密"
            }

            // 正确口令往返
            val data = backupManager.parse(bytes.inputStream(), pwd)
            val expectRecords = recordRepository.listBetween(0L, Long.MAX_VALUE).size
            val expectPartners = partnerRepository.listAll().size
            check(data.records.size == expectRecords) { "记录数不符：${data.records.size} vs $expectRecords" }
            check(data.partners.size == expectPartners) { "伴侣数不符：${data.partners.size} vs $expectPartners" }

            // 错误口令必须被 GCM 认证拒绝
            val rejected = runCatching {
                backupManager.parse(bytes.inputStream(), "wrong_password".toCharArray())
            }.exceptionOrNull()
            check(rejected is javax.crypto.AEADBadTagException) {
                "错误口令未被拒绝：$rejected"
            }

            Log.i(tag, "PASS: 加密导出/正确口令解析/错误口令拒绝 全部正常（${bytes.size} bytes，$expectRecords 条记录）")
        } catch (t: Throwable) {
            Log.e(tag, "FAIL: 备份链路自检失败", t)
        }
    }

}
