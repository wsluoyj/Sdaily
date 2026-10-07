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

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        database = AppDatabaseProvider.get(this)
        recordRepository = RecordRepository(database.recordDao())
        partnerRepository = PartnerRepository(database.partnerDao())

        if (BuildConfig.DEBUG) {
            appScope.launch { runStorageSelfTest() }
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

}
