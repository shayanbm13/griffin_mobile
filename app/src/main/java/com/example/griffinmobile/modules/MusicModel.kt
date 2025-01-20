package com.example.griffinmobile.mudels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class MusicModel(
    val title: String,
    val artist: String,
    val imageUrl: String,
    val audioUrl: String,
    var isplaying: String
)

var musicList = mutableListOf<MusicModel>()
var musicList2 = mutableListOf<MusicModel>()

