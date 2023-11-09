package com.hkh.androiddatasecurity.ui.asymmetric

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hkh.androiddatasecurity.R
import com.hkh.common.KeyStoreManager
import com.hkh.common.SecurityConstant
import com.hkh.common.asymmetric.AsymmetricKeyGenerationUtil

class AsymmetricViewModel : ViewModel() {

    val keyStoreManager by lazy {
        KeyStoreManager()
    }
    private val asymmetricKeyGenerationUtil by lazy {
        AsymmetricKeyGenerationUtil(keyStoreManager)
    }

    private val _signedData = MutableLiveData<String?>()
    val signedData: LiveData<String?> = _signedData
    fun resetSignedData() {
        _signedData.value = null
    }

    private val _isVerifiedData = MutableLiveData<Boolean?>()
    val isVerifiedData: LiveData<Boolean?> = _isVerifiedData
    fun resetVerifiedData() {
        _signedData.value = null
    }

    private val _showErrorMessage = MutableLiveData<Int>()
    val showErrorMessage: LiveData<Int> = _showErrorMessage

    private val _isKeyExist = MutableLiveData<Boolean>()
    val isKeyExist: LiveData<Boolean> = _isKeyExist

    private val _checkKeyGeneration = MutableLiveData<Boolean>()
    val checkKeyGeneration: LiveData<Boolean> = _checkKeyGeneration
    fun checkKeyGeneration() {
        if (!keyStoreManager.isKeyExist(SecurityConstant.KEY_ALIAS_ASYMMETRIC)) {
            asymmetricKeyGenerationUtil.getOrGenerateKey()
            _checkKeyGeneration.value = true
        } else {
            _isKeyExist.value = true
        }
    }

    private val _checkKeyRemove = MutableLiveData<Boolean>()
    val checkKeyRemove: LiveData<Boolean> = _checkKeyRemove
    fun checkKeyRemove() {
        if (keyStoreManager.isKeyExist(SecurityConstant.KEY_ALIAS_ASYMMETRIC)) {
            keyStoreManager.removeKey(SecurityConstant.KEY_ALIAS_ASYMMETRIC)
            _checkKeyRemove.value = true
        } else {
            _isKeyExist.value = false
        }
    }


    private val _checkSigningMessage = MutableLiveData<String>()
    val checkSigningMessage: LiveData<String> = _checkSigningMessage
    fun checkSigning(userInputText: String) {
        if (userInputText.isNotEmpty()) {
            if (keyStoreManager.isKeyExist(SecurityConstant.KEY_ALIAS_ASYMMETRIC)) {
                _checkSigningMessage.value = userInputText
            } else {
                _isKeyExist.value = false
            }
        } else {
            _showErrorMessage.value = R.string.user_input_is_empty
        }
    }
    fun signData(userInputText: String) {
        _signedData.value = asymmetricKeyGenerationUtil.signData(userInputText)
    }


    private val _checkVerifiedMessage = MutableLiveData<String>()
    val checkVerifiedMessage: LiveData<String> = _checkVerifiedMessage
    fun checkVerifying(signedText: String, defaultText: String) {
        if (signedText != defaultText) {
            if (keyStoreManager.isKeyExist(SecurityConstant.KEY_ALIAS_ASYMMETRIC)) {
                _checkVerifiedMessage.value = signedText
            } else {
                _isKeyExist.value = false
            }
        } else {
            _showErrorMessage.value = R.string.signed_text_is_empty
        }
    }
    fun verifyData(plainText: String, signedText: String) {
        _isVerifiedData.value = asymmetricKeyGenerationUtil.verifyData(
            plainMessage = plainText,
            base64Signature = signedText
        )
    }

}