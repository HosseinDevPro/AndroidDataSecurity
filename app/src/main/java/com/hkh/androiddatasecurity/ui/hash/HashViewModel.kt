package com.hkh.androiddatasecurity.ui.hash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HashViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "We are using irreversible-hash way with SHA-256 algorithm"
    }
    val text: LiveData<String> = _text
}