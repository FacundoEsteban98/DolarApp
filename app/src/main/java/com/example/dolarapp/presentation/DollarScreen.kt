package com.example.dolarapp.presentation

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.example.dolarapp.MainActivity
import com.example.dolarapp.MyApplication
import com.example.dolarapp.data.DollarEntity
import com.example.dolarapp.data.DollarModel
import com.example.dolarapp.persistence.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private lateinit var database: AppDatabase
@SuppressLint("CoroutineCreationDuringComposition")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DollarScreen(dollarViewModel: DollarViewModel = viewModel()) {
    // Observa el estado del ViewModel
    val uiState by dollarViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        dollarViewModel.getDollar()
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Cotización Dólar Argentino",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 16.dp)
            )

            DatePickerScreen()

            when (uiState) {
                is UiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is UiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Error: ${(uiState as UiState.Error).message}", color = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.height(16.dp)) // Espaciador para separar del DatePicker
                        }
                    }
                }
                is UiState.Success -> {
                    val dollar = (uiState as UiState.Success).dollar

                    database = (LocalContext.current.applicationContext as MyApplication).database
                    val dollarEntities = dollar.map { dollarViewModel.toEntity(it) }

                    CoroutineScope(Dispatchers.IO).launch {
                        database.dollarDao().insertAll(*dollarEntities.toTypedArray())
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        items(dollar) { item ->
                            DollarItem(item)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}




@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DatePickerScreen(dollarViewModel: DollarViewModel = viewModel()) {
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(top = 16.dp) ,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Fecha seleccionada: ${selectedDate?.let { it.toString() } ?: "Ninguna"}")

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { showDatePicker = true }) {
            Text("Buscar por fecha")
        }

        if (showDatePicker) {
            DatePickerModal(
                onDateSelected = { date ->
                    selectedDate = date
                    showDatePicker = false

                    dollarViewModel.getDollarByDate(selectedDate.toString().plus("%"), database)

                },
                onDismiss = { showDatePicker = false }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DollarItem(item: DollarModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(text = item.name.uppercase(), fontSize = 24.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = "Compra: ", fontSize = 18.sp,  fontWeight = FontWeight.Bold)
                Text(text = "$" + item.purchase.toString(), fontSize = 18.sp)
            }

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(fontSize = 18.sp,  text = "Venta: ", fontWeight = FontWeight.Bold)
                Text(fontSize = 18.sp,  text = "$" + item.sale.toString())
            }

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(fontSize = 18.sp,  text = "Fecha: ", fontWeight = FontWeight.Bold)
                val (fecha, hora) = checkIfTodayDate(item.updateDate)
                Text(fontSize = 18.sp,  text = fecha)
                Text(text = " $hora")
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun checkIfTodayDate(date: String): Pair<String,String> {
    val date2 = date.split("T")
    val formatoSalida = DateTimeFormatter.ofPattern("dd 'de' MMMM", Locale("es", "ES"))
    val fecha = LocalDate.parse(date2[0])
    return Pair(fecha.format(formatoSalida), date2[1].split(".")[0])
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (LocalDate?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val selectedDateMillis = datePickerState.selectedDateMillis
                val selectedDate = selectedDateMillis?.let {
                    Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                }
                onDateSelected(selectedDate)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun Preview() {
    DollarScreen()
}