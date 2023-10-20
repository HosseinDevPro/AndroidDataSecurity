package com.hkh.androiddatasecurity.common

import androidx.biometric.BiometricManager

object Constant {

    const val APP_TAG = "DataSecurityTAG"
    const val ANDROID_KEY_STORE_PROVIDER = "AndroidKeyStore"
    const val KEY_ALIAS_SYMMETRIC = "AES_KEY_DEMO"
    const val KEY_SIZE = 256
    const val AES_GCM_NOPADDING = "AES/GCM/NoPadding"
    const val AUTHENTICATION_TAG_SIZE = 128

    const val AUTHENTICATORS = BiometricManager.Authenticators.BIOMETRIC_STRONG

}