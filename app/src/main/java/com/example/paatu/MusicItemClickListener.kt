package com.example.paatu

interface MusicItemClickListener {

    fun onItemClicked(position: Int, music: Music)

    fun onPlayAllClicked(musicList: List<Music>)
}