package com.androidrider.mediaplayer.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.androidrider.mediaplayer.Activity.MainActivity
import com.androidrider.mediaplayer.Adapter.PlayListAdapter
import com.androidrider.mediaplayer.Model.MusicPlaylist
import com.androidrider.mediaplayer.Model.Playlist
import com.androidrider.mediaplayer.R
import com.androidrider.mediaplayer.databinding.AddPlaylistDialogBinding
import com.androidrider.mediaplayer.databinding.FragmentPlayListBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PlayListFragment : Fragment() {

    private lateinit var binding: FragmentPlayListBinding
    private lateinit var playlistAdapter: PlayListAdapter

    companion object{
        var musicPlaylist: MusicPlaylist = MusicPlaylist()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        requireContext().theme.applyStyle(MainActivity.currentTheme[MainActivity.themeIndex], true)

        binding = FragmentPlayListBinding.inflate(layoutInflater)

        binding.playlistRecyclerView.setHasFixedSize(true)
        binding.playlistRecyclerView.setItemViewCacheSize(15)
        playlistAdapter = PlayListAdapter(requireContext(), playlistList = musicPlaylist.ref)
        binding.playlistRecyclerView.adapter = playlistAdapter
        playlistAdapter.notifyDataSetChanged()

        binding.addPlaylist.setOnClickListener { customAlertDialog() }

        return binding.root
    }


    private fun customAlertDialog() {
        val customDialog = LayoutInflater.from(requireContext())
            .inflate(R.layout.add_playlist_dialog, binding.root, false)

        val binder = AddPlaylistDialogBinding.bind(customDialog)
        val dialog = MaterialAlertDialogBuilder(requireContext())
        dialog.setView(customDialog)
            .setTitle("Playlist Details")
            .setPositiveButton("Add") { dialog, _ ->

                val playlistName = binder.playlistName.text
                val createdBy = binder.yourName.text
                if (playlistName != null && createdBy != null)
                    if (playlistName.isNotEmpty() && createdBy.isNotEmpty()) {

                        addPlaylist(playlistName.toString(), createdBy.toString())
                    }
                dialog.dismiss()
            }.show()
    }


    private fun addPlaylist(name: String, createdBy: String) {

        var playlistExist = false
        for (i in musicPlaylist.ref){
            if (name.equals(i.name)){
                playlistExist = true
                break
            }
        }

        if (playlistExist) Toast.makeText(requireContext(), "Playlist Exist!!", Toast.LENGTH_SHORT).show()
        else{
            val tempPlaylist = Playlist()
            tempPlaylist.name = name
            tempPlaylist.playlist = ArrayList()
            tempPlaylist.createdBy = createdBy
            val calendar = Calendar.getInstance().time
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
            tempPlaylist.createdOn = sdf.format(calendar)
            musicPlaylist.ref.add(tempPlaylist)
            playlistAdapter.refreshPlayList()
        }
    }

    override fun onResume() {
        super.onResume()
        playlistAdapter.notifyDataSetChanged()
    }

}