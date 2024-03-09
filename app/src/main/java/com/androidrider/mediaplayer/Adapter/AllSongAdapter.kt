package com.androidrider.mediaplayer.Adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.androidrider.mediaplayer.Activity.PlayerActivity
import com.androidrider.mediaplayer.Activity.PlaylistDetailActivity
import com.androidrider.mediaplayer.Activity.SelectionActivity
import com.androidrider.mediaplayer.Fragment.AllSongFragment
import com.androidrider.mediaplayer.Fragment.PlayListFragment
import com.androidrider.mediaplayer.Model.AllSongModel
import com.androidrider.mediaplayer.Model.formatDuration
import com.androidrider.mediaplayer.R
import com.androidrider.mediaplayer.databinding.RecyclerAllSongLayoutBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class AllSongAdapter(private val context: Context, private var musicList: ArrayList<AllSongModel>,
                     private val playlistDetails: Boolean = false, private val selectionActivity: Boolean = false):
    RecyclerView.Adapter<AllSongAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerAllSongLayoutBinding.inflate(LayoutInflater.from(context),parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = musicList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val list = musicList[position]

        holder.binding.songTitle.text = list.title
        holder.binding.songAlbum.text = list.album
        holder.binding.songDuration.text = formatDuration(list.duration)

        Glide.with(context).load(list.artUri)
            .apply(RequestOptions().placeholder(R.drawable.music_logo).centerCrop())
            .into(holder.binding.songImage)

        when{
            playlistDetails -> {
                holder.root.setOnClickListener {
                    sendIntent(ref = "PlaylistDetailsAdapter", pos = position)
                }
            }
            selectionActivity -> {
                holder.root.setOnClickListener {
                    if (addSong(musicList[position])) {
//                        holder.root.setBackgroundColor(ContextCompat.getColor(context, R.color.mainColor))
                        holder.root.setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_green_light))

                        Toast.makeText(context, "Added", Toast.LENGTH_SHORT).show()
                    } else {
                        holder.root.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
                        Toast.makeText(context, "Removed", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            else ->{
                holder.root.setOnClickListener {
                    when{
                        AllSongFragment.search -> sendIntent(ref = "MusicAdapterSearch", pos = position)
                        musicList[position].id == PlayerActivity.nowPlayingId ->
                            sendIntent(ref = "NowPlaying", pos = PlayerActivity.songPosition)
                        else->  sendIntent(ref = "MusicAdapter", pos = position)
                    }
                }
            }
        }

    }

    class ViewHolder(val binding: RecyclerAllSongLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        val root = binding.root
    }

    private fun sendIntent(ref: String, pos: Int){
        val intent = Intent(context, PlayerActivity::class.java)
        intent.putExtra("index", pos)
        intent.putExtra("class", ref)
        ContextCompat.startActivity(context, intent, null)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateMusicList(searchList: ArrayList<AllSongModel>) {
        musicList = ArrayList()
        musicList.addAll(searchList)
        notifyDataSetChanged()
    }


    companion object {
        @SuppressLint("NotifyDataSetChanged")
        fun updateMusicList(adapter: AllSongAdapter, searchList: ArrayList<AllSongModel>) {
            adapter.musicList = ArrayList()
            adapter.musicList.addAll(searchList)
            adapter.notifyDataSetChanged()
        }
    }

    private fun addSong(song: AllSongModel): Boolean{
        PlayListFragment.musicPlaylist.ref[PlaylistDetailActivity.currentPlaylistPosition].playlist.forEachIndexed { index, allSongModel ->
            if (song.id == allSongModel.id){
                PlayListFragment.musicPlaylist.ref[PlaylistDetailActivity.currentPlaylistPosition].playlist.removeAt(index)
                return false
            }
        }
        PlayListFragment.musicPlaylist.ref[PlaylistDetailActivity.currentPlaylistPosition].playlist.add(song)
        return true
    }

    fun refreshPlaylist(){
        musicList = ArrayList()
        musicList = PlayListFragment.musicPlaylist.ref[PlaylistDetailActivity.currentPlaylistPosition].playlist
        notifyDataSetChanged()
    }

}

