package com.example.apptruefalse

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File
import java.io.FileOutputStream

class SumarPreguntas : AppCompatActivity() {

    lateinit var botonMas: Button
    lateinit var nuevaPregunta: EditText
    lateinit var botonInicio: Button
    lateinit var checkTrue: RadioButton
    lateinit var checkFalse: RadioButton
    lateinit var textoError: TextView
    var preguntaSumada: String = ""
    var respuestaSumada: Boolean? = null
    lateinit var archivoEscribir: FileOutputStream

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sumar_preguntas)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        botonMas = findViewById<Button>(R.id.botonmas)
        nuevaPregunta = findViewById<EditText>(R.id.nuevapregunta)
        checkTrue = findViewById<RadioButton>(R.id.checkTrue)
        checkFalse = findViewById<RadioButton>(R.id.checkFalse)
        textoError = findViewById<TextView>(R.id.resultadoNuevaPregunta)
        botonInicio = findViewById<Button>(R.id.volverinicio)






        botonMas.setOnClickListener(clickBotonMas)

        botonInicio.setOnClickListener {
            finish()
        }
    }

    val clickBotonMas = object : View.OnClickListener {
        override fun onClick(view: View) {
            if(view.id == R.id.botonmas)
            {
                preguntaSumada = nuevaPregunta.text.toString()
                if(checkTrue.isChecked) respuestaSumada = true
                if(checkFalse.isChecked) respuestaSumada = false
                if(nuevaPregunta.text.isBlank())
                {
                    textoError.text = "Debes escribir la pregunta."
                }
                else if (!checkFalse.isChecked && !checkTrue.isChecked)
                {
                    textoError.text = "Debes marcar la respuesta."
                }
                else
                {
                    archivoEscribir = FileOutputStream(File(filesDir, "preguntas.txt"), true)
                    var linea = preguntaSumada + ";" + respuestaSumada + "\n"
                    archivoEscribir.write(linea.toByteArray())
                    archivoEscribir.close()
                    Toast.makeText(this@SumarPreguntas, "Tu pregunta se ha guardado con éxito :)", Toast.LENGTH_SHORT).show()
                    nuevaPregunta.setText("")
                    checkTrue.isChecked = false
                    checkFalse.isChecked = false
                }
            }
        }
    }
}