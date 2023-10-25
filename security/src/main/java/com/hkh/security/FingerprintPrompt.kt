package com.hkh.security

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import javax.crypto.Cipher

class FingerprintPrompt(private val activity: FragmentActivity) {

    fun canAuthenticate() = BiometricManager.from(activity.applicationContext)
        .canAuthenticate(SecurityConstant.AUTHENTICATORS) == BIOMETRIC_SUCCESS

    fun show(title: String, description: String, cipher: Cipher? = null) =
        MutableLiveData<AuthenticationResultModel>().also { resultModel ->
            val biometricPrompt = buildBiometricPrompt(resultModel)
            val promptInfo = buildPromptInfo(title, description)
            with(biometricPrompt) {
                cipher?.let {
                    authenticate(promptInfo, BiometricPrompt.CryptoObject(it))
                } ?: authenticate(promptInfo)
            }
        }

    private fun buildBiometricPrompt(out: MutableLiveData<AuthenticationResultModel>) =
        BiometricPrompt(
            activity,
            ContextCompat.getMainExecutor(activity),
            AuthenticationCallback(out)
        )

    private fun buildPromptInfo(title: String, description: String) = PromptInfo.Builder()
        .setTitle(title)
        .setNegativeButtonText(description)
        .setAllowedAuthenticators(SecurityConstant.AUTHENTICATORS)
        .build()

    private class AuthenticationCallback(
        private val out: MutableLiveData<AuthenticationResultModel>
    ) : BiometricPrompt.AuthenticationCallback() {

        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            super.onAuthenticationSucceeded(result)
            out.postValue(
                AuthenticationResultModel(
                    true,
                    BIOMETRIC_SUCCESS,
                    result.cryptoObject?.cipher
                )
            )
            result.authenticationType
        }

        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            super.onAuthenticationError(errorCode, errString)
            out.postValue(AuthenticationResultModel(false, errorCode))
        }

        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
            out.postValue(AuthenticationResultModel(false, -1))
        }
    }

}
