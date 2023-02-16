package com.pablogonzalezpatarro.organizador

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import com.google.firebase.auth.FirebaseAuth
import com.pablogonzalezpatarro.organizador.model.RepoDB
import com.pablogonzalezpatarro.organizador.ui.create.CreateContactoFragment

//Función que muestra un diálogo de error.
fun mensajeError(mensaje: String, context: Context)
{
    //creamos un mensaje de alerta.
    val builder = AlertDialog.Builder(context)
    builder.setTitle("Error")
    builder.setMessage(mensaje)
    builder.setPositiveButton("Aceptar",null)
    val dialogo : AlertDialog = builder.create()
    dialogo.show()
}
