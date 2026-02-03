package com.example.practica3multimedia.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.practica3multimedia.viewmodel.GastoViewModel
import com.example.practica3multimedia.ui.theme.ElectricViolet
import com.example.practica3multimedia.ui.theme.MintGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGastoScreen(
    navController: NavController,
    viewModel: GastoViewModel,
    gastoId: Int // Recibimos el ID (-1 si es nuevo, >0 si es edición)
) {
    // Estados del formulario
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var observations by remember { mutableStateOf("") }
    var originalDate by remember { mutableStateOf(0L) } // Para guardar la fecha original al editar

    // Determinar si estamos en modo edición
    val isEditing = gastoId != -1
    val screenTitle = if (isEditing) "Editar Gasto" else "Nuevo Gasto"
    val buttonText = if (isEditing) "Actualizar Gasto" else "Guardar Gasto"

    LaunchedEffect(gastoId) {
        if (isEditing) {
            val gasto = viewModel.getGastoById(gastoId)
            if (gasto != null) {
                title = gasto.title
                amount = gasto.amount.toString()
                observations = gasto.observations
                originalDate = gasto.date
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(screenTitle) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Atrás", tint = ElectricViolet)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Concepto") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ElectricViolet,
                    cursorColor = ElectricViolet,
                    focusedLabelColor = ElectricViolet
                )
            )

            OutlinedTextField(
                value = amount,
                onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) amount = it },
                label = { Text("Importe (€)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                leadingIcon = { Icon(Icons.Default.Add, contentDescription = null, tint = MintGreen) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MintGreen,
                    cursorColor = MintGreen,
                    focusedLabelColor = MintGreen
                )
            )

            OutlinedTextField(
                value = observations,
                onValueChange = { observations = it },
                label = { Text("Observaciones") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                minLines = 3,
                maxLines = 5,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = ElectricViolet) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ElectricViolet,
                    cursorColor = ElectricViolet,
                    focusedLabelColor = ElectricViolet
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (title.isNotBlank() && amount.isNotBlank()) {
                        if (isEditing) {
                            // ACTUALIZAR
                            viewModel.updateGasto(gastoId, title, amount, observations, originalDate)
                        } else {
                            // CREAR NUEVO
                            viewModel.addGasto(title, amount, observations)
                        }
                        navController.popBackStack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet)
            ) {
                Text(buttonText, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}