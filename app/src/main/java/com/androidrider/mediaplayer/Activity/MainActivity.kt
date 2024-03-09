package com.androidrider.mediaplayer.Activity


import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
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
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.androidrider.mediaplayer.Utils.FontUtils
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private var i = 0

    //FOR THE THEME
    companion object {

        var themeIndex: Int = 0

        val currentTheme = arrayOf(
            R.style.theme0, R.style.theme1, R.style.theme2, R.style.theme3, R.style.theme4,
            R.style.theme5, R.style.theme6, R.style.theme7, R.style.theme8, R.style.theme9,
            R.style.theme10, R.style.theme11, R.style.theme12, R.style.theme13, R.style.theme14,
            R.style.theme15, R.style.theme16, R.style.theme17, R.style.theme18, R.style.theme19,
            R.style.theme20, R.style.theme21, R.style.theme22, R.style.theme23, R.style.theme24,
            R.style.theme25, R.style.theme26, R.style.theme27, R.style.theme28, R.style.theme29,
            R.style.theme30, R.style.theme31, R.style.theme32, R.style.theme33, R.style.theme34,
            R.style.theme35,
            )

        val currentThemeNav = arrayOf(
            R.style.navTheme0, R.style.navTheme1, R.style.navTheme2, R.style.navTheme3,
            R.style.navTheme4, R.style.navTheme5
        )

//        val currentGradient = arrayOf(R.drawable.gradient_pink, R.drawable.gradient_blue, R.drawable.gradient_purple, R.drawable.gradient_green,
//            R.drawable.gradient_black)

        var sortOrder: Int = 0
        val sortingList = arrayOf(
            MediaStore.Audio.Media.DATE_ADDED + " DESC", MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.SIZE + " DESC"
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        loadSavedNightMode()
        FontUtils.setAppFont(this) //for the font
        // Load the saved Night Mode
        super.onCreate(savedInstanceState)
        //for the theme
        val themeEditor = getSharedPreferences("THEMES", MODE_PRIVATE)
        themeIndex = themeEditor.getInt("themeIndex", 0)
        setTheme(currentTheme[themeIndex])
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        //************************* Nav Code *******************************
        binding.toolbar.title = "Music Player"
        binding.navigationDrawer.setNavigationItemSelectedListener(this)
        // Hamburger Icon
        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout,
            binding.toolbar, R.string.nav_open, R.string.nav_close
        )

        // Set the color filter for the navigation icon -  when it night mode
        toggle.drawerArrowDrawable.colorFilter = PorterDuffColorFilter(
            ContextCompat.getColor(this, R.color.white),
            PorterDuff.Mode.SRC_ATOP
        )

        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()


        openFragment()

        // for retrieving favorite data using shared preferences
        FavoriteSongFragment.favoriteSongs = ArrayList()
        val editor = getSharedPreferences("FAVORITES", MODE_PRIVATE)
        val jsonString = editor.getString("favoriteSongs", null)
        val typeToken = object : TypeToken<ArrayList<AllSongModel>>() {}.type
        if (jsonString != null) {
            val data: ArrayList<AllSongModel> =
                GsonBuilder().create().fromJson(jsonString, typeToken)
            FavoriteSongFragment.favoriteSongs.addAll(data)
        }

        // for retrieving favorite data using shared preferences
        PlayListFragment.musicPlaylist = MusicPlaylist()
        val jsonStringPlaylist = editor.getString("MusicPlaylist", null)
        if (jsonStringPlaylist != null) {
            val dataPlaylist: MusicPlaylist =
                GsonBuilder().create().fromJson(jsonStringPlaylist, MusicPlaylist::class.java)
            PlayListFragment.musicPlaylist = dataPlaylist
        }


    }


    // Load the saved Night Mode on activity creation
    private fun loadSavedNightMode() {
        val sharedPreferences = getSharedPreferences("NIGHT_MODE", MODE_PRIVATE)
        val savedNightMode = sharedPreferences.getInt("night_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        // Apply the saved theme
        AppCompatDelegate.setDefaultNightMode(savedNightMode)
    }

    //************************* Nav Code *******************************
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation item selection here
        when (item.itemId) {
            R.id.nav_home -> startActivity(Intent(this, MainActivity::class.java))
            R.id.nav_about -> startActivity(Intent(this, AboutActivity::class.java))
            R.id.nav_feedback -> startActivity(Intent(this, FeedbackActivity::class.java))
            R.id.nav_settings -> startActivity(Intent(this, SettingsActivity::class.java))
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
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

                R.id.albumFragment -> {
                    i = 2
                    navController.navigate(R.id.albumFragment)
                }

                R.id.playListFragment -> {
                    i = 3
                    navController.navigate(R.id.playListFragment)
                }
            }
            true
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
        if (sortOrder != sortValue) {
            sortOrder = sortValue
            musicListMA = AllSongFragment.instance.getAllSongFromStorage()
            AllSongAdapter.updateMusicList(allSongAdapter, musicListMA)
        }
        if (PlayerActivity.musicService != null) binding.nowPlayingContainer.visibility =
            View.VISIBLE
    }

//    override fun onBackPressed() {
//        super.onBackPressed()
//        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
//        val navController = navHostFragment!!.findNavController()
//        // Get the current destination
//        val currentDestination = navController.currentDestination?.id
//        if (currentDestination != R.id.allSongFragment) {
//            navController.navigate(R.id.allSongFragment)
//        } else {
//            // If already on the home fragment, finish the activity and destroy the app
//            finishAffinity()
//        }
//    }

    // **************************************************************************************

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        val navController = navHostFragment?.findNavController()

        // Check if the navigation drawer is open, and close it if so
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            // Check if the current destination is the home fragment
            if (navController?.currentDestination?.id == R.id.allSongFragment) {
                // If on the home fragment, finish the activity and destroy the app
                finishAffinity()
            } else {
                // If not on the home fragment, navigate to the home fragment
                navController?.navigate(R.id.allSongFragment)
            }
        }
    }


}