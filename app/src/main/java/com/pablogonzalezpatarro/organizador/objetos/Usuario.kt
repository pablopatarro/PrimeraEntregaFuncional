package com.pablogonzalezpatarro.organizador.objetos

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Usuario(
    val email: String,
    val urlFoto: String,
    val contactos: List<Contacto>? = null
) : Parcelable

