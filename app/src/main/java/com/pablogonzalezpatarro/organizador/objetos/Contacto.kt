package com.pablogonzalezpatarro.organizador.objetos

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Contacto(
    val nombre: String? ="",
    val telefono: String? ="",
    val email:String? ="",
    val urlImagen :String?=""
) : Parcelable

