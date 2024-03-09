package com.androidrider.mediaplayer.Activity


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.androidrider.mediaplayer.R
import com.androidrider.mediaplayer.Utils.FontUtils.setAppFont
import com.androidrider.mediaplayer.databinding.ActivityAboutBinding
import com.google.android.material.appbar.MaterialToolbar

class AboutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setAppFont(this)
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentTheme[MainActivity.themeIndex])
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbarAboutActivity)
        setSupportActionBar(toolbar)
        // Enable the Up button (back arrow)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        binding.aboutText.text = aboutText()

    }

    private fun aboutText():String{
        return "Developed by : Md Arfe Alam" +
                "\n\n you want to provide feedback, I will love to hear that. "

    }


    // Handle the click on the back arrow
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


}