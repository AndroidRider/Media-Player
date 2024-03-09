package com.androidrider.mediaplayer.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.provider.MediaStore
import android.util.Log
import android.view.SurfaceView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidrider.mediaplayer.Adapter.SongListAdapter
import com.androidrider.mediaplayer.Fragment.AllSongFragment
import com.androidrider.mediaplayer.Fragment.FavoriteSongFragment
import com.androidrider.mediaplayer.Fragment.PlayListFragment
import com.androidrider.mediaplayer.Model.AllSongModel
import com.androidrider.mediaplayer.Model.exitApplication
import com.androidrider.mediaplayer.Model.favoriteChecker
import com.androidrider.mediaplayer.Model.formatDuration
import com.androidrider.mediaplayer.Model.getImageArt
import com.androidrider.mediaplayer.Model.setSongPosition
import com.androidrider.mediaplayer.Utils.MusicService
import com.androidrider.mediaplayer.R
import com.androidrider.mediaplayer.databinding.ActivityPlayerBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.androidrider.mediaplayer.Utils.FontUtils
import com.gauravk.audiovisualizer.visualizer.BarVisualizer


class PlayerActivity : AppCompatActivity(), ServiceConnection, MediaPlayer.OnCompletionListener {

    // Visualizer
    private lateinit var barVisualizer: BarVisualizer


    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: ActivityPlayerBinding
        lateinit var musicListPA: ArrayList<AllSongModel>
        var originalMusicList: List<AllSongModel> = mutableListOf()
        var songPosition: Int = 0
        var isPlaying: Boolean = false
        var musicService: MusicService? = null
        var repeat: Boolean = false
        var shuffle: Boolean = false

        var isFavorite: Boolean = false
        var fIndex: Int = -1
        var nowPlayingId: String = ""
        var min_5: Boolean = false
        var min_10: Boolean = false
        var min_15: Boolean = false
        var min_30: Boolean = false
        var min_45: Boolean = false
        var min_60: Boolean = false

