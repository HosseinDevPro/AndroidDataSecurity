package com.hkh.androiddatasecurity.ui.asymmetric

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AsymmetricViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "We are using reversible-asymmetric way with EC-256 algorithm data signing/verifying"
    }
    val text: LiveData<String> = _text
}