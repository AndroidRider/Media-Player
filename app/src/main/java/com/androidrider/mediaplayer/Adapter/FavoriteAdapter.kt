package com.androidrider.mediaplayer.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.androidrider.mediaplayer.Activity.PlayerActivity
import com.androidrider.mediaplayer.Fragment.AllSongFragment
import com.androidrider.mediaplayer.Model.AllSongModel
import com.androidrider.mediaplayer.Model.formatDuration
import com.androidrider.mediaplayer.R
import com.androidrider.mediaplayer.databinding.RecyclerAllSongLayoutBinding
import com.androidrider.mediaplayer.databinding.RecyclerFavoriteLayoutBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class FavoriteAdapter(private val context: Context, private var musicList: ArrayList<AllSongModel> ):
    RecyclerView.Adapter<FavoriteAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerFavoriteLayoutBinding.inflate(LayoutInflater.from(context),parent, false)
        return ViewHolder(binding)
    }
    override fun getItemCount(): Int {
        return musicList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.titleFavorite.text = musicList[position].title

        val data = musicList[position]
        Glide.with(context).load(data.artUri)
            .apply(RequestOptions().placeholder(R.drawable.music_logo).centerCrop())
            .into(holder.image)

        holder.root.setOnClickListener {
            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra("index", position)
            intent.putExtra("class", "FavoriteAdapter")
            ContextCompat.startActivity(context, intent, null)
        }
    }

    class ViewHolder(val binding: RecyclerFavoriteLayoutBinding): RecyclerView.ViewHolder(binding.root) {

        val image = binding.imageFavorite
        val titleFavorite = binding.titleFavorite
        val root = binding.root


    }


    private fun sendIntent(ref: String, pos: Int){
        val intent = Intent(context, PlayerActivity::class.java)
        intent.putExtra("index", pos)
        intent.putExtra("class", ref)
        ContextCompat.startActivity(context, intent, null)
    }


}