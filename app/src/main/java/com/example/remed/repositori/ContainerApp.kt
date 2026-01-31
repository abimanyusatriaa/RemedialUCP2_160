package com.example.remed.repositori

import android.content.Context
import com.example.remed.room.BukuDatabase

interface AppContainer {
    val repositoriBuku: RepositoriBuku
}

class ContainerApp(private val context: Context) : AppContainer {
    override val repositoriBuku: RepositoriBuku by lazy {
        val database = BukuDatabase.getDatabase(context)
        RepositoriBukuImpl(database.bukuDao(), database)
    }
}
