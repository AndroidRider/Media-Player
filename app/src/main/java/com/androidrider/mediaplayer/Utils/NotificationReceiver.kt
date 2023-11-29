package com.androidrider.mediaplayer.Utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.androidrider.mediaplayer.Activity.PlayerActivity
import com.androidrider.mediaplayer.Fragment.NowPlayingFragment
import com.androidrider.mediaplayer.Model.exitApplication
import com.androidrider.mediaplayer.Model.favoriteChecker
import com.androidrider.mediaplayer.Model.setSongPosition
import com.androidrider.mediaplayer.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlin.system.exitProcess

class NotificationReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        when(intent?.action){
            ApplicationClass.PREVIOUS -> prevNext(increment = false, context = context!!)

            ApplicationClass.PLAY -> if (PlayerActivity.isPlaying) pauseMusic() else playMusic()

            ApplicationClass.NEXT -> prevNext(increment = true, context = context!!)

            ApplicationClass.EXIT -> {
                exitApplication()
            }
        }
    }


    private fun playMusic(){

        PlayerActivity.isPlaying = true
        PlayerActivity.musicService!!.mediaPlayer!!.start()
        PlayerActivity.musicService!!.showNotification(R.drawable.notification_pause, 1F)
        PlayerActivity.binding.playPause.setImageResource(R.drawable.icon_pause)
        NowPlayingFragment.binding.playPauseButtonNP.setImageResource(R.drawable.icon_pause)
    }

    private fun pauseMusic(){

        PlayerActivity.isPlaying = false
        PlayerActivity.musicService!!.mediaPlayer!!.pause()
        PlayerActivity.musicService!!.showNotification(R.drawable.notification_play, 0F)
        PlayerActivity.binding.playPause.setImageResource(R.drawable.icon_play)
        NowPlayingFragment.binding.playPauseButtonNP.setImageResource(R.drawable.icon_play)

    }

    private fun prevNext(increment: Boolean, context: Context){

        setSongPosition(increment = increment)
        PlayerActivity.musicService!!.createMediaPlayer()

        val data = PlayerActivity.musicListPA[PlayerActivity.songPosition]
        Glide.with(context)
            .load(data.artUri)
            .apply(RequestOptions()
            .placeholder(R.drawable.music_logo).centerCrop())
            .into(PlayerActivity.binding.musicImage)
        PlayerActivity.binding.songTitle.text = data.title

        Glide.with(context).load(data.artUri)
            .apply(RequestOptions().placeholder(R.drawable.music_logo).centerCrop())
            .into(NowPlayingFragment.binding.songImageNP)

        NowPlayingFragment.binding.songTitleNP.text = data.title

        playMusic()

        PlayerActivity.fIndex = favoriteChecker(PlayerActivity.musicListPA[PlayerActivity.songPosition].id)
        if (PlayerActivity.isFavorite)
            PlayerActivity.binding.favoriteButton.setImageResource(R.drawable.favorite_filled_icon)
        else
            PlayerActivity.binding.favoriteButton.setImageResource(R.drawable.favorite_empty_icon)

    }
}