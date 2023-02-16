package com.pablogonzalezpatarro.organizador.ui.agenda

import android.content.Context
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.pablogonzalezpatarro.organizador.model.RepoDB
import com.pablogonzalezpatarro.organizador.objetos.Contacto
import kotlinx.coroutines.flow.Flow

class AgendaViewModel() : ViewModel() {

    private val _state = MutableLiveData(UiState())
    val state : LiveData<UiState> get() = _state

    var uid = FirebaseAuth.getInstance().currentUser?.uid

    init
    {
        //Pedimos la lista de contactos a la base de datos. CON FLOW
        _state.value = _state.value?.copy(contactos = RepoDB.getFlow(uid!!))
    }

    fun navigateTo(contacto: Contacto)
    {
        _state.value = _state.value?.copy(navigateTo = contacto)
    }

    fun onNavigateDone()
    {
        _state.value = _state.value?.copy(navigateTo = null)
    }

    //Para navegar al fragment de creación de contacto.
    fun navigateToCreate() {
        _state.value = _state.value?.copy(navigateToCreate = true)
    }

    fun navigateToCreateDone() {
        _state.value = _state.value?.copy(navigateToCreate = false)
    }

    fun borrarContacto(contacto: Contacto, contexto: Context)
    {
        RepoDB.borrarContacto(contacto,contexto)
    }
    data class UiState(
        val contactos : Flow<List<Contacto>>? = null,
        val navigateTo: Contacto? = null,
        val navigateToCreate: Boolean = false
    )


}

class AgendaViewModelFactory(): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AgendaViewModel() as T
    }
}