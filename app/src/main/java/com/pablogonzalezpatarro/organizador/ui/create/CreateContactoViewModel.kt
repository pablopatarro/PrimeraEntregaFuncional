package com.pablogonzalezpatarro.organizador.ui.create

import android.content.Context
import androidx.lifecycle.*
import com.pablogonzalezpatarro.organizador.model.RepoDB
import com.pablogonzalezpatarro.organizador.objetos.Contacto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreateContactoViewModel(contacto: Contacto) : ViewModel() {
    private val _contacto = MutableLiveData(contacto)
    val contacto: LiveData<Contacto> get() = _contacto
    //Para crear...
    fun crearContacto(contacto: Contacto,context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            RepoDB.crearContacto(contacto, context)
        }
    }
    //Para modificar...
    fun modificarContacto(contacto: Contacto, contactoAModificar: Contacto, context: Context){
        viewModelScope.launch (Dispatchers.IO){
            RepoDB.modificarContacto(contacto,contactoAModificar,context)
        }
    }
}
@Suppress("UNCHECKED_CAST")
class CreateContactoViewModelFactory(private val contacto: Contacto): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CreateContactoViewModel(contacto) as T
    }

}