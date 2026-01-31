package com.example.remed.view

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.remed.view.uicontroller.HalamanEntry
import com.example.remed.view.uicontroller.HalamanHome
import com.example.remed.view.uicontroller.HalamanKategori

@Composable
fun PetaNavigasi() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home_route"
    ) {
        composable("home_route") {
            HalamanHome(
                navigateToEntry = { navController.navigate("entry_route") },
                navigateToKategori = { navController.navigate("kategori_route") },
                onDetailClick = { /* TODO: Navigasi ke Detail */ }
            )
        }
        composable("entry_route") {
            HalamanEntry(
                navigateBack = { navController.popBackStack() }
            )
        }
        composable("kategori_route") {
            HalamanKategori(
                navigateBack = { navController.popBackStack() }
            )
        }
    }
}
