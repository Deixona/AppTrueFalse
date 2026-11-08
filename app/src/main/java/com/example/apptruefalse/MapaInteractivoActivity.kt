package com.example.apptruefalse

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import java.io.File
import java.io.FileOutputStream

class MapaInteractivoActivity : AppCompatActivity() {

    lateinit var nombreMarcador : EditText
    lateinit var descripcionMarcador : EditText
    lateinit var mapa : MapView
    lateinit var botonAñadir : Button
    lateinit var eliminarSeleccion : Button

    lateinit var centroMapa : GeoPoint
    lateinit var marcadorElegido : Marker

    var marcadorPendiente : Boolean = false
    var listaMarcadoresPersonalizados = ArrayList<MarcadoresPersonalizados>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE))
        Configuration.getInstance().userAgentValue = "AppTrueFalse-Deixona-SchoolPractice/1.0"


        setContentView(R.layout.activity_mapa_interactivo)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        nombreMarcador = findViewById<EditText>(R.id.nombremarcador)
        descripcionMarcador = findViewById<EditText>(R.id.descripcionmarcador)
        mapa = findViewById<MapView>(R.id.mapa)
        botonAñadir = findViewById<Button>(R.id.botonañadir)
        eliminarSeleccion = findViewById<Button>(R.id.eliminarseleccion)

        val tileSource = XYTileSource("tiles", 1, 19, 256, ".png", arrayOf("https://tile.openstreetmap.org/"))
        mapa.setTileSource(tileSource)
        //aquí se usa GeoPoint que es el equivalente a LatLng en omsdroid (openstreetmaps)
        centroMapa = GeoPoint(28.487435, -16.318297)
        mapa.controller.setCenter(centroMapa)
        mapa.controller.setZoom(15.0)
        mapa.setMultiTouchControls(true)

        botonAñadir.setOnClickListener(clickBoton)
        eliminarSeleccion.setOnClickListener(clickBoton)

        marcadorElegido = Marker(mapa)

        var receptor = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                if(p != null)
                {
                    mapa.overlays.remove(marcadorElegido)
                    marcadorElegido.position = p
                    mapa.overlays.add(marcadorElegido)
                    mapa.invalidate()
                    marcadorPendiente = true
                    return true
                }
                else {
                    return(false)
                }
            }

            override fun longPressHelper(p: GeoPoint?): Boolean {
                return false
            }
        }

        mapa.overlays.add(0, MapEventsOverlay(receptor))

    }

    val clickBoton = object : View.OnClickListener {
        override fun onClick(view: View) {
            if (view.id == R.id.botonañadir) {
                if(nombreMarcador.text.isBlank() || !marcadorPendiente)
                {
                    nombreMarcador.error = "¡Debes rellenar todos los campos!"
                    descripcionMarcador.error = "¡Debes rellenar todos los campos!"
                }
                else{
                    escribirArchivo(nombreMarcador.text.toString(), descripcionMarcador.text.toString(), marcadorElegido.position)
                    añadirMarcador(nombreMarcador.text.toString(), descripcionMarcador.text.toString(), marcadorElegido.position)
                }

            }

            if(view.id == R.id.eliminarseleccion)
            {
                mapa.overlays.remove(marcadorElegido)
                mapa.invalidate()
                marcadorPendiente = false
            }

        }
    }

    fun añadirMarcador(title: String, snippet: String, position: GeoPoint)
    {
        val marcadorAñadido = Marker(mapa)
        marcadorAñadido.title = nombreMarcador.text.toString()
        marcadorAñadido.snippet = descripcionMarcador.text.toString()
        marcadorAñadido.position = marcadorElegido.position
        mapa.overlays.add(marcadorAñadido)
        mapa.overlays.remove(marcadorElegido)
        mapa.invalidate()
        nombreMarcador.text.clear()
        descripcionMarcador.text.clear()
        marcadorPendiente = false
    }

    fun escribirArchivo(title: String, snippet: String, position: GeoPoint)
    {
        listaMarcadoresPersonalizados.add(MarcadoresPersonalizados(position.latitude, position.longitude, title, snippet, null))
        val archivoMarcadores = FileOutputStream(File(filesDir, "marcadores.txt"), true)
        archivoMarcadores.write(("${position.latitude};${position.longitude};$title;$snippet\n").toByteArray())
        archivoMarcadores.close()
    }
}