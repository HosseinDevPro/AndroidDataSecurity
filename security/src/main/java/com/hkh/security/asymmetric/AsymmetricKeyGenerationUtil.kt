package com.hkh.security.asymmetric

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import android.util.Log
import com.hkh.security.Base64Utils.base64Decode
import com.hkh.security.Base64Utils.base64Encode
import com.hkh.security.SecurityConstant
import com.hkh.security.SecurityConstant.KEY_SIZE
import com.hkh.security.KeyStoreManager
import com.hkh.security.SecurityConstant.ALGORITHM_SHA256_WITH_ECDSA
import com.hkh.security.SecurityConstant.AUTHENTICATION_VALIDITY_DURATION
import com.hkh.security.SecurityConstant.KEY_ALIAS_ASYMMETRIC
import java.io.IOException
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.NoSuchProviderException
import java.security.PrivateKey
import java.security.ProviderException
import java.security.PublicKey
import java.security.SecureRandom
import java.security.Signature
import java.security.SignatureException
import java.security.UnrecoverableKeyException
import java.security.cert.Certificate
import java.security.cert.CertificateException
import java.security.spec.ECGenParameterSpec

class AsymmetricKeyGenerationUtil(private val keyStoreManager: KeyStoreManager) {

    private var keyEntry: KeyStore.PrivateKeyEntry? = null

    private fun loadKeyEntry() {
        keyEntry = keyStoreManager.loadKeyEntry(KEY_ALIAS_ASYMMETRIC)
    }

    fun getOrGenerateKey(): KeyStore.PrivateKeyEntry? {
        if (!keyStoreManager.isKeyExist(KEY_ALIAS_ASYMMETRIC)) {
            createAndStoreSecretKey()
        }
        return keyStoreManager.loadKeyEntry(KEY_ALIAS_ASYMMETRIC)
    }

