package com.example.remed.viewmodel.provider

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.remed.AplikasiBuku
import com.example.remed.viewmodel.EntryViewModel
import com.example.remed.viewmodel.HomeViewModel
import com.example.remed.viewmodel.KategoriViewModel

object PenyediaViewModel {
    val Factory = viewModelFactory {
        initializer {
            HomeViewModel(aplikasiBuku().container.repositoriBuku)
        }
        initializer {
            EntryViewModel(aplikasiBuku().container.repositoriBuku)
        }
        initializer {
            KategoriViewModel(aplikasiBuku().container.repositoriBuku)
        }
    }
}

fun CreationExtras.aplikasiBuku(): AplikasiBuku =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AplikasiBuku)
