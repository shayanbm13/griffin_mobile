package com.example.griffinmobile

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.griffinmobile.fragments.room_edit

class rooms : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_rooms)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        this.onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Define custom behavior for back press in this fragment
                if (shouldHandleBack()) {
                    // Do something like navigating back in the fragment
                } else {
                    // Let the system handle it or finish the fragment
                    isEnabled = false
                    this@rooms.onBackPressed()
                }
            }
        })



    }


    override fun onResume() {
        super.onResume()

        supportFragmentManager.beginTransaction()
            .replace(R.id.main ,room_edit() ).commit()



    }
    private fun shouldHandleBack(): Boolean {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.main)
        var r : Boolean
        r = false
        if (currentFragment != null) {
            when (currentFragment) {
                is room_edit -> {
                    // YourFragment is currently displayed
                    r = false
                }

                else -> {
                    r= true
                    // Some other fragment is displayed
                }
            }
        }

        return r
    }
}