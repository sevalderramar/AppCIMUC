package com.example.appcimuc.viewmodel

import androidx.lifecycle.ViewModel
import com.example.appcimuc.model.AppInfo
import com.example.appcimuc.repository.AppRepository

/**
 * ViewModel base: conecta la interfaz con los datos sin poner lógica en la Activity.
 */
class MainViewModel(
    private val repository: AppRepository = AppRepository()
) : ViewModel() {

    val appInfo: AppInfo = repository.getAppInfo()
}
