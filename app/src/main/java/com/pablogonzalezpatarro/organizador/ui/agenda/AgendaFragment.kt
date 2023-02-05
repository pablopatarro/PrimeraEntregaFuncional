package com.pablogonzalezpatarro.organizador.ui.agenda

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pablogonzalezpatarro.organizador.MainActivity
import com.pablogonzalezpatarro.organizador.R
import com.pablogonzalezpatarro.organizador.databinding.FragmentAgendaBinding
import com.pablogonzalezpatarro.organizador.objetos.Contacto
import com.pablogonzalezpatarro.organizador.ui.create.CreateContactoFragment
import com.pablogonzalezpatarro.organizador.ui.detail.ContactoDetailFragment
import kotlinx.coroutines.launch

class AgendaFragment : Fragment(R.layout.fragment_agenda) {
    private lateinit var binding: FragmentAgendaBinding
    private val agendaViewModel: AgendaViewModel by viewModels() { AgendaViewModelFactory() }
    private val adapter = ContactoAdapter(){ contacto -> agendaViewModel.navigateTo(contacto)  }


/*
    private val binding get() = _binding!!

    override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
    ): View {

    val agendaViewModel = ViewModelProvider(this).get(AgendaViewModel::class.java)
    val adapter = ContactoAdapter(){ contacto -> agendaViewModel.navigateTo(contacto)  }


    _binding = FragmentAgendaBinding.inflate(inflater, container, false)
    val root: View = binding.root
        _binding = FragmentAgendaBinding.bind(view).apply {
            recycler.adapter = adapter
        }
    //Aquí es donde hacemos la lógica de qué pasa cuando le dan a un contacto.

    return root
    }
*/
     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAgendaBinding.bind(view).apply {
            recycler.adapter = adapter
        }
//esto se implementa al final.
        val busqueda = binding!!.buscador

        agendaViewModel.state.observe(viewLifecycleOwner){state->

            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED){
                    state.contactos?.collect(){
                        adapter.contactos = it
                        adapter.notifyDataSetChanged()
                    }
                }
            }//Fin del lifecycleScope

            //Navegación al detalle.
            state.navigateTo?.let {
                findNavController().navigate(
                    R.id.action_nav_agenda_to_contactoDetailFragment,
                bundleOf(ContactoDetailFragment.EXTRA_CONTACTO to it)
                )
                agendaViewModel.onNavigateDone()
            }

            state.navigateToCreate?.let {
                if(it)
                {
                    findNavController().navigate(
                        R.id.action_nav_agenda_to_createContactoFragment,
                        bundleOf(CreateContactoFragment.EXTRA_CREAR to Contacto("","",""))
                    )
                    agendaViewModel.navigateToCreateDone()
                }
            }



        }//Fin del observe

    //Añadimos al fab un evento para que nos mande a la pantalla de creación
    //del contacto.
    binding.fabSpace.fab.setOnClickListener{
       agendaViewModel.navigateToCreate()
    }

    }//fin del onCreateView.

   /*
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
    */
}