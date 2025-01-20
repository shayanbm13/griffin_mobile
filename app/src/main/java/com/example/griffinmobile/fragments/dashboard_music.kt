package com.example.griffinmobile.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.activityViewModels
import com.example.griffinmobile.R
import com.example.griffinmobile.mudels.SharedViewModel


class dashboard_music : Fragment() {
    val SharedViewModel : SharedViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard_music, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



    }
    override fun onResume() {
        super.onResume()

        println("music")
        SharedViewModel.update_dash_center_index("3")

        fun View.setButtonScaleOnTouchListener() {
            setOnTouchListener(fun(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        v.animate().scaleX(0.8f).scaleY(0.8f).setDuration(80).start()
//                        SoundManager.playSound()
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        v.animate().scaleX(1f).scaleY(1f).setDuration(80).start()
                    }
                }
                return false
            })
        }


        val privius_music = requireView().findViewById<Button>(R.id.privius_music)
        val play_puse = requireView().findViewById<Button>(R.id.play_puse)
        val next_music = requireView().findViewById<Button>(R.id.next_music)


        val btn_list = listOf<Button>(privius_music,play_puse,next_music)
        for (button in btn_list) {
            button.setButtonScaleOnTouchListener()



        }


    }

}