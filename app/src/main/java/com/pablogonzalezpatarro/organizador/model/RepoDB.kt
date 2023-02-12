package com.pablogonzalezpatarro.organizador.model

import android.content.Context
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pablogonzalezpatarro.organizador.objetos.Contacto
import com.google.firebase.firestore.ktx.snapshots
import com.pablogonzalezpatarro.organizador.mensajeError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object RepoDB {
    const val COLLECTION_USUARIOS = "Usuarios"
    const val COLLECTION_CONTACTOS = "Contactos"

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

    fun modificarContacto(contacto: Contacto, contactoAModificar: Contacto, contexto: Context)
    {
        //recuperamos el uid del usuario actual
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        //Tenemos que recuperar el registro en la base de datos para actualizarlo.
        FirebaseFirestore.getInstance().collection(COLLECTION_USUARIOS)
            .document(uid!!).collection(COLLECTION_CONTACTOS)
            .whereEqualTo("email", contactoAModificar.email)
            .get().addOnCompleteListener {
               //Si consigo encontrar el registro...
                    if(it.isSuccessful){
                        val id = it.result.first().id
                        FirebaseFirestore.getInstance()
                            .collection(COLLECTION_USUARIOS).document(uid)
                            .collection(COLLECTION_CONTACTOS).document(id)
                            .update("nombre",contacto.nombre, "telefono",contacto.telefono,
                                        "email",contacto.email, "urlImagen",contacto.urlImagen)
                            .addOnCompleteListener {
                                if (it.isSuccessful){
                                    Toast.makeText(contexto,"Contacto modificado con éxito",Toast.LENGTH_LONG).show()
                                }
                            }
                            .addOnFailureListener {
                                mensajeError("Error al actualizar el contacto",contexto)
                            }
                    }
           }
    }//Fin de modificarContacto

    fun borrarContacto(contacto: Contacto,context: Context)
    {
        //TODO
        //Cogemos el usuario actual y le borramos el contacto pasado como parámetro.

    }


    fun eliminarCuenta(contexto: Context)
    {
        //Eliminamos al usuario actual de la base de datos.
        //Recuperamos el email del usuario en firebaseAuth
        val usuarioActual = FirebaseAuth.getInstance().currentUser!!
        val email = FirebaseAuth.getInstance().currentUser!!.email

        //Buscamos el usuario en la base de datos que sea el correspondiente al usuario actual.
        FirebaseFirestore.getInstance().collection(COLLECTION_USUARIOS)
            .whereEqualTo("email",email)
            .get()
            .addOnCompleteListener {
                //Si consigue recuperar al usuario de la colección de usuarios, hacemos el proceso de borrado.
                if (it.isSuccessful) {
                    //Cogemos el id del usuario de la base de datos correspondiente
                    // al usuario actual de firebase.
                    val id = it.result.first().id

                    //Recuperamos la colección Contactos del usuario actual.
                    FirebaseFirestore.getInstance()
                        .collection(COLLECTION_USUARIOS)
                        .document(id)
                        .collection(COLLECTION_CONTACTOS)
                        .get()
                        .addOnSuccessListener { contactos->
                            //Cogemos cada contacto y lo borramos.
                            for(contacto in contactos) {
                                FirebaseFirestore.getInstance()
                                    .collection(COLLECTION_USUARIOS)
                                    .document(id)
                                    .collection(COLLECTION_CONTACTOS)
                                    .document(contacto.id)
                                    .delete()
                            }
                        }

                    FirebaseFirestore.getInstance().collection(COLLECTION_USUARIOS)
                        .document(id)
                        .delete().addOnFailureListener {
                            mensajeError("Error al eliminar el documento",contexto)
                            }
                        .addOnSuccessListener {
                            //Si borramos el usuario en la base de datos,
                            // se borra el usuario de firebase.
                            usuarioActual.delete()
                        }
                }
            }
    } //Función para borrar al usuario de FireStore y del registro de FirebaseAuth



}