package com.androidrider.mediaplayer.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidrider.mediaplayer.Adapter.AllSongAdapter
import com.androidrider.mediaplayer.Fragment.AllSongFragment
import com.androidrider.mediaplayer.Fragment.PlayListFragment
import com.androidrider.mediaplayer.R
import com.androidrider.mediaplayer.databinding.ActivityPlaylistDetailBinding
import com.androidrider.mediaplayer.databinding.ActivitySelectionBinding

class SelectionActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectionBinding
    private lateinit var adapter:AllSongAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentTheme[MainActivity.themeIndex])
        binding = ActivitySelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.selectionRecyclerView.setItemViewCacheSize(10)
        binding.selectionRecyclerView.setHasFixedSize(true)
        binding.selectionRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AllSongAdapter(this, AllSongFragment.musicListMA, selectionActivity = true)
        binding.selectionRecyclerView.adapter = adapter



        //for searchView
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean = true
            override fun onQueryTextChange(newText: String?): Boolean {
                AllSongFragment.musicListSearch = ArrayList()
                if (newText != null){
                    val userInput = newText.lowercase()
                    for (song in AllSongFragment.musicListMA)
                        if (song.title.lowercase().contains(userInput))
                            AllSongFragment.musicListSearch.add(song)
                    AllSongFragment.search = true
                    adapter.updateMusicList(searchList = AllSongFragment.musicListSearch)
                }
                return true
            }
        })
    }
}