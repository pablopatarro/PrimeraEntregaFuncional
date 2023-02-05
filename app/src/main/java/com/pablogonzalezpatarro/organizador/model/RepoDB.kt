package com.pablogonzalezpatarro.organizador.model

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pablogonzalezpatarro.organizador.objetos.Contacto

import com.google.firebase.firestore.ktx.snapshots
import com.pablogonzalezpatarro.organizador.mensajeError

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.coroutines.coroutineContext


object RepoDB {
    const val COLLECTION_USUARIOS = "Usuarios"
    const val COLLECTION_CONTACTOS = "Contactos"
/**
    suspend fun getAll(uid:String): List<Contacto> {
        val contactos = mutableListOf<Contacto>()
        var snapshot = FirebaseFirestore.getInstance().collection("Usuarios")
            .document(uid).collection("Contactos")
            .get().addOnSuccessListener {

            }


            //Si la operación ha ido bien, se transforma cada uno de los elementos de la colección
            // Contactos a la clase contactos que ya tenemos.
            for(doc in snapshot)
            {
                val contacto = doc.toObject(Contacto::class.java)
                if (contacto != null) {
                    contactos.add(contacto)
                }
            }
        return contactos
    }
**/
    fun getFlow(uid:String): Flow<List<Contacto>> {
        return FirebaseFirestore.getInstance()
            .collection(COLLECTION_USUARIOS)
            .document(uid).collection(COLLECTION_CONTACTOS)
            .snapshots().map {snapshot ->
                snapshot.toObjects(Contacto::class.java)
            }
    }

    suspend fun crearContacto(contacto:Contacto,contexto:Context) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        FirebaseFirestore.getInstance().collection(COLLECTION_USUARIOS)
            .document(uid!!)
            .collection(COLLECTION_CONTACTOS)
            .add(contacto)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText( contexto,"Contacto guardado con éxito",Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener {
               mensajeError("Error al guardar el contacto",contexto)
            }
    }
}