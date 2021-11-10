package com.example.paatu

import android.Manifest
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
import com.example.paatu.databinding.FragmentMusicListBinding

import android.app.Activity
import androidx.fragment.app.activityViewModels
import java.lang.ClassCastException


class MusicListFragment : Fragment(), MusicItemClickListener {

    private lateinit var binding: FragmentMusicListBinding

    private val musicList: ArrayList<Music> = ArrayList()

    private val viewModel: MusicListViewModel by viewModels {
        MusicListViewModelFactory((activity?.application as AppApplication).musicListRepository)
    }

    private val sharedViewModel: ItemViewModel by activityViewModels()

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

        val isPermissionGranted = checkPermission()

        if (isPermissionGranted) {
            getMusicFromContentProvider()

        } else if (!isPermissionGranted) {
            requestPermissions(
                permissionList,
                REQ_MULTIPLE_PERMISSIONS_CODE
            )
        }

    }

    private fun checkPermission(): Boolean {

        for (i in permissionList) {

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

            tvMusicOption.apply {
                if (!musicList.isEmpty()) {
                    text = musicList[musicList.lastIndex].name
                }
            }
            tvSongListSize.apply {
                text = musicList.size.toString()
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

    override fun onItemClicked(position: Int, music: Music) {
        Toast.makeText(requireContext(), "CLicked", Toast.LENGTH_SHORT).show()
        sharedViewModel.selectItem(music)
    }

    override fun onPlayAllClicked(musicList: List<Music>) {
    }

    companion object {

        private val permissionList = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
        )

        private const val REQ_MULTIPLE_PERMISSIONS_CODE = 100
    }

}