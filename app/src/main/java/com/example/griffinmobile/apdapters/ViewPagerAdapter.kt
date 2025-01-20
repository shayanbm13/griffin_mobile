package com.example.griffinmobile.apdapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.griffinmobile.fragments.dash_fragment
import com.example.griffinmobile.fragments.room_fragment
import com.example.griffinmobile.fragments.scenario_fragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 3 // تعداد فرگمنت‌ها

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> scenario_fragment()
            1 -> dash_fragment()
            2 -> room_fragment()
            else -> throw IllegalStateException("Invalid position")
        }
    }
}