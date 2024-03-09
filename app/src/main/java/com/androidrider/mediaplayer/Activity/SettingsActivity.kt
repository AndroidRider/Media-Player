package com.androidrider.mediaplayer.Activity



import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.androidrider.mediaplayer.R
import com.androidrider.mediaplayer.Utils.FontUtils
import com.androidrider.mediaplayer.databinding.ActivitySettingsBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        FontUtils.setAppFont(this)
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentTheme[MainActivity.themeIndex])
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // FOR BACK ARROW
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbarSettingActivity)
        setSupportActionBar(toolbar)
        // Enable the Up button (back arrow)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        binding.setTheme.setOnClickListener {
            startActivity(Intent(this, ThemeActivity::class.java))
        }

        // FOR VERSION
        binding.versionName.text = setVersionDetails()

        // FOR SORT
        binding.sortButton.setOnClickListener {
            val menuList = arrayOf("Recently Added", "Song Title", "File Size")
            var currentSort = MainActivity.sortOrder

            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle("Sorting")
                .setPositiveButton("OK") { _, _ ->
                    val editor = getSharedPreferences("SORTING", MODE_PRIVATE).edit()
                    editor.putInt("sortOrder", currentSort)
                    editor.apply()
                }
                .setSingleChoiceItems(menuList, currentSort) { _, which ->
                    currentSort = which
                }
            val customDialog = builder.create()
            customDialog.show()
        }

        // FOR FONT
        binding.selectFont.setOnClickListener {
            showFontSelectionDialog()
        }

        setAppFont()


        // Night mode switch
        val nightModeSwitch = findViewById<SwitchMaterial>(R.id.nightModeSwitch)
        nightModeSwitch.isChecked = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES

        nightModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            toggleNightMode(isChecked)
        }


    }

    private fun toggleNightMode(isNightMode: Boolean) {
        val newNightMode = if (isNightMode) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        // Apply the new night mode
        AppCompatDelegate.setDefaultNightMode(newNightMode)
        // Save the new night mode to preferences
        saveNightMode(newNightMode)
        // Recreate the activity to apply the new mode
        recreate()
    }

    // Save the new night mode to preferences
    private fun saveNightMode(nightMode: Int) {
        val sharedPreferences = getSharedPreferences("NIGHT_MODE", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("night_mode", nightMode)
        editor.apply()
    }




    /* ========================= FOR FONT ========================= */
    private fun setAppFont() {
        when (getSelectedFont()) {
            "Arial" -> setTheme(R.style.Font1)
            "Angeline Vintage" -> setTheme(R.style.Font2)
            "Bookman Regular" -> setTheme(R.style.Font3)
            "Dancing Script Regular" -> setTheme(R.style.Font4)
            "Asthelica Questak Serif" -> setTheme(R.style.Font5)
            "New Tegomin Regular" -> setTheme(R.style.Font6)
            "Nova Square regular" -> setTheme(R.style.Font7)
            "Nunito Medium" -> setTheme(R.style.Font8)
            "Playball" -> setTheme(R.style.Font9)
            "Poppins Regular" -> setTheme(R.style.Font10)
            "Productive Day" -> setTheme(R.style.Font11)
            "Public Sans Regular" -> setTheme(R.style.Font12)
            "Satisfy Regular" -> setTheme(R.style.Font13)
            "Black Rocky" -> setTheme(R.style.Font14)
            "Calibri Regular" -> setTheme(R.style.Font15)
            "Calibri Light" -> setTheme(R.style.Font16)
            "Calibri Bold" -> setTheme(R.style.Font17)
            "Calibri Italic" -> setTheme(R.style.Font18)
            "Christmas Sabila" -> setTheme(R.style.Font19)
            "Christmas Theme" -> setTheme(R.style.Font20)
            "Fonphoria" -> setTheme(R.style.Font21)
            "Fountained" -> setTheme(R.style.Font22)
            "Glaston" -> setTheme(R.style.Font23)
            "Ramadan greeting" -> setTheme(R.style.Font24)
            "Specta Ordinary Regular" -> setTheme(R.style.Font25)
            "Thiqall" -> setTheme(R.style.Font26)

            // Add more cases for other fonts if needed
            else -> setTheme(R.style.AppTheme)
        }
    }
    private fun showFontSelectionDialog() {
        val fontOptions =
            arrayOf(
                "Arial",
                "Angeline Vintage",
                "Bookman Regular",
                "Dancing Script Regular",
                "Asthelica Questak Serif",
                "New Tegomin Regular",
                "Nova Square regular",
                "Nunito Medium",
                "Playball",
                "Poppins Regular",
                "Productive Day",
                "Public Sans Regular",
                "Satisfy Regular",
                "Black Rocky",
                "Calibri Regular",
                "Calibri Light",
                "Calibri Bold",
                "Calibri Italic",
                "Christmas Sabila",
                "Christmas Theme",
                "Fonphoria",
                "Fountained",
                "Glaston",
                "Ramadan greeting",
                "Specta Ordinary Regular",
                "Thiqall",

                ) // Add more fonts as needed

        MaterialAlertDialogBuilder(this)
            .setTitle("Select Font")
            .setSingleChoiceItems(fontOptions, fontOptions.indexOf(getSelectedFont())) { dialog, which ->
                val selectedFont = fontOptions[which]
                saveSelectedFont(selectedFont)
//                recreate()
                dialog.dismiss()

                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finishAffinity()
                overridePendingTransition(R.anim.zoom_in, R.anim.static_animation)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    private fun getSelectedFont(): String {
        return getSharedPreferences("FONT_PREFS", MODE_PRIVATE)
            .getString("selectedFont", "Font1") ?: "Font1"
    }
    private fun saveSelectedFont(selectedFont: String) {
        val editor = getSharedPreferences("FONT_PREFS", MODE_PRIVATE).edit()
        editor.putString("selectedFont", selectedFont)
        editor.apply()
    }
    // FOR VERSION
    private fun setVersionDetails(): String {
        return "1.0.0"
    }

    // Handle the click on the back arrow
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}

