package com.example.dolarapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dolarapp.data.DollarEntity
import com.example.dolarapp.data.DollarModel
import com.example.dolarapp.data.DollarRepository
import com.example.dolarapp.data.DollarSummary
import com.example.dolarapp.persistence.AppDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel //Etiqueta para inyectar dependencias
class DollarViewModel @Inject constructor(
    private val dollarRepository: DollarRepository,
) : ViewModel() {

    //Uso Kotlin State Flow para almacenar el estado de la UI
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    //Método para enviar la solicitud a la API
    fun getDollar() {
        _uiState.value = UiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = dollarRepository.getDollar()
                when {
                    response.isSuccessful -> {
                        val history = response.body()
                        _uiState.value = UiState.Success(history!!)
                    }
                    else -> {
                        _uiState.value = UiState.Error("Error en la respuesta")
                    }
                }

            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun getDollarByDate(date: String, database: AppDatabase) {
        _uiState.value = UiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val entity = database.dollarDao().findByDate(date)
                val model = mutableListOf<DollarModel>()
                for (m in entity) {
                    model.add(m.toModel())
                }
                if (model.size == 0) {
                    _uiState.value = UiState.Error("No hay datos para esa fecha")
                } else {
                    _uiState.value = UiState.Success(model)
                }

            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    private fun DollarSummary.toModel(): DollarModel {
        return DollarModel(
            currency = this.currency,
            house = this.house,
            name = this.name,
            purchase = this.purchase,
            updateDate = this.updateDate,
            sale = this.sale
        )
    }

    fun toEntity(model: DollarModel): DollarEntity {
        return DollarEntity(
            uid = 0,
            currency = model.currency,
            house = model.house,
            name = model.name,
            purchase = model.purchase,
            updateDate = model.updateDate,
            sale = model.sale
        )
    }

}


// UI State enum
sealed class UiState {
    data object Loading : UiState()
    class Success(val dollar: MutableList<DollarModel>) : UiState()
    data class Error(val message: String) : UiState()
}
