package com.example.griffinmobile.apdapters
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.griffinmobile.fragments.dash_fragment
import com.example.griffinmobile.fragments.room_edit
import com.example.griffinmobile.fragments.room_fragment
import com.example.griffinmobile.fragments.scenario_fragment

class room_edit_view_pager(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 2 // تعداد فرگمنت‌ها

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 ->room_edit()
            1 -> dash_fragment()

            else -> throw IllegalStateException("Invalid position")
        }
    }
}