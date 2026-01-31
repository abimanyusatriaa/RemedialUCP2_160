package com.example.remed.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BukuDao {
    // --- BUKU ---
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBuku(buku: Buku): Long

    @Update
    suspend fun updateBuku(buku: Buku)

    @Query("SELECT * FROM buku WHERE isDeleted = 0 ORDER BY judul ASC")
    fun getAllBuku(): Flow<List<Buku>>

    @Query("SELECT * FROM buku WHERE id = :id AND isDeleted = 0")
    fun getBukuById(id: Int): Flow<Buku>

    @Query("UPDATE buku SET isDeleted = 1 WHERE id = :id")
    suspend fun softDeleteBuku(id: Int)
    
    // Update kategori buku secara batch (misal saat kategori dihapus)
    @Query("UPDATE buku SET kategoriId = :newKategoriId WHERE kategoriId = :oldKategoriId")
    suspend fun moveBukuToCategory(oldKategoriId: Int, newKategoriId: Int?)

    @Query("UPDATE buku SET isDeleted = 1 WHERE kategoriId = :kategoriId")
    suspend fun softDeleteBukuByCategory(kategoriId: Int)

    // Cek status buku di kategori tertentu
    @Query("SELECT * FROM buku WHERE kategoriId = :kategoriId AND status = 'DIPINJAM' AND isDeleted = 0")
    suspend fun getBorrowedBooksInCategory(kategoriId: Int): List<Buku>

    // --- KATEGORI ---
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertKategori(kategori: Kategori)

    @Update
    suspend fun updateKategori(kategori: Kategori)

    @Query("SELECT * FROM kategori WHERE isDeleted = 0")
    fun getAllKategori(): Flow<List<Kategori>>

    @Query("SELECT * FROM kategori WHERE id = :id")
    suspend fun getKategoriByIdSuspend(id: Int): Kategori?

    @Query("UPDATE kategori SET isDeleted = 1 WHERE id = :id")
    suspend fun softDeleteKategori(id: Int)

    // RECURSIVE QUERY: Ambil semua ID sub-kategori
    // Membutuhkan Room versi terbaru yang support CTE (SQLite 3.8.3+)
    @Query("""
        WITH RECURSIVE CategoryHierarchy AS (
            SELECT id FROM kategori WHERE id = :parentId AND isDeleted = 0
            UNION ALL 
            SELECT k.id FROM kategori k 
            JOIN CategoryHierarchy ch ON k.parentId = ch.id 
            WHERE k.isDeleted = 0
        )
        SELECT * FROM buku WHERE kategoriId IN (SELECT id FROM CategoryHierarchy) AND isDeleted = 0
    """)
    fun getBukuByCategoryRecursive(parentId: Int): Flow<List<Buku>>

    // --- PENULIS & RELASI ---
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPenulis(penulis: Penulis)
    
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCrossRef(crossRef: BukuPenulisCrossRef)

    // --- AUDIT LOG ---
    @Insert
    suspend fun insertAudit(log: AuditLog)
}