package com.wsluoyj.sdaily.`data`.local.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.wsluoyj.sdaily.`data`.local.Converters
import com.wsluoyj.sdaily.`data`.local.entity.EjacRecord
import com.wsluoyj.sdaily.`data`.local.entity.RecordMethod
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class RecordDao_Impl(
  __db: RoomDatabase,
) : RecordDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfEjacRecord: EntityInsertAdapter<EjacRecord>

  private val __converters: Converters = Converters()

  private val __deleteAdapterOfEjacRecord: EntityDeleteOrUpdateAdapter<EjacRecord>

  private val __updateAdapterOfEjacRecord: EntityDeleteOrUpdateAdapter<EjacRecord>
  init {
    this.__db = __db
    this.__insertAdapterOfEjacRecord = object : EntityInsertAdapter<EjacRecord>() {
      protected override fun createQuery(): String = "INSERT OR ABORT INTO `records` (`id`,`timeMs`,`method`,`durationMin`,`rating`,`partnerId`,`location`,`internalEjac`,`protectionUsed`,`note`,`createdAt`,`updatedAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: EjacRecord) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.timeMs)
        val _tmp: String? = __converters.methodToString(entity.method)
        if (_tmp == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmp)
        }
        val _tmpDurationMin: Int? = entity.durationMin
        if (_tmpDurationMin == null) {
          statement.bindNull(4)
        } else {
          statement.bindLong(4, _tmpDurationMin.toLong())
        }
        val _tmpRating: Int? = entity.rating
        if (_tmpRating == null) {
          statement.bindNull(5)
        } else {
          statement.bindLong(5, _tmpRating.toLong())
        }
        val _tmpPartnerId: Long? = entity.partnerId
        if (_tmpPartnerId == null) {
          statement.bindNull(6)
        } else {
          statement.bindLong(6, _tmpPartnerId)
        }
        val _tmpLocation: String? = entity.location
        if (_tmpLocation == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpLocation)
        }
        val _tmpInternalEjac: Boolean? = entity.internalEjac
        val _tmp_1: Int? = _tmpInternalEjac?.let { if (it) 1 else 0 }
        if (_tmp_1 == null) {
          statement.bindNull(8)
        } else {
          statement.bindLong(8, _tmp_1.toLong())
        }
        val _tmpProtectionUsed: Boolean? = entity.protectionUsed
        val _tmp_2: Int? = _tmpProtectionUsed?.let { if (it) 1 else 0 }
        if (_tmp_2 == null) {
          statement.bindNull(9)
        } else {
          statement.bindLong(9, _tmp_2.toLong())
        }
        val _tmpNote: String? = entity.note
        if (_tmpNote == null) {
          statement.bindNull(10)
        } else {
          statement.bindText(10, _tmpNote)
        }
        statement.bindLong(11, entity.createdAt)
        statement.bindLong(12, entity.updatedAt)
      }
    }
    this.__deleteAdapterOfEjacRecord = object : EntityDeleteOrUpdateAdapter<EjacRecord>() {
      protected override fun createQuery(): String = "DELETE FROM `records` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: EjacRecord) {
        statement.bindLong(1, entity.id)
      }
    }
    this.__updateAdapterOfEjacRecord = object : EntityDeleteOrUpdateAdapter<EjacRecord>() {
      protected override fun createQuery(): String = "UPDATE OR ABORT `records` SET `id` = ?,`timeMs` = ?,`method` = ?,`durationMin` = ?,`rating` = ?,`partnerId` = ?,`location` = ?,`internalEjac` = ?,`protectionUsed` = ?,`note` = ?,`createdAt` = ?,`updatedAt` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: EjacRecord) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.timeMs)
        val _tmp: String? = __converters.methodToString(entity.method)
        if (_tmp == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmp)
        }
        val _tmpDurationMin: Int? = entity.durationMin
        if (_tmpDurationMin == null) {
          statement.bindNull(4)
        } else {
          statement.bindLong(4, _tmpDurationMin.toLong())
        }
        val _tmpRating: Int? = entity.rating
        if (_tmpRating == null) {
          statement.bindNull(5)
        } else {
          statement.bindLong(5, _tmpRating.toLong())
        }
        val _tmpPartnerId: Long? = entity.partnerId
        if (_tmpPartnerId == null) {
          statement.bindNull(6)
        } else {
          statement.bindLong(6, _tmpPartnerId)
        }
        val _tmpLocation: String? = entity.location
        if (_tmpLocation == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpLocation)
        }
        val _tmpInternalEjac: Boolean? = entity.internalEjac
        val _tmp_1: Int? = _tmpInternalEjac?.let { if (it) 1 else 0 }
        if (_tmp_1 == null) {
          statement.bindNull(8)
        } else {
          statement.bindLong(8, _tmp_1.toLong())
        }
        val _tmpProtectionUsed: Boolean? = entity.protectionUsed
        val _tmp_2: Int? = _tmpProtectionUsed?.let { if (it) 1 else 0 }
        if (_tmp_2 == null) {
          statement.bindNull(9)
        } else {
          statement.bindLong(9, _tmp_2.toLong())
        }
        val _tmpNote: String? = entity.note
        if (_tmpNote == null) {
          statement.bindNull(10)
        } else {
          statement.bindText(10, _tmpNote)
        }
        statement.bindLong(11, entity.createdAt)
        statement.bindLong(12, entity.updatedAt)
        statement.bindLong(13, entity.id)
      }
    }
  }

  public override suspend fun insert(record: EjacRecord): Long = performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfEjacRecord.insertAndReturnId(_connection, record)
    _result
  }

  public override suspend fun insertAll(records: List<EjacRecord>): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfEjacRecord.insert(_connection, records)
  }

  public override suspend fun delete(record: EjacRecord): Unit = performSuspending(__db, false, true) { _connection ->
    __deleteAdapterOfEjacRecord.handle(_connection, record)
  }

  public override suspend fun update(record: EjacRecord): Unit = performSuspending(__db, false, true) { _connection ->
    __updateAdapterOfEjacRecord.handle(_connection, record)
  }

  public override suspend fun getById(id: Long): EjacRecord? {
    val _sql: String = "SELECT * FROM records WHERE id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTimeMs: Int = getColumnIndexOrThrow(_stmt, "timeMs")
        val _columnIndexOfMethod: Int = getColumnIndexOrThrow(_stmt, "method")
        val _columnIndexOfDurationMin: Int = getColumnIndexOrThrow(_stmt, "durationMin")
        val _columnIndexOfRating: Int = getColumnIndexOrThrow(_stmt, "rating")
        val _columnIndexOfPartnerId: Int = getColumnIndexOrThrow(_stmt, "partnerId")
        val _columnIndexOfLocation: Int = getColumnIndexOrThrow(_stmt, "location")
        val _columnIndexOfInternalEjac: Int = getColumnIndexOrThrow(_stmt, "internalEjac")
        val _columnIndexOfProtectionUsed: Int = getColumnIndexOrThrow(_stmt, "protectionUsed")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: EjacRecord?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpTimeMs: Long
          _tmpTimeMs = _stmt.getLong(_columnIndexOfTimeMs)
          val _tmpMethod: RecordMethod
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfMethod)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfMethod)
          }
          val _tmp_1: RecordMethod? = __converters.stringToMethod(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'com.wsluoyj.sdaily.`data`.local.entity.RecordMethod', but it was NULL.")
          } else {
            _tmpMethod = _tmp_1
          }
          val _tmpDurationMin: Int?
          if (_stmt.isNull(_columnIndexOfDurationMin)) {
            _tmpDurationMin = null
          } else {
            _tmpDurationMin = _stmt.getLong(_columnIndexOfDurationMin).toInt()
          }
          val _tmpRating: Int?
          if (_stmt.isNull(_columnIndexOfRating)) {
            _tmpRating = null
          } else {
            _tmpRating = _stmt.getLong(_columnIndexOfRating).toInt()
          }
          val _tmpPartnerId: Long?
          if (_stmt.isNull(_columnIndexOfPartnerId)) {
            _tmpPartnerId = null
          } else {
            _tmpPartnerId = _stmt.getLong(_columnIndexOfPartnerId)
          }
          val _tmpLocation: String?
          if (_stmt.isNull(_columnIndexOfLocation)) {
            _tmpLocation = null
          } else {
            _tmpLocation = _stmt.getText(_columnIndexOfLocation)
          }
          val _tmpInternalEjac: Boolean?
          val _tmp_2: Int?
          if (_stmt.isNull(_columnIndexOfInternalEjac)) {
            _tmp_2 = null
          } else {
            _tmp_2 = _stmt.getLong(_columnIndexOfInternalEjac).toInt()
          }
          _tmpInternalEjac = _tmp_2?.let { it != 0 }
          val _tmpProtectionUsed: Boolean?
          val _tmp_3: Int?
          if (_stmt.isNull(_columnIndexOfProtectionUsed)) {
            _tmp_3 = null
          } else {
            _tmp_3 = _stmt.getLong(_columnIndexOfProtectionUsed).toInt()
          }
          _tmpProtectionUsed = _tmp_3?.let { it != 0 }
          val _tmpNote: String?
          if (_stmt.isNull(_columnIndexOfNote)) {
            _tmpNote = null
          } else {
            _tmpNote = _stmt.getText(_columnIndexOfNote)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _result = EjacRecord(_tmpId,_tmpTimeMs,_tmpMethod,_tmpDurationMin,_tmpRating,_tmpPartnerId,_tmpLocation,_tmpInternalEjac,_tmpProtectionUsed,_tmpNote,_tmpCreatedAt,_tmpUpdatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeAll(): Flow<List<EjacRecord>> {
    val _sql: String = "SELECT * FROM records ORDER BY timeMs DESC"
    return createFlow(__db, false, arrayOf("records")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTimeMs: Int = getColumnIndexOrThrow(_stmt, "timeMs")
        val _columnIndexOfMethod: Int = getColumnIndexOrThrow(_stmt, "method")
        val _columnIndexOfDurationMin: Int = getColumnIndexOrThrow(_stmt, "durationMin")
        val _columnIndexOfRating: Int = getColumnIndexOrThrow(_stmt, "rating")
        val _columnIndexOfPartnerId: Int = getColumnIndexOrThrow(_stmt, "partnerId")
        val _columnIndexOfLocation: Int = getColumnIndexOrThrow(_stmt, "location")
        val _columnIndexOfInternalEjac: Int = getColumnIndexOrThrow(_stmt, "internalEjac")
        val _columnIndexOfProtectionUsed: Int = getColumnIndexOrThrow(_stmt, "protectionUsed")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<EjacRecord> = mutableListOf()
        while (_stmt.step()) {
          val _item: EjacRecord
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpTimeMs: Long
          _tmpTimeMs = _stmt.getLong(_columnIndexOfTimeMs)
          val _tmpMethod: RecordMethod
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfMethod)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfMethod)
          }
          val _tmp_1: RecordMethod? = __converters.stringToMethod(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'com.wsluoyj.sdaily.`data`.local.entity.RecordMethod', but it was NULL.")
          } else {
            _tmpMethod = _tmp_1
          }
          val _tmpDurationMin: Int?
          if (_stmt.isNull(_columnIndexOfDurationMin)) {
            _tmpDurationMin = null
          } else {
            _tmpDurationMin = _stmt.getLong(_columnIndexOfDurationMin).toInt()
          }
          val _tmpRating: Int?
          if (_stmt.isNull(_columnIndexOfRating)) {
            _tmpRating = null
          } else {
            _tmpRating = _stmt.getLong(_columnIndexOfRating).toInt()
          }
          val _tmpPartnerId: Long?
          if (_stmt.isNull(_columnIndexOfPartnerId)) {
            _tmpPartnerId = null
          } else {
            _tmpPartnerId = _stmt.getLong(_columnIndexOfPartnerId)
          }
          val _tmpLocation: String?
          if (_stmt.isNull(_columnIndexOfLocation)) {
            _tmpLocation = null
          } else {
            _tmpLocation = _stmt.getText(_columnIndexOfLocation)
          }
          val _tmpInternalEjac: Boolean?
          val _tmp_2: Int?
          if (_stmt.isNull(_columnIndexOfInternalEjac)) {
            _tmp_2 = null
          } else {
            _tmp_2 = _stmt.getLong(_columnIndexOfInternalEjac).toInt()
          }
          _tmpInternalEjac = _tmp_2?.let { it != 0 }
          val _tmpProtectionUsed: Boolean?
          val _tmp_3: Int?
          if (_stmt.isNull(_columnIndexOfProtectionUsed)) {
            _tmp_3 = null
          } else {
            _tmp_3 = _stmt.getLong(_columnIndexOfProtectionUsed).toInt()
          }
          _tmpProtectionUsed = _tmp_3?.let { it != 0 }
          val _tmpNote: String?
          if (_stmt.isNull(_columnIndexOfNote)) {
            _tmpNote = null
          } else {
            _tmpNote = _stmt.getText(_columnIndexOfNote)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item = EjacRecord(_tmpId,_tmpTimeMs,_tmpMethod,_tmpDurationMin,_tmpRating,_tmpPartnerId,_tmpLocation,_tmpInternalEjac,_tmpProtectionUsed,_tmpNote,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun timesBetween(startMs: Long, endMs: Long): List<Long> {
    val _sql: String = "SELECT timeMs FROM records WHERE timeMs BETWEEN ? AND ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, startMs)
        _argIndex = 2
        _stmt.bindLong(_argIndex, endMs)
        val _result: MutableList<Long> = mutableListOf()
        while (_stmt.step()) {
          val _item: Long
          _item = _stmt.getLong(0)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun listBetween(startMs: Long, endMs: Long): List<EjacRecord> {
    val _sql: String = "SELECT * FROM records WHERE timeMs BETWEEN ? AND ? ORDER BY timeMs ASC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, startMs)
        _argIndex = 2
        _stmt.bindLong(_argIndex, endMs)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTimeMs: Int = getColumnIndexOrThrow(_stmt, "timeMs")
        val _columnIndexOfMethod: Int = getColumnIndexOrThrow(_stmt, "method")
        val _columnIndexOfDurationMin: Int = getColumnIndexOrThrow(_stmt, "durationMin")
        val _columnIndexOfRating: Int = getColumnIndexOrThrow(_stmt, "rating")
        val _columnIndexOfPartnerId: Int = getColumnIndexOrThrow(_stmt, "partnerId")
        val _columnIndexOfLocation: Int = getColumnIndexOrThrow(_stmt, "location")
        val _columnIndexOfInternalEjac: Int = getColumnIndexOrThrow(_stmt, "internalEjac")
        val _columnIndexOfProtectionUsed: Int = getColumnIndexOrThrow(_stmt, "protectionUsed")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<EjacRecord> = mutableListOf()
        while (_stmt.step()) {
          val _item: EjacRecord
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpTimeMs: Long
          _tmpTimeMs = _stmt.getLong(_columnIndexOfTimeMs)
          val _tmpMethod: RecordMethod
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfMethod)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfMethod)
          }
          val _tmp_1: RecordMethod? = __converters.stringToMethod(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'com.wsluoyj.sdaily.`data`.local.entity.RecordMethod', but it was NULL.")
          } else {
            _tmpMethod = _tmp_1
          }
          val _tmpDurationMin: Int?
          if (_stmt.isNull(_columnIndexOfDurationMin)) {
            _tmpDurationMin = null
          } else {
            _tmpDurationMin = _stmt.getLong(_columnIndexOfDurationMin).toInt()
          }
          val _tmpRating: Int?
          if (_stmt.isNull(_columnIndexOfRating)) {
            _tmpRating = null
          } else {
            _tmpRating = _stmt.getLong(_columnIndexOfRating).toInt()
          }
          val _tmpPartnerId: Long?
          if (_stmt.isNull(_columnIndexOfPartnerId)) {
            _tmpPartnerId = null
          } else {
            _tmpPartnerId = _stmt.getLong(_columnIndexOfPartnerId)
          }
          val _tmpLocation: String?
          if (_stmt.isNull(_columnIndexOfLocation)) {
            _tmpLocation = null
          } else {
            _tmpLocation = _stmt.getText(_columnIndexOfLocation)
          }
          val _tmpInternalEjac: Boolean?
          val _tmp_2: Int?
          if (_stmt.isNull(_columnIndexOfInternalEjac)) {
            _tmp_2 = null
          } else {
            _tmp_2 = _stmt.getLong(_columnIndexOfInternalEjac).toInt()
          }
          _tmpInternalEjac = _tmp_2?.let { it != 0 }
          val _tmpProtectionUsed: Boolean?
          val _tmp_3: Int?
          if (_stmt.isNull(_columnIndexOfProtectionUsed)) {
            _tmp_3 = null
          } else {
            _tmp_3 = _stmt.getLong(_columnIndexOfProtectionUsed).toInt()
          }
          _tmpProtectionUsed = _tmp_3?.let { it != 0 }
          val _tmpNote: String?
          if (_stmt.isNull(_columnIndexOfNote)) {
            _tmpNote = null
          } else {
            _tmpNote = _stmt.getText(_columnIndexOfNote)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item = EjacRecord(_tmpId,_tmpTimeMs,_tmpMethod,_tmpDurationMin,_tmpRating,_tmpPartnerId,_tmpLocation,_tmpInternalEjac,_tmpProtectionUsed,_tmpNote,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun latest(): EjacRecord? {
    val _sql: String = "SELECT * FROM records ORDER BY timeMs DESC LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTimeMs: Int = getColumnIndexOrThrow(_stmt, "timeMs")
        val _columnIndexOfMethod: Int = getColumnIndexOrThrow(_stmt, "method")
        val _columnIndexOfDurationMin: Int = getColumnIndexOrThrow(_stmt, "durationMin")
        val _columnIndexOfRating: Int = getColumnIndexOrThrow(_stmt, "rating")
        val _columnIndexOfPartnerId: Int = getColumnIndexOrThrow(_stmt, "partnerId")
        val _columnIndexOfLocation: Int = getColumnIndexOrThrow(_stmt, "location")
        val _columnIndexOfInternalEjac: Int = getColumnIndexOrThrow(_stmt, "internalEjac")
        val _columnIndexOfProtectionUsed: Int = getColumnIndexOrThrow(_stmt, "protectionUsed")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: EjacRecord?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpTimeMs: Long
          _tmpTimeMs = _stmt.getLong(_columnIndexOfTimeMs)
          val _tmpMethod: RecordMethod
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfMethod)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfMethod)
          }
          val _tmp_1: RecordMethod? = __converters.stringToMethod(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'com.wsluoyj.sdaily.`data`.local.entity.RecordMethod', but it was NULL.")
          } else {
            _tmpMethod = _tmp_1
          }
          val _tmpDurationMin: Int?
          if (_stmt.isNull(_columnIndexOfDurationMin)) {
            _tmpDurationMin = null
          } else {
            _tmpDurationMin = _stmt.getLong(_columnIndexOfDurationMin).toInt()
          }
          val _tmpRating: Int?
          if (_stmt.isNull(_columnIndexOfRating)) {
            _tmpRating = null
          } else {
            _tmpRating = _stmt.getLong(_columnIndexOfRating).toInt()
          }
          val _tmpPartnerId: Long?
          if (_stmt.isNull(_columnIndexOfPartnerId)) {
            _tmpPartnerId = null
          } else {
            _tmpPartnerId = _stmt.getLong(_columnIndexOfPartnerId)
          }
          val _tmpLocation: String?
          if (_stmt.isNull(_columnIndexOfLocation)) {
            _tmpLocation = null
          } else {
            _tmpLocation = _stmt.getText(_columnIndexOfLocation)
          }
          val _tmpInternalEjac: Boolean?
          val _tmp_2: Int?
          if (_stmt.isNull(_columnIndexOfInternalEjac)) {
            _tmp_2 = null
          } else {
            _tmp_2 = _stmt.getLong(_columnIndexOfInternalEjac).toInt()
          }
          _tmpInternalEjac = _tmp_2?.let { it != 0 }
          val _tmpProtectionUsed: Boolean?
          val _tmp_3: Int?
          if (_stmt.isNull(_columnIndexOfProtectionUsed)) {
            _tmp_3 = null
          } else {
            _tmp_3 = _stmt.getLong(_columnIndexOfProtectionUsed).toInt()
          }
          _tmpProtectionUsed = _tmp_3?.let { it != 0 }
          val _tmpNote: String?
          if (_stmt.isNull(_columnIndexOfNote)) {
            _tmpNote = null
          } else {
            _tmpNote = _stmt.getText(_columnIndexOfNote)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _result = EjacRecord(_tmpId,_tmpTimeMs,_tmpMethod,_tmpDurationMin,_tmpRating,_tmpPartnerId,_tmpLocation,_tmpInternalEjac,_tmpProtectionUsed,_tmpNote,_tmpCreatedAt,_tmpUpdatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clear() {
    val _sql: String = "DELETE FROM records"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
