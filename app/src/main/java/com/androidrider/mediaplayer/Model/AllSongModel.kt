package com.androidrider.mediaplayer.Model

import android.media.MediaMetadataRetriever
import com.androidrider.mediaplayer.Activity.PlayerActivity
import com.androidrider.mediaplayer.Fragment.FavoriteSongFragment
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

data class AllSongModel (
    val id: String,
    val title: String,
    val album: String,
    val artist: String,
    val duration: Long = 0,
    val path: String,
    val artUri: String
)

class Playlist{
    lateinit var name: String
    lateinit var playlist: ArrayList<AllSongModel>
    lateinit var createdBy: String
    lateinit var createdOn: String
}

class MusicPlaylist{
    var ref: ArrayList<Playlist> = ArrayList()
}

fun formatDuration(duration: Long): String{

    val minutes = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
    val seconds = (TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS) -
            minutes*TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES))
    return String.format("%02d:%02d", minutes, seconds)
}


fun getImageArt(path: String): ByteArray? {
    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(path)
    return retriever.embeddedPicture
}

fun setSongPosition(increment: Boolean){
    if (!PlayerActivity.repeat){
        if (increment){
            if (PlayerActivity.musicListPA.size - 1 == PlayerActivity.songPosition)
                PlayerActivity.songPosition = 0
            else
                ++PlayerActivity.songPosition
        }else
        {
            if (0 == PlayerActivity.songPosition)
                PlayerActivity.songPosition = PlayerActivity.musicListPA.size-1
            else
                --PlayerActivity.songPosition
        }
    }
}

fun exitApplication(){
    if (PlayerActivity.musicService != null){
        PlayerActivity.musicService!!.audioManager.abandonAudioFocus(PlayerActivity.musicService)
        PlayerActivity.musicService!!.stopForeground(true)
        PlayerActivity.musicService!!.mediaPlayer!!.release()
        PlayerActivity.musicService = null
    }
    exitProcess(1)
}

fun favoriteChecker(id : String): Int{
    PlayerActivity.isFavorite = false
    FavoriteSongFragment.favoriteSongs.forEachIndexed { index, allSongModel ->
        if (id == allSongModel.id){
            PlayerActivity.isFavorite = true
            return index
        }
    }
    return -1
}

fun checkPlaylist(playlist: ArrayList<AllSongModel>): ArrayList<AllSongModel>{
    playlist.forEachIndexed { index, allSongModel ->
        val file = File(allSongModel.path)
        if (!file.exists())
            playlist.removeAt(index)
    }
    return playlist
}