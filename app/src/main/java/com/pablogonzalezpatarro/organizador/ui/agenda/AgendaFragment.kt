package com.pablogonzalezpatarro.organizador.ui.agenda

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.pablogonzalezpatarro.organizador.AuthActivity
import com.pablogonzalezpatarro.organizador.R
import com.pablogonzalezpatarro.organizador.databinding.FragmentAgendaBinding
import com.pablogonzalezpatarro.organizador.interfaces.SwipeToDeleteCallback
import com.pablogonzalezpatarro.organizador.model.RepoDB
import com.pablogonzalezpatarro.organizador.objetos.Contacto
import com.pablogonzalezpatarro.organizador.ui.create.CreateContactoFragment
import com.pablogonzalezpatarro.organizador.ui.detail.ContactoDetailFragment
import kotlinx.coroutines.launch

class AgendaFragment : Fragment(R.layout.fragment_agenda) {
    private lateinit var binding: FragmentAgendaBinding
    private val agendaViewModel: AgendaViewModel by viewModels() { AgendaViewModelFactory() }
    private val adapter = ContactoAdapter(){ contacto -> agendaViewModel.navigateTo(contacto)  }
     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAgendaBinding.bind(view).apply {
            recycler.adapter = adapter
        }

    //Creamos un objeto SwipeToDeleteCallBack y sobreescribimos el método de deslizado.
         val swipetoDeleteCallback = object : SwipeToDeleteCallback(requireContext()){
             override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                 val pos = viewHolder.bindingAdapterPosition
                 //val position = viewHolder.adapterPosition
                 val contacto:Contacto = adapter.contactos[pos]
                 //Borramos el contacto del recycler, del adapter y de la base de datos.
                 agendaViewModel.borrarContacto( contacto,requireContext() )
             }
         }

         val itemTouchHelper = ItemTouchHelper(swipetoDeleteCallback)
         itemTouchHelper.attachToRecyclerView(binding.recycler)

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


}
