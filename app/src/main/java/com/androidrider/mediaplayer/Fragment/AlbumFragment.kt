package com.androidrider.mediaplayer.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.androidrider.mediaplayer.Activity.AboutActivity
import com.androidrider.mediaplayer.Activity.MainActivity
import com.androidrider.mediaplayer.Activity.PlayerActivity
import com.androidrider.mediaplayer.R
import com.androidrider.mediaplayer.databinding.FragmentAlbumBinding
import com.androidrider.mediaplayer.databinding.FragmentAllSongBinding

class AlbumFragment : Fragment() {

    private lateinit var binding: FragmentAlbumBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        requireContext().theme.applyStyle(MainActivity.currentTheme[MainActivity.themeIndex], true)
        // Inflate the layout for this fragment

        binding = FragmentAlbumBinding.inflate(layoutInflater)

        binding.openSettingButton.setOnClickListener {
            startActivity(Intent(requireContext(), AboutActivity::class.java))

        }

        return binding.root
    }


}