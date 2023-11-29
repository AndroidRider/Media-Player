//package com.androidrider.mediaplayer.Fragment
//
//import android.annotation.SuppressLint
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.net.Uri
//import android.os.Bundle
//import android.provider.MediaStore
//import android.provider.Settings
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Toast
//import androidx.appcompat.app.AlertDialog
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContentProviderCompat
//import com.androidrider.mediaplayer.Adapter.AllSongAdapter
//import com.androidrider.mediaplayer.Model.AllSongModel
//import com.androidrider.mediaplayer.R
//import com.androidrider.mediaplayer.databinding.FragmentAllSongBinding
//import java.io.File
//
//
//class AllSongFragment : Fragment() {
//
//    private lateinit var binding: FragmentAllSongBinding
//
//    companion object {
//        lateinit var musicListMA: ArrayList<AllSongModel>
//
//        fun getMusicList(): ArrayList<AllSongModel> {
//            return musicListMA
//        }
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//
//        binding = FragmentAllSongBinding.inflate(layoutInflater)
//
//        initializeLayout()
//
//        return binding.root
//    }
//
//
//    private fun initializeLayout() {
//        if (hasPermission()) {
//            // Permission is granted, update the UI
//            updateUI()
//        } else {
//            if (shouldShowRationale()) {
//                // Show permission rationale dialog
//                showPermissionRationale()
//            } else {
//                // Request the permission
//                requestPermission()
//            }
//        }
//    }
//
//
//    private fun shouldShowRationale(): Boolean {
//        return shouldShowRequestPermissionRationale(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
//    }
//    private fun hasPermission(): Boolean {
//        return ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
//    }
//    private fun requestPermission() {
//        requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 13)
//    }
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == 13 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            // Permission is granted, update the UI
//            Toast.makeText(requireContext(), "Permission Granted", Toast.LENGTH_SHORT).show()
//            updateUI()
//        }else{
//            Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
//            showPermissionRationale()
//        }
//    }
//
//    private fun showPermissionRationale() {
//        val permissionRationaleDialog = AlertDialog.Builder(requireContext())
//        permissionRationaleDialog.setTitle("Permission Required")
//        permissionRationaleDialog.setMessage(R.string.permissionDialog)
//        permissionRationaleDialog.setCancelable(false)
//        permissionRationaleDialog.setPositiveButton("SETTINGS") { _, _ ->
//            // User has denied permission twice, open app settings
//            openAppSettings()
//        }
//        permissionRationaleDialog.setNegativeButton("CANCEL") { _, _ ->
//            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 13)
//            showPermissionRationale()
//        }
//        permissionRationaleDialog.show()
//    }
//
//    private fun openAppSettings() {
//        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
//        val uri = Uri.fromParts("package", requireActivity().packageName, null)
//        intent.data = uri
//        startActivity(intent)
//    }
//
//
//    private fun updateUI() {
//        // Update the UI with the data or perform any other necessary actions
//        musicListMA = getAllSongFromStorage()
//        binding.allSongRecycleView.setHasFixedSize(true)
//        binding.allSongRecycleView.setItemViewCacheSize(15)
//        val adapter = AllSongAdapter(requireContext(), musicListMA)
//        binding.allSongRecycleView.adapter = adapter
//        adapter.notifyDataSetChanged()
//    }
//
//    @SuppressLint("Recycle", "Range")
//    private fun getAllSongFromStorage(): ArrayList<AllSongModel> {
//
//        val tempList = ArrayList<AllSongModel>()
//        val selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0"
//
//        val projection = arrayOf(
//            MediaStore.Audio.Media._ID,
//            MediaStore.Audio.Media.TITLE,
//            MediaStore.Audio.Media.ALBUM,
//            MediaStore.Audio.Media.ARTIST,
//            MediaStore.Audio.Media.DURATION,
//            MediaStore.Audio.Media.DATE_ADDED,
//            MediaStore.Audio.Media.DATA,
//            MediaStore.Audio.Media.ALBUM_ID
//        )
//        val cursor = requireContext().contentResolver.query(
//            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//            projection, selection, null, MediaStore.Audio.Media.DATE_ADDED + " DESC", null
//        )
//        if (cursor != null) {
//            if (cursor.moveToFirst())
//                do {
//                    val titleC =
//                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
//                    val idC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
//                    val albumC =
//                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
//                    val artistC =
//                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
//                    val pathC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
//                    val durationC =
//                        cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
//                    val albumIdC =
//                        cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
//                            .toString()
//                    val uri = Uri.parse("content://media/external/audio/albumart")
//                    val artUriC = Uri.withAppendedPath(uri, albumIdC).toString()
//
//                    val music = AllSongModel(
//                        id = idC, title = titleC, album = albumC,
//                        artist = artistC, path = pathC, duration = durationC, artUri = artUriC
//                    )
//                    val file = File(music.path)
//                    if (file.exists())
//                        tempList.add(music)
//                } while (cursor.moveToNext())
//            cursor.close()
//        }
//        return tempList
//    }
//
//
//
//
//
//
//
//}


// without foreground permissions
