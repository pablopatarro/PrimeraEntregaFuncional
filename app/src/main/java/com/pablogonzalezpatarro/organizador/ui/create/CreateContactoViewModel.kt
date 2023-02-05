package com.pablogonzalezpatarro.organizador.ui.create

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pablogonzalezpatarro.organizador.model.RepoDB
import com.pablogonzalezpatarro.organizador.objetos.Contacto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreateContactoViewModel : ViewModel() {
    fun crearContacto(contacto: Contacto,context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            RepoDB.crearContacto(contacto, context)
        }
    }
}
