package com.example.paatu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ItemViewModel: ViewModel() {

    private val mutableSelectedItem = MutableLiveData<Music>()
    val selectedItem: LiveData<Music> get() = mutableSelectedItem

    fun selectItem(music: Music) {
        mutableSelectedItem.value = music
    }
}