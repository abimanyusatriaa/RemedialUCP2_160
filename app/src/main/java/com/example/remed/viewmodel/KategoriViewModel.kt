package com.example.remed.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.remed.repositori.RepositoriBuku
import com.example.remed.room.Kategori
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class KategoriViewModel(private val repositoriBuku: RepositoriBuku) : ViewModel() {

    var namaKategori by mutableStateOf("")
    var deskripsiKategori by mutableStateOf("")
    var parentIdKategori by mutableStateOf<Int?>(null)

    var errorMessage by mutableStateOf<String?>(null)


    val listKategori: StateFlow<List<Kategori>> = repositoriBuku.getAllKategori()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun insertKategori() {
        if (namaKategori.isBlank()) {
            errorMessage = "Nama kategori tidak boleh kosong"
            return
        }
        
        viewModelScope.launch {
            try {
                repositoriBuku.insertKategori(
                    Kategori(
                        nama = namaKategori,
                        deskripsi = deskripsiKategori,
                        parentId = parentIdKategori
                    )
                )

                namaKategori = ""
                deskripsiKategori = ""
                parentIdKategori = null
                errorMessage = null
            } catch (e: Exception) {
                errorMessage = "Gagal: ${e.message}"
            }
        }
    }
    

    fun deleteKategori(id: Int, deleteBooks: Boolean) {
        viewModelScope.launch {
            try {
                repositoriBuku.deleteKategori(id, deleteBooks)
                errorMessage = null
            } catch (e: Exception) {

                errorMessage = e.message
            }
        }
    }
}
