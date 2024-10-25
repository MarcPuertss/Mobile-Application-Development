package com.marcpuertas.colorsapp

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.switchmaterial.SwitchMaterial

class MainActivity : AppCompatActivity() {

    // Declaració dels botons i textos (lateinit vol dir que es carregaran més tard)
    private lateinit var button_red: Button
    private lateinit var button_green: Button
    private lateinit var button_blue: Button
    private lateinit var buton_reset: Button
    private lateinit var WinningMessage: TextView
    private lateinit var GameCounter: TextView
    private lateinit var LossCounter: TextView

    // Array que conté els colors que utilitzarem en els botons
    private val colors = arrayOf(Color.RED, Color.GREEN, Color.BLUE)

    // Variables per a controlar l'estat del joc: si ha acabat i el nombre de jocs
    private var gameFinished = false
    private var gameCount = 0
    private var lossCount = 0

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)  // Assigna el layout a l'activitat

        // Inicialitza els botons i elements de la interfície associant-los als ID del layout XML
        button_red = findViewById(R.id.button_red)
        button_green = findViewById(R.id.button_green)
        button_blue = findViewById(R.id.button_blue)
        buton_reset = findViewById(R.id.buton_reset)
        WinningMessage = findViewById(R.id.WinningMessage)
        GameCounter = findViewById(R.id.GameCounter)
        val switchOscure = findViewById<SwitchMaterial>(R.id.switchOscure)  // Switch per canviar el tema
        LossCounter = findViewById(R.id.LossCounter)


        if (savedInstanceState != null) {
            // Si hi ha estat guardat, recupera els colors dels botons
            val colorButton1 = savedInstanceState.getInt("color_button1", colors[0])
            val colorButton2 = savedInstanceState.getInt("color_button2", colors[1])
            val colorButton3 = savedInstanceState.getInt("color_button3", colors[2])

            // Aplica els colors guardats als botons i configura els seus tags
            button_red.setBackgroundColor(colorButton1)
            button_red.setTag(colorButton1)

            button_green.setBackgroundColor(colorButton2)
            button_green.setTag(colorButton2)

            button_blue.setBackgroundColor(colorButton3)
            button_blue.setTag(colorButton3)

            // Recupera i mostra el comptador de jocs
            gameCount = savedInstanceState.getInt("game_count", 1)
            GameCounter.text = "Partides guanyades: $gameCount"

            lossCount = savedInstanceState.getInt("loss_count", 0)
            LossCounter.text = "Partides pèrdues: $lossCount"

            // Recupera l'estat del joc (si ja havia acabat) i mostra el missatge de victòria
            gameFinished = savedInstanceState.getBoolean("game_finished", false)
            if (gameFinished) {
                WinningMessage.text = "                  \uD83C\uDFC6Enhorabona!\uD83C\uDFC6 \n Tots els botons tenen el mateix color."
            }
        } else {
            // Si no hi ha estat guardat, inicialitza els colors i assigna un color inicial a cada botó
            button_red.setTag(colors[0])
            button_red.setBackgroundColor(colors[0])

            button_green.setTag(colors[1])
            button_green.setBackgroundColor(colors[1])

            button_blue.setTag(colors[2])
            button_blue.setBackgroundColor(colors[2])
        }

        // Configura el comportament del Switch per canviar entre mode clar i fosc
        switchOscure.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                enableDarkMode()  // Activa el mode fosc
            } else {
                disableDarkMode()  // Desactiva el mode fosc
            }
        }

        // Afegeix un "listener" a cada botó per canviar el color quan es faci clic
        button_red.setOnClickListener { changeColor(it as Button) }
        button_green.setOnClickListener { changeColor(it as Button) }
        button_blue.setOnClickListener { changeColor(it as Button) }
        buton_reset.setOnClickListener { resetGame() }
    }

    // Funció que activa el mode fosc
    private fun enableDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }

    // Funció que desactiva el mode fosc
    private fun disableDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    // Funció per canviar el color d'un botó específic quan es fa clic
    private fun changeColor(button: Button) {
        if (!gameFinished) {
            // Obté el color actual del botó a partir del seu "tag" i obté el següent color
            val currentColor = button.getTag() as Int
            val nextColor = getNextColor(currentColor)
            button.setBackgroundColor(nextColor)
            button.setTag(nextColor)

            // Comprova si tots els botons tenen ara el mateix color per acabar el joc
            GameFinished()
        }
    }

    // Funció per obtenir el següent color dins l'array de colors
    private fun getNextColor(currentColor: Int): Int {
        val currentIndex = colors.indexOf(currentColor)
        val nextIndex = (currentIndex + 1) % 3
        return colors[nextIndex]
    }

    // Funció per comprovar si tots els botons tenen el mateix color i, si és així, acaba el joc
    private fun GameFinished() {
        // Obté el color dels tres botons utilitzant els seus "tags"
        val color1 = button_red.getTag() as Int
        val color2 = button_green.getTag() as Int
        val color3 = button_blue.getTag() as Int

        // Comprova si tots tres botons tenen el mateix color
        if (color1 == color2 && color2 == color3) {
            gameFinished = true
            WinningMessage.text = "                  \uD83C\uDFC6Enhorabona!\uD83C\uDFC6 \n Tots els botons tenen el mateix color."
        }
    }

    // Funció per reiniciar el joc quan es fa clic al botó de reset
    private fun resetGame() {
        if (gameFinished) {
            // Si el joc ha acabat (és a dir, el jugador ha guanyat), incrementa el contador de jocs
            gameCount++
            GameCounter.text = "Partides guanyades: $gameCount"
        } else {
            // Si no ha guanyat, incrementa el contador de pèrdues
            lossCount++
            LossCounter.text = "Partides pèrdues: $lossCount"

        }

        // Reinicia els botons als seus colors inicials
        button_red.setBackgroundColor(colors[0])
        button_red.setTag(colors[0])

        button_green.setBackgroundColor(colors[1])
        button_green.setTag(colors[1])

        button_blue.setBackgroundColor(colors[2])
        button_blue.setTag(colors[2])

        // Esborra el missatge de victòria i restableix l'estat del joc a no finalitzat
        WinningMessage.text = ""
        gameFinished = false
    }



    // Funció per guardar l'estat actual del joc (colors, comptador, etc.) quan l'activitat es reinicia
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Desa els colors dels botons en el "Bundle"
        outState.putInt("color_button1", button_red.getTag() as Int)
        outState.putInt("color_button2", button_green.getTag() as Int)
        outState.putInt("color_button3", button_blue.getTag() as Int)
        // Desa el comptador de jocs i si el joc ha acabat o no
        outState.putInt("game_count", gameCount)
        outState.putInt("loss_count", lossCount)
        outState.putBoolean("game_finished", gameFinished)
    }
}
