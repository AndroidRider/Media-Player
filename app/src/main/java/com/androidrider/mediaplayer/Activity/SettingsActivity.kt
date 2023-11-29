package com.androidrider.mediaplayer.Activity

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.androidrider.mediaplayer.Model.exitApplication
import com.androidrider.mediaplayer.databinding.ActivitySettingsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentTheme[MainActivity.themeIndex])
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Settings"

        when(MainActivity.themeIndex){
            0 -> binding.theme0.setBackgroundColor(Color.CYAN)
            1 -> binding.theme1.setBackgroundColor(Color.CYAN)
            2 -> binding.theme2.setBackgroundColor(Color.CYAN)
            3 -> binding.theme3.setBackgroundColor(Color.CYAN)
            4 -> binding.theme4.setBackgroundColor(Color.CYAN)
            5 -> binding.theme5.setBackgroundColor(Color.CYAN)
        }

        binding.theme0.setOnClickListener { saveTheme(0) }
        binding.theme1.setOnClickListener { saveTheme(1) }
        binding.theme2.setOnClickListener { saveTheme(2) }
        binding.theme3.setOnClickListener { saveTheme(3) }
        binding.theme4.setOnClickListener { saveTheme(4) }
        binding.theme5.setOnClickListener { saveTheme(5) }

        binding.versionName.text = setVersionDetails()

        binding.sortButton.setOnClickListener {
            val menuList = arrayOf("Recently Added", "Song Title", "File Size")
            var currentSort = MainActivity.sortOrder

            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle("Sorting")
                .setPositiveButton("OK"){ _, _ ->
                    val editor = getSharedPreferences("SORTING", MODE_PRIVATE).edit()
                    editor.putInt("sortOrder", currentSort)
                    editor.apply()
                }
                .setSingleChoiceItems(menuList, currentSort){ _,which->
                    currentSort = which
                }
            val customDialog = builder.create()
            customDialog.show()

//            setDialogBtnBackground(this, customDialog)
        }


    }

    private fun saveTheme(index: Int){

        if (MainActivity.themeIndex != index){
            val editor = getSharedPreferences("THEMES", MODE_PRIVATE).edit()
            editor.putInt("themeIndex", index)
            editor.apply()

            val mDialog = MaterialAlertDialogBuilder(this)
            mDialog.setTitle("Apply Theme")
            mDialog.setMessage("Do you want to apply this theme ?")
            mDialog.setCancelable(false)
            mDialog.setPositiveButton("YES") { dialog, _ ->
                exitApplication()
            }
            mDialog.setNegativeButton("NO") { dialog, _ ->
                dialog.dismiss()
            }
            mDialog.show()
            val customDialog = mDialog.create()
            customDialog.show()

//            setDialogBtnBackground(this, customDialog)
        }
    }


    private fun setVersionDetails():String{
//        return "Version : ${BuildConfig.VERSION_NAME}"
        return "Version : 1.0"

    }

}