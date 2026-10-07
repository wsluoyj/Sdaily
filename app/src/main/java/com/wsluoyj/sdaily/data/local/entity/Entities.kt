package com.wsluoyj.sdaily.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/** 射精记录方式 */
enum class RecordMethod(val label: String) {
    SEX("性生活"),
    MASTURBATION("自慰"),
    DREAM("春梦"),
    OTHER("其他")
}

/**
 * 核心记录表。
 * 同一天多次记录通过 [timeMs] 精确到毫秒自然支持。
 * 时长/评分/伴侣/地点/内射/防护均允许为空（非必填）。
 */
@Entity(
    tableName = "records",
    indices = [Index("timeMs"), Index("partnerId")]
)
data class EjacRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    /** 实际发生时间（epoch 毫秒） */
    val timeMs: Long,
    /** 方式 */
    val method: RecordMethod,
    /** 时长（分钟），未填为 null */
    val durationMin: Int? = null,
    /** 满意度 1..5，未填为 null */
    val rating: Int? = null,
    /** 性伴侣 id（伴侣表），无/自慰时为 null */
    val partnerId: Long? = null,
    /** 地点，自由文本（家/酒店/其他…） */
    val location: String? = null,
    /** 是否体内射精，仅性生活有意义；未知为 null */
    val internalEjac: Boolean? = null,
    /** 是否采取防护措施（安全套等），未知为 null */
    val protectionUsed: Boolean? = null,
    /** 自慰时是否使用器具；仅自慰有意义，未知为 null */
    val usedToy: Boolean? = null,
    /** 备注 */
    val note: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/** 性伴侣档案 */
@Entity(tableName = "partners")
data class Partner(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val note: String? = null,
    /** 是否归档（软删除，保留历史记录引用） */
    val archived: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
