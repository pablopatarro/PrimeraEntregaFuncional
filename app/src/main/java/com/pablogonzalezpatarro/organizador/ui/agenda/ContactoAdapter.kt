package com.pablogonzalezpatarro.organizador.ui.agenda

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pablogonzalezpatarro.organizador.R
import com.pablogonzalezpatarro.organizador.databinding.ViewContactoBinding
import com.pablogonzalezpatarro.organizador.objetos.Contacto

class ContactoAdapter (val listener: (Contacto)->Unit):RecyclerView.Adapter<ContactoAdapter.ViewHolder>(){

    var contactos = emptyList<Contacto>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_contacto,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contacto = contactos[position]
        holder.bind(contacto)

        holder.itemView.setOnClickListener{
            listener(contacto)
        }
    }

    override fun getItemCount(): Int = contactos.size

    class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        private  val binding = ViewContactoBinding.bind(view)

        fun bind(contacto: Contacto)
        {
            binding.nombre.text = "Nombre: "+contacto.nombre
            binding.telefono.text= "Número: "+contacto.telefono
        }
    }



}
