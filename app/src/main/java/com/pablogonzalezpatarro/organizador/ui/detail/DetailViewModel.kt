package com.pablogonzalezpatarro.organizador.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pablogonzalezpatarro.organizador.objetos.Contacto

class DetailViewModel (contacto: Contacto): ViewModel(){
    private val _contacto = MutableLiveData(contacto)
    val contacto: LiveData<Contacto> get() = _contacto

}

@Suppress("UNCHECKED_CAST")
class DetailViewModelFactory(private val contacto: Contacto): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DetailViewModel(contacto) as T
    }

}