        //RECORD_AUDIO_PERMISSION
        private const val RECORD_AUDIO_PERMISSION_REQUEST_CODE = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        FontUtils.setAppFont(this)
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentTheme[MainActivity.themeIndex])
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Visualizer
        barVisualizer = binding.bar

        setupVisualizer() // VISUALIZER

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbarPlayerActivity)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initializeLayout()

        if (intent.data?.scheme.contentEquals("content")){
            val intentService = Intent(this, MusicService::class.java)
            bindService(intentService, this, BIND_AUTO_CREATE)
            startService(intentService)
            musicListPA = ArrayList()
            musicListPA.add(getMusicDetails(intent.data!!))

            val data = musicListPA[songPosition]
            Glide.with(this).load(getImageArt(data.path)).apply(
                RequestOptions().placeholder(R.drawable.music_logo).centerCrop()
            ).into(binding.musicImage)

            binding.songTitle.text = data.title
        }

        binding.playPause.setOnClickListener {
            if (isPlaying) pauseMusic() else playMusic()
        }

        binding.songTitle.isSelected = true

        binding.fastRewind.setOnClickListener { previousNextSong(increment = false) }

        binding.fastForward.setOnClickListener { previousNextSong(increment = true) }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) musicService!!.mediaPlayer!!.seekTo(progress)

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.repeatButton.setOnClickListener {
            if (!repeat) {
                repeat = true
                binding.repeatButton.setImageResource(R.drawable.player_repeat_one_icon)
//                binding.repeat.setColorFilter(ContextCompat.getColor(this, R.color.mainColor))
            } else {
                repeat = false
                binding.repeatButton.setImageResource(R.drawable.player_repeat_icon)
//                binding.repeat.setColorFilter(ContextCompat.getColor(this, R.color.mainColor))
            }
        }

        binding.shuffleButton.setOnClickListener {
            if (!shuffle) {
                // Save the original order of the list
                if (originalMusicList.isEmpty()) {
                    originalMusicList = musicListPA.toList()
                }
                // Shuffle the music list
                musicListPA.shuffle()
//                binding.shuffleButton.setImageResource(R.drawable.player_repeat_one_icon)
                binding.shuffleButton.setColorFilter(ContextCompat.getColor(this, R.color.red))
                Toast.makeText(this, "Shuffle play on", Toast.LENGTH_SHORT).show()
            } else {
                // Restore the original order of the list
                musicListPA.clear()
                musicListPA.addAll(originalMusicList)
                // Update other UI elements or perform actions when shuffle is disabled
//                binding.shuffleButton.setImageResource(R.drawable.player_shuffle_icon)
                binding.shuffleButton.setColorFilter(ContextCompat.getColor(this, R.color.controlColor))
                Toast.makeText(this, "Shuffle play off", Toast.LENGTH_SHORT).show()
            }
            shuffle = !shuffle
        }

        binding.favoriteButton.setOnClickListener {

            fIndex = favoriteChecker(musicListPA[songPosition].id)
            if (isFavorite){
                isFavorite = false
                binding.favoriteButton.setImageResource(R.drawable.player_favorite_empty_icon)
                FavoriteSongFragment.favoriteSongs.removeAt(fIndex)
            }
            else{
                isFavorite = true
                binding.favoriteButton.setImageResource(R.drawable.player_favorite_filled_icon)
                FavoriteSongFragment.favoriteSongs.add(musicListPA[songPosition])
            }
//            FavoriteSongFragment.favoritesChanged = true
        }

        binding.equalizerButton.setOnClickListener {
            try {
                val eqIntent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
                eqIntent.putExtra(
                    AudioEffect.EXTRA_AUDIO_SESSION,
                    musicService!!.mediaPlayer!!.audioSessionId
                )
                eqIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, baseContext.packageName)
                eqIntent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
                startActivityForResult(eqIntent, 13)
            } catch (e: Exception) {
                Toast.makeText(this, "Equalizer Features not Supported!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.timerButton.setOnClickListener {
            val timer = min_5 || min_10 || min_15 || min_30 || min_45 || min_60
            if (!timer) showBottomSheetDialog()
            else {
                val dialog = MaterialAlertDialogBuilder(this)
                    .setTitle("Stop timer")
                    .setMessage("Do you want to stop timer?")

                    .setPositiveButton("YES") { _, _ ->
                        min_5 = false
                        min_10 = false
                        min_15 = false
                        min_30 = false
                        min_45 = false
                        min_60 = false
                        binding.timerButton.setColorFilter(ContextCompat.getColor(this, R.color.controlColor))
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                val customDialog = dialog.create()
                customDialog.show()
                customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.GREEN)
                customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GREEN)
            }
        }

        binding.shareButton.setOnClickListener {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "audio/*"
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(musicListPA[songPosition].path))
            startActivity(Intent.createChooser(shareIntent, "Sharing Music File!"))
        }

        //RECORD_AUDIO_PERMISSION
        binding.playerList.setOnClickListener {
            showSongListDialog()
        }

        //RECORD_AUDIO_PERMISSION
        checkRecordAudioPermission() // Check and request RECORD_AUDIO permission if needed

    }

    private fun showSongListDialog() {
        val songListDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.dialog_song_list, null)
        songListDialog.setContentView(view)

        // RecyclerView setup
        val recyclerView = view.findViewById<RecyclerView>(R.id.songListRecyclerView)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        val adapter = SongListAdapter(this, musicListPA) { position ->
            // Handle the item click in the list (e.g., play the selected song)
            songListDialog.dismiss()
            songPosition = position
            setLayout()
            createMediaPlayer()
        }
        recyclerView.adapter = adapter

        songListDialog.show()
    }



    /* ======================== OnCreate End ======================== */

    private fun getMusicDetails(contentUri: Uri): AllSongModel {

        var cursor: Cursor? = null
        try {
            val projection = arrayOf(MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DURATION)
            cursor = this.contentResolver.query(contentUri, projection, null, null, null)
            val dataColumn = cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val durationColumn = cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            cursor!!.moveToFirst()
            val path = dataColumn?.let { cursor.getString(it) }
            val duration = durationColumn?.let { cursor.getLong(it) }
            return AllSongModel(id = "unknown", title = path.toString(), album = "unknown", artist = "unknown", duration = duration!!,
                artUri = "unknown", path = path.toString())
        }finally {
            cursor?.close()
        }
    }

    private fun initializeLayout() {

        songPosition = intent.getIntExtra("index", 0)

        when (intent.getStringExtra("class")) {

            "FavoriteAdapter" -> {
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(FavoriteSongFragment.favoriteSongs)
                setLayout()
            }
            "NowPlaying" -> {
                binding.tvStartTime.text =formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
                binding.tvEndTime.text = formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
                binding.seekBar.progress = musicService!!.mediaPlayer!!.currentPosition
                binding.seekBar.max = musicService!!.mediaPlayer!!.duration
                if (isPlaying) binding.playPause.setImageResource(R.drawable.player_pause_icon)
                else binding.playPause.setImageResource(R.drawable.player_play_icon)
                setLayout()
            }
            "MusicAdapterSearch" -> {
                startService()
                musicListPA = ArrayList()
                musicListPA.addAll(AllSongFragment.musicListSearch)
                setLayout()
            }
            "MusicAdapter" -> {
                startService()
                musicListPA = ArrayList()
                musicListPA.addAll(AllSongFragment.musicListMA) // Access the musicListMA using the static method
                setLayout()
            }
            "PlaylistDetailsAdapter" ->{
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(PlayListFragment.musicPlaylist.ref[PlaylistDetailActivity.currentPlaylistPosition].playlist)
                setLayout()
            }
        }
    }

    // For Starting Service
    private fun startService() {
        val intent = Intent(this, MusicService::class.java)
        bindService(intent, this, BIND_AUTO_CREATE)
        startService(intent)
    }

    private fun setLayout() {

        fIndex = favoriteChecker(musicListPA[songPosition].id)

        val data = musicListPA[songPosition]
        Glide.with(this).load(data.artUri).apply(
            RequestOptions().placeholder(R.drawable.music_logo).centerCrop()
        ).into(binding.musicImage)

        binding.songTitle.text = data.title

        if (repeat) binding.repeatButton.setImageResource(R.drawable.player_repeat_one_icon)

//        if (shuffle) binding.shuffleButton.setImageResource(R.drawable.player_repeat_one_icon)

        if (min_5 || min_10 || min_15 || min_30 || min_45 || min_60)
            binding.timerButton.setColorFilter(ContextCompat.getColor(this, R.color.green))

        if (isFavorite) binding.favoriteButton.setImageResource(R.drawable.player_favorite_filled_icon)
        else binding.favoriteButton.setImageResource(R.drawable.player_favorite_empty_icon)
    }

    private fun createMediaPlayer() {

        try {
            if (musicService!!.mediaPlayer == null) musicService!!.mediaPlayer = MediaPlayer()
            musicService!!.mediaPlayer!!.reset()
            musicService!!.mediaPlayer!!.setDataSource(musicListPA[songPosition].path)
            musicService!!.mediaPlayer!!.prepare()
            musicService!!.mediaPlayer!!.start()
            isPlaying = true
            binding.playPause.setImageResource(R.drawable.player_pause_icon)
            musicService!!.showNotification(R.drawable.notification_pause, 1F)

            binding.tvStartTime.text =
                formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
            binding.tvEndTime.text = formatDuration(musicService!!.mediaPlayer!!.duration.toLong())

            binding.seekBar.progress = 0
            binding.seekBar.max = musicService!!.mediaPlayer!!.duration
            musicService!!.mediaPlayer!!.setOnCompletionListener(this)

            nowPlayingId = musicListPA[songPosition].id


        } catch (e: Exception) {
            return
        }
    }

    private fun playMusic() {
        isPlaying = true
        musicService!!.mediaPlayer!!.start()
        musicService!!.showNotification(R.drawable.notification_pause, 1F)
        binding.playPause.setImageResource(R.drawable.player_pause_icon)
    }

    private fun pauseMusic() {
        isPlaying = false
        musicService!!.mediaPlayer!!.pause()
        musicService!!.showNotification(R.drawable.notification_play, 0F)
        binding.playPause.setImageResource(R.drawable.player_play_icon)
    }

