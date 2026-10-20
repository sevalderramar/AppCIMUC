package com.example.appcimuc.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.appcimuc.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ParticipacionViewModel(proyectoId: String, repository: AppRepository = AppRepository()) : ViewModel() {
    private val _uiState = MutableStateFlow(ParticipacionUiState(proyecto = repository.getProyecto(proyectoId)))
    val uiState: StateFlow<ParticipacionUiState> = _uiState.asStateFlow()

    fun abrir(tipo: TipoParticipacion) { _uiState.update { abrirParticipacion(it, tipo) } }
    fun cerrar() { _uiState.update(::cerrarParticipacion) }
    fun cambiar(tipo: TipoParticipacion, campo: CampoParticipacion, valor: String) {
        _uiState.update { cambiarCampoParticipacion(it, tipo, campo, valor) }
    }
    fun enviar(tipo: TipoParticipacion): Boolean {
        val actual = _uiState.value
        val siguiente = enviarParticipacion(actual, tipo)
        return _uiState.compareAndSet(actual, siguiente) && actual.modal != siguiente.modal &&
            (siguiente.modal == ModalParticipacion.EXITO_POSTULACION || siguiente.modal == ModalParticipacion.EXITO_APORTE)
    }

    companion object {
        fun factory(proyectoId: String): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                require(modelClass.isAssignableFrom(ParticipacionViewModel::class.java))
                return ParticipacionViewModel(proyectoId) as T
            }
        }
    }
}
