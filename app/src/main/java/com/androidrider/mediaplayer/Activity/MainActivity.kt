package com.androidrider.mediaplayer.Activity


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.View
import android.view.View.GONE
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.isNotEmpty
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.androidrider.mediaplayer.Adapter.AllSongAdapter
import com.androidrider.mediaplayer.Fragment.AllSongFragment
import com.androidrider.mediaplayer.Fragment.AllSongFragment.Companion.allSongAdapter
import com.androidrider.mediaplayer.Fragment.AllSongFragment.Companion.musicListMA
import com.androidrider.mediaplayer.Fragment.FavoriteSongFragment
import com.androidrider.mediaplayer.Fragment.PlayListFragment
import com.androidrider.mediaplayer.Model.AllSongModel
import com.androidrider.mediaplayer.Model.MusicPlaylist
import com.androidrider.mediaplayer.R
import com.androidrider.mediaplayer.databinding.ActivityMainBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var i = 0

    //for the theme
    companion object{

        var themeIndex: Int = 0
        val currentTheme = arrayOf(R.style.theme0, R.style.theme1, R.style.theme2, R.style.theme3,
            R.style.theme4, R.style.theme5)

        val currentThemeNav = arrayOf(R.style.navTheme0, R.style.navTheme1, R.style.navTheme2, R.style.navTheme3,
            R.style.navTheme4, R.style.navTheme5)

//        val currentGradient = arrayOf(R.drawable.gradient_pink, R.drawable.gradient_blue, R.drawable.gradient_purple, R.drawable.gradient_green,
//            R.drawable.gradient_black)

        var sortOrder: Int = 0
        val sortingList = arrayOf(
            MediaStore.Audio.Media.DATE_ADDED + " DESC", MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.SIZE + " DESC")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //for the theme
        val themeEditor = getSharedPreferences("THEMES", MODE_PRIVATE)
        themeIndex = themeEditor.getInt("themeIndex", 0)
        setTheme(currentTheme[themeIndex])
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

//        if (binding.nowPlayingContainer.isNotEmpty()){
//            binding.nowPlayingContainer.visibility = View.VISIBLE
//        }

        // for retrieving favorite data using shared preferences
        FavoriteSongFragment.favoriteSongs = ArrayList()
        val editor = getSharedPreferences("FAVORITES", MODE_PRIVATE)
        val jsonString = editor.getString("favoriteSongs", null)
        val typeToken = object : TypeToken<ArrayList<AllSongModel>>(){}.type
        if (jsonString != null){
            val data: ArrayList<AllSongModel> = GsonBuilder().create().fromJson(jsonString, typeToken)
            FavoriteSongFragment.favoriteSongs.addAll(data)
        }

        // for retrieving favorite data using shared preferences
        PlayListFragment.musicPlaylist = MusicPlaylist()
        val jsonStringPlaylist = editor.getString("MusicPlaylist", null)
        if (jsonStringPlaylist != null){
            val dataPlaylist: MusicPlaylist = GsonBuilder().create().fromJson(jsonStringPlaylist, MusicPlaylist::class.java)
            PlayListFragment.musicPlaylist= dataPlaylist
        }

        openFragment()

    }


    // Function to Navigate Fragments
    private fun openFragment() {

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        val navController = navHostFragment!!.findNavController()

        val popupMenu = PopupMenu(this, null)
        popupMenu.inflate(R.menu.bottom_menu)
        NavigationUI.setupWithNavController(binding.bottomBar, navController)

        binding.bottomBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.allSongFragment -> {
                    i = 0
                    navController.navigate(R.id.allSongFragment)
                }
                R.id.favoriteSongFragment -> {
                    i = 1
                    navController.navigate(R.id.favoriteSongFragment)
                }
                R.id.onlineHomeFragment -> {
                    i = 2
                    navController.navigate(R.id.onlineHomeFragment)
                }
                R.id.playListFragment -> {
                    i = 3
                    navController.navigate(R.id.playListFragment)
                }
            }
            true
        }
    }


    override fun onBackPressed() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        val navController = navHostFragment!!.findNavController()
        // Get the current destination
        val currentDestination = navController.currentDestination?.id

        if (currentDestination != R.id.allSongFragment) {
            navController.navigate(R.id.allSongFragment)
        } else {
            // If already on the home fragment, finish the activity and destroy the app
            finishAffinity()
        }
    }

    override fun onResume() {
        super.onResume()
        // for storing favorite data using shared preferences
        val editor = getSharedPreferences("FAVORITES", MODE_PRIVATE).edit()
        val jsonString = GsonBuilder().create().toJson(FavoriteSongFragment.favoriteSongs)
        editor.putString("favoriteSongs", jsonString)

        // for storing playlist data using shared preferences
        val jsonStringPlaylist = GsonBuilder().create().toJson(PlayListFragment.musicPlaylist)
        editor.putString("MusicPlaylist", jsonStringPlaylist)
        editor.apply()

        //for sorting
        val sortEditor = getSharedPreferences("SORTING", MODE_PRIVATE)
        val sortValue = sortEditor.getInt("sortOrder", 0)
        if(sortOrder != sortValue){
            sortOrder = sortValue
            //************************ test ************
            musicListMA = AllSongFragment.instance.getAllSongFromStorage()
            AllSongAdapter.updateMusicList(allSongAdapter, musicListMA)
        }

        if(PlayerActivity.musicService != null) binding.nowPlayingContainer.visibility = View.VISIBLE
    }


}