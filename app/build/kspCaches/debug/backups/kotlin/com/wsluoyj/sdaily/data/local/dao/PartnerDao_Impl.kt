package com.wsluoyj.sdaily.`data`.local.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.wsluoyj.sdaily.`data`.local.entity.Partner
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
public class PartnerDao_Impl(
  __db: RoomDatabase,
) : PartnerDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfPartner: EntityInsertAdapter<Partner>

  private val __deleteAdapterOfPartner: EntityDeleteOrUpdateAdapter<Partner>

  private val __updateAdapterOfPartner: EntityDeleteOrUpdateAdapter<Partner>
  init {
    this.__db = __db
    this.__insertAdapterOfPartner = object : EntityInsertAdapter<Partner>() {
      protected override fun createQuery(): String = "INSERT OR ABORT INTO `partners` (`id`,`name`,`note`,`archived`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: Partner) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.name)
        val _tmpNote: String? = entity.note
        if (_tmpNote == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpNote)
        }
        val _tmp: Int = if (entity.archived) 1 else 0
        statement.bindLong(4, _tmp.toLong())
        statement.bindLong(5, entity.createdAt)
      }
    }
    this.__deleteAdapterOfPartner = object : EntityDeleteOrUpdateAdapter<Partner>() {
      protected override fun createQuery(): String = "DELETE FROM `partners` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: Partner) {
        statement.bindLong(1, entity.id)
      }
    }
    this.__updateAdapterOfPartner = object : EntityDeleteOrUpdateAdapter<Partner>() {
      protected override fun createQuery(): String = "UPDATE OR ABORT `partners` SET `id` = ?,`name` = ?,`note` = ?,`archived` = ?,`createdAt` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: Partner) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.name)
        val _tmpNote: String? = entity.note
        if (_tmpNote == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpNote)
        }
        val _tmp: Int = if (entity.archived) 1 else 0
        statement.bindLong(4, _tmp.toLong())
        statement.bindLong(5, entity.createdAt)
        statement.bindLong(6, entity.id)
      }
    }
  }

  public override suspend fun insert(partner: Partner): Long = performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfPartner.insertAndReturnId(_connection, partner)
    _result
  }

  public override suspend fun insertAll(partners: List<Partner>): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfPartner.insert(_connection, partners)
  }

  public override suspend fun delete(partner: Partner): Unit = performSuspending(__db, false, true) { _connection ->
    __deleteAdapterOfPartner.handle(_connection, partner)
  }

  public override suspend fun update(partner: Partner): Unit = performSuspending(__db, false, true) { _connection ->
    __updateAdapterOfPartner.handle(_connection, partner)
  }

  public override suspend fun getById(id: Long): Partner? {
    val _sql: String = "SELECT * FROM partners WHERE id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _columnIndexOfArchived: Int = getColumnIndexOrThrow(_stmt, "archived")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: Partner?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpNote: String?
          if (_stmt.isNull(_columnIndexOfNote)) {
            _tmpNote = null
          } else {
            _tmpNote = _stmt.getText(_columnIndexOfNote)
          }
          val _tmpArchived: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfArchived).toInt()
          _tmpArchived = _tmp != 0
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _result = Partner(_tmpId,_tmpName,_tmpNote,_tmpArchived,_tmpCreatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeActive(): Flow<List<Partner>> {
    val _sql: String = "SELECT * FROM partners WHERE archived = 0 ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("partners")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _columnIndexOfArchived: Int = getColumnIndexOrThrow(_stmt, "archived")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: MutableList<Partner> = mutableListOf()
        while (_stmt.step()) {
          val _item: Partner
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpNote: String?
          if (_stmt.isNull(_columnIndexOfNote)) {
            _tmpNote = null
          } else {
            _tmpNote = _stmt.getText(_columnIndexOfNote)
          }
          val _tmpArchived: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfArchived).toInt()
          _tmpArchived = _tmp != 0
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item = Partner(_tmpId,_tmpName,_tmpNote,_tmpArchived,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun listAll(): List<Partner> {
    val _sql: String = "SELECT * FROM partners ORDER BY archived ASC, createdAt DESC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _columnIndexOfArchived: Int = getColumnIndexOrThrow(_stmt, "archived")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: MutableList<Partner> = mutableListOf()
        while (_stmt.step()) {
          val _item: Partner
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpNote: String?
          if (_stmt.isNull(_columnIndexOfNote)) {
            _tmpNote = null
          } else {
            _tmpNote = _stmt.getText(_columnIndexOfNote)
          }
          val _tmpArchived: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfArchived).toInt()
          _tmpArchived = _tmp != 0
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item = Partner(_tmpId,_tmpName,_tmpNote,_tmpArchived,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clear() {
    val _sql: String = "DELETE FROM partners"
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
