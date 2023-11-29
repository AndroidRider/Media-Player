package com.androidrider.mediaplayer.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.androidrider.mediaplayer.Activity.PlayerActivity
import com.androidrider.mediaplayer.Activity.PlaylistDetailActivity
import com.androidrider.mediaplayer.Fragment.AllSongFragment
import com.androidrider.mediaplayer.Fragment.PlayListFragment
import com.androidrider.mediaplayer.Model.AllSongModel
import com.androidrider.mediaplayer.Model.Playlist
import com.androidrider.mediaplayer.Model.formatDuration
import com.androidrider.mediaplayer.R
import com.androidrider.mediaplayer.databinding.AddPlaylistDialogBinding
import com.androidrider.mediaplayer.databinding.PlaylistLayoutBinding
import com.androidrider.mediaplayer.databinding.RecyclerAllSongLayoutBinding
import com.androidrider.mediaplayer.databinding.RecyclerFavoriteLayoutBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PlayListAdapter(private val context: Context, private var playlistList: ArrayList<Playlist>) :
    RecyclerView.Adapter<PlayListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = PlaylistLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return playlistList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.playlistTitle.text = playlistList[position].name
        holder.playlistTitle.isSelected = true

        holder.delete.setOnClickListener {

            val dialog = MaterialAlertDialogBuilder(context)
                .setTitle(playlistList[position].name)
                .setMessage("Do you want to delete this playlist?")
                .setPositiveButton("Yes") { dialog, _ ->
                    PlayListFragment.musicPlaylist.ref.removeAt(position)
                    refreshPlayList()
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
            val customDialog = dialog.create()
            customDialog.show()
            customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
            customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLUE)
        }

        holder.root.setOnClickListener {
            val intent = Intent(context, PlaylistDetailActivity::class.java)
            intent.putExtra("Index", position)
            ContextCompat.startActivity(context, intent, null)
        }
        if (PlayListFragment.musicPlaylist.ref[position].playlist.size > 0){
            Glide.with(context).load(PlayListFragment.musicPlaylist.ref[position].playlist[0].artUri)
                .apply(RequestOptions().placeholder(R.drawable.music_logo).centerCrop())
                .into(holder.binding.playlistImage)
        }
    }


        class ViewHolder(val binding: PlaylistLayoutBinding) :
            RecyclerView.ViewHolder(binding.root) {
            val playlistTitle = binding.playlistName
            val root = binding.root

            val delete = binding.playlistDeleteButton

        }

        fun refreshPlayList() {
            playlistList = ArrayList()
            playlistList.addAll(PlayListFragment.musicPlaylist.ref)
            notifyDataSetChanged()
        }
    }

