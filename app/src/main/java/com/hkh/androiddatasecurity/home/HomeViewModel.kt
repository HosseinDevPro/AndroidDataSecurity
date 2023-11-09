package com.hkh.androiddatasecurity.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Please select your data security way:"
    }
    val text: LiveData<String> = _text
}