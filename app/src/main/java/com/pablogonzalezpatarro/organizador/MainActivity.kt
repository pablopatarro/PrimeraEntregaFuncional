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

    private val REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 0
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
                R.id.nav_home, R.id.nav_agenda
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
            else -> return super.onOptionsItemSelected(item)
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
