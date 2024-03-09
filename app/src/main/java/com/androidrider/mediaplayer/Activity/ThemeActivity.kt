package com.androidrider.mediaplayer.Activity

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.androidrider.mediaplayer.Model.exitApplication
import com.androidrider.mediaplayer.R
import com.androidrider.mediaplayer.Utils.FontUtils
import com.androidrider.mediaplayer.databinding.ActivityThemeBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ThemeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityThemeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        FontUtils.setAppFont(this)
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentTheme[MainActivity.themeIndex])
        binding = ActivityThemeBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        // FOR BACK ARROW
        val toolbar = findViewById<Toolbar>(R.id.toolbarThemeActivity)
        setSupportActionBar(toolbar)
        // Enable the Up button (back arrow)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // FOR THEME
        when (MainActivity.themeIndex) {
            0 -> binding.theme0.setBackgroundColor(Color.GRAY)
            1 -> binding.theme1.setBackgroundColor(Color.GRAY)
            2 -> binding.theme2.setBackgroundColor(Color.GRAY)
            3 -> binding.theme3.setBackgroundColor(Color.GRAY)
            4 -> binding.theme4.setBackgroundColor(Color.GRAY)
            5 -> binding.theme5.setBackgroundColor(Color.GRAY)
            6 -> binding.theme6.setBackgroundColor(Color.GRAY)
            7 -> binding.theme7.setBackgroundColor(Color.GRAY)
            8 -> binding.theme8.setBackgroundColor(Color.GRAY)
            9 -> binding.theme9.setBackgroundColor(Color.GRAY)
            10 -> binding.theme10.setBackgroundColor(Color.GRAY)
            11 -> binding.theme11.setBackgroundColor(Color.GRAY)
            12 -> binding.theme12.setBackgroundColor(Color.GRAY)
            13 -> binding.theme13.setBackgroundColor(Color.GRAY)
            14 -> binding.theme14.setBackgroundColor(Color.GRAY)
            15 -> binding.theme15.setBackgroundColor(Color.GRAY)
            16 -> binding.theme16.setBackgroundColor(Color.GRAY)
            17 -> binding.theme17.setBackgroundColor(Color.GRAY)
            18 -> binding.theme18.setBackgroundColor(Color.GRAY)
            19 -> binding.theme19.setBackgroundColor(Color.GRAY)
            20 -> binding.theme20.setBackgroundColor(Color.GRAY)
            21 -> binding.theme21.setBackgroundColor(Color.GRAY)
            22 -> binding.theme22.setBackgroundColor(Color.GRAY)
            23 -> binding.theme23.setBackgroundColor(Color.GRAY)
            24 -> binding.theme24.setBackgroundColor(Color.GRAY)
            25 -> binding.theme25.setBackgroundColor(Color.GRAY)
            26 -> binding.theme26.setBackgroundColor(Color.GRAY)
            27 -> binding.theme27.setBackgroundColor(Color.GRAY)
            28 -> binding.theme28.setBackgroundColor(Color.GRAY)
            29 -> binding.theme29.setBackgroundColor(Color.GRAY)
            30 -> binding.theme30.setBackgroundColor(Color.GRAY)
            31 -> binding.theme31.setBackgroundColor(Color.GRAY)
            32 -> binding.theme32.setBackgroundColor(Color.GRAY)
            33 -> binding.theme33.setBackgroundColor(Color.GRAY)
            34 -> binding.theme34.setBackgroundColor(Color.GRAY)
            35 -> binding.theme35.setBackgroundColor(Color.GRAY)

        }

        // FOR THEME
        binding.theme0.setOnClickListener { saveTheme(0) }
        binding.theme1.setOnClickListener { saveTheme(1) }
        binding.theme2.setOnClickListener { saveTheme(2) }
        binding.theme3.setOnClickListener { saveTheme(3) }
        binding.theme4.setOnClickListener { saveTheme(4) }
        binding.theme5.setOnClickListener { saveTheme(5) }
        binding.theme6.setOnClickListener { saveTheme(6) }
        binding.theme7.setOnClickListener { saveTheme(7) }
        binding.theme8.setOnClickListener { saveTheme(8) }
        binding.theme9.setOnClickListener { saveTheme(9) }
        binding.theme10.setOnClickListener { saveTheme(10) }
        binding.theme11.setOnClickListener { saveTheme(11) }
        binding.theme12.setOnClickListener { saveTheme(12) }
        binding.theme13.setOnClickListener { saveTheme(13) }
        binding.theme14.setOnClickListener { saveTheme(14) }
        binding.theme15.setOnClickListener { saveTheme(15) }
        binding.theme16.setOnClickListener { saveTheme(16) }
        binding.theme17.setOnClickListener { saveTheme(17) }
        binding.theme18.setOnClickListener { saveTheme(18) }
        binding.theme19.setOnClickListener { saveTheme(19) }
        binding.theme20.setOnClickListener { saveTheme(20) }
        binding.theme21.setOnClickListener { saveTheme(21) }
        binding.theme22.setOnClickListener { saveTheme(22) }
        binding.theme23.setOnClickListener { saveTheme(23) }
        binding.theme24.setOnClickListener { saveTheme(24) }
        binding.theme25.setOnClickListener { saveTheme(25) }
        binding.theme26.setOnClickListener { saveTheme(26) }
        binding.theme27.setOnClickListener { saveTheme(27) }
        binding.theme28.setOnClickListener { saveTheme(28) }
        binding.theme29.setOnClickListener { saveTheme(29) }
        binding.theme30.setOnClickListener { saveTheme(30) }
        binding.theme31.setOnClickListener { saveTheme(31) }
        binding.theme32.setOnClickListener { saveTheme(32) }
        binding.theme33.setOnClickListener { saveTheme(33) }
        binding.theme34.setOnClickListener { saveTheme(34) }
        binding.theme35.setOnClickListener { saveTheme(35) }

    }

    /* ========================= FOR THEME ========================= */
    private fun saveTheme(index: Int) {
        if (MainActivity.themeIndex != index) {
            val editor = getSharedPreferences("THEMES", MODE_PRIVATE).edit()
            editor.putInt("themeIndex", index)
            editor.apply()

            MaterialAlertDialogBuilder(this)
                .setTitle("Apply Theme")
                .setMessage("Do you want to apply this theme?")
                .setCancelable(false)
                .setPositiveButton("YES") { _, _ ->
//                    exitApplication()

                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finishAffinity()
                    overridePendingTransition(R.anim.zoom_in, R.anim.static_animation)
                }
                .setNegativeButton("NO") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }


    // Handle the click on the back arrow
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}