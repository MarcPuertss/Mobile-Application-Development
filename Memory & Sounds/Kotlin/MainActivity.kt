package com.marcpuertas.memorygame

import android.annotation.SuppressLint
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var gridLayout: GridLayout
    private lateinit var resetButton: Button
    private lateinit var victoryMessage: TextView
    private lateinit var background_theme: MediaPlayer

    private lateinit var soundPool: SoundPool
    private var flipSound = 0
    private var victorySound = 0
    private var failSound = 0
    private var finalVictorySound = 0

    private val totalPairs = 18
    private lateinit var hiddenImages: MutableList<Int>
    private var firstClicked: ImageView? = null
    private var secondClicked: ImageView? = null
    private var firstCoverImage: ImageView? = null
    private var secondCoverImage: ImageView? = null
    private var handler = Handler()
    private var foundPairs = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        gridLayout = findViewById(R.id.gridLayout)
        resetButton = findViewById(R.id.resetButton)
        victoryMessage = findViewById(R.id.victoryMessage)

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()

        soundPool = SoundPool.Builder().setMaxStreams(3).setAudioAttributes(audioAttributes).build()

        flipSound = soundPool.load(this, R.raw.flip_card, 1)
        victorySound = soundPool.load(this, R.raw.victory_sound, 1)
        failSound = soundPool.load(this, R.raw.fail_sound, 1)
        finalVictorySound = soundPool.load(this, R.raw.victoryfinal, 1)

        background_theme = MediaPlayer.create(this, R.raw.background_theme)
        background_theme.isLooping = true
        background_theme.setVolume(0.4f, 0.4f)
        background_theme.start()

        resetButton.setOnClickListener {
            resetGame()
        }

        resetGame()
    }

    @SuppressLint("MissingInflatedId", "ResourceType")
    private fun resetGame() {
        foundPairs = 0
        victoryMessage.visibility = TextView.GONE

        if (!background_theme.isPlaying) {
            background_theme.start()
        }

        val images = (1..totalPairs).map { resources.getIdentifier("fruta$it", "drawable", packageName) }
        hiddenImages = (images + images).shuffled().toMutableList()

        gridLayout.removeAllViews()

        for (i in 0 until 36) {
            val itemLayout = layoutInflater.inflate(R.layout.card_item, gridLayout, false)
            val coverImage = itemLayout.findViewById<ImageView>(R.id.coverImage)
            val cardImage = itemLayout.findViewById<ImageView>(R.id.cardImage)
            cardImage.setImageResource(hiddenImages[i])
            cardImage.alpha = 0f
            coverImage.setImageResource(R.drawable.cover_card)

            coverImage.setOnClickListener { onCardClicked(coverImage, cardImage, i) }

            gridLayout.addView(itemLayout)
        }
    }

    private fun onCardClicked(coverImage: ImageView, cardImage: ImageView, index: Int) {
        if (firstClicked != null && secondClicked != null || cardImage.alpha == 1f) {
            return
        }

        coverImage.alpha = 0f
        cardImage.alpha = 1f

        if (firstClicked == null) {
            soundPool.play(flipSound, 1f, 1f, 0, 0, 1f)
            firstClicked = cardImage
            firstCoverImage = coverImage
        } else {
            secondClicked = cardImage
            secondCoverImage = coverImage
            checkForMatch()
        }
    }


    private fun checkForMatch() {
        if (firstClicked?.drawable?.constantState == secondClicked?.drawable?.constantState) {
            soundPool.play(victorySound, 1f, 1f, 0, 0, 1f)
            foundPairs++
            firstClicked = null
            secondClicked = null
            firstCoverImage = null
            secondCoverImage = null


            if (foundPairs == totalPairs) {
                background_theme.pause()
                soundPool.play(finalVictorySound, 1f, 1f, 0, 0, 1f)
                victoryMessage.visibility = TextView.VISIBLE
            }

        } else {
            soundPool.play(failSound, 1f, 1f, 0, 0, 1f)
            handler.postDelayed({
                firstClicked?.alpha = 0f
                secondClicked?.alpha = 0f
                firstCoverImage?.alpha = 1f
                secondCoverImage?.alpha = 1f
                firstClicked = null
                secondClicked = null
                firstCoverImage = null
                secondCoverImage = null
            }, 2000)
        }
    }
}
