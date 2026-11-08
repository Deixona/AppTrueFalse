package com.example.apptruefalse

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class WelcomeActivity : AppCompatActivity() {

    lateinit var botonJuego: Button
    lateinit var botonMapas: Button
    lateinit var textoBienvenida: TextView
    lateinit var botonMusica: ImageButton
    lateinit var botonMunicipio : Button
    lateinit var botonMapaInteractivo : Button

    var reproductorMusica: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_welcome)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val usuario = intent.getStringExtra("usuario")

        botonJuego = findViewById<Button>(R.id.botonjuego)
        botonMapas = findViewById<Button>(R.id.botonmapas)
        botonMapaInteractivo = findViewById<Button>(R.id.botonmapainteractivo)
        botonMunicipio = findViewById<Button>(R.id.botonmunicipio)
        textoBienvenida = findViewById<TextView>(R.id.bienvenida)
        botonMusica = findViewById<ImageButton>(R.id.musica)

        botonJuego.setOnClickListener(clickBoton)
        botonMapas.setOnClickListener(clickBoton)
        botonMunicipio.setOnClickListener(clickBoton)
        botonMusica.setOnClickListener(clickBoton)
        botonMapaInteractivo.setOnClickListener(clickBoton)

        textoBienvenida.text = "¡Bienvenid@, " + usuario + "!"
        var puntoActual = intent.getIntExtra("musica", 0)
        reproductorMusicaFondo(R.raw.bandasonora)
        reproductorMusica?.seekTo(puntoActual)
        reproductorMusica?.start()


    }

    val clickBoton = object : View.OnClickListener {
        override fun onClick(view: View) {
            if (view.id == R.id.botonjuego) {
                val intentJuego = Intent(this@WelcomeActivity, menuPrincipal::class.java)
                liberarReproductor()
                startActivity(intentJuego)
            }

            if (view.id == R.id.musica)
            {
                reproductorMusica?.let { reproductor ->
                    if(reproductor.isPlaying)
                    {
                        reproductor.pause()
                        botonMusica.setImageResource(R.drawable.altavozmuteado)
                    }
                    else
                    {
                        reproductor.start()
                        botonMusica.setImageResource(R.drawable.altavozsonando)
                    }
                }
            }

            if (view.id == R.id.botonmunicipio) {
                val intentMapas = Intent(this@WelcomeActivity, GaleriaImagen::class.java)
                intentMapas.putExtra("musica", reproductorMusica?.currentPosition)
                liberarReproductor()
                startActivity(intentMapas)
            }

            if (view.id == R.id.botonmapas) {
                val intentMapas = Intent(this@WelcomeActivity, MapaActivity::class.java)
                liberarReproductor()
                startActivity(intentMapas)
            }

            if (view.id == R.id.botonmapainteractivo) {
                val intentMapasInterac = Intent(this@WelcomeActivity, MapaInteractivoActivity::class.java)
                liberarReproductor()
                startActivity(intentMapasInterac)
            }
        }
    }

    fun reproductorMusicaFondo(sonido: Int) {
        reproductorMusica?.release()
        reproductorMusica = null
        reproductorMusica = MediaPlayer.create(this, sonido)
        reproductorMusica?.setOnCompletionListener {
            liberarReproductor()
        }
        reproductorMusica?.start()
        reproductorMusica?.isLooping = true
    }
    fun liberarReproductor() {
        reproductorMusica?.release()
        reproductorMusica = null
    }

}

