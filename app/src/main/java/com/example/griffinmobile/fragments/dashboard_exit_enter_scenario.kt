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


class dashboard_exit_enter_scenario : Fragment() {
    lateinit var enter_scenario_button:Button
    lateinit var exit_scenario_button:Button

    val SharedViewModel : SharedViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_dashboard_exit_enter_scenario, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        enter_scenario_button= requireView().findViewById(R.id.enter_scenario_button)
        exit_scenario_button= requireView().findViewById(R.id.exit_scenario_button)

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
        val btn_list = listOf<Button>(enter_scenario_button,exit_scenario_button)
        for (button in btn_list) {
            button.setButtonScaleOnTouchListener()



        }


        

        
    }
    override fun onResume() {
        super.onResume()
        println("ex_en")

        SharedViewModel.update_dash_center_index("1")
    }


}