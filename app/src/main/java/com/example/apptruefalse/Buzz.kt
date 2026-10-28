package com.example.apptruefalse

import android.animation.ObjectAnimator
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color.GREEN
import android.graphics.Color.RED
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File
import java.io.FileInputStream
import androidx.constraintlayout.widget.Group
import android.os.Handler

class Buzz : AppCompatActivity() {

    lateinit var archivoLeer: FileInputStream
    var contador: Int = 0
    var listaPreguntas = ArrayList<Pregunta>()


    // DECLARO AQUI LAS VARIABLES DE LOS BOTONES Y LOS TEXTVIEWS PARA PODER USARLOS EN LAS FUNCIONES. Como no quiero asignarles valor todavía uso lateinit
    lateinit var botonTrue: Button
    lateinit var botonFalse: Button
    lateinit var botonNext: Button
    lateinit var botonBack: Button
    lateinit var botonRandom: Button
    lateinit var preguntaTexto: TextView
    lateinit var respuesta: TextView
    lateinit var botonInicio: Button
    lateinit var timer: TextView
    lateinit var play: ImageButton
    lateinit var loop: ImageButton
    lateinit var grupoReproductor : Group
    lateinit var barraReproductor: SeekBar
    lateinit var tiempoTranscurrido: TextView
    lateinit var tiempoTotal: TextView
    lateinit var botonWeb: ImageButton

    var reproductorEfectos: MediaPlayer? = null
    var reproductorPreguntas: MediaPlayer? = null
    val handler = Handler(Looper.getMainLooper())
    var loopActivado = false

    // aquí no puedo usar lateinit porque por algún motivo si pongo ColorStateList sin que sea nullable abajo no se asigna bien pero lateinit no permite nullables, así que directamente
    // lo inicializo null y abajo después de iniciar los botones le asigno el color.
    // todo esto es para que cuando le de a back/random/next el color vuelva a ser el del resto de botones por qué este comentario es verde??
    var colorOriginal: ColorStateList? = null
    var countDownTimer: CountDownTimer? = null
    var tiempoRestante: Long = 0
    var respondida: Boolean = false

    //ESTAS VARIABLES SON PARA HACER EL RECUENTO PARA LAS STATS.
    //Óliver me estoy haciendo un lío así que voy a comentar esto como si fuera bobo porque me pierdo con todos los cambios que le he hecho a esto
    var aciertos: Int = 0
    var fallos: Int = 0
    var clicks: Int = 0
    var modoDificil: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        listaPreguntas.add(Pregunta("¿El tipo agua es débil al tipo fuego?", false, null))
        listaPreguntas.add(Pregunta("¿Wartortle evoluciona a Squirtle?", false, null))
        listaPreguntas.add(Pregunta("¿Actualmente hay más de 5 generaciones de Pokémon?",true,null))
        listaPreguntas.add(Pregunta("¿Existen 18 tipos de Pokémon?", true, null))
        listaPreguntas.add(Pregunta("¿Existen más de 5 generaciones de Pokémon?", true, null))
        listaPreguntas.add(Pregunta("¿Este sonido pertenece a Pikachu?", false, R.raw.pikachu))
        listaPreguntas.add(Pregunta("¿Es este el opening de la primera generación de Pokémon?",false,R.raw.giveuup))
        listaPreguntas.add(Pregunta("¿Este sonido pertenece a una Pokéball?", true, R.raw.pokebol))
        listaPreguntas.add(Pregunta("¿Dirías que este sonido se corresponde con algún Pokémon?",false,R.raw.cupcake))
        listaPreguntas.add(Pregunta("¿Pertenece este sonido al videojuego Pokémon Edición Negro 2?",false,R.raw.mamamia))

        preguntaTexto = findViewById<TextView>(R.id.pregunta)
        botonTrue = findViewById<Button>(R.id.botontrue)
        botonFalse = findViewById<Button>(R.id.botonfalse)
        botonNext = findViewById<Button>(R.id.botonnext)
        botonBack = findViewById<Button>(R.id.botonback)
        botonRandom = findViewById<Button>(R.id.botonrandom)
        respuesta = findViewById<TextView>(R.id.respuesta)
        botonInicio = findViewById<Button>(R.id.inicio)
        timer = findViewById<TextView>(R.id.timer)
        play = findViewById<ImageButton>(R.id.playopause)
        loop = findViewById<ImageButton>(R.id.loop)
        grupoReproductor = findViewById<Group>(R.id.gruporeproductor)
        barraReproductor = findViewById<SeekBar>(R.id.seekbar)
        tiempoTranscurrido = findViewById<TextView>(R.id.tiempotranscurrido)
        tiempoTotal = findViewById<TextView>(R.id.tiempototal)
        botonWeb = findViewById<ImageButton>(R.id.botonweb)

