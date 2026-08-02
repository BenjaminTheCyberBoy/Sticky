package com.example.sticky.model.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import com.example.sticky.model.database.table.StickerTable
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

@Dao
interface StickerDAO {
    @Query("SELECT * FROM sticker WHERE packId = :packId ORDER BY createdAt DESC")
    fun getStickersByPack(packId: Uuid): Flow<List<StickerTable>>

    @Query("SELECT * FROM sticker WHERE packId = :packId ORDER BY createdAt DESC")
    fun getStickersByPackSync(packId: Uuid): List<StickerTable>

    @Query("DELETE FROM sticker WHERE id = :id")
    suspend fun deleteSticker(id: Int)

    @Upsert
    suspend fun insertSticker(sticker: StickerTable)
}
