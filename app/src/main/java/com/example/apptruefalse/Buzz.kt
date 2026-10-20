package com.example.apptruefalse

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color.GREEN
import android.graphics.Color.RED
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File
import java.io.FileInputStream

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
    // aquí no puedo usar lateinit porque por algún motivo si pongo ColorStateList sin que sea nullable abajo no se asigna bien pero lateinit no permite nullables, así que directamente
    // lo inicializo null y abajo después de iniciar los botones le asigno el color.
    // todo esto es para que cuando le de a back/random/next el color vuelva a ser el del resto de botones por qué este comentario es verde??
    var colorOriginal: ColorStateList? = null
    var countDownTimer: CountDownTimer? = null

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

        preguntaTexto = findViewById<TextView>(R.id.pregunta)
        botonTrue = findViewById<Button>(R.id.botontrue)
        botonFalse = findViewById<Button>(R.id.botonfalse)
        botonNext = findViewById<Button>(R.id.botonnext)
        botonBack = findViewById<Button>(R.id.botonback)
        botonRandom = findViewById<Button>(R.id.botonrandom)
        respuesta = findViewById<TextView>(R.id.respuesta)
        botonInicio = findViewById<Button>(R.id.inicio)
        timer = findViewById<TextView>(R.id.timer)
        colorOriginal = botonTrue.backgroundTintList

        if(File(filesDir, "preguntas.txt").exists()) {
            leerArchivo()
            preguntaTexto.text = listaPreguntas[contador].texto
        }
        else{
            botonFalse.isEnabled = false
            botonTrue.isEnabled = false
            botonBack.isEnabled = false
            botonNext.isEnabled = false
            botonRandom.isEnabled = false
            preguntaTexto.text = "Todavía no hay preguntas guardadas. Guarda primero una pregunta."
        }
        //Aquí hago que recoja el valor que pasa el launcher del menú principal pa que siga contando si salgo y entro más de una vez
        aciertos = intent.getIntExtra("aciertos", aciertos)
        fallos = intent.getIntExtra("fallos", fallos)
        clicks = intent.getIntExtra("clicks", clicks)
        modoDificil = intent.getBooleanExtra("modo", false)

        if(modoDificil)
        {
            timerModoDificil()
        }

        botonFalse.setOnClickListener(trueOrFalse)
        botonTrue.setOnClickListener(trueOrFalse)
        botonBack.setOnClickListener(clickBotonBack)
        botonNext.setOnClickListener(clickBotonNext)
        botonRandom.setOnClickListener(clickBotonRandom)


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


// FUNCIONES PARA LOS BOTONCITOS

    val clickBotonNext = object : View.OnClickListener {
        override fun onClick(view: View) {
            if(view.id == R.id.botonnext)
            {
                contador++
                clicks ++
                if (contador > listaPreguntas.size - 1)
                {
                    contador = 0
                }
                preguntaTexto.text = listaPreguntas[contador].texto
                respuesta.text = ""
                botonTrue.backgroundTintList = colorOriginal
                botonFalse.backgroundTintList = colorOriginal
                if (modoDificil) timerModoDificil()
            }
        }
    }

    val clickBotonBack = object : View.OnClickListener {
        override fun onClick(view: View) {
            if(view.id == R.id.botonback)
            {
                contador --
                clicks ++
                if (contador < 0)
                {
                    contador = listaPreguntas.size - 1
                }
                preguntaTexto.text = listaPreguntas[contador].texto
                respuesta.text = ""
                botonTrue.backgroundTintList = colorOriginal
                botonFalse.backgroundTintList = colorOriginal
                if (modoDificil) timerModoDificil()
            }
        }
    }

    val clickBotonRandom = object : View.OnClickListener {
        override fun onClick(view: View) {
            if(view.id == R.id.botonrandom)
            {
                contador = (0..listaPreguntas.size -1).random()
                clicks ++
                preguntaTexto.text = listaPreguntas[contador].texto
                respuesta.text = ""
                botonTrue.backgroundTintList = colorOriginal
                botonFalse.backgroundTintList = colorOriginal
                if (modoDificil) timerModoDificil()
            }
        }
    }
    val trueOrFalse = object : View.OnClickListener {
        override fun onClick(view: View){
            if(view.id == R.id.botontrue && listaPreguntas[contador].resultado == true)
            {
                respuestaCorrecta()
                colorBotonesTrue()
                clicks++
            }
            else if(view.id == R.id.botonfalse && listaPreguntas[contador].resultado == false)
            {
                respuestaCorrecta()
                colorBotonesFalse()
                clicks++
            }
            else if(view.id == R.id.botonfalse && listaPreguntas[contador].resultado == true)
            {
                respuestaIncorrecta()
                colorBotonesTrue()
                clicks++
            }
            else
            {
                respuestaIncorrecta()
                colorBotonesFalse()
                clicks++
            }
            countDownTimer?.cancel()
        }
    }

    fun respuestaIncorrecta(){
        respuesta.text = "Incorrecto. Vuelve a intentarlo."
        fallos ++
        respuesta.setTextColor(RED)
    }
    fun respuestaCorrecta(){
        respuesta.text = "¡Correcto!"
        aciertos ++
        respuesta.setTextColor(GREEN)
    }
    fun colorBotonesTrue(){
        botonTrue.backgroundTintList = ColorStateList.valueOf(GREEN)
        botonFalse.backgroundTintList = ColorStateList.valueOf(RED)
    }
    fun colorBotonesFalse(){
        botonTrue.backgroundTintList = ColorStateList.valueOf(RED)
        botonFalse.backgroundTintList = ColorStateList.valueOf(GREEN)
    }
    fun timerModoDificil(){
        //primero cancelo el que ya existe por si le doy a next/back/random y luego creo el otro
            countDownTimer?.cancel()
            countDownTimer = object : CountDownTimer(15000, 1000) {

                override fun onTick(millisUntilFinished: Long) {
                    timer.setText("Te quedan " + millisUntilFinished / 1000  + " segundos para responder.")
                }

                override fun onFinish() {
                    if(listaPreguntas[contador].resultado)
                    {
                        respuestaIncorrecta()
                        colorBotonesTrue()
                    }
                    else
                    {
                        respuestaIncorrecta()
                        colorBotonesFalse()
                    }
                }
            }.start()
        }
    fun leerArchivo(){
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
                    listaPreguntas.add(Pregunta(partes[0], partes[1].toBoolean()))
                }
            }
        }
}
