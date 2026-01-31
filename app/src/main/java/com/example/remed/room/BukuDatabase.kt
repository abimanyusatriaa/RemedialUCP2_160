package com.example.remed.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

@Database(
    entities = [Buku::class, Kategori::class, Penulis::class, BukuPenulisCrossRef::class, AuditLog::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class BukuDatabase : RoomDatabase() {
    abstract fun bukuDao(): BukuDao

    companion object {
        @Volatile
        private var Instance: BukuDatabase? = null

        fun getDatabase(context: Context): BukuDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, BukuDatabase::class.java, "buku_db")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}

class Converters {
    @TypeConverter
    fun fromStatus(status: StatusBuku): String {
        return status.name
    }

    @TypeConverter
    fun toStatus(status: String): StatusBuku {
        return try {
            StatusBuku.valueOf(status)
        } catch (e: IllegalArgumentException) {
            StatusBuku.TERSEDIA
        }
    }
}