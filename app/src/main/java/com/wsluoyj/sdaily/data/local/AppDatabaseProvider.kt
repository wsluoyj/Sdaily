package com.wsluoyj.sdaily.data.local

import android.content.Context
import androidx.room.Room
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory

/** 构建 SQLCipher 加密的 Room 数据库 */
object AppDatabaseProvider {

    @Volatile
    private var instance: SdailyDatabase? = null

    fun get(context: Context): SdailyDatabase {
        return instance ?: synchronized(this) {
            instance ?: build(context.applicationContext).also { instance = it }
        }
    }

    private fun build(appContext: Context): SdailyDatabase {
        // sqlcipher-android 4.6+ 加载 native 库
        System.loadLibrary("sqlcipher")

        val passphrase = DatabaseKeyManager(appContext).getOrCreatePassphrase()
        val factory = SupportOpenHelperFactory(passphrase)

        return Room.databaseBuilder(
            appContext,
            SdailyDatabase::class.java,
            SdailyDatabase.DB_NAME
        )
            .openHelperFactory(factory)
            .build()
    }
}
