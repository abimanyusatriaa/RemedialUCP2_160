package com.example.remed.view.uicontroller

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.remed.room.Kategori
import com.example.remed.viewmodel.KategoriViewModel
import com.example.remed.viewmodel.provider.PenyediaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanKategori(
    navigateBack: () -> Unit,
    viewModel: KategoriViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val listKategori by viewModel.listKategori.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var kategoriToDelete by remember { mutableStateOf<Kategori?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kelola Kategori") },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Kategori")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            if (viewModel.errorMessage != null) {
                Text(
                    text = viewModel.errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
            
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(listKategori) { kategori ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = kategori.nama, style = MaterialTheme.typography.titleMedium)
                                if (kategori.parentId != null) {
                                    Text(text = "Sub-kategori dari ID: ${kategori.parentId}", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                            IconButton(onClick = { kategoriToDelete = kategori }) {
                                Icon(Icons.Default.Delete, contentDescription = "Hapus")
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        DialogTambahKategori(
            onDismiss = { showAddDialog = false },
            onConfirm = {
                viewModel.insertKategori()
                showAddDialog = false
            },
            viewModel = viewModel,
            listKategori = listKategori
        )
    }

    if (kategoriToDelete != null) {
        DialogHapusKategori(
            kategori = kategoriToDelete!!,
            onDismiss = { kategoriToDelete = null },
            onConfirm = { deleteBooks ->
                viewModel.deleteKategori(kategoriToDelete!!.id, deleteBooks)
                kategoriToDelete = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogTambahKategori(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    viewModel: KategoriViewModel,
    listKategori: List<Kategori>
) {
    var expanded by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Kategori Baru") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = viewModel.namaKategori,
                    onValueChange = { viewModel.namaKategori = it },
                    label = { Text("Nama Kategori") }
                )
                OutlinedTextField(
                    value = viewModel.deskripsiKategori,
                    onValueChange = { viewModel.deskripsiKategori = it },
                    label = { Text("Deskripsi") }
                )
                
                // Dropdown Parent ID
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = viewModel.parentIdKategori?.let { id -> listKategori.find { it.id == id }?.nama } ?: "Root (Tidak ada induk)",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Root (Tidak ada induk)") },
                            onClick = {
                                viewModel.parentIdKategori = null
                                expanded = false
                            }
                        )
                        listKategori.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item.nama) },
                                onClick = {
                                    viewModel.parentIdKategori = item.id
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = { Button(onClick = onConfirm) { Text("Simpan") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } }
    )
}

@Composable
fun DialogHapusKategori(
    kategori: Kategori,
    onDismiss: () -> Unit,
    onConfirm: (Boolean) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Hapus Kategori '${kategori.nama}'?") },
        text = {
            Text("Pilih aksi untuk buku dalam kategori ini:\n\n" +
                    "1. Soft Delete: Buku ikut dihapus (sbg sampah).\n" +
                    "2. Unlink: Status buku jadi 'Tanpa Kategori'.\n\n" +
                    "Catatan: Jika ada buku DIPINJAM, operasi akan digagalkan otomatis.")
        },
        confirmButton = {
            Column {
                 Button(onClick = { onConfirm(true) }) { Text("Hapus Buku Juga (Soft Delete)") }
                 Button(onClick = { onConfirm(false) }) { Text("Hanya Lepas Kategori (Unlink)") }
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } }
    )
}
