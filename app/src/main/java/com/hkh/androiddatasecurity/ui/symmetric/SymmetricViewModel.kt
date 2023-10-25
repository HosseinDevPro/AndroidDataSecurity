package com.hkh.androiddatasecurity.ui.symmetric

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hkh.androiddatasecurity.R
import com.hkh.security.SecurityConstant
import com.hkh.security.KeyStoreManager
import com.hkh.security.symmetric.SymmetricKeyGenerationUtil
import com.hkh.security.symmetric.SealedData

class SymmetricViewModel : ViewModel() {

    val keyStoreManager by lazy {
        KeyStoreManager()
    }
    private val symmetricKeyGenerationUtil by lazy {
        SymmetricKeyGenerationUtil(keyStoreManager)
    }

    var sealedData: SealedData? = null
    var decryptedData: String? = null

    private val _isKeyExist = MutableLiveData<Boolean>()
    val isKeyExist: LiveData<Boolean> = _isKeyExist

    private val _showErrorMessage = MutableLiveData<Int>()
    val showErrorMessage: LiveData<Int> = _showErrorMessage


    private val _checkKeyGeneration = MutableLiveData<Boolean>()
    val checkKeyGeneration: LiveData<Boolean> = _checkKeyGeneration

    fun checkKeyGeneration() {
        if (!keyStoreManager.isKeyExist(SecurityConstant.KEY_ALIAS_SYMMETRIC)) {
            symmetricKeyGenerationUtil.getOrGenerateKey()
            _checkKeyGeneration.value = true
        } else {
            _isKeyExist.value = true
        }
    }

    private val _checkKeyRemove = MutableLiveData<Boolean>()
    val checkKeyRemove: LiveData<Boolean> = _checkKeyRemove
    fun checkKeyRemove() {
        if (keyStoreManager.isKeyExist(SecurityConstant.KEY_ALIAS_SYMMETRIC)) {
            keyStoreManager.removeKey(SecurityConstant.KEY_ALIAS_SYMMETRIC)
            _checkKeyRemove.value = true
        } else {
            _isKeyExist.value = false
        }
    }


    private val _checkEncryptMessage = MutableLiveData<String>()
    val checkEncryptMessage: LiveData<String> = _checkEncryptMessage
    fun checkEncryption(userInputText: String) {
        if (userInputText.isNotEmpty()) {
            if (keyStoreManager.isKeyExist(SecurityConstant.KEY_ALIAS_SYMMETRIC)) {
                _checkEncryptMessage.value = userInputText
            } else {
                _isKeyExist.value = false
            }
        } else {
            _showErrorMessage.value = R.string.user_input_is_empty
        }
    }

    fun encryptData(userInputText: String) {
        sealedData = symmetricKeyGenerationUtil.encrypt(userInputText)
    }


    private val _checkDecryptMessage = MutableLiveData<String>()
    val checkDecryptMessage: LiveData<String> = _checkDecryptMessage
    fun checkDecryption(encryptedText: String, defaultText: String) {
        if (encryptedText != defaultText) {
            if (keyStoreManager.isKeyExist(SecurityConstant.KEY_ALIAS_SYMMETRIC)) {
                _checkDecryptMessage.value = encryptedText
            } else {
                _isKeyExist.value = false
            }
        } else {
            _showErrorMessage.value = R.string.encrypted_text_is_empty
        }
    }

    fun decryptData(encryptedText: String) {
        decryptedData = symmetricKeyGenerationUtil.decrypt(
            encryptedBase64EncodedText = encryptedText,
            iv = sealedData?.initialVector
        )
    }

}