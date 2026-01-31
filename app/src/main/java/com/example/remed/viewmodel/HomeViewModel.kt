package com.example.remed.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.remed.repositori.RepositoriBuku
import com.example.remed.room.Buku
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(private val repositoriBuku: RepositoriBuku) : ViewModel() {
    
    val listBuku: StateFlow<List<Buku>> = repositoriBuku.getAllBuku()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
