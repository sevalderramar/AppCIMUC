package com.example.appcimuc.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.appcimuc.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ContactoViewModel(proyectoId: String, repository: AppRepository = AppRepository()) : ViewModel() {
    private val _uiState = MutableStateFlow(ContactoUiState(proyecto = repository.getProyecto(proyectoId)))
    val uiState: StateFlow<ContactoUiState> = _uiState.asStateFlow()

    fun cambiarNombre(nombre: String) = actualizarBorrador { estado ->
        estado.copy(nombre = nombre, errores = estado.errores.copy(nombre =
            if (estado.errores.nombre != null) validarContacto(nombre, estado.correo, estado.mensaje).nombre else null))
    }

    fun cambiarCorreo(correo: String) = actualizarBorrador { estado ->
        estado.copy(correo = correo, errores = estado.errores.copy(correo =
            if (estado.errores.correo != null) validarContacto(estado.nombre, correo, estado.mensaje).correo else null))
    }

    fun cambiarMensaje(mensaje: String) = actualizarBorrador { estado ->
        estado.copy(mensaje = mensaje, errores = estado.errores.copy(mensaje =
            if (estado.errores.mensaje != null) validarContacto(estado.nombre, estado.correo, mensaje).mensaje else null))
    }

    private fun actualizarBorrador(transformar: (ContactoUiState) -> ContactoUiState) {
        _uiState.update { if (it.paso == PasoContacto.FORMULARIO && it.proyecto != null) transformar(it) else it }
    }

    fun revisar(): Boolean {
        val actual = _uiState.value
        val siguiente = revisarContacto(actual)
        return _uiState.compareAndSet(actual, siguiente) && actual.paso == PasoContacto.FORMULARIO &&
            siguiente.paso == PasoContacto.RESUMEN
    }

    fun editar() {
        _uiState.update(::editarContacto)
    }

    fun confirmar(): Boolean {
        val actual = _uiState.value
        val siguiente = confirmarContacto(actual)
        return _uiState.compareAndSet(actual, siguiente) && actual.paso == PasoContacto.RESUMEN &&
            siguiente.paso == PasoContacto.CONFIRMACION
    }

    companion object {
        fun factory(proyectoId: String): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                require(modelClass.isAssignableFrom(ContactoViewModel::class.java))
                return ContactoViewModel(proyectoId) as T
            }
        }
    }
}