//    private fun previousNextSong(increment: Boolean) {
//        if (increment) {
//            setSongPosition(increment = true)
//            setLayout()
//            createMediaPlayer()
//        } else {
//            setSongPosition(increment = false)
//            setLayout()
//            createMediaPlayer()
//        }
//    }

    private fun previousNextSong(increment: Boolean) {
        if (shuffle) {
            // Handle shuffle logic to get a random song position
            songPosition = getRandomSongPosition()
        } else {
            if (increment) {
                setSongPosition(increment = true)
            } else {
                setSongPosition(increment = false)

            }
        }
        setLayout()
        createMediaPlayer()
    }

    // For Shuffle
    private fun getRandomSongPosition(): Int {
        return if (musicListPA.isNotEmpty()) {
            val randomIndex = (0 until musicListPA.size).random()
            randomIndex
        } else {
            // Handle the case when the music list is empty
            0
        }
    }


    private fun showBottomSheetDialog() {
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(R.layout.bottom_sheet_dialog)
        dialog.show()

        dialog.findViewById<LinearLayout>(R.id.min_5)?.setOnClickListener {
            Toast.makeText(
                this@PlayerActivity,
                "Music will stop after 5 minutes",
                Toast.LENGTH_SHORT
            ).show()

            binding.timerButton.setColorFilter(ContextCompat.getColor(this, R.color.green))
            min_5 = true
            Thread {
                Thread.sleep(5 * 60000)
                if (min_5) exitApplication()
            }.start()

            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min_10)?.setOnClickListener {
            Toast.makeText(
                this@PlayerActivity,
                "Music will stop after 10 minutes",
                Toast.LENGTH_SHORT
            ).show()

            binding.timerButton.setColorFilter(ContextCompat.getColor(this, R.color.green))
            min_10 = true
            Thread {
                Thread.sleep(10 * 60000)
                if (min_10) exitApplication()
            }.start()

            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min_15)?.setOnClickListener {
            Toast.makeText(
                this@PlayerActivity,
                "Music will stop after 15 minutes",
                Toast.LENGTH_SHORT
            ).show()

            binding.timerButton.setColorFilter(ContextCompat.getColor(this, R.color.green))
            min_15 = true
            Thread {
                Thread.sleep(15 * 60000)
                if (min_15) exitApplication()
            }.start()

            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min_30)?.setOnClickListener {
            Toast.makeText(
                this@PlayerActivity,
                "Music will stop after 30 minutes",
                Toast.LENGTH_SHORT
            ).show()

            binding.timerButton.setColorFilter(ContextCompat.getColor(this, R.color.green))
            min_30 = true
            Thread {
                Thread.sleep(30 * 60000)
                if (min_30) exitApplication()
            }.start()

            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min_45)?.setOnClickListener {
            Toast.makeText(
                this@PlayerActivity,
                "Music will stop after 45 minutes",
                Toast.LENGTH_SHORT
            ).show()

            binding.timerButton.setColorFilter(ContextCompat.getColor(this, R.color.green))
            min_45 = true
            Thread {
                Thread.sleep(45 * 60000)
                if (min_45) exitApplication()
            }.start()

            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min_60)?.setOnClickListener {
            Toast.makeText(
                this@PlayerActivity,
                "Music will stop after 60 minutes",
                Toast.LENGTH_SHORT
            ).show()

            binding.timerButton.setColorFilter(ContextCompat.getColor(this, R.color.green))
            min_60 = true
            Thread {
                Thread.sleep(60 * 60000)
                if (min_60) exitApplication()
            }.start()

            dialog.dismiss()
        }
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        Log.d("PlayerActivity", "onServiceConnected: Service connected")

        if(musicService == null){
            val binder = service as MusicService.MyBinder
            musicService = binder.currentService()
            musicService!!.audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
            musicService!!.audioManager.requestAudioFocus(musicService, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
        }
        createMediaPlayer()
        musicService!!.seekBarSetup()

        setupVisualizer() // VISUALIZER

    }

    override fun onResume() {
        super.onResume()
        setupVisualizer() // VISUALIZER
    }


    // VISUALIZER
    private fun setupVisualizer(){
        try {
            val mediaPlayer = musicService!!.mediaPlayer
            if (mediaPlayer != null) {
                val audioSessionId = mediaPlayer.audioSessionId
                if (audioSessionId != -1) {
                    barVisualizer.setAudioSessionId(audioSessionId)
                }
            }
        } catch (e: Exception) {
            Log.e("VisualizerSetup", "Error setting up visualizer: ${e.message}")
        }

    }


    override fun onServiceDisconnected(p0: ComponentName?) {
        Log.d("PlayerActivity", "onServiceDisconnected: Service disconnected")
        musicService = null
    }

    override fun onCompletion(mPlayer: MediaPlayer?) {
        setSongPosition(true)
        createMediaPlayer()
        try {
            setLayout()
        } catch (e: Exception) {
            return
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 13 || requestCode == RESULT_OK)
            return
    }

    // Handle the click on the back arrow
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()

        if (musicListPA[songPosition].id == "unknown" && !isPlaying) exitApplication()
    }



    override fun onBackPressed() {
        super.onBackPressed()
        // zoom-in animation
        overridePendingTransition(R.anim.zoom_in, R.anim.static_animation)
        // zoom-out animation
        // overridePendingTransition(R.anim.static_animation, R.anim.zoom_out)
        // for bottom to top animation
//         overridePendingTransition(R.anim.slide_from_bottom, R.anim.slide_to_top)
//         overridePendingTransition(R.anim.slide_to_top, R.anim.slide_from_bottom)


    }




    //RECORD_AUDIO_PERMISSION
    private fun checkRecordAudioPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                RECORD_AUDIO_PERMISSION_REQUEST_CODE
            )
        } else {
            // Permission is already granted
            initializeLayout()
        }
    }

    //RECORD_AUDIO_PERMISSION
    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            RECORD_AUDIO_PERMISSION_REQUEST_CODE -> {
                // Check if the permission is granted
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, initialize layout
                    initializeLayout()
                } else {
                    // Permission denied, handle accordingly (show a message, etc.)
                    Toast.makeText(this, "Recording permission denied", Toast.LENGTH_SHORT).show()
                }
            }
            // Handle other permission requests if needed
            // ...
        }
    }
}