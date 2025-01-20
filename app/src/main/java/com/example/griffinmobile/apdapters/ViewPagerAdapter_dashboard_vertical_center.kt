package com.example.griffinmobile.apdapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.griffinmobile.fragments.dashboard_exit_enter_scenario
import com.example.griffinmobile.fragments.dashboard_music
import com.example.griffinmobile.fragments.dashboard_security

class ViewPagerAdapter_dashboard_vertical_center(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 3 // تعداد فرگمنت‌ها

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> dashboard_exit_enter_scenario()
            1 -> dashboard_security()
            2 -> dashboard_music()
            else -> throw IllegalStateException("Invalid position")
        }
    }
}