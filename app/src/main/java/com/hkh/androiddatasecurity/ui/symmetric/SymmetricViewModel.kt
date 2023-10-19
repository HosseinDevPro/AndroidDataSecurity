package com.hkh.androiddatasecurity.ui.symmetric

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SymmetricViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "We are using reversible-symmetric way with AES-256 algorithm data encryption/decryption"
    }
    val text: LiveData<String> = _text
}