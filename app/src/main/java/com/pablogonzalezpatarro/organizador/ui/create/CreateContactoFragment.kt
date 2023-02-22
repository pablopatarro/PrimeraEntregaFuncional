package com.pablogonzalezpatarro.organizador.ui.create

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.drawToBitmap
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.pablogonzalezpatarro.organizador.R
import com.pablogonzalezpatarro.organizador.databinding.FragmentCreateContactoBinding
import com.pablogonzalezpatarro.organizador.mensajeError
import com.pablogonzalezpatarro.organizador.objetos.Contacto
import java.io.ByteArrayOutputStream
import java.net.URI

class CreateContactoFragment : Fragment(R.layout.fragment_create_contacto) {
    private val GALLERY_REQUEST_CODE = 1
    private lateinit var binding: FragmentCreateContactoBinding
    private val viewModel: CreateContactoViewModel by viewModels{
        CreateContactoViewModelFactory(arguments?.getParcelable<Contacto>(EXTRA_CREAR)!!)
    }
    private  var URI_ACTUAL:Uri? = null
    companion object {
        const val EXTRA_CREAR = "contacto"
    }
    @SuppressLint("IntentReset")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCreateContactoBinding.bind(view)
        (requireActivity() as AppCompatActivity).supportActionBar?.title="Crear/Modificar contacto"

        lateinit var contactoAModificar:Contacto
        viewModel.contacto.observe(viewLifecycleOwner){ contacto->
            //Si los campos no están vacíos, significa que venimos de la ficha de un contacto,
            //por tanto, se debe mostrar el botón de modificar y ocultar el de crear.
            if(contacto.nombre?.isNotEmpty() == true &&
                contacto.telefono?.isNotEmpty() == true &&
                contacto.email?.isNotEmpty() == true)
            {
                binding.botonModificar.visibility= View.VISIBLE
                binding.botonCrear.visibility = View.GONE
                contactoAModificar = contacto
                URI_ACTUAL=null
                //Mostramos la imagen del contacto que queremos modificar en el campo correspondiente.
                Glide.with(binding.imagenContacto)
                    .load(contacto.urlImagen)
                    .into(binding.imagenContacto)
            }
            else
            {
                binding.botonCrear.visibility = View.VISIBLE
                binding.botonModificar.visibility = View.GONE

            }
            binding.etNombre.setText(contacto.nombre.toString())
            binding.etEmail.setText(contacto.email.toString())
            binding.etTelefono.setText(contacto.telefono.toString())


        }

        binding.botonModificar.setOnClickListener{
            //Recuperamos los datos de los campos de texto.
            val nombre = binding.etNombre.text
            val telefono = binding.etTelefono.text
            val email = binding.etEmail.text

            //Si alguno de los campos es vacío, se muestra un error
            if(nombre.isEmpty() || telefono.isEmpty() || email.isEmpty())
            {
                mensajeError("Todos los campos deben estar rellenados para crear un contacto",
                    requireContext())
            }
            else
            {
                var urlImagen = contactoAModificar.urlImagen
                if(URI_ACTUAL != null) {
                    val storageRef = FirebaseStorage.getInstance().reference
                    //Borramos la foto anterior...
                    FirebaseStorage
                        .getInstance().reference
                        .child("fotos").child("foto_de_" + contactoAModificar.email)
                        .delete()

                    val refImagen = storageRef.child("fotos").child("foto_de_" + email)
                    println(email)
                    //"subimos" la foto al storage.
                    val subidaFoto = refImagen.putFile(URI_ACTUAL!!)
                    subidaFoto.addOnSuccessListener {
                    refImagen.downloadUrl.addOnSuccessListener { uri ->
                        urlImagen = uri.toString()

                    }
                }//fin de la tarea de la foto
                }
                val nuevoContacto = Contacto(nombre.toString(),telefono.toString()
                    ,email.toString(),urlImagen)

                viewModel.modificarContacto(nuevoContacto,contactoAModificar,requireContext())
                Thread.sleep(500)
                URI_ACTUAL=null
                findNavController().navigate(
                    R.id.action_createContactoFragment_to_nav_agenda
                )

            }


        } //Fin evento modificar datos.

        binding.botonCrear.setOnClickListener{
            //Si el contacto que nos llega a esta pantalla es null o vacío, hacemos el proceso de creación.
                val nombre = binding.etNombre.text
                val telefono = binding.etTelefono.text
                val email = binding.etEmail.text

                //obligamos al usuario a elegir foto.

            if(URI_ACTUAL == null)
            {
                mensajeError("Debe elegir una fotografía para el contacto",requireContext())
            }
            else{
                //Si alguno de los campos es vacío, se muestra un error
                if(nombre.isEmpty() || telefono.isEmpty() || email.isEmpty()) {
                    mensajeError("Todos los campos deben estar rellenados para crear un contacto",
                        requireContext())
                }
                else {
                    //Aquí debemos recuperar la referencia a la imagen que acabamos de cargar...
                    val storageRef = FirebaseStorage.getInstance().reference
                    val refImagen = storageRef.child("fotos").child("foto_de_" + email)
                    val subidaFoto = refImagen.putFile(URI_ACTUAL!!)

                    subidaFoto.addOnSuccessListener {
                        refImagen.downloadUrl.addOnSuccessListener { uri ->
                            val urlImagen = uri.toString()
                            //Aquí debemos guardar la imagen en Storage, recuperar la url y mandarsela al contacto.

                            val contacto = Contacto(nombre.toString(),telefono.toString(),
                                                    email.toString(),urlImagen)
                            viewModel.crearContacto(contacto, requireContext())
                            Thread.sleep(500)
                            URI_ACTUAL=null
                            findNavController().navigate(
                                R.id.action_createContactoFragment_to_nav_agenda
                            )
                        }
                    }//fin de la tarea de la foto
                }
                }
            }

        binding.botonImagen.setOnClickListener{
            //Cada vez que pulsemos el botón, hacemos un intent para abrir la galeria.
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,GALLERY_REQUEST_CODE)
        }

        }//Fin de onViewCreate


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK ){
            //Recuperamos la imagen seleccionada.
            val imagenSeleccionada = data?.data
            //La mandamos a la imagen del contacto.
            URI_ACTUAL = imagenSeleccionada!!
            binding.imagenContacto.setImageURI(imagenSeleccionada)

        }
    }
}


