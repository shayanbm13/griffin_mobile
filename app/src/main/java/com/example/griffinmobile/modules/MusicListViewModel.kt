package com.example.griffinmobile.mudels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.griffinmobile.mudels.MusicModel

class MusicListViewModel : ViewModel() {
    private val _musicList_live = MutableLiveData<List<MusicModel>>()
    val musicList_live: LiveData<List<MusicModel>> get() = _musicList_live

    fun setMusicList(newList: List<MusicModel>) {
        _musicList_live.value = newList
    }
}
