package com.example.paatu

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.paatu.Constants.PERMISSIONS_LIST
import com.example.paatu.Constants.REQ_MULTIPLE_PERMISSIONS_CODE
import com.example.paatu.databinding.FragmentMusicListBinding

class MusicListFragment : Fragment(), MusicItemClickListener {

    private lateinit var binding: FragmentMusicListBinding

    private val musicList: ArrayList<Music> = ArrayList()

    private val APPPERMISSIONSTATUS = "APPPERMISSIONSTATUS"
    private val sharedPreferences = PreferenceHelper()

    private val viewModel: MusicListViewModel by viewModels {
        MusicListViewModelFactory((activity?.application as AppApplication).musicListRepository)
    }

    private lateinit var musicAdapter: MusicAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentMusicListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getAppPermission()
        setRecycler()
        observeLiveData()
        initViews()
    }

    private fun getAppPermission() {

        sharedPreferences.getSharedPreference(requireContext())

        val isPermissionGranted = checkPermission()

        if (isPermissionGranted) {
            sharedPreferences.writeAppPermissionStatusToPreference(
                requireContext(),
                APPPERMISSIONSTATUS,
                true
            )
            getMusicFromContentProvider()

        } else if (!isPermissionGranted) {
            sharedPreferences.writeAppPermissionStatusToPreference(
                requireContext(),
                APPPERMISSIONSTATUS,
                false
            )
            requestPermissions(
                PERMISSIONS_LIST,
                REQ_MULTIPLE_PERMISSIONS_CODE
            )
        }

    }

    private fun checkPermission(): Boolean {

        for (i in PERMISSIONS_LIST) {

            if (checkSelfPermission(
                    requireContext(),
                    i
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQ_MULTIPLE_PERMISSIONS_CODE) {

            var isGranted = true
            for (i in grantResults) {
                if (i == PackageManager.PERMISSION_DENIED) {
                    isGranted = false
                }
            }

            if (isGranted) {
                sharedPreferences.writeAppPermissionStatusToPreference(
                    requireContext(),
                    APPPERMISSIONSTATUS,
                    true
                )
                getMusicFromContentProvider()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Read Storage Permission Required",
                    Toast.LENGTH_SHORT
                ).show()
                requireActivity().finish()
            }
        }
    }

    private fun getMusicFromContentProvider() {
        viewModel.getSongsList(requireActivity().contentResolver)
    }

    private fun initViews() {

        binding.apply {
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            ivSearch.setOnClickListener {
                findNavController().navigate(R.id.action_musicListFragment_to_searchFragment)
            }

            tvMusicOption.apply {
                if (!musicList.isEmpty()) {
                    text = musicList[musicList.lastIndex].name
                }
            }

            tvSongListSize.apply {
                text = musicList.size.toString()
            }

            btnPlayAll.setOnClickListener {
                startMusicService(0, Constants.PLAY_ALL_MUSIC)
            }
        }
    }

    private fun setRecycler() {

        musicAdapter = MusicAdapter(musicList, this)

        binding.rcMusics.apply {
            this.layoutManager = LinearLayoutManager(requireContext())
            this.adapter = musicAdapter
        }
    }

    private fun updateRecyclerView() {

        musicAdapter.updateData(musicList)
    }

    private fun observeLiveData() {

        viewModel.musicLiveData.observe(requireActivity(), {
            musicList.clear()
            musicList.addAll(it)
            updateRecyclerView()
        })
    }

    /*
    fun sortAlphabetically(arrayList: ArrayList<Music>): ArrayList<Music> {
        var returnList: ArrayList<Music> = arrayListOf()
        var list = arrayList as MutableList<Music>
        list.sortWith(Comparator { o1: Music, o2: Music ->
            o1.song.compareTo(o2.song)
        })
        returnList = list as ArrayList<Music>
        return returnList
    }*/

    override fun onItemClicked(position: Int) {
        startMusicService(position, Constants.PLAY_MUSIC)
    }

    private fun startMusicService(item: Int, action: String) {

        if (musicList.size != 0) {

            if (foregroundServiceRunning()) {
                val intent = Intent(requireContext(), MusicService::class.java)
                requireContext().stopService(intent)
            }
            val intent = Intent(requireContext(), MusicService::class.java)

            when (action) {

                Constants.PLAY_MUSIC -> {
                    intent.putParcelableArrayListExtra("SongList", musicList)
                    intent.putExtra("ItemPosition", item)
                    intent.setAction(Constants.PLAY_MUSIC)
                }

                Constants.PLAY_ALL_MUSIC -> {
                    intent.putParcelableArrayListExtra("SongList", musicList)
                    intent.setAction(Constants.PLAY_ALL_MUSIC)
                }
            }
            requireContext().startService(intent)
        }

    }

    fun foregroundServiceRunning(): Boolean {
        val activityManager: ActivityManager =
            requireActivity().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        for (service: ActivityManager.RunningServiceInfo in activityManager.getRunningServices(
            Integer.MAX_VALUE
        )) {

            if (MusicService::class.java.name.equals(service.service.className)) {
                return true
            }
        }
        return false
    }

}