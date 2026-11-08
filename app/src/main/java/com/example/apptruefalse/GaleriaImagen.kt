package com.example.apptruefalse

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class GaleriaImagen : AppCompatActivity() {

    lateinit var imagenIzquierda : ImageView
    lateinit var imagenCentro: ImageView
    lateinit var imagenDerecha: ImageView
    lateinit var nextButton: Button
    lateinit var prevButton: Button
    lateinit var atrasBoton: Button
    lateinit var textoImagen: TextView
    lateinit var botonMusica: ImageButton

    var contadorIzquierda = 0
    var contadorCentro = 0
    var contadorDerecha = 0

    var reproductorMusica: MediaPlayer? = null
    var listaImagenes = ArrayList<ClaseGaleria>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_galeria_imagen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        imagenDerecha = findViewById<ImageButton>(R.id.imagenderecha)
        imagenIzquierda = findViewById<ImageButton>(R.id.imagenizquierda)
        imagenCentro = findViewById<ImageButton>(R.id.imagencentro)
        nextButton = findViewById<Button>(R.id.siguiente)
        prevButton = findViewById<Button>(R.id.anterior)
        textoImagen = findViewById<TextView>(R.id.textoimagen)
        atrasBoton = findViewById<Button>(R.id.inicio)
        botonMusica = findViewById<ImageButton>(R.id.musica)


        listaImagenes.add(ClaseGaleria(R.drawable.adelantado, R.string.descripcionadelantado))
        listaImagenes.add(ClaseGaleria(R.drawable.museocienciaycosmos, R.string.descripcionmuseo))
        listaImagenes.add(ClaseGaleria(R.drawable.torreconcepcion, R.string.descripcionconcepcion))
        listaImagenes.add(ClaseGaleria(R.drawable.amaropargo, R.string.descripcionamaro))

        contadorCentro = 0
        contadorDerecha = contadorCentro + 1
        contadorIzquierda = listaImagenes.size - 1

        imagenCentro.setImageResource(listaImagenes[0].imagen)
        textoImagen.setText(listaImagenes[0].descripcion)
        imagenDerecha.setImageResource(listaImagenes[1].imagen)
        imagenIzquierda.setImageResource(listaImagenes[listaImagenes.size -1].imagen)

        nextButton.setOnClickListener(clickBoton)
        prevButton.setOnClickListener(clickBoton)
        atrasBoton.setOnClickListener(clickBoton)
        botonMusica.setOnClickListener(clickBoton)


    }

    val clickBoton = object : View.OnClickListener {
        override fun onClick(view: View) {
            if(view.id == R.id.siguiente)
            {
                contadorDerecha++
                contadorIzquierda++
                contadorCentro++
                contadorCentro = checkControladores(contadorCentro)
                contadorDerecha = checkControladores(contadorDerecha)
                contadorIzquierda = checkControladores(contadorIzquierda)
                cambiarVista()
            }
            if(view.id == R.id.anterior)
            {
                contadorDerecha--
                contadorIzquierda--
                contadorCentro--
                contadorCentro = checkControladores(contadorCentro)
                contadorDerecha = checkControladores(contadorDerecha)
                contadorIzquierda = checkControladores(contadorIzquierda)
                cambiarVista()
            }

            if(view.id == R.id.inicio)
            {
                val irainicio = Intent(this@GaleriaImagen, WelcomeActivity::class.java)
                irainicio.putExtra("musica", reproductorMusica?.currentPosition)
                startActivity(irainicio)
            }

            if (view.id == R.id.musica)
            {
                if(GestorMusicaBackground.estaSonando())
                {
                    GestorMusicaBackground.pausar()
                    botonMusica.setImageResource(R.drawable.altavozmuteado)
                }
                else
                {
                    GestorMusicaBackground.reanudar()
                    botonMusica.setImageResource(R.drawable.altavozsonando)
                }
            }
        }

    }

    fun cambiarVista()
    {
        imagenCentro.setImageResource(listaImagenes[contadorCentro].imagen)
        textoImagen.setText(listaImagenes[contadorCentro].descripcion)
        imagenDerecha.setImageResource(listaImagenes[contadorDerecha].imagen)
        imagenIzquierda.setImageResource(listaImagenes[contadorIzquierda].imagen)
    }

    fun checkControladores(contador : Int) : Int
    {
        var contador = contador
        if(contador < 0) contador = listaImagenes.size - 1
        if(contador > listaImagenes.size - 1) contador = 0
        return(contador)
    }
}