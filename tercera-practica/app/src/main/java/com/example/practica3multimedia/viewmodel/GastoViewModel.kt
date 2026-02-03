package com.example.practica3multimedia.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practica3multimedia.data.local.dao.GastoDao
import com.example.practica3multimedia.data.local.model.Gasto
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GastoViewModel(private val dao: GastoDao) : ViewModel() {

    val gastos: StateFlow<List<Gasto>> = dao.getAllGastos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Función para obtener un gasto por ID (usada al editar)
    suspend fun getGastoById(id: Int): Gasto? {
        return dao.getGastoById(id)
    }

    // Añadir nuevo (ID es 0, se autogenera)
    fun addGasto(title: String, amount: String, observations: String) {
        val amountDouble = amount.toDoubleOrNull() ?: 0.0
        val currentDate = System.currentTimeMillis()

        val newGasto = Gasto(
            title = title,
            amount = amountDouble,
            category = "General",
            date = currentDate,
            observations = observations
        )

        viewModelScope.launch {
            dao.insertGasto(newGasto)
        }
    }

    fun updateGasto(id: Int, title: String, amount: String, observations: String, originalDate: Long) {
        val amountDouble = amount.toDoubleOrNull() ?: 0.0

        val updatedGasto = Gasto(
            id = id,
            title = title,
            amount = amountDouble,
            category = "General",
            date = originalDate,
            observations = observations
        )

        viewModelScope.launch {
            dao.updateGasto(updatedGasto)
        }
    }

    fun deleteGasto(gasto: Gasto) {
        viewModelScope.launch {
            dao.deleteGasto(gasto)
        }
    }
}