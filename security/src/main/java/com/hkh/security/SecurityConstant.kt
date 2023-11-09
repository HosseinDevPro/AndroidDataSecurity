package com.hkh.security

import androidx.biometric.BiometricManager

object SecurityConstant {

    const val APP_TAG = "DataSecurityTAG"
    const val ANDROID_KEY_STORE_PROVIDER = "AndroidKeyStore"
    const val KEY_ALIAS_SYMMETRIC = "AES_KEY_DEMO"
    const val KEY_ALIAS_ASYMMETRIC = "EC_KEY_DEMO"
    const val KEY_SIZE = 256
    const val AES_GCM_NOPADDING = "AES/GCM/NoPadding"
    const val AUTHENTICATION_TAG_SIZE = 128
    const val AUTHENTICATION_VALIDITY_DURATION = 10
    const val ALGORITHM_SHA256_WITH_ECDSA = "SHA256withECDSA"
    const val ELLIPTIC_CURVE_STANDARD_NAME = "secp256r1"
    const val SHA_256 = "SHA-256"


    const val AUTHENTICATORS = BiometricManager.Authenticators.BIOMETRIC_STRONG

}