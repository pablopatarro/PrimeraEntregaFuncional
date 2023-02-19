package com.pablogonzalezpatarro.organizador

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.pablogonzalezpatarro.organizador.databinding.ActivityMainBinding
import com.pablogonzalezpatarro.organizador.model.RepoDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter

class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 1
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel:MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        //Botón de cierre de sesión.
        binding.appBarMain.fab.setOnClickListener {
            /**Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        */
            //Para volver a la pantalla de login
            FirebaseAuth.getInstance().signOut()
            //Hacer la navegación forzada al login
            val intent = Intent(this@MainActivity,AuthActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView

        val navigationView = binding.navView
        val headerView = navigationView.getHeaderView(0)

        //Ponemos el correo en el header del navigation drawer
        var headerTitle = headerView.findViewById<TextView>(R.id.tvEmail)
        headerTitle.text = FirebaseAuth.getInstance().currentUser?.email.toString()

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_agenda, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        //Recuperamos los datos del usuario y cambiamos el encabezado del drawer.
        //val email :TextView = binding.navView.get(R.id.tvEmail) as TextView

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.borrarCuenta -> {
                ventanaConfirmación()
                return true
            }
            R.id.exportarContactosCSV -> {
                exportarContactosCSV()
            return true
        }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun exportarContactosCSV() {
        //Hacemos una corrutina para obtener los contactos...
        mainViewModel.state.observe(this) {estado->
            lifecycleScope.launch(Dispatchers.Main) {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    estado.contactos?.collect { contactos ->

                        //Preguntar por permisos de escritura.
                        if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            // Si los permisos no se han otorgado, mostrar diálogo para solicitarlos al usuario
                            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE_WRITE_EXTERNAL_STORAGE)
                        } else {
                            // Si los permisos ya se han otorgado, continuar con la exportación de contactos
                            //val archivoCSV = File(Environment.getExternalStorageDirectory().toString() + "/contactos.csv" )
                            val carpeta = File(getExternalFilesDir(null),"Contactos")
                            //Si la carpeta donde queremos guardar el fichero no existe, la creamos.
                            if(!carpeta.exists())
                            {
                                carpeta.mkdir()
                            }
                            //Creamos el fichero, el writer y escribimos los contactos en el fichero.
                            val ficheroCSV = File(carpeta,"contactos.csv")
                            val escritor = FileWriter(ficheroCSV)
                            escritor.append("nombre,telefono,email\n")

                            println(contactos.toString())
                            contactos.forEach { contacto ->
                                escritor.append("${contacto.nombre},${contacto.telefono},${contacto.email}\n")
                                println(contacto.nombre)
                            }

                            escritor.flush()
                            escritor.close()

                        }

                    }
                }
            }

        }
    }//Fin de la función exportar.
//Función para manejar la respuesta de los permisos...

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE_WRITE_EXTERNAL_STORAGE -> {
                // Si el usuario concede los permisos, continuar con la exportación de contactos
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    exportarContactosCSV()
                } else {
                    Toast.makeText(this@MainActivity, "Permiso de escritura denegado", Toast.LENGTH_SHORT).show()
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }



    private fun ventanaConfirmación() {

        val builder = AlertDialog.Builder(this)
        builder.setMessage("¿Estás seguro de que deseas eliminar la cuenta?")
            .setPositiveButton("Sí", DialogInterface.OnClickListener { dialog, id ->
                RepoDB.eliminarCuenta(this)
                //Aquí navegamos al login.

                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this@MainActivity,AuthActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            })
            .setNegativeButton("No", DialogInterface.OnClickListener { dialog, id ->
            })
        val alert = builder.create()
        alert.show()

    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}