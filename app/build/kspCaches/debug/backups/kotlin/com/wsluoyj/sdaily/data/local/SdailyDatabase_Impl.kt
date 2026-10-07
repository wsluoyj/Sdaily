package com.wsluoyj.sdaily.`data`.local

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.wsluoyj.sdaily.`data`.local.dao.PartnerDao
import com.wsluoyj.sdaily.`data`.local.dao.PartnerDao_Impl
import com.wsluoyj.sdaily.`data`.local.dao.RecordDao
import com.wsluoyj.sdaily.`data`.local.dao.RecordDao_Impl
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class SdailyDatabase_Impl : SdailyDatabase() {
  private val _recordDao: Lazy<RecordDao> = lazy {
    RecordDao_Impl(this)
  }

  private val _partnerDao: Lazy<PartnerDao> = lazy {
    PartnerDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(1, "06b14366941e9f5598e862ab44b54de1", "36d78356653dc04ac937685ba5c10d2b") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `records` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `timeMs` INTEGER NOT NULL, `method` TEXT NOT NULL, `durationMin` INTEGER, `rating` INTEGER, `partnerId` INTEGER, `location` TEXT, `internalEjac` INTEGER, `protectionUsed` INTEGER, `note` TEXT, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_records_timeMs` ON `records` (`timeMs`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_records_partnerId` ON `records` (`partnerId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `partners` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `note` TEXT, `archived` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '06b14366941e9f5598e862ab44b54de1')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `records`")
        connection.execSQL("DROP TABLE IF EXISTS `partners`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection): RoomOpenDelegate.ValidationResult {
        val _columnsRecords: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsRecords.put("id", TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRecords.put("timeMs", TableInfo.Column("timeMs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRecords.put("method", TableInfo.Column("method", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRecords.put("durationMin", TableInfo.Column("durationMin", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRecords.put("rating", TableInfo.Column("rating", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRecords.put("partnerId", TableInfo.Column("partnerId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRecords.put("location", TableInfo.Column("location", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRecords.put("internalEjac", TableInfo.Column("internalEjac", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRecords.put("protectionUsed", TableInfo.Column("protectionUsed", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRecords.put("note", TableInfo.Column("note", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRecords.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRecords.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysRecords: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesRecords: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesRecords.add(TableInfo.Index("index_records_timeMs", false, listOf("timeMs"), listOf("ASC")))
        _indicesRecords.add(TableInfo.Index("index_records_partnerId", false, listOf("partnerId"), listOf("ASC")))
        val _infoRecords: TableInfo = TableInfo("records", _columnsRecords, _foreignKeysRecords, _indicesRecords)
        val _existingRecords: TableInfo = read(connection, "records")
        if (!_infoRecords.equals(_existingRecords)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |records(com.wsluoyj.sdaily.data.local.entity.EjacRecord).
              | Expected:
              |""".trimMargin() + _infoRecords + """
              |
              | Found:
              |""".trimMargin() + _existingRecords)
        }
        val _columnsPartners: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsPartners.put("id", TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPartners.put("name", TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPartners.put("note", TableInfo.Column("note", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPartners.put("archived", TableInfo.Column("archived", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPartners.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysPartners: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesPartners: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoPartners: TableInfo = TableInfo("partners", _columnsPartners, _foreignKeysPartners, _indicesPartners)
        val _existingPartners: TableInfo = read(connection, "partners")
        if (!_infoPartners.equals(_existingPartners)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |partners(com.wsluoyj.sdaily.data.local.entity.Partner).
              | Expected:
              |""".trimMargin() + _infoPartners + """
              |
              | Found:
              |""".trimMargin() + _existingPartners)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "records", "partners")
  }

  public override fun clearAllTables() {
    super.performClear(false, "records", "partners")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(RecordDao::class, RecordDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(PartnerDao::class, PartnerDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>): List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun recordDao(): RecordDao = _recordDao.value

  public override fun partnerDao(): PartnerDao = _partnerDao.value
}
