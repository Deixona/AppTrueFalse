package com.example.apptruefalse

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.Polyline
import java.io.File
import java.io.FileInputStream

class MapaActivity : AppCompatActivity() {

    lateinit var mapa : MapView
    lateinit var spinner: Spinner
    lateinit var dibujarRuta: Button
    lateinit var textoDistancias: TextView
    val puntosLinea = ArrayList<GeoPoint>()
    lateinit var centroMapa : GeoPoint
    lateinit var archivoLeer: FileInputStream

    val arrayMarcadores = ArrayList<MarcadoresPersonalizados>()
    var arrayLugares = ArrayList<ClaseLugares>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE))
        Configuration.getInstance().userAgentValue = "AppTrueFalse-Deixona-SchoolPractice/1.0"

        setContentView(R.layout.activity_mapa)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mapa = findViewById<MapView>(R.id.mapa)
        spinner = findViewById<Spinner>(R.id.spinner)
        dibujarRuta = findViewById<Button>(R.id.botonruta)
        textoDistancias = findViewById<TextView>(R.id.distancias)
        val linea = Polyline()
        var circulo = Polygon()

        val tileSource = XYTileSource("tiles", 1, 19, 256, ".png", arrayOf("https://tile.openstreetmap.org/"))
        mapa.setTileSource(tileSource)
        //aquí se usa GeoPoint que es el equivalente a LatLng en omsdroid (openstreetmaps)
        centroMapa = GeoPoint(28.487435, -16.318297)
        mapa.controller.setCenter(centroMapa)
        mapa.controller.setZoom(15.0)
        mapa.setMultiTouchControls(true)

        arrayLugares.add(ClaseLugares("Seleccione un lugar", 28.487435, -16.318297))

        val icono = ContextCompat.getDrawable(this, R.drawable.ic_marcador)

        arrayMarcadores.add(MarcadoresPersonalizados(28.487487, -16.313437, "Plaza del Adelantado", "Una plaza preciosa", icono))
        arrayMarcadores.add(MarcadoresPersonalizados(28.475713,-16.308955, "Museo de la Ciencia y el Cosmos", "Un museo guapísimo", icono))
        arrayMarcadores.add(MarcadoresPersonalizados(28.49069, -16.3203687, "Torre de la Concepción", "Una torre de 28 metros", icono))
        arrayMarcadores.add(MarcadoresPersonalizados(28.485917, -16.3133698, "Tumba de Amaro Pargo", "La tumba de un pirata", icono))

        if (File(filesDir, "marcadores.txt").exists()) leerArchivo()

        for(x in arrayMarcadores)
        {
            crearMarcador(x.latitud, x.longitud, x.nombre, x.descripcion, x.icono)
            mapa.invalidate()
        }

        val adaptador = ArrayAdapter(this, android.R.layout.simple_spinner_item, arrayLugares.map{it.nombreLugar})
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adaptador

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                    if(spinner.selectedItem == arrayLugares[position].nombreLugar && position > 0)
                    {
                        var lugarEncontrado = GeoPoint(arrayLugares[position].latitud, arrayLugares[position].longitud)
                        mapa.controller.animateTo(lugarEncontrado, 19.0, 1000)
                    }
                else
                    {
                        mapa.controller.animateTo(centroMapa, 15.0, 1000)
                    }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        linea.setPoints(puntosLinea)
        circulo.setPoints(Polygon.pointsAsCircle(centroMapa, calcularDistanciaMaxima()))
        circulo.outlinePaint.setColor(Color.RED)
        circulo.outlinePaint.setAlpha(60)

        dibujarRuta.setOnClickListener{
            if(linea !in mapa.overlays) {
                mapa.overlays.add(linea)
                mapa.overlays.add(circulo)
                calcularDistancias()
            }
            else {
                mapa.overlays.remove(linea)
                mapa.overlays.remove(circulo)
                textoDistancias.text = ""
            }
            mapa.invalidate()
        }
}

    fun crearMarcador(latitud : Double, longitud : Double, titulo : String, snippet : String, icono : Drawable?)
    {
        val marcador = Marker(mapa)
        marcador.position = GeoPoint(latitud, longitud)
        marcador.title = titulo
        marcador.snippet = snippet
        if(icono != null) marcador.icon = icono
        marcador.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        mapa.overlays.add(marcador)
        arrayLugares.add(ClaseLugares(titulo, latitud, longitud))
        puntosLinea.add(GeoPoint(latitud, longitud))
    }

    fun calcularDistancias()
    {
        var distanciaTotal = 0.00
        val distanciaMaxima = calcularDistanciaMaxima()
        for(i in 0..puntosLinea.size - 2)
        {
            //en vez de usar Location.distanceBetween usé el distanceToAsDouble que pertenece a omsdroid
            val distancia = puntosLinea[i].distanceToAsDouble(puntosLinea[i + 1])
            distanciaTotal += distancia
            if(distancia > 1000)
            {
                textoDistancias.append("La distancia entre el punto " + (i+1) + " y el punto " + (i+2) + " es " + "%.2f".format(distancia/1000) + " kilómetros.\n")
            }
            else textoDistancias.append("La distancia entre el punto " + (i+1) + " y el punto " + (i+2) + " es " + distancia.toInt() + " metros.\n")
        }
        textoDistancias.append("La distancia total es " + "%.2f".format(distanciaTotal/1000) + " kilómetros.\n")
        textoDistancias.append("La distancia desde el centro del mapa al punto más lejano es " + "%.2f".format(distanciaMaxima/1000) + " kilómetros.")
    }

    fun calcularDistanciaMaxima() : Double
    {
        var distanciaMaxima = 0.00
        var distanciaCentro : Double
        for(j in 0..< puntosLinea.size)
        {
            distanciaCentro = centroMapa.distanceToAsDouble(puntosLinea[j])
            if(distanciaCentro > distanciaMaxima) distanciaMaxima = distanciaCentro
        }
        return distanciaMaxima
    }

    fun leerArchivo() {
        archivoLeer = FileInputStream(File(filesDir, "marcadores.txt"))
        //primero leo todo el archivo y lo meto en un string
        val contenido = String(archivoLeer.readBytes())
        archivoLeer.close()
        //luego separo cada linea y las meto en una list
        val lineas = contenido.split("\n")
        //luego separo cada linea en la pregunta y la respuesta y las meto en otra list que uso para ir construyendo el array
        for (x in lineas) {
            if (x.isNotBlank()) {
                val partes = x.split(";")
                arrayMarcadores.add(MarcadoresPersonalizados(partes[0].toDouble(), partes[1].toDouble(), partes[2], partes[3], null))
            }
        }
    }

}