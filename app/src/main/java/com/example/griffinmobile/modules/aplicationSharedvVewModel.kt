package com.example.griffinmobile.mudels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class aplicationSharedvVewModel(application: Application) : AndroidViewModel(application) {
    // MutableLiveData یا داده‌های مورد نیاز خود را اینجا تعریف کنید
    private val _current_IR_learning = MutableLiveData<String?>()

    val current_IR_learning: LiveData<String?>
        get() = _current_IR_learning

    fun update_current_IR_learning(newText: String?) {
        _current_IR_learning.value = newText
    }
}