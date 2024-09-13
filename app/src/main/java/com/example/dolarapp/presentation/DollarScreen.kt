package com.example.dolarapp.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dolarapp.data.DollarModel
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DollarScreen(dollarViewModel: DollarViewModel = viewModel()) {
    // Observa el estado del ViewModel
    val uiState by dollarViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        dollarViewModel.getDollar()
    }

    // Define el contenido de la pantalla basado en el estado
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        when (uiState) {
            is UiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is UiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Error: ${(uiState as UiState.Error).message}", color = MaterialTheme.colorScheme.error)
                }
            }
            is UiState.Success -> {
                val dollar = (uiState as UiState.Success).dollar
                LazyColumn(modifier = Modifier.padding(16.dp)) {
                    items(dollar) { item ->
                        DollarItem(item)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DollarItem(item: DollarModel) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "Nombre: ${item.name}", fontSize = 16.sp)
        Text(text = "Compra: ${item.purchase}", fontSize = 16.sp)
        Text(text = "Venta: ${item.sale}", fontSize = 16.sp)
        Text(text = "Fecha de Actualización: ${item.updateDate}", fontSize = 14.sp)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun Preview() {
    DollarScreen()
}