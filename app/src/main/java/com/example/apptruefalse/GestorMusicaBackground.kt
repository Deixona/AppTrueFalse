package com.example.apptruefalse

import android.content.Context
import android.media.MediaPlayer

object GestorMusicaBackground {
    var reproductor : MediaPlayer?=null

    fun iniciar(context: Context, sonido: Int)
    {
        if(reproductor == null)
        {
            reproductor = MediaPlayer.create(context, sonido)
            reproductor?.isLooping = true
            reproductor?.start()
            reproductor?.setOnCompletionListener {
                reproductor?.release()
                reproductor = null
            }
        }
    }

    fun pausar()
    {
        reproductor?.pause()
    }

    fun reanudar()
    {
        reproductor?.start()
    }

    fun estaSonando(): Boolean
    {
        return reproductor?.isPlaying == true
    }

    fun bajarVolumen()
    {
        reproductor?.setVolume(0.1f, 0.1f)
    }

    fun subirVolumen()
    {
        reproductor?.setVolume(1f,1f)
    }
}