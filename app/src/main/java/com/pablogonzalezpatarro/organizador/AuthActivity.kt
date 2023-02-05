package com.pablogonzalezpatarro.organizador

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.pablogonzalezpatarro.organizador.databinding.ActivityAuthBinding
import com.pablogonzalezpatarro.organizador.objetos.Usuario

class AuthActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //iniciamos el binding.
        binding= ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //iniciamos la variable de autenticación.
        auth = FirebaseAuth.getInstance()

        //Si el usuario se quiere registrar...
        binding.btnRegistrar.setOnClickListener {
            //recogemos los valores de los campos email y contraseña.
            val email = binding.emailEditText.text.toString()
            val password = binding.passEditText.text.toString()

            //Si alguno de los campos es vacío, mostramos un toast y salimos con un return.
            if (email.isEmpty() || password.isEmpty()) {
                mensajeError("Debe introducir email y contraseña",this@AuthActivity)
                return@setOnClickListener
            }

            if(password.length<6)
            {
                mensajeError("La contraseña debe tener más de 5 carácteres",this@AuthActivity)
                return@setOnClickListener
            }

            //Si llegamos aquí,creamos al usuario.
            //Usamos createUserWithEmailAndPassword y comprobaremos que si la operación ha ido bien.
            auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener { respuesta->
                    if(respuesta.isSuccessful) {
                        //si la operación ha ido bien, deberá entrar en la app.
                        // Debería crear al usuario en el storage.
                        if(respuesta.result.user!=null) {
                            guardarUsuario()
                            navegacionHome(respuesta.result.user!!.uid)
                        }
                    }
                    else
                    {
                        //Si no ha ido bien, mostrarmos error.
                        mensajeError("Error de autenticación\n"+
                                respuesta.exception.toString(),this@AuthActivity)
                    }
                }
        }


    //Manejador de eventos para el botón de acceso.
        binding.btnAcceder.setOnClickListener {
            //recogemos los valores de los campos email y contraseña.
            val email = binding.emailEditText.text.toString()
            val password = binding.passEditText.text.toString()

            //Si alguno de los campos es vacío, mostramos un toast y salimos con un return.
            if (email.isEmpty() || password.isEmpty()) {
                mensajeError("Debe introducir email y contraseña",this@AuthActivity)
                return@setOnClickListener
            }

            if(password.length<6)
            {
                mensajeError("La contraseña debe tener más de 5 carácteres",this@AuthActivity)
                return@setOnClickListener
            }

            //Si llegamos aquí, es porque los campos están rellenos.
            //Hacemos la petición de autenticación.
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { respuesta ->
                    if (respuesta.isSuccessful) {
                        if(respuesta.result.user?.uid!=null){
                            navegacionHome(respuesta.result.user?.uid!!)
                        }

                    } else {
                       //Mostrar errores.
                        mensajeError("Error inesperado en la autenticación de usuario\n"
                                +respuesta.exception.toString(),this@AuthActivity)
                    }
                }
        }
    }

    //Función para navegar al mainActivity
    private fun navegacionHome(uid:String) {
            val intent = Intent(this@AuthActivity, MainActivity::class.java)
            intent.putExtra("uid",uid)

            startActivity(intent)
    }


    private fun guardarUsuario()
    {
        //Si entro en esta función es porque el usuario registrado
        //no está en la coleción de Usuarios de la base de datos.
        //Hay que meterlo.
        val usuarioActual:FirebaseUser? = FirebaseAuth.getInstance().currentUser;
        val paraAñadir:Usuario
        if(usuarioActual!=null) {
            paraAñadir = Usuario(usuarioActual.email.toString(), "")
            //Le mandamos el uid a través de referencia.
            val ref = FirebaseFirestore.getInstance().collection("Usuarios")
                .document(usuarioActual.uid)

            ref.set(paraAñadir)
        }

    }

}