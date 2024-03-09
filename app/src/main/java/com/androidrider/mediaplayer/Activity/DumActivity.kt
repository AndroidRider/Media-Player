package com.androidrider.mediaplayer.Activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.androidrider.mediaplayer.R
import androidx.core.view.GravityCompat
import androidx.fragment.app.FragmentManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.appcompat.app.ActionBarDrawerToggle
import com.androidrider.mediaplayer.Fragment.AllSongFragment
import com.google.android.material.navigation.NavigationView
import com.androidrider.mediaplayer.databinding.ActivityDumBinding

class DumActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var binding: ActivityDumBinding

    private lateinit var fragmentManager: FragmentManager
    private lateinit var fragmentTransaction : FragmentTransaction

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDumBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        binding.toolbar.title = "mPlayer"


        fragmentManager = supportFragmentManager
        // By Default Open Home Fragment
        openFragment(AllSongFragment())

//        binding.navigationDrawer.setNavigationItemSelectedListener(this)
//
//        // Hamburger Icon
//        val toggle = ActionBarDrawerToggle(this,
//            binding.drawerLayout, binding.toolbar, R.string.nav_open, R.string.nav_close)
//        binding.drawerLayout.addDrawerListener(toggle)
//        toggle.syncState()

    }


    // Open Fragment Method
    private fun openFragment(fragment: Fragment){
        fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
    }

    // When click on Navigation Item
    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.nav_home-> openFragment(AllSongFragment())
            R.id.nav_about-> startActivity(Intent(this, AboutActivity::class.java))
            R.id.nav_feedback-> startActivity(Intent(this, FeedbackActivity::class.java))
            R.id.nav_settings-> startActivity(Intent(this, SettingsActivity::class.java))

        }
//        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

//    override fun onBackPressed() {
//        when (fragmentManager.findFragmentById(R.id.fragment_container)) {
//            !is AllSongFragment -> {
//                binding.navigationDrawer.menu.getItem(0).isChecked = true
//                openFragment(AllSongFragment())
//            }
////            else -> super.getOnBackPressedDispatcher().onBackPressed()
//            else -> super.onBackPressed()
//        }
//    }
}