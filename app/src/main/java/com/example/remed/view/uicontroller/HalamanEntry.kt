package com.example.remed.view.uicontroller

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.remed.viewmodel.DetailBukuUiState
import com.example.remed.viewmodel.EntryViewModel
import com.example.remed.viewmodel.provider.PenyediaViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanEntry(
    navigateBack: () -> Unit,
    viewModel: EntryViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            // Kita bisa reuse komponen TopAppBar Custom jika ada, atau pakai default
            androidx.compose.material3.TopAppBar(
                title = { Text("Tambah Buku Baru") },
                navigationIcon = {
                     // Tambahkan tombol back jika perlu
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        EntryBody(
            uiState = viewModel.uiStateBuku,
            onValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.saveBuku()
                    navigateBack()
                }
            },
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        )
    }
}

@Composable
fun EntryBody(
    uiState: DetailBukuUiState,
    onValueChange: (com.example.remed.viewmodel.DetailBuku) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = modifier.padding(16.dp)
    ) {
        FormInput(
            detailBuku = uiState.detailBuku,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = onSaveClick,
            enabled = uiState.isEntryValid,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.small
        ) {
            Text("Simpan")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormInput(
    detailBuku: com.example.remed.viewmodel.DetailBuku,
    onValueChange: (com.example.remed.viewmodel.DetailBuku) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = detailBuku.judul,
            onValueChange = { onValueChange(detailBuku.copy(judul = it)) },
            label = { Text("Judul Buku") },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        OutlinedTextField(
            value = detailBuku.deskripsi,
            onValueChange = { onValueChange(detailBuku.copy(deskripsi = it)) },
            label = { Text("Deskripsi") },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = false
        )
        
        // Pilihan Status (Radio Button / Dropdown) - Simplifikasi: Text dulu atau Dropdown Status
        // Disini kita tambahkan pilihan Kategori
        
        // Note: Idealnya kita pass listKategori dari ViewModel
        // Namun untuk sementara kita biarkan manual ID atau update ViewModel nanti 
        // agar mengambil list kategori.
        
        OutlinedTextField(
             value = detailBuku.kategoriId?.toString() ?: "",
             onValueChange = {
                 val newId = it.toIntOrNull()
                 onValueChange(detailBuku.copy(kategoriId = newId))
             },
             label = { Text("ID Kategori (Masukan Angka)") },
             keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
             modifier = Modifier.fillMaxWidth(),
             enabled = enabled
        )

        if (enabled) {
            Text(
                text = "* Pastikan data yang diisi benar.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
        Divider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    }
}
