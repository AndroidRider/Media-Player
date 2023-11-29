
package com.androidrider.mediaplayer.Fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import com.androidrider.mediaplayer.Activity.MainActivity
import com.androidrider.mediaplayer.Activity.MainActivity.Companion.sortOrder
import com.androidrider.mediaplayer.Adapter.AllSongAdapter
import com.androidrider.mediaplayer.Model.AllSongModel
import com.androidrider.mediaplayer.R
import com.androidrider.mediaplayer.databinding.FragmentAllSongBinding
import java.io.File
import java.util.Locale

class AllSongFragment : Fragment() {

    private lateinit var binding: FragmentAllSongBinding

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var allSongAdapter: AllSongAdapter

        lateinit var musicListMA: ArrayList<AllSongModel>
        lateinit var musicListSearch: ArrayList<AllSongModel>
        var search : Boolean = false

        //************************ test ************
        lateinit var instance: AllSongFragment
            private set

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        requireContext().theme.applyStyle(MainActivity.currentTheme[MainActivity.themeIndex], true)
        // Inflate the layout for this fragment

        binding = FragmentAllSongBinding.inflate(layoutInflater)

        setHasOptionsMenu(true) // This line is important to indicate that the fragment has options menu items.

        initializeLayout()

        return binding.root
    }

    //************************ test ************
    override fun onAttach(context: Context) {
        super.onAttach(context)
        instance = this
    }

    private fun initializeLayout() {
        if (hasBothPermissions()) {
            // Both permissions are granted, update the UI
            updateUI()
        } else {
            val deniedPermissions = getDeniedPermissions()
            if (shouldShowRationale(deniedPermissions)) {
                // Show permission rationale dialog
                showPermissionRationale(deniedPermissions)
            } else {
                // Request the permissions
                requestPermissions(deniedPermissions.toTypedArray(), 13)
            }
        }
    }

    private fun shouldShowRationale(deniedPermissions: List<String>): Boolean {
        for (permission in deniedPermissions) {
            if (!shouldShowRequestPermissionRationale(permission)) {
                return false
            }
        }
        return true
    }

    private fun hasBothPermissions(): Boolean {
        return hasPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                hasPermission(Manifest.permission.FOREGROUND_SERVICE)
    }

    private fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun getDeniedPermissions(): List<String> {
        val deniedPermissions = mutableListOf<String>()
        if (!hasPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            deniedPermissions.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (!hasPermission(Manifest.permission.FOREGROUND_SERVICE)) {
            deniedPermissions.add(Manifest.permission.FOREGROUND_SERVICE)
        }
        return deniedPermissions
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 13) {
            val deniedPermissions = getDeniedPermissions()
            if (deniedPermissions.isEmpty()) {
                // Both permissions are granted, update the UI
                Toast.makeText(requireContext(), "Permissions Granted", Toast.LENGTH_SHORT).show()
                updateUI()
            } else {
                // Permissions denied, handle it as needed
                Toast.makeText(requireContext(), "Permissions Denied", Toast.LENGTH_SHORT).show()
                showPermissionRationale(deniedPermissions)
            }
        }
    }

    private fun showPermissionRationale(deniedPermissions: List<String>) {
        val permissionRationaleDialog = AlertDialog.Builder(requireContext())
        permissionRationaleDialog.setTitle("Permissions Required")
        permissionRationaleDialog.setMessage(R.string.permissionDialog)
        permissionRationaleDialog.setCancelable(false)
        permissionRationaleDialog.setPositiveButton("SETTINGS") { _, _ ->
            // User has denied permissions, open app settings
            openAppSettings()
        }
        permissionRationaleDialog.setNegativeButton("CANCEL") { _, _ ->
            // Request the denied permissions again
            requestPermissions(deniedPermissions.toTypedArray(), 13)

            showPermissionRationale(deniedPermissions)
        }
        permissionRationaleDialog.show()
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", requireActivity().packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    private fun updateUI() {
        // Update the UI with the data or perform any other necessary actions
        search = false
        val sortEditor = requireContext().getSharedPreferences("SORTING", MODE_PRIVATE)
        sortOrder = sortEditor.getInt("sortOrder", 0)
        musicListMA = getAllSongFromStorage()
        binding.allSongRecycleView.setHasFixedSize(true)
        binding.allSongRecycleView.setItemViewCacheSize(15)
        allSongAdapter = AllSongAdapter(requireContext(), musicListMA)
        binding.allSongRecycleView.adapter = allSongAdapter
        allSongAdapter.notifyDataSetChanged()
    }

    @SuppressLint("Recycle", "Range")
    fun getAllSongFromStorage(): ArrayList<AllSongModel> {
        val tempList = ArrayList<AllSongModel>()
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0"

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID
        )
        val cursor = requireContext().contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection, selection, null, MainActivity.sortingList[MainActivity.sortOrder], null
        )
//        MediaStore.Audio.Media.DATE_ADDED + " DESC"
        if (cursor != null) {
            if (cursor.moveToFirst())
                do {
                    val titleC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    val idC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                    val albumC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                    val artistC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    val pathC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val durationC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val albumIdC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)).toString()
                    val uri = Uri.parse("content://media/external/audio/albumart")
                    val artUriC = Uri.withAppendedPath(uri, albumIdC).toString()

                    val music = AllSongModel(
                        id = idC, title = titleC, album = albumC,
                        artist = artistC, path = pathC, duration = durationC, artUri = artUriC
                    )
                    val file = File(music.path)
                    if (file.exists())
                        tempList.add(music)
                } while (cursor.moveToNext())
            cursor.close()
        }
        return tempList
    }



    //Song Search Code
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.search_view, menu)
        val item = menu.findItem(R.id.searchView)
        val searchView = item.actionView as SearchView
        searchView.queryHint = "Search notes here..."

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean = true
            override fun onQueryTextChange(newText: String?): Boolean {
                musicListSearch = ArrayList()
                if (newText != null){
                    val userInput = newText.lowercase()
                    for (song in musicListMA)
                        if (song.title.lowercase().contains(userInput))
                            musicListSearch.add(song)
                    search = true
                    allSongAdapter.updateMusicList(searchList = musicListSearch)
                }
                return true
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }



}