    private fun createAndStoreSecretKey(): KeyPair? = try {
        KeyPairGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_EC,
            SecurityConstant.ANDROID_KEY_STORE_PROVIDER
        ).run {
            initialize(getKeyGenParameterSpec(), SecureRandom())
            generateKeyPair()
        }
    } catch (e: NoSuchAlgorithmException) {
        Log.d(SecurityConstant.APP_TAG, e.stackTraceToString())
        null
    } catch (e: NoSuchProviderException) {
        Log.d(SecurityConstant.APP_TAG, e.stackTraceToString())
        null
    } catch (e: InvalidAlgorithmParameterException) {
        Log.d(SecurityConstant.APP_TAG, e.stackTraceToString())
        null
    } catch (e: CertificateException) {
        Log.d(SecurityConstant.APP_TAG, e.stackTraceToString())
        null
    } catch (e: IOException) {
        Log.d(SecurityConstant.APP_TAG, e.stackTraceToString())
        null
    } catch (e: IllegalStateException) {
        Log.d(SecurityConstant.APP_TAG, e.stackTraceToString())
        null
    } catch (e: NullPointerException) {
        Log.d(SecurityConstant.APP_TAG, e.stackTraceToString())
        null
    } catch (e: SecurityException) {
        Log.d(SecurityConstant.APP_TAG, e.stackTraceToString())
        null
    }

    private fun getKeyGenParameterSpec() =
        KeyGenParameterSpec.Builder(
            KEY_ALIAS_ASYMMETRIC,
            KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
        ).run {
            setDigests(KeyProperties.DIGEST_SHA256)
            setKeySize(KEY_SIZE)
            setUserAuthenticationRequired(true)
            setRandomizedEncryptionRequired(true)
            setAlgorithmParameterSpec(ECGenParameterSpec(SecurityConstant.ELLIPTIC_CURVE_STANDARD_NAME))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                setUserAuthenticationParameters(
                    AUTHENTICATION_VALIDITY_DURATION,
                    KeyProperties.AUTH_BIOMETRIC_STRONG
                )
            } else {
                setUserAuthenticationValidityDurationSeconds(AUTHENTICATION_VALIDITY_DURATION)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                setIsStrongBoxBacked(true)
                setUnlockedDeviceRequired(true)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                setInvalidatedByBiometricEnrollment(true)
            }
            this
        }.build()

    fun signData(plainMessage: String): String? = try {
        loadKeyEntry()
        Signature.getInstance(ALGORITHM_SHA256_WITH_ECDSA).run {
            initSign(getPrivateKey())
            update(plainMessage.toByteArray())
            sign()
        }.run {
            base64Encode()
        }
    } catch (e: SignatureException) {
        Log.d(SecurityConstant.APP_TAG, e.stackTraceToString())
        null
    } catch (e: KeyPermanentlyInvalidatedException) {
        Log.d(SecurityConstant.APP_TAG, e.stackTraceToString())
        null
    } catch (e: KeyStoreException) {
        Log.d(SecurityConstant.APP_TAG, e.stackTraceToString())
        null
    } catch (e: CertificateException) {
        Log.d(SecurityConstant.APP_TAG, e.stackTraceToString())
        null
    } catch (e: UnrecoverableKeyException) {
        Log.d(SecurityConstant.APP_TAG, e.stackTraceToString())
        null
    } catch (e: NoSuchAlgorithmException) {
        Log.d(SecurityConstant.APP_TAG, e.stackTraceToString())
        null
    } catch (e: InvalidKeyException) {
        Log.d(SecurityConstant.APP_TAG, e.stackTraceToString())
        null
    } catch (e: IllegalStateException) {
        Log.d(SecurityConstant.APP_TAG, e.stackTraceToString())
        null
    } catch (e: ProviderException) {
        Log.d(SecurityConstant.APP_TAG, e.stackTraceToString())
        null
    } catch (e: IOException) {
        Log.d(SecurityConstant.APP_TAG, e.stackTraceToString())
        null
    } catch (e: InvalidAlgorithmParameterException) {
        Log.d(SecurityConstant.APP_TAG, e.stackTraceToString())
        null
    }

    fun verifyData(plainMessage: String, base64Signature: String): Boolean = try {
        loadKeyEntry()
        Signature.getInstance(ALGORITHM_SHA256_WITH_ECDSA).run {
            initVerify(getPublicKey())
            update(plainMessage.toByteArray())
            verify(base64Signature.base64Decode())
        }
    } catch (e: SignatureException) {
        Log.d(SecurityConstant.APP_TAG, e.stackTraceToString())
        false
    } catch (e: KeyPermanentlyInvalidatedException) {
        Log.d(SecurityConstant.APP_TAG, e.stackTraceToString())
        false
    } catch (e: KeyStoreException) {
        Log.d(SecurityConstant.APP_TAG, e.stackTraceToString())
        false
    } catch (e: CertificateException) {
        Log.d(SecurityConstant.APP_TAG, e.stackTraceToString())
        false
    } catch (e: UnrecoverableKeyException) {
        Log.d(SecurityConstant.APP_TAG, e.stackTraceToString())
        false
    } catch (e: NoSuchAlgorithmException) {
        Log.d(SecurityConstant.APP_TAG, e.stackTraceToString())
        false
    } catch (e: InvalidKeyException) {
        Log.d(SecurityConstant.APP_TAG, e.stackTraceToString())
        false
    } catch (e: IllegalStateException) {
        Log.d(SecurityConstant.APP_TAG, e.stackTraceToString())
        false
    } catch (e: ProviderException) {
        Log.d(SecurityConstant.APP_TAG, e.stackTraceToString())
        false
    } catch (e: IOException) {
        Log.d(SecurityConstant.APP_TAG, e.stackTraceToString())
        false
    } catch (e: InvalidAlgorithmParameterException) {
        Log.d(SecurityConstant.APP_TAG, e.stackTraceToString())
        false
    }

    private fun getPublicKey(): PublicKey? = getCertificate()?.publicKey

    private fun getCertificate(): Certificate? = keyEntry?.certificate

    private fun getPrivateKey(): PrivateKey? = keyEntry?.privateKey

}