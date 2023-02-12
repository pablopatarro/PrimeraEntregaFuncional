package com.pablogonzalezpatarro.organizador

import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.pablogonzalezpatarro.organizador.model.RepoDB
import com.pablogonzalezpatarro.organizador.objetos.Contacto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MainViewModel (): ViewModel(){
    private val _state = MutableLiveData(MainState())
    val state: LiveData<MainState> get() = _state
    var uid = FirebaseAuth.getInstance().currentUser?.uid

    data class MainState(
        val contactos: Flow<List<Contacto>>? = null,
        val loading:Boolean = false)

    init{
       viewModelScope.launch {
           _state.value = _state.value?.copy(loading = true)
           _state.value = _state.value?.copy(contactos = RepoDB.getFlow(uid!!))
       }
    }


}

@Suppress("UNCHECKED_CAST")
class MainViewModelFactory(): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel() as T
    }

}