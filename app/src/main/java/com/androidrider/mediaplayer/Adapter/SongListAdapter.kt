package com.androidrider.mediaplayer.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.androidrider.mediaplayer.Model.AllSongModel
import com.androidrider.mediaplayer.Model.formatDuration
import com.androidrider.mediaplayer.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class SongListAdapter(val context: Context, private val songList: List<AllSongModel>, private val onItemClick: (Int) -> Unit)
    : RecyclerView.Adapter<SongListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_dialog_list_item_song, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return songList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val list = songList[position]

        holder.songTitle.text = list.title
        holder.songAlbum.text = list.album
        holder.songDuration.text = formatDuration(list.duration)

        Glide.with(context).load(list.artUri)
            .apply(RequestOptions().placeholder(R.drawable.music_logo).centerCrop())
            .into(holder.songImage)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val songImage: ImageView = itemView.findViewById(R.id.dialogSongImage)
        val songTitle: TextView = itemView.findViewById(R.id.dialogSongTitle)
        val songDuration: TextView = itemView.findViewById(R.id.dialogSongDuration)
        val songAlbum: TextView = itemView.findViewById(R.id.dialogSongAlbum)
        // Add other views for artist, album, etc., if needed
        init {
            itemView.setOnClickListener {
                onItemClick(adapterPosition)
            }
        }
    }



}
