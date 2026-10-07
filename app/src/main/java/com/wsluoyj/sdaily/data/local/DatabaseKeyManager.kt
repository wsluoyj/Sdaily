package com.wsluoyj.sdaily.data.local

import android.content.Context
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import java.security.SecureRandom

/**
 * 数据库口令管理：
 * 1. 首次启动生成 32 字节随机口令（SQLCipher 加密密钥）
 * 2. 口令用 Android Keystore 中硬件保护的 AES-GCM 主密钥加密后存入 SharedPreferences
 * 3. Keystore 私钥不可导出，即使拿到手机存储也无法解密数据库
 */
class DatabaseKeyManager(context: Context) {

    private val appContext: Context = context.applicationContext

    private val prefs: SharedPreferences =
        appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }

    /** 获取数据库口令；不存在则创建；主密钥失效时删残库重建（避免启动崩溃循环） */
    @Synchronized
    fun getOrCreatePassphrase(): ByteArray {
        prefs.getString(KEY_ENC_PASSPHRASE, null)?.let { stored ->
            try {
                return decrypt(Base64.decode(stored, Base64.NO_WRAP))
            } catch (e: Exception) {
                // Keystore 主密钥失效（换机恢复 / 恢复出厂 / 密钥被系统清除）：
                // 旧口令已不可解，数据库无法打开，删除残库用新口令重建；
                // 用户可稍后通过 .mbak 备份恢复数据
                prefs.edit().remove(KEY_ENC_PASSPHRASE).apply()
                SQLiteDatabase.deleteDatabase(appContext.getDatabasePath(SdailyDatabase.DB_NAME))
            }
        }
        val raw = ByteArray(32).also { SecureRandom().nextBytes(it) }
        val encrypted = encrypt(raw)
        prefs.edit()
            .putString(KEY_ENC_PASSPHRASE, Base64.encodeToString(encrypted, Base64.NO_WRAP))
            .apply()
        return raw
    }

    private fun ensureMasterKey(): SecretKey {
        (keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry)?.let { return it.secretKey }
        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
        generator.init(
            KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()
        )
        return generator.generateKey()
    }

    /** 输出 = IV(12) + 密文 */
    private fun encrypt(plain: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, ensureMasterKey())
        val iv = cipher.iv
        val ct = cipher.doFinal(plain)
        return ByteArray(iv.size + ct.size).also {
            System.arraycopy(iv, 0, it, 0, iv.size)
            System.arraycopy(ct, 0, it, iv.size, ct.size)
        }
    }

    private fun decrypt(payload: ByteArray): ByteArray {
        val iv = payload.copyOfRange(0, GCM_IV_LENGTH)
        val ct = payload.copyOfRange(GCM_IV_LENGTH, payload.size)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, ensureMasterKey(), GCMParameterSpec(GCM_TAG_LENGTH, iv))
        return cipher.doFinal(ct)
    }

    private companion object {
        const val PREFS_NAME = "sdaily_secure"
        const val KEY_ENC_PASSPHRASE = "db_passphrase_enc"
        const val KEY_ALIAS = "sdaily_db_master"
        const val ANDROID_KEYSTORE = "AndroidKeyStore"
        const val TRANSFORMATION = "AES/GCM/NoPadding"
        const val GCM_IV_LENGTH = 12
        const val GCM_TAG_LENGTH = 128
    }
}
