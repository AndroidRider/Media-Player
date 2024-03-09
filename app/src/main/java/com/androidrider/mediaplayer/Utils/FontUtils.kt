package com.androidrider.mediaplayer.Utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.androidrider.mediaplayer.R

object FontUtils {

    fun setAppFont(context: Context) {
        when (getSelectedFont(context)) {
            "Arial" -> context.setTheme(R.style.Font1)
            "Angeline Vintage" -> context.setTheme(R.style.Font2)
            "Bookman Regular" -> context.setTheme(R.style.Font3)
            "Dancing Script Regular" -> context.setTheme(R.style.Font4)
            "Asthelica Questak Serif" -> context.setTheme(R.style.Font5)
            "New Tegomin Regular" -> context.setTheme(R.style.Font6)
            "Nova Square regular" -> context.setTheme(R.style.Font7)
            "Nunito Medium" -> context.setTheme(R.style.Font8)
            "Playball" -> context.setTheme(R.style.Font9)
            "Poppins Regular" -> context.setTheme(R.style.Font10)
            "Productive Day" -> context.setTheme(R.style.Font11)
            "Public Sans Regular" -> context.setTheme(R.style.Font12)
            "Satisfy Regular" -> context.setTheme(R.style.Font13)

            "Black Rocky" -> context.setTheme(R.style.Font14)
            "Calibri Regular" -> context.setTheme(R.style.Font15)
            "Calibri Light" -> context.setTheme(R.style.Font16)
            "Calibri Bold" -> context.setTheme(R.style.Font17)
            "Calibri Italic" -> context.setTheme(R.style.Font18)
            "Christmas Sabila" -> context.setTheme(R.style.Font19)
            "Christmas Theme" -> context.setTheme(R.style.Font20)
            "Fonphoria" -> context.setTheme(R.style.Font21)
            "Fountained" -> context.setTheme(R.style.Font22)
            "Glaston" -> context.setTheme(R.style.Font23)
            "Ramadan greeting" -> context.setTheme(R.style.Font24)
            "Specta Ordinary Regular" -> context.setTheme(R.style.Font25)
            "Thiqall" -> context.setTheme(R.style.Font26)


            // Add more cases for other fonts if needed
            else -> context.setTheme(R.style.AppTheme)
        }
    }

    private fun getSelectedFont(context: Context): String {
        return context.getSharedPreferences("FONT_PREFS", MODE_PRIVATE)
            .getString("selectedFont", "Font1") ?: "Font1"
    }


}

