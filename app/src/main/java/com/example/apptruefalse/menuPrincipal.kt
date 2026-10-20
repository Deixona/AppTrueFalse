package com.example.apptruefalse

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class menuPrincipal : AppCompatActivity() {

    lateinit var botonJugar: Button
    lateinit var botonEstadisticas: Button
    lateinit var botonModoDificil: Button
    lateinit var botonMas: Button

    //aquí estoy creando un launcher
    val launcherJuego = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult())
    {
        //este es el if que no entiendo muy bien
        resultado ->
        if(resultado.resultCode == RESULT_OK)
        {
            // resultado.data son los datos que me traigo de Buzz.kt al pulsar el boton pa volver
            val datos = resultado.data
            // pongo q el valor puede ser 0 por si vas a jugar y luego le das directamente a inicio que no explote (me explotó)
            aciertos = datos?.getIntExtra("aciertos", 0) ?: 0
            fallos = datos?.getIntExtra("fallos", 0) ?: 0
            clicks = datos?.getIntExtra("clicks", 0) ?: 0
        }
    }
    var aciertos = 0
    var fallos = 0
    var clicks = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu_principal)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    var modoDificil: Boolean = false

        botonJugar = findViewById<Button>(R.id.jugar)
        botonEstadisticas = findViewById<Button>(R.id.estadisticas)
        botonModoDificil = findViewById<Button>(R.id.jugardificil)
        botonMas = findViewById<Button>(R.id.mas)

        botonJugar.setOnClickListener {
            modoDificil = false
            val irajugar = Intent(this, Buzz::class.java)
            irajugar.putExtra("aciertos", aciertos)
            irajugar.putExtra("fallos", fallos)
            irajugar.putExtra("clicks", clicks)
            irajugar.putExtra("modo", modoDificil)
            //aqui uso el launcher q creé arriba para ir a la activity Buzz.kt
            launcherJuego.launch(irajugar)
        }

        botonModoDificil.setOnClickListener {
            modoDificil = true
            val irajugar = Intent(this, Buzz::class.java)
            irajugar.putExtra("aciertos", aciertos)
            irajugar.putExtra("fallos", fallos)
            irajugar.putExtra("clicks", clicks)
            irajugar.putExtra("modo", modoDificil)
            //aqui uso el launcher q creé arriba para ir a la activity Buzz.kt
            launcherJuego.launch(irajugar)
        }

        botonEstadisticas.setOnClickListener {
            val iraestadisticas = Intent(this, Stats::class.java)
            iraestadisticas.putExtra("aciertos", aciertos)
            iraestadisticas.putExtra("fallos", fallos)
            iraestadisticas.putExtra("clicks", clicks)
            startActivity(iraestadisticas)
        }
        botonMas.setOnClickListener {
            val iramas = Intent(this, SumarPreguntas::class.java)
            startActivity(iramas)
        }
    }
}