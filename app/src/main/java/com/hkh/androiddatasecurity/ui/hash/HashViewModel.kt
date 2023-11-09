package com.hkh.androiddatasecurity.ui.hash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hkh.security.hash.HashUtil

class HashViewModel : ViewModel() {

    private val hashUtil by lazy {
        HashUtil()
    }

    private val _hashedMessage = MutableLiveData<String?>()
    val hashedMessage: LiveData<String?> = _hashedMessage
    fun resetHashedMessage() {
        _hashedMessage.value = null
    }
    fun generateHashedData(data: String) {
        _hashedMessage.value = hashUtil.generateHashedMessage(data)
    }


    private val _verifiedMessage = MutableLiveData<Boolean?>()
    val verifiedMessage: LiveData<Boolean?> = _verifiedMessage
    fun resetVerifiedMessage() {
        _verifiedMessage.value = null
    }
    fun verifyHashedData(plainText: String, sha256HashedText: String) {
        _verifiedMessage.value = hashUtil.verifyHashedMessage(plainText, sha256HashedText)
    }

}