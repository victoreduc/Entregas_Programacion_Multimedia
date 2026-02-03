package com.example.practica3multimedia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.practica3multimedia.data.local.model.Gasto
import com.example.practica3multimedia.viewmodel.GastoViewModel
import com.example.practica3multimedia.ui.theme.ElectricViolet
import com.example.practica3multimedia.ui.theme.MintGreen
import com.example.practica3multimedia.ui.theme.TextPrimary
import com.example.practica3multimedia.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: GastoViewModel,
    onNavigateToAdd: (Int) -> Unit // Ahora pasamos un Int (el ID)
) {
    val listaGastos by viewModel.gastos.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var gastoToDelete by remember { mutableStateOf<Gasto?>(null) }

    if (showDeleteDialog && gastoToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Estás seguro de que quieres eliminar el gasto '${gastoToDelete?.title}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        gastoToDelete?.let { viewModel.deleteGasto(it) }
                        showDeleteDialog = false
                        gastoToDelete = null
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar", color = TextPrimary)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = TextPrimary,
            textContentColor = TextSecondary
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mis Finanzas", style = MaterialTheme.typography.headlineMedium) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = ElectricViolet
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onNavigateToAdd(-1) }, // -1 significa "Nuevo Gasto"
                containerColor = ElectricViolet,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Add, "Añadir") },
                text = { Text("Gasto") }
            )
        }
    ) { padding ->
        if (listaGastos.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No tienes gastos registrados", color = TextSecondary)
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(listaGastos) { gasto ->
                    GastoItem(
                        gasto = gasto,
                        onClick = { onNavigateToAdd(gasto.id) }, // Navegamos con el ID real
                        onDelete = {
                            gastoToDelete = gasto
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun GastoItem(gasto: Gasto, onClick: () -> Unit, onDelete: () -> Unit) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.clickable { onClick() } // Hacemos la tarjeta clicable
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(color = ElectricViolet.copy(alpha = 0.2f), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("€", color = ElectricViolet, style = MaterialTheme.typography.titleLarge)
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(gasto.title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                    Text(gasto.category, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "-${gasto.amount}€",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MintGreen
                )
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Borrar", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}