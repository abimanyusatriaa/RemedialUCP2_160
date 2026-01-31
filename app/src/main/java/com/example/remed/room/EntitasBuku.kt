package com.example.remed.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// Tabel Kategori dengan Hierarki
@Entity(
    tableName = "kategori",
    foreignKeys = [
        ForeignKey(
            entity = Kategori::class,
            parentColumns = ["id"],
            childColumns = ["parentId"],
            onDelete = ForeignKey.RESTRICT // Cegah hapus jika masih ada child, handle manual di repo
        )
    ],
    indices = [Index(value = ["parentId"])]
)
data class Kategori(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nama: String,
    val deskripsi: String,
    val parentId: Int? = null, // null berarti root category
    val isDeleted: Boolean = false
)

// Tabel Penulis
@Entity(tableName = "penulis")
data class Penulis(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nama: String,
    val bio: String,
    val isDeleted: Boolean = false
)

// Tabel Buku
@Entity(
    tableName = "buku",
    foreignKeys = [
        ForeignKey(
            entity = Kategori::class,
            parentColumns = ["id"],
            childColumns = ["kategoriId"],
            onDelete = ForeignKey.SET_NULL // Opsi default, logika ketat ada di Repo
        )
    ],
    indices = [Index(value = ["kategoriId"])]
)
data class Buku(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val judul: String,
    val deskripsi: String,
    val status: StatusBuku, // Tersedia, Dipinjam, Hilang
    val kategoriId: Int? = null,
    val isDeleted: Boolean = false
)

enum class StatusBuku {
    TERSEDIA, DIPINJAM, HILANG
}

// Relasi Many-to-Many Buku & Penulis
@Entity(
    tableName = "buku_penulis_cross_ref",
    primaryKeys = ["bukuId", "penulisId"],
    indices = [Index(value = ["penulisId"])]
)
data class BukuPenulisCrossRef(
    val bukuId: Int,
    val penulisId: Int
)

// Audit Log untuk Integritas Data
@Entity(tableName = "audit_log")
data class AuditLog(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val tableName: String,
    val recordId: Int,
    val action: String, // INSERT, UPDATE, DELETE, SOFT_DELETE
    val timestamp: Long = System.currentTimeMillis(),
    val oldDataJson: String? = null,
    val newDataJson: String? = null
)