        colorOriginal = botonTrue.backgroundTintList

        if (File(filesDir, "preguntas.txt").exists()) {
            leerArchivo()
        } else {
            botonFalse.isEnabled = false
            botonTrue.isEnabled = false
            botonBack.isEnabled = false
            botonNext.isEnabled = false
            botonRandom.isEnabled = false
            preguntaTexto.text = getString(R.string.nopregunta)
        }
        //Aquí hago que recoja el valor que pasa el launcher del menú principal pa que siga contando si salgo y entro más de una vez
        aciertos = intent.getIntExtra("aciertos", aciertos)
        fallos = intent.getIntExtra("fallos", fallos)
        clicks = intent.getIntExtra("clicks", clicks)
        modoDificil = intent.getBooleanExtra("modo", false)

        if (savedInstanceState != null) {
            contador = savedInstanceState.getInt("contador")
            aciertos = savedInstanceState.getInt("aciertos")
            fallos = savedInstanceState.getInt("fallos")
            clicks = savedInstanceState.getInt("clicks")
            tiempoRestante = savedInstanceState.getLong("tiempoRestante")
            respondida = savedInstanceState.getBoolean("respondida")
            respuesta.text = savedInstanceState.getString("textoRespuesta")
            if (respondida == false) {
                botonTrue.backgroundTintList = colorOriginal
                botonFalse.backgroundTintList = colorOriginal
            } else {
                if (listaPreguntas[contador].resultado) {
                    colorBotonesTrue()
                } else {
                    colorBotonesFalse()
                }
                botonTrue.isEnabled = false
                botonFalse.isEnabled = false
            }
        }

        mostrarReproductor()

        if (modoDificil) {
            if (savedInstanceState == null) {
                timerModoDificil()
            } else if (!respondida) {
                timerModoDificil(tiempoRestante)
            }
        }
        if (listaPreguntas.isNotEmpty()) preguntaTexto.text = listaPreguntas[contador].texto

        botonFalse.setOnClickListener(trueOrFalse)
        botonTrue.setOnClickListener(trueOrFalse)
        botonBack.setOnClickListener(clickBotonBack)
        botonNext.setOnClickListener(clickBotonNext)
        botonRandom.setOnClickListener(clickBotonRandom)
        play.setOnClickListener(clickPlay)
        loop.setOnClickListener(clickLoop)
        //botonWeb.setOnClickListener(clickBotonWeb)


        barraReproductor.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser)
                {
                    reproductorPreguntas?.seekTo(progress)
                    tiempoTranscurrido.text = formatearTiempo(progress)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })


        //Aquí le paso los valores nuevos después de hacer el recuento, se pasan otra vez a menuPrincipal porque el launcher espera una respuesta?? eso entendí
        botonInicio.setOnClickListener {
            val irainicio = Intent(this, menuPrincipal::class.java)
            irainicio.putExtra("aciertos", aciertos)
            irainicio.putExtra("fallos", fallos)
            irainicio.putExtra("clicks", clicks)
            //esto no sé qué es pero estaba en los apuntes, creo q pasa un código ok al launcher para que pueda hacer las cosas del launcher,
            // hay un if que comprueba que esto se pasa pero no entiendo muy bien por qué quiero esto
            setResult(RESULT_OK, irainicio)
            finish()
        }


    }


