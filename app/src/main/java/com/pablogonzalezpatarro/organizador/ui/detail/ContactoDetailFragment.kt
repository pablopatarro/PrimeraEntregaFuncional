package com.pablogonzalezpatarro.organizador.ui.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import com.pablogonzalezpatarro.organizador.R
import com.pablogonzalezpatarro.organizador.databinding.FragmentContactoDetailBinding
import com.pablogonzalezpatarro.organizador.objetos.Contacto

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

    }


}