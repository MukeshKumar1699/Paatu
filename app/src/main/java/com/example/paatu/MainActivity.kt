package com.example.paatu

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.example.paatu.databinding.ActivityMainBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: ItemViewModel by viewModels()
    private lateinit var bottomSheetBehavior : BottomSheetBehavior<ConstraintLayout>


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getIntentData()
        viewModel.selectedItem.observe(this, { music ->

            if(music != null) {

                if(foregroundServiceRunning()) {
                    val intent = Intent(this@MainActivity, MusicService::class.java)
                    stopService(intent)
                }
                val intent = Intent(this@MainActivity, MusicService::class.java)

                intent.putExtra("SongUri", music.songURI.toString())
                intent.putExtra("SongName", music.name)
                intent.setAction("PLAY")
                isPlaying = true
                startService(intent)

                binding.apply {

                    binding.incBottomSheetLayout.apply {
                        setVisible(true)
                        layMusicCollapsed.tvMusicTitle.text = music.name
                        tvSongTitle.text = music.name

                        tvEndTime.text = (music.duration / 1000).toString()

                        layMusicCollapsed.ivMusicPlayPause.setOnClickListener {

                            if(isPlaying) {

                                val pauseIntent = Intent(this@MainActivity, MusicService::class.java)
                                intent.setAction("PAUSE")
                                startService(pauseIntent)
                                isPlaying = false
                            }
                        }

                    }

                }
            }

        })

         bottomSheetBehavior =
            BottomSheetBehavior.from(binding.incBottomSheetLayout.layMusicExpanded).apply {
                this.isDraggable = true

            }
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, state: Int) {
                print(state)
                when (state) {

                    BottomSheetBehavior.STATE_HIDDEN -> {
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        binding.botNav.visibility = View.GONE
                        binding.incBottomSheetLayout.layMusicCollapsed.clCollapsed.visibility = View.GONE
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        binding.botNav.visibility = View.VISIBLE
                        binding.incBottomSheetLayout.layMusicCollapsed.clCollapsed.visibility = View.VISIBLE

                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                        binding.botNav.visibility = View.GONE
                        binding.incBottomSheetLayout.layMusicCollapsed.clCollapsed.visibility = View.GONE

                    }
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {

                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })

        binding.incBottomSheetLayout.layMusicExpanded.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        val navController = Navigation.findNavController(this@MainActivity, R.id.nav_host_fragment)
        binding.botNav.setupWithNavController(navController)

    }

    override fun onBackPressed() {

        if(bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        else {
            super.onBackPressed()
        }
    }

    fun foregroundServiceRunning(): Boolean {
        val activityManager: ActivityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        for (service: ActivityManager.RunningServiceInfo in activityManager.getRunningServices(Integer.MAX_VALUE)) {

            if(MusicService::class.java.name.equals(service.service.className)) {
                return true
            }
        }
        return false
    }

    fun getIntentData() {
        var progress = intent.getStringExtra("MediaPlayerProgress")
        if (progress != null) {
            binding.incBottomSheetLayout.seekDuration.progress = progress.toInt()
        }
    }


    companion object {
        var isPlaying: Boolean = false
    }

}