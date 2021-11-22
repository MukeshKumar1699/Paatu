package com.example.paatu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.paatu.databinding.FragmentSearchBinding

class SearchFragment : Fragment(), MusicItemClickListener {

    private lateinit var binding: FragmentSearchBinding

    private val musicList: ArrayList<Music> = ArrayList()

    private val viewModel: MusicListViewModel by viewModels {
        MusicListViewModelFactory((activity?.application as AppApplication).musicListRepository)
    }

    private lateinit var musicAdapter: MusicAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRecycler()
        observeLiveData()
        initViews()

    }

    private fun initViews() {

        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.svSearchMusic.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                newText?.let {
                    if(newText.length != 0) {
                        searchSong(newText)
                    }
                    else{
                        updateRecyclerView(musicList)
                    }
                }
                return false
            }

        })
    }

    private fun searchSong(searchKey: String?) {

        val searchList = ArrayList<Music>()
        for(i in musicList) {
            if(searchKey?.let { i.name.contains(it) } == true) {
                searchList.add(i)
            }
        }
        updateRecyclerView(searchList)
    }

    private fun setRecycler() {

        musicAdapter = MusicAdapter(musicList, this)

        binding.rcSearch.apply {
            this.layoutManager = LinearLayoutManager(requireContext())
            this.adapter = musicAdapter
        }
    }

    private fun observeLiveData() {

        viewModel.musicLiveData.observe(requireActivity(), {
            musicList.clear()
            musicList.addAll(it)
            updateRecyclerView(musicList)
        })
    }

    private fun updateRecyclerView(musicList: ArrayList<Music>) {

        musicAdapter.updateData(musicList)
    }

    override fun onItemClicked(position: Int) {
    }


}