package com.androidrider.mediaplayer.Fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.androidrider.mediaplayer.Activity.MainActivity
import com.androidrider.mediaplayer.Activity.PlayerActivity
import com.androidrider.mediaplayer.Model.setSongPosition
import com.androidrider.mediaplayer.R
import com.androidrider.mediaplayer.databinding.FragmentNowPlayingBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class NowPlayingFragment : Fragment() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: FragmentNowPlayingBinding
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        requireContext().theme.applyStyle(MainActivity.currentTheme[MainActivity.themeIndex], true)
        // Inflate the layout for this fragment
        binding = FragmentNowPlayingBinding.inflate(layoutInflater)


        binding.root.visibility = View.INVISIBLE

        binding.playPauseButtonNP.setOnClickListener {
            if (PlayerActivity.isPlaying) pauseMusic() else playMusic()
        }

        binding.nextButtonNP.setOnClickListener {
            setSongPosition(increment = true)
            PlayerActivity.musicService!!.createMediaPlayer()
            val data = PlayerActivity.musicListPA[PlayerActivity.songPosition]
            Glide.with(this).load(data.artUri).apply(
                RequestOptions().placeholder(R.drawable.music_logo).centerCrop()
            ).into(binding.songImageNP)
            binding.songTitleNP.text = PlayerActivity.musicListPA[PlayerActivity.songPosition].title
            PlayerActivity.musicService!!.showNotification(R.drawable.player_pause_icon, 1F)
            playMusic()
        }

        binding.root.setOnClickListener {

            val intent = Intent(requireContext(), PlayerActivity::class.java)
            intent.putExtra("index", PlayerActivity.songPosition)
            intent.putExtra("class", "NowPlaying")
            ContextCompat.startActivity(requireContext(), intent, null)


            // Add animation to the activity transition
//            activity?.overridePendingTransition(R.anim.slide_from_bottom, R.anim.slide_to_top)
//            activity?.overridePendingTransition(R.anim.slide_to_top, R.anim.slide_from_bottom)
            activity?.overridePendingTransition(R.anim.static_animation, R.anim.zoom_out)

        }

        return binding.root
    }


    override fun onResume() {
        super.onResume()
        if (PlayerActivity.musicService != null) {

            binding.root.visibility = View.VISIBLE

            val data = PlayerActivity.musicListPA[PlayerActivity.songPosition]
            Glide.with(this).load(data.artUri).apply(
                RequestOptions().placeholder(R.drawable.music_logo).centerCrop()
            ).into(binding.songImageNP)

            binding.songTitleNP.text = PlayerActivity.musicListPA[PlayerActivity.songPosition].title

            if (PlayerActivity.isPlaying) binding.playPauseButtonNP.setImageResource(R.drawable.player_pause_icon)
            else binding.playPauseButtonNP.setImageResource(R.drawable.player_play_icon)

            binding.songTitleNP.isSelected = true
        }
    }

    private fun playMusic() {
        PlayerActivity.musicService!!.mediaPlayer!!.start()
        binding.playPauseButtonNP.setImageResource(R.drawable.player_pause_icon)
        PlayerActivity.musicService!!.showNotification(R.drawable.player_pause_icon, 1F)
        PlayerActivity.binding.playPause.setImageResource(R.drawable.player_pause_icon)
        PlayerActivity.isPlaying = true

    }

    private fun pauseMusic() {
        PlayerActivity.musicService!!.mediaPlayer!!.pause()
        binding.playPauseButtonNP.setImageResource(R.drawable.player_play_icon)
        PlayerActivity.musicService!!.showNotification(R.drawable.player_play_icon, 0F)
        PlayerActivity.binding.playPause.setImageResource(R.drawable.player_play_icon)
        PlayerActivity.isPlaying = false

    }

}