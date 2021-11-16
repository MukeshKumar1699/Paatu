package com.example.paatu

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.paatu.Constants.PERMISSIONS_LIST
import com.example.paatu.databinding.FragmentMyMusicBinding

class MyMusicFragment : Fragment() {

    private lateinit var binding: FragmentMyMusicBinding

    private val sharedPreferences = PreferenceHelper()

    private val viewModelLocalMp3: MusicListViewModel by viewModels {
        MusicListViewModelFactory((activity?.application as AppApplication).musicListRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //observeLiveData()
        binding = FragmentMyMusicBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

    }

    private fun checkPermission(): Boolean {

        for (i in PERMISSIONS_LIST) {

            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    i
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    private fun initViews() {

        binding.incDownload.apply {

            tvTitle.apply {
                text = "Downloads"
            }
            tvSongListSize.apply {
                text = downloadsListSize.toString()
            }
            clItem.setOnClickListener {
            }
        }

        binding.incLocalMp3.apply {

            tvTitle.apply {
                text = "Local Mp3"
            }
            tvSongListSize.apply {
                text = localMp3SongSize.toString()
            }

            clItem.setOnClickListener {
                findNavController().navigate(R.id.action_myMusicFragment_to_musicListFragment)
            }
        }
    }

    private fun observeLiveData() {

        if(checkPermission()) {
            viewModelLocalMp3.musicLiveData.observe(requireActivity(), {
                localMp3SongSize = it.size
            })
        }

    }

    companion object {
        var localMp3SongSize: Int = 0
        var downloadsListSize: Int = 0
    }
}