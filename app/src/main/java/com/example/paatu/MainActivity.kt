package com.example.paatu

import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.paatu.databinding.ActivityMainBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        visibleBottomSheet(false)
        observeLiveData()
        initBottomSheet()
    }

    private fun initPlayPause() {

        Log.d(TAG, "updateUI: playButton CLicked")

        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            binding.incBottomSheetLayout.layMusicCollapsed.ivMusicPlayPause.setImageResource(R.drawable.play_button)
            binding.incBottomSheetLayout.ivMusicPlayPauseExtended.setImageResource(R.drawable.play_button)
        } else {
            mediaPlayer.start()
            binding.incBottomSheetLayout.layMusicCollapsed.ivMusicPlayPause.setImageResource(R.drawable.pause_button)
            binding.incBottomSheetLayout.ivMusicPlayPauseExtended.setImageResource(R.drawable.pause_button)
        }
    }

    private fun observeLiveData() {

        MusicService.musicLiveData.observe(this, {
            it?.let {
                updateUI(it)
                visibleBottomSheet(true)
            }
        })

        MusicService.musicPositionLiveData.observe(this, {
            it?.let {
                updateProgress(it)
            }
        })
    }

    private fun visibleBottomSheet(visibility: Boolean) {

        if(visibility) {
            binding.incBottomSheetLayout.layMusicExpanded.visibility = View.VISIBLE
        }else {
            binding.incBottomSheetLayout.layMusicExpanded.visibility = View.GONE

        }
    }

    private fun initBottomSheet() {
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
                        binding.incBottomSheetLayout.layMusicCollapsed.clCollapsed.visibility =
                            View.GONE
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        binding.botNav.visibility = View.VISIBLE
                        binding.incBottomSheetLayout.layMusicCollapsed.clCollapsed.visibility =
                            View.VISIBLE

                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                        binding.botNav.visibility = View.GONE
                        binding.incBottomSheetLayout.layMusicCollapsed.clCollapsed.visibility =
                            View.GONE

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

    private fun updateUI(music: Music) {

        binding.cooBottomSheet.visibility = View.VISIBLE

        binding.apply {

            mediaPlayer = MusicService.mediaPlayer

            binding.cooBottomSheet.visibility = View.VISIBLE
            binding.incBottomSheetLayout.apply {

                layMusicCollapsed.apply {

                    tvMusicTitle.apply {
                        text = music.name
                    }
                    ivLogo.apply {
                        setImageResource(R.drawable.astronaut)
                    }
                    ivMusicPlayPause.apply {
                        setImageResource(R.drawable.pause_button)
                    }
                    ivMusicPlayPause.apply {

                    }.setOnClickListener {
                        initPlayPause()
                    }
                }

                tvSongTitle.apply {
                    text = music.name
                }
                tvStartTime.apply {
                    text = timeFormat(0)
                }
                tvEndTime.apply {
                    text = timeFormat(music.duration)
                }
                seekDuration.apply {
                    max = music.duration
                }
                ivSkipPrev.apply {

                }.setOnClickListener {

                }

                ivMusicPlayPauseExtended.apply {
                    setImageResource(R.drawable.pause_button)
                }.setOnClickListener {
                    initPlayPause()
                }

                ivSkipNext.apply {

                }.setOnClickListener {

                }

            }

        }

    }

    private fun updateProgress(progress: Int) {
        binding.incBottomSheetLayout.seekDuration.progress = progress
        binding.incBottomSheetLayout.tvStartTime.text = timeFormat(progress)
    }

    private fun timeFormat(time: Int): String {

        if (TimeUnit.MILLISECONDS.toHours(time.toLong()).toInt() == 0) {

            return String.format(
                "%d : %d",
                TimeUnit.MILLISECONDS.toMinutes(time.toLong()),
                TimeUnit.MILLISECONDS.toSeconds(time.toLong()) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time.toLong()))
            )
        } else {
            return String.format(
                "%d : %d : %d",
                TimeUnit.MILLISECONDS.toHours(time.toLong()),
                TimeUnit.MILLISECONDS.toMinutes(time.toLong()),
                TimeUnit.MILLISECONDS.toSeconds(time.toLong()) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time.toLong()))
            )
        }
    }

    private fun foregroundServiceRunning(): Boolean {
        val activityManager: ActivityManager =
            this.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        for (service: ActivityManager.RunningServiceInfo in activityManager.getRunningServices(
            Integer.MAX_VALUE
        )) {

            if (MusicService::class.java.name.equals(service.service.className)) {
                return true
            }
        }
        return false
    }

    private fun findFragmentVisibility(): Boolean {

        if (findNavController(R.id.nav_host_fragment).currentDestination?.id == R.id.homeFragment) {
            Log.d(
                TAG,
                "findFragmentVisibility: " + findNavController(R.id.nav_host_fragment).currentDestination?.id
            )
            return true
        }
        return false
    }

    override fun onBackPressed() {

        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        } else if (findFragmentVisibility() && foregroundServiceRunning()) {

            val alertDialogBuilder = AlertDialog.Builder(this@MainActivity)
            alertDialogBuilder.setMessage("Want to play music in background?")
                .setCancelable(false)
                .setPositiveButton("Yes") { _, _ ->
                    super.onBackPressed()
                }
                .setNegativeButton("No") { _, _ ->
                    val intent = Intent(this, MusicService::class.java)
                    stopService(intent)
                    super.onBackPressed()
                }

            val alert = alertDialogBuilder.create()
            alert.show()

        } else {
            super.onBackPressed()
        }
    }

    companion object {
        private const val TAG = "MainActivity"
        private lateinit var mediaPlayer: MediaPlayer
    }

}