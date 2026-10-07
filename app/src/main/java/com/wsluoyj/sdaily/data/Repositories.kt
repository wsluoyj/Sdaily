package com.wsluoyj.sdaily.data

import com.wsluoyj.sdaily.data.local.dao.PartnerDao
import com.wsluoyj.sdaily.data.local.dao.RecordDao
import com.wsluoyj.sdaily.data.local.entity.EjacRecord
import com.wsluoyj.sdaily.data.local.entity.Partner
import kotlinx.coroutines.flow.Flow

class RecordRepository(private val dao: RecordDao) {

    val allRecords: Flow<List<EjacRecord>> = dao.observeAll()

    suspend fun save(record: EjacRecord): Long =
        if (record.id == 0L) dao.insert(record) else { dao.update(record); record.id }

    suspend fun get(id: Long): EjacRecord? = dao.getById(id)
    suspend fun delete(record: EjacRecord) = dao.delete(record)
    suspend fun latest(): EjacRecord? = dao.latest()
    suspend fun listBetween(startMs: Long, endMs: Long): List<EjacRecord> =
        dao.listBetween(startMs, endMs)
    suspend fun timesBetween(startMs: Long, endMs: Long): List<Long> =
        dao.timesBetween(startMs, endMs)

    /** 备份恢复：清空后批量写入，整体处于一个事务中由调用方数据库封装 */
    suspend fun replaceAll(records: List<EjacRecord>) {
        dao.clear()
        if (records.isNotEmpty()) dao.insertAll(records)
    }
}

class PartnerRepository(private val dao: PartnerDao) {

    val activePartners: Flow<List<Partner>> = dao.observeActive()
    val allPartners: Flow<List<Partner>> = dao.observeAll()

    suspend fun save(partner: Partner): Long =
        if (partner.id == 0L) dao.insert(partner) else { dao.update(partner); partner.id }

    suspend fun get(id: Long): Partner? = dao.getById(id)
    suspend fun delete(partner: Partner) = dao.delete(partner)
    suspend fun listAll(): List<Partner> = dao.listAll()

    suspend fun replaceAll(partners: List<Partner>) {
        dao.clear()
        if (partners.isNotEmpty()) dao.insertAll(partners)
    }
}
