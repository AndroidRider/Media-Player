package com.androidrider.mediaplayer.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.androidrider.mediaplayer.R
import com.androidrider.mediaplayer.databinding.ActivityAboutBinding
import com.androidrider.mediaplayer.databinding.ActivityMainBinding

class AboutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentTheme[MainActivity.themeIndex])
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "About"

        binding.aboutText.text = aboutText()


        binding.feedback.setOnClickListener {
            startActivity(Intent(this, FeedbackActivity::class.java))
        }
        binding.settings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun aboutText():String{
        return "Developed by : Md Arfe Alam" +
                "\n\n you want to provide feedback, I will love to hear that. "

    }
}