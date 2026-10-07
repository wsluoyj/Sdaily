package com.wsluoyj.sdaily.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/** v1 → v2：records 新增 usedToy（自慰是否使用器具）；方式枚举新增 DREAM(春梦) 无需改表 */
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE records ADD COLUMN usedToy INTEGER")
    }
}
