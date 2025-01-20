package com.example.griffinmobile.apdapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.griffinmobile.R
import com.example.griffinmobile.apdapters.ScenarioAdapter
import com.example.griffinmobile.database.scenario_db
//import com.example.griffinmobile.griffin_home

import com.example.griffinmobile.mudels.MusicModel
import com.example.griffinmobile.mudels.Music_player
import com.example.griffinmobile.mudels.SharedViewModel
import com.example.griffinmobile.mudels.scenario

class MusicAdapter private constructor(private val context: Context) : RecyclerView.Adapter<MusicAdapter.ViewHolder>() {

    companion object {
        private var instance: MusicAdapter? = null

        fun getInstance(context: Context): MusicAdapter {
            return instance ?: synchronized(this) {
                instance ?: MusicAdapter(context).also { instance = it }
            }
        }
    }

    var musicList= com.example.griffinmobile.mudels.musicList


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_music, parent, false)
        val textTitle=view.findViewById<TextView>(R.id.textTitle)
        val textArtist=view.findViewById<TextView>(R.id.textArtist)
        textTitle.setSelected(true)
        textArtist.setSelected(true)
        return ViewHolder(view)
    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateAdapterValue(newValue: MutableList<MusicModel>) {
        musicList = newValue
        notifyDataSetChanged()
        println(musicList)
        println("changed")

    }

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val musicInfo = musicList[position]

        // Access properties of MusicInfo directly
        val songName = musicInfo.title
        val artistName = musicInfo.artist

        // Display song and artist names
        holder.textSongName.text = songName
        holder.textArtistName.text = artistName

        // Set visibility based on the stored state in MusicModel
        val drawable = context.resources.getDrawable(R.drawable.music_wave, null)


        println(musicInfo.isplaying)
        when(musicInfo.isplaying){
            "true"->{
                println("truuuuuuuuuuuuuuuuuuuuuuu")
                holder.wave_gif.visibility=View.VISIBLE
                holder.wave_gif.setImageResource(R.drawable.music_wave)
//                Glide.with(context).(drawable).into(holder.wave_gif)
            }
            "false"->{
                holder.wave_gif.visibility=View.GONE
            }
            "pause"->{
                holder.wave_gif.visibility=View.VISIBLE
                holder.wave_gif.setImageDrawable(drawable)
            }

        }
//        if (musicInfo.isplaying=="true"){
//            holder.wave_gif.visibility=View.VISIBLE
//            Glide.with(context).load(drawable).into(holder.wave_gif)
//
//
//
//
//        }else if(musicInfo.isplaying=="false") {
//            holder.wave_gif.visibility=View.GONE
//
//        }else if(musicInfo.isplaying=="pause") {
//            holder.wave_gif.visibility=View.VISIBLE
//            holder.wave_gif.setImageDrawable(drawable)
//
//        }





        // Add code for playing music on item click
        holder.itemView.setOnClickListener {
            hideAllWaveGifs()
            musicInfo.isplaying = "true"
//            for (item in musicList){

            val index = com.example.griffinmobile.mudels.musicList.indexOf(musicInfo)
            var old_list = com.example.griffinmobile.mudels.musicList

            old_list[index] = musicInfo

            com.example.griffinmobile.mudels.musicList= old_list
            val musicPlayer = Music_player.getInstance(context)
            musicPlayer.playMusic(musicInfo.audioUrl)
            com.example.griffinmobile.mudels.musicList=old_list


            println(com.example.griffinmobile.mudels.musicList)

            notifyDataSetChanged()
            updateAdapterValue(com.example.griffinmobile.mudels.musicList)

        }

        val inflater4 = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val customPopupView4: View = inflater4.inflate(R.layout.add_music_to_scenario_popup, null)



        val popupWidth4 = 650
        val popupHeight4 = 650
        val popupWindow4 = PopupWindow(customPopupView4, popupWidth4, popupHeight4, true)
        popupWindow4.isFocusable = true

        val recyclerView: RecyclerView = customPopupView4.findViewById(R.id.scenario_add_music_recycler_view)
        val cancel_btn: Button = customPopupView4.findViewById(R.id.cancel)
        val layoutManager = GridLayoutManager(context, 3) // تعداد ستون‌ها را 3 قرار دهید
        recyclerView.layoutManager = layoutManager


        cancel_btn.setOnClickListener {
            popupWindow4.dismiss()
        }


        holder.add_to_scenario_menu.setOnClickListener {
//            println(scenario_side)
            val db_handler = scenario_db.Scenario_db.getInstance(context)
            var newlist = db_handler.getAllScenario()
            val adapter = ScenarioAdapter(newlist) { selectedItem ->
                println(selectedItem.scenario_name)
                var current_music= selectedItem.music
                if (current_music != ""){
                    current_music += ",${musicInfo.audioUrl}"

                }else{
                    current_music = musicInfo.audioUrl

                }
                db_handler.updatemusicById(selectedItem.id,current_music)
                popupWindow4.dismiss()
                Toast.makeText(context, "Added to ${selectedItem.scenario_name}", Toast.LENGTH_SHORT).show()


            }

            recyclerView.adapter = adapter
            adapter.setItems(newlist)

            val popupMenu = PopupMenu(context, holder.add_to_scenario_menu)
            popupMenu.gravity = Gravity.TOP or Gravity.END
            popupMenu.menuInflater.inflate(R.menu.add_music_scenario, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->


                when(menuItem.itemId){
                    R.id.add_scenario -> {
                        popupWindow4.showAtLocation(holder.itemView, Gravity.CENTER, 0, 0)

                    }




                }
                true

            }
            popupMenu.show()

        }

    } @SuppressLint("NotifyDataSetChanged")


    override fun getItemCount(): Int {
        return musicList.size
    }

    private fun hideAllWaveGifs() {

        var need_change = mutableListOf<MusicModel>()
        var new_list= com.example.griffinmobile.mudels.musicList
        for (music in musicList){
            if (music.isplaying =="true"){
                music.isplaying="false"
                new_list[musicList.indexOf(music)]=music

            }

        }
        com.example.griffinmobile.mudels.musicList= new_list as MutableList<MusicModel>
        // Set isplaying to false for all items in the list
        for (musicInfo in musicList) {
            musicInfo.isplaying = "false"
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textSongName: TextView = itemView.findViewById(R.id.textTitle)
        val textArtistName: TextView = itemView.findViewById(R.id.textArtist)
        val add_to_scenario_menu: ImageView = itemView.findViewById(R.id.add_to_scenario_menu)
        val wave_gif: pl.droidsonroids.gif.GifImageView = itemView.findViewById(R.id.wave_gif)
    }
}