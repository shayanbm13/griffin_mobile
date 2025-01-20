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


class dashboard_security : Fragment() {

    val SharedViewModel : SharedViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("1")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard_security, container, false)
        println("2")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        println("3")
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


        val arm_button = view.findViewById<Button>(R.id.arm_button)
        val disarm_button = view.findViewById<Button>(R.id.disarm_button)
        val sleep_button = view.findViewById<Button>(R.id.sleep_button)


        val btn_list = listOf<Button>(arm_button,disarm_button,sleep_button)
        for (button in btn_list) {
            button.setButtonScaleOnTouchListener()



        }



    }

    override fun onResume() {
        super.onResume()

        println("security")
        SharedViewModel.update_dash_center_index("2")
    }



}