package com.androidrider.mediaplayer.Utils

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import com.androidrider.mediaplayer.Activity.MainActivity
import com.androidrider.mediaplayer.Activity.PlayerActivity
import com.androidrider.mediaplayer.Fragment.NowPlayingFragment
import com.androidrider.mediaplayer.Model.formatDuration
import com.androidrider.mediaplayer.Model.getImageArt
import com.androidrider.mediaplayer.R


class MusicService: Service(), AudioManager.OnAudioFocusChangeListener {

    private var myBinder = MyBinder()
    var mediaPlayer: MediaPlayer? = null
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var runnable: Runnable
    lateinit var audioManager:AudioManager

    override fun onBind(intent: Intent?): IBinder {
        mediaSession = MediaSessionCompat(this, "My Music")
        return myBinder
    }

    inner class MyBinder:Binder(){
        fun currentService(): MusicService {
            return  this@MusicService
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun  showNotification(playPauseButton: Int, playbackSpeed: Float){


        // for open to MainActivity
        val mainActIntent = Intent(baseContext, MainActivity::class.java)
        val contentIntent = PendingIntent.getActivity(this, 0, mainActIntent,0)

        // for open to PlayerActivity
//        val intent = Intent(baseContext, PlayerActivity::class.java)
//        intent.putExtra("index", PlayerActivity.songPosition)
//        intent.putExtra("class", "NowPlaying")
//        val contentIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val prevIntent = Intent(this, NotificationReceiver::class.java)
            .setAction(ApplicationClass.PREVIOUS)
        val prevPendingIntent = PendingIntent.getBroadcast(baseContext, 0, prevIntent,
            PendingIntent.FLAG_UPDATE_CURRENT)

        val playIntent = Intent(this, NotificationReceiver::class.java)
            .setAction(ApplicationClass.PLAY)
        val playPendingIntent = PendingIntent.getBroadcast(baseContext, 0, playIntent,
            PendingIntent.FLAG_UPDATE_CURRENT)

        val nextIntent = Intent(this, NotificationReceiver::class.java)
            .setAction(ApplicationClass.NEXT)
        val nextPendingIntent = PendingIntent.getBroadcast(baseContext, 0, nextIntent,
            PendingIntent.FLAG_UPDATE_CURRENT)

        val exitIntent = Intent(this, NotificationReceiver::class.java)
            .setAction(ApplicationClass.EXIT)
        val exitPendingIntent = PendingIntent.getBroadcast(baseContext, 0, exitIntent,
            PendingIntent.FLAG_UPDATE_CURRENT)

        val imageArt = getImageArt(PlayerActivity.musicListPA[PlayerActivity.songPosition].path)
        val image = if (imageArt != null){
            BitmapFactory.decodeByteArray(imageArt, 0, imageArt.size)
        }else{
            BitmapFactory.decodeResource(resources, R.drawable.music_logo)
        }

        val notification = NotificationCompat.Builder(this, ApplicationClass.CHANNEL_ID)
            .setContentIntent(contentIntent)
            .setContentTitle(PlayerActivity.musicListPA[PlayerActivity.songPosition].title)
            .setContentText(PlayerActivity.musicListPA[PlayerActivity.songPosition].artist)
            .setSmallIcon(R.drawable.notification_bar_icon)
            .setLargeIcon(image)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.sessionToken))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .addAction(R.drawable.notification_backward, "Previous", prevPendingIntent)
            .addAction(playPauseButton, "Play", playPendingIntent)
            .addAction(R.drawable.notification_forward, "Next", nextPendingIntent)
            .addAction(R.drawable.notification_close, "Exit", exitPendingIntent)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            mediaSession.setMetadata(MediaMetadataCompat.Builder()
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, mediaPlayer!!.duration.toLong())
                .build())
            mediaSession.setPlaybackState(PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_PLAYING, mediaPlayer!!.currentPosition.toLong(), playbackSpeed)
                .setActions(PlaybackStateCompat.ACTION_SEEK_TO)
                .build())
        }

        startForeground(13, notification)
    }

    fun createMediaPlayer(){
        try {
            if (PlayerActivity.musicService!!.mediaPlayer == null) PlayerActivity.musicService!!.mediaPlayer = MediaPlayer()
            PlayerActivity.musicService!!.mediaPlayer!!.reset()
            PlayerActivity.musicService!!.mediaPlayer!!.setDataSource(PlayerActivity.musicListPA[PlayerActivity.songPosition].path)
            PlayerActivity.musicService!!.mediaPlayer!!.prepare()
            PlayerActivity.binding.playPause.setImageResource(R.drawable.icon_pause)
            PlayerActivity.musicService!!.showNotification(R.drawable.notification_pause, 0F)

            PlayerActivity.binding.tvStartTime.text = formatDuration(mediaPlayer!!.currentPosition.toLong())
            PlayerActivity.binding.tvEndTime.text = formatDuration(mediaPlayer!!.duration.toLong())
            PlayerActivity.binding.seekBar.progress = 0
            PlayerActivity.binding.seekBar.max = mediaPlayer!!.duration

            PlayerActivity.nowPlayingId = PlayerActivity.musicListPA[PlayerActivity.songPosition].id

        }catch (e: Exception){return}
    }

    fun seekBarSetup(){
        runnable = Runnable {
            PlayerActivity.binding.tvStartTime.text = formatDuration(mediaPlayer!!.currentPosition.toLong())
            PlayerActivity.binding.seekBar.progress = mediaPlayer!!.currentPosition
            Handler(Looper.getMainLooper()).postDelayed(runnable,200)
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable,0)
    }

    override fun onAudioFocusChange(focusChange: Int) {
        if (focusChange <= 0){
            // pause music
            PlayerActivity.isPlaying = false
            mediaPlayer!!.pause()
            showNotification(R.drawable.notification_play, 0F)
            PlayerActivity.binding.playPause.setImageResource(R.drawable.icon_play)
            NowPlayingFragment.binding.playPauseButtonNP.setImageResource(R.drawable.icon_play)
        }
        else{
            //play music
            PlayerActivity.isPlaying = true
            mediaPlayer!!.start()
            showNotification(R.drawable.notification_pause, 1F)
            PlayerActivity.binding.playPause.setImageResource(R.drawable.icon_pause)
            NowPlayingFragment.binding.playPauseButtonNP.setImageResource(R.drawable.icon_pause)
        }
    }


}