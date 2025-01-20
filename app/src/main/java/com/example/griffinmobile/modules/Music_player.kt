package com.example.griffinmobile.mudels

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import com.example.griffinmobile.apdapters.MusicAdapter

class Music_player private constructor(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null


    init {
        // این قسمت برای اولین بار اجرا می‌شود
        // اینجا می‌توانید تنظیمات اولیه یا مقداردهی اولیه انجام دهید
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: Music_player? = null

        // تابعی برای دریافت یک نمونه از MusicPlayer
        fun getInstance(context: Context): Music_player {
            return instance ?: synchronized(this) {
                // اگر نمونه وجود نداشته باشد، یک نمونه ایجاد می‌شود
                instance ?: Music_player(context).also { instance = it }
            }
        }
    }

    fun playMusic(path: String) {
        stopMusic()
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            setDataSource(context, Uri.parse(path))
            prepare()
            start()
            setOnCompletionListener {
                val musicPlayer = Music_player.getInstance(context)

                var index= -1

                for ( item in musicList){
                    if (item.isplaying=="true"){
                        index= musicList.indexOf(item)
//                    play_target=item.audioUrl
                    }

                }
                if (index == -1){


                }else{

                    if ((index+1) >= musicList.count()){


//                        musicPlayer.playMusic(musicList[0].audioUrl)
                        var new_list= musicList

                        var current=new_list[index]
                        current.isplaying="false"
                        new_list[index]=current


//                        var new_status_music=new_list[0]
//                        new_status_music.isplaying=true
//                        new_list[0]= new_status_music
//                        musicList=new_list
                    }else if ((index + 1) < musicList.count()){
                        musicPlayer.playMusic(musicList[(index + 1)].audioUrl)
                        var new_list= musicList
                        var current=new_list[index]
                        current.isplaying="false"
                        new_list[index]=current

                        var new_status_music=new_list[(index + 1 )]
                        new_status_music.isplaying="true"
                        new_list[(index + 1 )]= new_status_music
                        musicList=new_list
                    }
                }
                val adapter = MusicAdapter.getInstance(context)
                adapter.updateAdapterValue(musicList)
            }
        }
    }

    fun pauseMusic() {
        mediaPlayer?.pause()
    }

    fun resumeMusic() {
        mediaPlayer?.start()
    }

    fun stopMusic() {
        mediaPlayer?.release()
         
    }

    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }
    fun getCurrentPosition(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }
    fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
    }
}