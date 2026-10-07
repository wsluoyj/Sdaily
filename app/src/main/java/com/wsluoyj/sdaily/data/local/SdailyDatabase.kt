package com.wsluoyj.sdaily.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.wsluoyj.sdaily.data.local.dao.PartnerDao
import com.wsluoyj.sdaily.data.local.dao.RecordDao
import com.wsluoyj.sdaily.data.local.entity.EjacRecord
import com.wsluoyj.sdaily.data.local.entity.Partner

@Database(
    entities = [EjacRecord::class, Partner::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class SdailyDatabase : RoomDatabase() {
    abstract fun recordDao(): RecordDao
    abstract fun partnerDao(): PartnerDao

    companion object {
        const val DB_NAME = "sdaily.db"
    }
}
