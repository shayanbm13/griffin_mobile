package com.example.griffinmobile

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.TypedValue
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.example.griffinmobile.apdapters.ViewPagerAdapter
import com.example.griffinmobile.apdapters.ViewPagerAdapter_dashboard_vertical_center
import com.example.griffinmobile.database.home_db
import com.example.griffinmobile.database.setting_network_db
import com.example.griffinmobile.modules.home
import com.example.griffinmobile.mudels.SharedViewModel
import com.example.griffinmobile.mudels.network_manual
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.qamar.curvedbottomnaviagtion.CurvedBottomNavigation

class MainActivity : AppCompatActivity() {
    val SharedviewModel :SharedViewModel by viewModels()

    override fun attachBaseContext(newBase: Context?) {
        val config = Configuration(newBase?.resources?.configuration)
        config.fontScale = 1.0f // ثابت کردن اندازه فونت
        val customContext = newBase?.createConfigurationContext(config)
        super.attachBaseContext(customContext)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val homeDb= home_db.getInstance(this)
        var home_count = homeDb.getHomeCount()
        if (home_count==0){
            var home1=home()
            home1.home_name="home"
            home1.tag="h"
            home1.location="_,_"
            home1.current_select="true"
            homeDb.set_to_db_home(home1)

        }



        var side ="home"
        val bottom_navigation =findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val scenario_side =findViewById<LinearLayout>(R.id.scenario_side)
        val home_side =findViewById<LinearLayout>(R.id.home_side)
        val room_side =findViewById<LinearLayout>(R.id.room_side)



        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        viewPager.adapter = ViewPagerAdapter(this)
        viewPager.setCurrentItem(1)
        viewPager.isUserInputEnabled = false




        scenario_side.setOnClickListener{
            bottom_navigation.setBackgroundResource(R.drawable.scenario_menu)
        viewPager.setCurrentItem(0)
            side="scenario"
        }

        home_side.setOnClickListener{
            bottom_navigation.setBackgroundResource(R.drawable.dash_menu)
            side="home"
            viewPager.setCurrentItem(1)
        }

        room_side.setOnClickListener{
            bottom_navigation.setBackgroundResource(R.drawable.dash_test_2)
            viewPager.setCurrentItem(2)
            side="room"
        }






    }

    override fun onResume() {
        super.onResume()




    }

}