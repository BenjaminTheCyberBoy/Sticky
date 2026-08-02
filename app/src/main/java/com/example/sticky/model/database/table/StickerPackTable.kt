package com.example.sticky.model.database.table

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import kotlin.uuid.Uuid

@Entity(tableName = "sticker_pack")
data class StickerPackTable(
    @PrimaryKey
    val packId: Uuid = Uuid.random(),
    val name: String,
    val trayIcon: String,
    val imageDataVersion: Int,
    val isAnimated: Boolean,
    val createdAt: Long
)