// FUNCIONES QUE HACEN COSAS

    fun animarPregunta()
    {
        ObjectAnimator.ofFloat(preguntaTexto, "alpha", 0f, 1f).apply {
            duration = 400
            start()
        }
    }

    fun animarTextoError()
    {
        ObjectAnimator.ofFloat(respuesta, "translationX", 50f, -50f, 50f, -50f, 0f).apply {
            duration = 200
            start()
        }
    }

    fun animarTextoAcierto()
    {
        ObjectAnimator.ofFloat(respuesta, "translationY", 0f, -50f, 0f).apply {
            duration = 500
            start()
        }
    }

    val clickLoop = object : View.OnClickListener {
    override fun onClick(view: View) {
        if (view.id == R.id.loop)
        {
            if(loopActivado == false)
            {
                loop.setImageResource(R.drawable.loop2)
            }
            else
            {
                loop.setImageResource(R.drawable.loop)
            }
            loopActivado = !loopActivado
            reproductorPreguntas?.isLooping = loopActivado
        }
    }
    }

    override fun onDestroy() {
        super.onDestroy()
        liberarReproductores()
        countDownTimer?.cancel()
    }

    override fun onStop() {
        super.onStop()
        if(reproductorPreguntas?.isPlaying == true)reproductorPreguntas?.pause()
        handler.removeCallbacks(actualizarBarra)
        play.setImageResource(R.drawable.play)
    }

   // val clickBotonWeb = object : View.OnClickListener {
  //      override fun onClick (view:View) {
   //         if (view.id == R.id.botonweb)
   //         {
   //             listaPreguntas[contador].enlace?.let{ url ->
   //                 val intentWeb = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        //startActivity(intentWeb)
   ////             }
   //         }
   //     }
  //  }

    val clickBotonNext = object : View.OnClickListener {
        override fun onClick(view : View) {
            if (view.id == R.id.botonnext) {
                contador++
                clicks++
                if (contador > listaPreguntas.size - 1) {
                    contador = 0
                }
                preguntaTexto.text = listaPreguntas[contador].texto
                respuesta.text = ""
                botonTrue.backgroundTintList = colorOriginal
                botonFalse.backgroundTintList = colorOriginal
                if (modoDificil) timerModoDificil()
                reiniciarEstados()
                mostrarReproductor()
            }
        }
    }
    val clickBotonBack = object : View.OnClickListener {
        override fun onClick(view: View) {
            if (view.id == R.id.botonback) {
                contador--
                clicks++
                if (contador < 0) {
                    contador = listaPreguntas.size - 1
                }
                preguntaTexto.text = listaPreguntas[contador].texto
                respuesta.text = ""
                botonTrue.backgroundTintList = colorOriginal
                botonFalse.backgroundTintList = colorOriginal
                if (modoDificil) timerModoDificil()
                reiniciarEstados()
                mostrarReproductor()
            }
        }
    }
    val clickBotonRandom = object : View.OnClickListener {
        override fun onClick(view: View) {
            if (view.id == R.id.botonrandom) {
                contador = (0..listaPreguntas.size - 1).random()
                clicks++
                preguntaTexto.text = listaPreguntas[contador].texto
                respuesta.text = ""
                botonTrue.backgroundTintList = colorOriginal
                botonFalse.backgroundTintList = colorOriginal
                if (modoDificil) timerModoDificil()
                reiniciarEstados()
                mostrarReproductor()
            }
        }
    }
    val trueOrFalse = object : View.OnClickListener {
        override fun onClick(view: View) {
            if (view.id == R.id.botontrue && listaPreguntas[contador].resultado == true) {
                respuestaCorrecta()
                colorBotonesTrue()
                clicks++
            } else if (view.id == R.id.botonfalse && listaPreguntas[contador].resultado == false) {
                respuestaCorrecta()
                colorBotonesFalse()
                clicks++
            } else if (view.id == R.id.botonfalse && listaPreguntas[contador].resultado == true) {
                respuestaIncorrecta()
                colorBotonesTrue()
                clicks++
            } else {
                respuestaIncorrecta()
                colorBotonesFalse()
                clicks++
            }
            countDownTimer?.cancel()
            botonTrue.isEnabled = false
            botonFalse.isEnabled = false
            respondida = true
            if(reproductorPreguntas?.isPlaying == true )reproductorPreguntas?.pause()
            handler.removeCallbacks(actualizarBarra)
            play.setImageResource(R.drawable.play)
        }
    }
    fun timerModoDificil(duracion: Long = 15000) {
        //primero cancelo el que ya existe por si le doy a next/back/random y luego creo el otro
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(duracion, 1000) {

            override fun onTick(duracion: Long) {
                timer.setText(getString(R.string.tiemporestante1) + duracion / 1000 + getString(R.string.tiemporestante2))
                tiempoRestante = duracion
            }

            override fun onFinish() {
                if (listaPreguntas[contador].resultado) {
                    respuestaIncorrecta()
                    colorBotonesTrue()
                } else {
                    respuestaIncorrecta()
                    colorBotonesFalse()
                }
            }
        }.start()
    }
    fun leerArchivo() {
        archivoLeer = FileInputStream(File(filesDir, "preguntas.txt"))
        //primero leo todo el archivo y lo meto en un string
        val contenido = String(archivoLeer.readBytes())
        archivoLeer.close()
        //luego separo cada linea y las meto en una list
        val lineas = contenido.split("\n")
        //luego separo cada linea en la pregunta y la respuesta y las meto en otra list que uso para ir construyendo el array
        for (x in lineas) {
            if (x.isNotBlank()) {
                val partes = x.split(";")
                listaPreguntas.add(Pregunta(partes[0], partes[1].toBoolean(), null))
            }
        }
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("contador", contador)
        outState.putInt("aciertos", aciertos)
        outState.putInt("fallos", fallos)
        outState.putInt("clicks", clicks)
        outState.putLong("tiempoRestante", tiempoRestante)
        outState.putBoolean("respondida", respondida)
        outState.putString("textoRespuesta", respuesta.text.toString())
    }
    fun reproducirSonidoEfectos(sonido: Int) {
        reproductorEfectos?.release()
        reproductorEfectos = null
        reproductorEfectos = MediaPlayer.create(this, sonido)
        reproductorEfectos?.setOnCompletionListener {
            reproductorEfectos?.release()
            reproductorEfectos = null
        }
        reproductorEfectos?.start()
    }
    fun crearReproductorPreguntas() {
        reproductorPreguntas?.release()
        reproductorPreguntas = null
        listaPreguntas[contador].sonido?.let { sonido ->
            reproductorPreguntas = MediaPlayer.create(this, sonido)
            reproductorPreguntas?.setOnCompletionListener {
                play.setImageResource(R.drawable.play)
                handler.removeCallbacks(actualizarBarra)
                tiempoTranscurrido.text = "00:00"
                barraReproductor.progress = 0
            }
            barraReproductorFunc()
            reproductorPreguntas?.isLooping = loopActivado
        }
    }
    val clickPlay = object : View.OnClickListener {
        override fun onClick(view: View) {
            if (view.id == R.id.playopause) {
             if (reproductorPreguntas?.isPlaying == true) {
                    play.setImageResource(R.drawable.play)
                    reproductorPreguntas?.pause()
                 handler.removeCallbacks(actualizarBarra)
                } else {
                    play.setImageResource(R.drawable.pause)
                    reproductorPreguntas?.start()
                 handler.removeCallbacks(actualizarBarra)
                 handler.post(actualizarBarra)
            }

        }
    }
    }
    fun barraReproductorFunc()
    {
        reproductorPreguntas?.let{
            reproductor ->
            barraReproductor.max = reproductor.duration
        }
    }
    val actualizarBarra = object: Runnable
    {
        override fun run() {
            reproductorPreguntas?.let { reproductor ->
                barraReproductor.progress = reproductor.currentPosition
                tiempoTranscurrido.text = formatearTiempo(reproductor.currentPosition)
                handler.postDelayed(this, 1000)
            }
        }
    }

    fun formatearTiempo(tiempo: Int): String
    {
        val segundosTotales = tiempo / 1000
        val minutos = segundosTotales / 60
        val segundos = segundosTotales % 60
        return String.format("%02d:%02d", minutos, segundos)
    }

    // FUNCIONES QUE HACEN COSAS

    // FUNCIONES PA OPTIMIZAR

    fun reiniciarEstados() {
        botonTrue.isEnabled = true
        botonFalse.isEnabled = true
        respondida = false
        play.setImageResource(R.drawable.play)
        liberarReproductores()
        tiempoTranscurrido.text = "00:00"
        barraReproductor.progress = 0
        animarPregunta()
    }

    fun liberarReproductores()
    {
        reproductorPreguntas?.release()
        reproductorPreguntas = null
        reproductorEfectos?.release()
        reproductorEfectos = null
        handler.removeCallbacks(actualizarBarra)
    }

    fun mostrarReproductor() {
        if(listaPreguntas[contador].sonido != null) {
            grupoReproductor.visibility = View.VISIBLE
            crearReproductorPreguntas()
            play.setImageResource(R.drawable.play)
            reproductorPreguntas?.let {reproductor -> tiempoTotal.text = formatearTiempo(reproductor.duration)}
        }
        else{
            grupoReproductor.visibility = View.GONE
        }
    }
    fun respuestaIncorrecta() {
        respuesta.text = getString(R.string.respuestaincorrecta)
        fallos++
        respuesta.setTextColor(RED)
        reproducirSonidoEfectos(R.raw.incorrecto)
        animarTextoError()
    }
    fun respuestaCorrecta() {
        respuesta.text = getString(R.string.respuestacorrecta)
        aciertos++
        respuesta.setTextColor(GREEN)
        reproducirSonidoEfectos(R.raw.correcto)
        animarTextoAcierto()
    }
    fun colorBotonesTrue() {
        botonTrue.backgroundTintList = ColorStateList.valueOf(GREEN)
        botonFalse.backgroundTintList = ColorStateList.valueOf(RED)
    }
    fun colorBotonesFalse() {
        botonTrue.backgroundTintList = ColorStateList.valueOf(RED)
        botonFalse.backgroundTintList = ColorStateList.valueOf(GREEN)
    }

}