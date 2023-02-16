package com.pablogonzalezpatarro.organizador.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toDrawable
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.pablogonzalezpatarro.organizador.R
import com.pablogonzalezpatarro.organizador.databinding.FragmentContactoDetailBinding
import com.pablogonzalezpatarro.organizador.objetos.Contacto
import com.pablogonzalezpatarro.organizador.ui.create.CreateContactoFragment

class ContactoDetailFragment : Fragment(R.layout.fragment_contacto_detail) {
    private lateinit var binding: FragmentContactoDetailBinding
    private val viewModel: DetailViewModel by viewModels{
        DetailViewModelFactory(arguments?.getParcelable<Contacto>(EXTRA_CONTACTO)!!)
    }
    companion object {
        const val EXTRA_CONTACTO = "contacto"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentContactoDetailBinding.bind(view)

        viewModel.contacto.observe(viewLifecycleOwner){contacto->
            (requireActivity() as AppCompatActivity).supportActionBar?.title="Detalles del contacto"

            binding.imagen.setImageDrawable(R.drawable.delete.toDrawable())
            binding.tvNombre.text = "Nombre: "+contacto.nombre
            binding.tvTelefono.text = "Teléfono: "+contacto.telefono
            binding.tvEmail.text = "Email: "+contacto.email


            //Metemos la imagen del contacto...
            Glide.with(binding.imagen)
                .load(contacto.urlImagen)
                .into(binding.imagen)

            //Hacemos la navegación al fragment de mostrar/modificar.
            binding.botonModificar.setOnClickListener{
                findNavController().navigate(
                    R.id.action_contactoDetailFragment_to_createContactoFragment,
                    bundleOf(CreateContactoFragment.EXTRA_CREAR to contacto)
                )
            }



            binding.botonLlamar.setOnClickListener{

                val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", contacto.telefono,null))
                startActivity(intent)
            }

            binding.botonEmail.setOnClickListener{
                val email = Uri.parse("mailto:")
                val intent = Intent(Intent.ACTION_SENDTO)
                intent.setData(email)
                intent.putExtra(Intent.EXTRA_EMAIL,contacto.email.toString())
                intent.putExtra(Intent.EXTRA_SUBJECT,"Email de prueba")
                intent.putExtra(Intent.EXTRA_TEXT,"Cuerpo del correo")
                startActivity(intent)
            }

        }//fin del observe.

    }//Fin del OnViewCreated.


}