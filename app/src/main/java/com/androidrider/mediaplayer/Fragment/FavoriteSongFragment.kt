package com.androidrider.mediaplayer.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.androidrider.mediaplayer.Activity.MainActivity
import com.androidrider.mediaplayer.Adapter.AllSongAdapter
import com.androidrider.mediaplayer.Adapter.FavoriteAdapter
import com.androidrider.mediaplayer.Model.AllSongModel
import com.androidrider.mediaplayer.Model.checkPlaylist
import com.androidrider.mediaplayer.R
import com.androidrider.mediaplayer.databinding.FragmentFavoriteSongBinding
import com.androidrider.mediaplayer.databinding.FragmentNowPlayingBinding

class FavoriteSongFragment : Fragment() {

    lateinit var binding: FragmentFavoriteSongBinding
    private lateinit var favoriteAdapter: FavoriteAdapter

    companion object{
        var favoriteSongs : ArrayList<AllSongModel> = ArrayList()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        requireContext().theme.applyStyle(MainActivity.currentTheme[MainActivity.themeIndex], true)
        // Inflate the layout for this fragment
        binding = FragmentFavoriteSongBinding.inflate(layoutInflater)

        // if songs deleted from storage then this code also delete that songs from favorite if added in favorite
        favoriteSongs = checkPlaylist(favoriteSongs)

        binding.recyclerViewFavorite.setHasFixedSize(true)
        binding.recyclerViewFavorite.setItemViewCacheSize(15)
        favoriteAdapter = FavoriteAdapter(requireContext(), favoriteSongs)
        binding.recyclerViewFavorite.adapter = favoriteAdapter
        AllSongFragment.allSongAdapter.notifyDataSetChanged()

        return binding.root
    }


}