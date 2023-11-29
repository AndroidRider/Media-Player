package com.androidrider.mediaplayer.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidrider.mediaplayer.Adapter.AllSongAdapter
import com.androidrider.mediaplayer.Fragment.AllSongFragment
import com.androidrider.mediaplayer.Fragment.PlayListFragment
import com.androidrider.mediaplayer.Model.MusicPlaylist
import com.androidrider.mediaplayer.Model.checkPlaylist
import com.androidrider.mediaplayer.R
import com.androidrider.mediaplayer.databinding.ActivityPlaylistDetailBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.GsonBuilder

class PlaylistDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlaylistDetailBinding
    private lateinit var adapter:AllSongAdapter

    companion object{
        var currentPlaylistPosition: Int = -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentTheme[MainActivity.themeIndex])
        binding = ActivityPlaylistDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        currentPlaylistPosition = intent.extras?.get("Index") as Int

        // if songs deleted from storage then this code also delete that songs from playlist if added in playlist
        PlayListFragment.musicPlaylist.ref[currentPlaylistPosition].playlist =
            checkPlaylist(playlist = PlayListFragment.musicPlaylist.ref[currentPlaylistPosition].playlist)

        binding.pdRecyclerView.setItemViewCacheSize(10)
        binding.pdRecyclerView.setHasFixedSize(true)
        binding.pdRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AllSongAdapter(this, PlayListFragment.musicPlaylist.ref[currentPlaylistPosition]
            .playlist, playlistDetails = true)
        binding.pdRecyclerView.adapter = adapter

        binding.addPlaylistDetailBtn.setOnClickListener {
            startActivity(Intent(this, SelectionActivity::class.java))
        }

        binding.removePlaylistDetailBtn.setOnClickListener {
            val mDialog = AlertDialog.Builder(this)
                .setTitle("Remove")
                .setMessage("Do you want to remove all songs from this playlist?")
                .setCancelable(false)
                .setPositiveButton("YES") { dialog, _ ->
                    PlayListFragment.musicPlaylist.ref[currentPlaylistPosition].playlist.clear()
                    adapter.refreshPlaylist()
                    dialog.dismiss()
            }
            mDialog.setNegativeButton("NO") { dialog, _ ->

            }
            mDialog.show()
        }

    }

    override fun onResume() {
        super.onResume()

        binding.toolbar.title = PlayListFragment.musicPlaylist.ref[currentPlaylistPosition].name

        binding.pdName.text = "Total ${adapter.itemCount} Songs.\n\n" +
                "Created On:\n${PlayListFragment.musicPlaylist.ref[currentPlaylistPosition].createdOn}\n\n" +
                " -- ${PlayListFragment.musicPlaylist.ref[currentPlaylistPosition].createdBy}"


        val playlist = PlayListFragment.musicPlaylist.ref[currentPlaylistPosition].playlist
        println("Playlist size: ${playlist.size}")
        if (adapter.itemCount > 0)
        {
//            Glide.with(this).load(PlayListFragment.musicPlaylist.ref[currentPlaylistPosition].playlist[0].artUri)
            Glide.with(this).load(playlist[0].artUri)
                .apply(RequestOptions().placeholder(R.drawable.music_logo).centerCrop())
                .into(binding.pdImage)

//            binding.shuffleBtnPd.visibility = View.VISIBLE
        }
        adapter.notifyDataSetChanged()

        // for storing playlist data using shared preferences
        val editor = getSharedPreferences("FAVORITES", MODE_PRIVATE).edit()
        val jsonStringPlaylist = GsonBuilder().create().toJson(PlayListFragment.musicPlaylist)
        editor.putString("MusicPlaylist", jsonStringPlaylist)
        editor.apply()
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed() // Handle the back arrow click event
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

}