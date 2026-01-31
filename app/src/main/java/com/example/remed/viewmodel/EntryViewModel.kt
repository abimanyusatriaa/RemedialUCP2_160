package com.example.remed.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.remed.repositori.RepositoriBuku
import com.example.remed.room.Buku
import com.example.remed.room.StatusBuku
import kotlinx.coroutines.launch

class EntryViewModel(private val repositoriBuku: RepositoriBuku) : ViewModel() {

    var uiStateBuku by mutableStateOf(DetailBukuUiState())
        private set

    fun updateUiState(detailBuku: DetailBuku) {
        uiStateBuku = DetailBukuUiState(detailBuku = detailBuku, isEntryValid = validasiInput(detailBuku))
    }

    private fun validasiInput(uiState: DetailBuku = uiStateBuku.detailBuku): Boolean {
        return with(uiState) {
            judul.isNotBlank() && deskripsi.isNotBlank()
        }
    }

    suspend fun saveBuku() {
        if (validasiInput()) {
            repositoriBuku.insertBuku(uiStateBuku.detailBuku.toBuku(), emptyList()) // Penulis sementara empty list
        }
    }
}

data class DetailBukuUiState(
    val detailBuku: DetailBuku = DetailBuku(),
    val isEntryValid: Boolean = false
)

data class DetailBuku(
    val id: Int = 0,
    val judul: String = "",
    val deskripsi: String = "",
    val status: StatusBuku = StatusBuku.TERSEDIA,
    val kategoriId: Int? = null,
)

fun DetailBuku.toBuku(): Buku = Buku(
    id = id,
    judul = judul,
    deskripsi = deskripsi,
    status = status,
    kategoriId = kategoriId,
    isDeleted = false
)

fun Buku.toDetailBuku(): DetailBuku = DetailBuku(
    id = id,
    judul = judul,
    deskripsi = deskripsi,
    status = status,
    kategoriId = kategoriId
)
