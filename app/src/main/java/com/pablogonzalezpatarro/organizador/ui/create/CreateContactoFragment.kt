package com.pablogonzalezpatarro.organizador.ui.create

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.pablogonzalezpatarro.organizador.R
import com.pablogonzalezpatarro.organizador.databinding.FragmentCreateContactoBinding
import com.pablogonzalezpatarro.organizador.mensajeError
import com.pablogonzalezpatarro.organizador.objetos.Contacto

class CreateContactoFragment : Fragment(R.layout.fragment_create_contacto) {
    private lateinit var binding: FragmentCreateContactoBinding
    private val viewModel: CreateContactoViewModel by viewModels()

    companion object {
        const val EXTRA_CREAR = "contacto"
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCreateContactoBinding.bind(view)


        binding.botonModificar.setOnClickListener{}







/**************************FALTA HACER LA PARTE EN LA QUE SE MODIFICA EL CONTACTO ***************************/
        binding.botonCrear.setOnClickListener{
                //Si el contacto que nos llega a esta pantalla es null o vacío, hacemos el proceso de creación.
                    val nombre = binding.etNombre.text
                    val telefono = binding.etTelefono.text
                    val email = binding.etEmail.text

                    //Si alguno de los campos es vacío, se muestra un error
                    if(nombre.isEmpty() || telefono.isEmpty() || email.isEmpty()) {
                        mensajeError("Todos los campos deben estar rellenados para crear un contacto",
                            requireContext())
                    }
                    else
                    {
                        val contacto = Contacto(nombre.toString(),telefono.toString(),email.toString())
                        viewModel.crearContacto(contacto,requireContext())
                        Thread.sleep(2000)
                        findNavController().navigate(
                            R.id.action_createContactoFragment_to_nav_agenda)
                    }
            }
        }//Fin de onViewCreate
}


