package com.hkh.androiddatasecurity.common

import androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import com.hkh.androiddatasecurity.R
import javax.crypto.Cipher

object FingerprintPrompt {

    fun show(activity: FragmentActivity, cipher: Cipher? = null) =
        MutableLiveData<AuthenticationResultModel>().also { resultModel ->
            val biometricPrompt = buildBiometricPrompt(activity, resultModel)
            val promptInfo = buildPromptInfo(activity)
            with(biometricPrompt) {
                cipher?.let {
                    authenticate(promptInfo, BiometricPrompt.CryptoObject(it))
                } ?: authenticate(promptInfo)
            }
        }

    private fun buildBiometricPrompt(
        activity: FragmentActivity,
        out: MutableLiveData<AuthenticationResultModel>
    ) = BiometricPrompt(
        activity,
        ContextCompat.getMainExecutor(activity),
        AuthenticationCallback(out)
    )

    private fun buildPromptInfo(activity: FragmentActivity) = PromptInfo.Builder()
        .setTitle(activity.getString(R.string.need_finger_print_for_operation))
        .setNegativeButtonText(activity.getString(R.string.cancel))
        .setAllowedAuthenticators(Constant.AUTHENTICATORS)
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
