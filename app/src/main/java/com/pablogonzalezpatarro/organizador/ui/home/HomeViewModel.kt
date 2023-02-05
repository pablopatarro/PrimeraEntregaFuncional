package com.pablogonzalezpatarro.organizador.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Esta es la pantalla principal"
    }
    val text: LiveData<String> = _text
}