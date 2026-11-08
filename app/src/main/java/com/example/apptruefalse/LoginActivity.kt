package com.example.apptruefalse

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class LoginActivity : AppCompatActivity() {

    lateinit var user : EditText
    lateinit var password : EditText
    lateinit var login : Button
    lateinit var textoError: TextView

    var listaUsuarios = hashMapOf<String, String>(
        "Jorge" to "pass1",
        "Ian" to "pass2",
        "Pablo" to "pass3"
    )



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        user = findViewById<EditText>(R.id.campouser)
        password = findViewById<EditText>(R.id.campopass)
        login = findViewById<Button>(R.id.botonlogin)
        textoError = findViewById<TextView>(R.id.textoerror)


        login.setOnClickListener(logearse)
    }

    val logearse = object: View.OnClickListener {
        override fun onClick(view: View) {
            if(view.id == R.id.botonlogin)
            {
                if(user.text.isBlank()) textoError.text = "Introduzca su nombre de usuario"
                else if(password.text.isBlank()) textoError.text = "Introduzca su contraseña"
                else{
                    if(password.text.toString() == listaUsuarios[user.text.toString()])
                    {
                        val irainicio = Intent(this@LoginActivity, WelcomeActivity::class.java)
                        irainicio.putExtra("usuario", user.text.toString())
                        startActivity(irainicio)
                        finish()
                    }
                    else
                    {
                        textoError.text = "Usuario o contraseña incorrecto."
                    }
                }
            }
        }
    }
}


