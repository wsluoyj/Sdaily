package com.wsluoyj.sdaily.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.wsluoyj.sdaily.data.local.entity.EjacRecord
import com.wsluoyj.sdaily.data.local.entity.Partner
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordDao {

    @Insert
    suspend fun insert(record: EjacRecord): Long

    @Update
    suspend fun update(record: EjacRecord)

    @Delete
    suspend fun delete(record: EjacRecord)

    @Query("SELECT * FROM records WHERE id = :id")
    suspend fun getById(id: Long): EjacRecord?

    @Query("SELECT * FROM records ORDER BY timeMs DESC")
    fun observeAll(): Flow<List<EjacRecord>>

    /** 日历用：区间内全部记录时间戳 */
    @Query("SELECT timeMs FROM records WHERE timeMs BETWEEN :startMs AND :endMs")
    suspend fun timesBetween(startMs: Long, endMs: Long): List<Long>

    /** 统计用：区间内全部记录 */
    @Query("SELECT * FROM records WHERE timeMs BETWEEN :startMs AND :endMs ORDER BY timeMs ASC")
    suspend fun listBetween(startMs: Long, endMs: Long): List<EjacRecord>

    /** 最近一次记录 */
    @Query("SELECT * FROM records ORDER BY timeMs DESC LIMIT 1")
    suspend fun latest(): EjacRecord?

    /** 备份恢复用：批量写入（忽略主键冲突时替换） */
    @Insert
    suspend fun insertAll(records: List<EjacRecord>)

    @Query("DELETE FROM records")
    suspend fun clear()
}

@Dao
interface PartnerDao {

    @Insert
    suspend fun insert(partner: Partner): Long

    @Update
    suspend fun update(partner: Partner)

    @Delete
    suspend fun delete(partner: Partner)

    @Query("SELECT * FROM partners WHERE id = :id")
    suspend fun getById(id: Long): Partner?

    @Query("SELECT * FROM partners WHERE archived = 0 ORDER BY createdAt DESC")
    fun observeActive(): Flow<List<Partner>>

    @Query("SELECT * FROM partners ORDER BY archived ASC, createdAt DESC")
    fun observeAll(): Flow<List<Partner>>

    @Query("SELECT * FROM partners ORDER BY archived ASC, createdAt DESC")
    suspend fun listAll(): List<Partner>

    @Insert
    suspend fun insertAll(partners: List<Partner>)

    @Query("DELETE FROM partners")
    suspend fun clear()
}
