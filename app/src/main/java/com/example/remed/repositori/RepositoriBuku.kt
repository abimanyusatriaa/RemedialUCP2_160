package com.example.remed.repositori

import androidx.room.withTransaction
import com.example.remed.room.*
import kotlinx.coroutines.flow.Flow

interface RepositoriBuku {
    fun getAllBuku(): Flow<List<Buku>>
    fun getAllKategori(): Flow<List<Kategori>>
    fun getBukuByCategoryRecursive(categoryId: Int): Flow<List<Buku>>
    
    suspend fun insertBuku(buku: Buku, penulisIds: List<Int>)
    suspend fun insertKategori(kategori: Kategori)

    suspend fun deleteKategori(kategoriId: Int, deleteBooks: Boolean)
}

class RepositoriBukuImpl(private val bukuDao: BukuDao, private val db: BukuDatabase) : RepositoriBuku {

    override fun getAllBuku(): Flow<List<Buku>> = bukuDao.getAllBuku()
    override fun getAllKategori(): Flow<List<Kategori>> = bukuDao.getAllKategori()
    override fun getBukuByCategoryRecursive(categoryId: Int): Flow<List<Buku>> = bukuDao.getBukuByCategoryRecursive(categoryId)

    override suspend fun insertBuku(buku: Buku, penulisIds: List<Int>) {
        db.withTransaction {
            val id = bukuDao.insertBuku(buku)
            penulisIds.forEach { pid ->
                bukuDao.insertCrossRef(BukuPenulisCrossRef(id.toInt(), pid))
            }
            // Audit Log
            bukuDao.insertAudit(AuditLog(
                tableName = "buku",
                recordId = id.toInt(),
                action = "INSERT",
                newDataJson = buku.toString()
            ))
        }
    }

    override suspend fun insertKategori(kategori: Kategori) {

        if (kategori.parentId != null) {
            validateCycle(kategori.id, kategori.parentId)
        }
        bukuDao.insertKategori(kategori)
    }

    private suspend fun validateCycle(childId: Int, newParentId: Int) {
        var currentParentId: Int? = newParentId
        while (currentParentId != null) {
            if (currentParentId == childId) {
                throw IllegalArgumentException("Terdeteksi Cyclic Reference! Kategori tidak bisa menjadi induk bagi dirinya sendiri.")
            }
            val parent = bukuDao.getKategoriByIdSuspend(currentParentId)
            currentParentId = parent?.parentId
        }
    }


    override suspend fun deleteKategori(kategoriId: Int, deleteBooks: Boolean) {
        db.withTransaction {

            val borrowedBooks = bukuDao.getBorrowedBooksInCategory(kategoriId)
            
            if (borrowedBooks.isNotEmpty()) {

                throw IllegalStateException("GAGAL: Terdapat ${borrowedBooks.size} buku yang sedang dipinjam dalam kategori ini. Operasi dibatalkan.")
            }


            if (deleteBooks) {
                bukuDao.softDeleteBukuByCategory(kategoriId)
                bukuDao.insertAudit(AuditLog(tableName = "buku", recordId = kategoriId, action = "SOFT_DELETE_BY_CATEGORY"))
            } else {

                bukuDao.moveBukuToCategory(kategoriId, null)
                bukuDao.insertAudit(AuditLog(tableName = "buku", recordId = kategoriId, action = "UNLINK_CATEGORY"))
            }


            bukuDao.softDeleteKategori(kategoriId)
            bukuDao.insertAudit(AuditLog(tableName = "kategori", recordId = kategoriId, action = "SOFT_DELETE"))
        }
    }
}
