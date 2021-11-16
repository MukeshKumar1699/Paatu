package com.example.paatu

import android.content.ContentResolver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MusicListViewModel(private val repository: MusicListRepository) : ViewModel() {

     lateinit var musicLiveData: LiveData<List<Music>>

    fun getSongsList(contentResolver: ContentResolver) {
       musicLiveData = repository.getSongsFromContentProvider(contentResolver)
    }
}

class MusicListViewModelFactory(private val repository: MusicListRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(MusicListViewModel::class.java)) {
            return MusicListViewModel(repository = repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")

    }

}