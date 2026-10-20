package com.example.apptruefalse

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Stats : AppCompatActivity() {
    lateinit var recuentoAciertos: TextView
    lateinit var porcentajeAciertos: TextView
    lateinit var recuentoFallos: TextView
    lateinit var porcentajeFallos: TextView
    lateinit var recuentoClicks: TextView
    lateinit var botonInicio: Button
    var aciertos = 0
    var fallos = 0
    var clicks = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_stats)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        aciertos = intent.getIntExtra("aciertos", 0)
        fallos = intent.getIntExtra("fallos", 0)
        clicks = intent.getIntExtra("clicks", 0)

        recuentoAciertos = findViewById<TextView>(R.id.recuentoaciertos)
        porcentajeAciertos = findViewById<TextView>(R.id.porcentajeaciertos)
        recuentoFallos = findViewById<TextView>(R.id.recuentofallos)
        porcentajeFallos = findViewById<TextView>(R.id.porcentajefallos)
        recuentoClicks = findViewById<TextView>(R.id.recuentoclicks)
        botonInicio = findViewById<Button>(R.id.inicio)

        recuentoAciertos.text = aciertos.toString()
        recuentoFallos.text = fallos.toString()
        recuentoClicks.text = clicks.toString()

        val total = aciertos + fallos
        if (total == 0)
        {
            porcentajeAciertos.text = "0%"
            porcentajeFallos.text = "0%"
        }
        else
        {
            porcentajeAciertos.text = "${aciertos * 100 / total}%"
            porcentajeFallos.text = "${fallos * 100 / total}%"
        }

        botonInicio.setOnClickListener {
            finish()
        }
    }
}