package com.hkh.common.asymmetric

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import android.util.Log
import com.hkh.common.Base64Utils.base64Decode
import com.hkh.common.Base64Utils.base64Encode
import com.hkh.common.SecurityConstant
import com.hkh.common.SecurityConstant.KEY_SIZE
import com.hkh.common.KeyStoreManager
import com.hkh.common.SecurityConstant.ALGORITHM_SHA256_WITH_ECDSA
import com.hkh.common.SecurityConstant.AUTHENTICATION_VALIDITY_DURATION
import com.hkh.common.SecurityConstant.KEY_ALIAS_ASYMMETRIC
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

    // Load the private key entry from the KeyStore
    private fun loadKeyEntry() {
        keyEntry = keyStoreManager.loadKeyEntry(KEY_ALIAS_ASYMMETRIC)
    }

    // Get or generate an asymmetric key pair
    fun getOrGenerateKey(): KeyStore.PrivateKeyEntry? {
        // Check if the key doesn't exist in the KeyStore and create it if necessary
        if (!keyStoreManager.isKeyExist(KEY_ALIAS_ASYMMETRIC)) {
            createAndStoreSecretKey()
        }
        // Return the private key entry
        return keyStoreManager.loadKeyEntry(KEY_ALIAS_ASYMMETRIC)
    }

    // Create and store an asymmetric key pair
    private fun createAndStoreSecretKey(): KeyPair? = try {
        // Create a KeyPairGenerator for the EC (Elliptic Curve) algorithm using Android KeyStore
        KeyPairGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_EC,
            SecurityConstant.ANDROID_KEY_STORE_PROVIDER
        ).run {
            // Initialize the KeyPairGenerator with the specified parameters
            initialize(getKeyGenParameterSpec(), SecureRandom())
            // Generate the key pair
            generateKeyPair()
        }
    } catch (e: NoSuchAlgorithmException) {
        // Handle an exception when the requested cryptographic algorithm is not available
        Log.d(SecurityConstant.APP_TAG, e.stackTraceToString())
        null
    } catch (e: NoSuchProviderException) {
        // Handle an exception when the specified security provider is not found
        Log.d(SecurityConstant.APP_TAG, e.stackTraceToString())
        null
    } catch (e: InvalidAlgorithmParameterException) {
        // Handle an exception when the provided parameters are invalid
        Log.d(SecurityConstant.APP_TAG, e.stackTraceToString())
        null
    } catch (e: CertificateException) {
        // Handle an exception related to certificate issues
        Log.d(SecurityConstant.APP_TAG, e.stackTraceToString())
        null
    } catch (e: IOException) {
        // Handle an exception related to input/output operations
        Log.d(SecurityConstant.APP_TAG, e.stackTraceToString())
        null
    } catch (e: IllegalStateException) {
        // Handle an exception related to the state of the system or security
        Log.d(SecurityConstant.APP_TAG, e.stackTraceToString())
        null
    } catch (e: NullPointerException) {
        // Handle a NullPointerException if it occurs
        Log.d(SecurityConstant.APP_TAG, e.stackTraceToString())
        null
    } catch (e: SecurityException) {
        // Handle a general security exception
        Log.d(SecurityConstant.APP_TAG, e.stackTraceToString())
        null
    }

    // Define the parameters for generating a key pair
    private fun getKeyGenParameterSpec() =
        KeyGenParameterSpec.Builder(
            KEY_ALIAS_ASYMMETRIC,  // Unique alias for the key pair in the KeyStore
            KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY  // Specify the key's purpose
        ).run {
            setDigests(KeyProperties.DIGEST_SHA256)  // Set the digest algorithm for the key
            setKeySize(KEY_SIZE)  // Set the size of the key
            setUserAuthenticationRequired(true)  // Require user authentication for key usage

            setRandomizedEncryptionRequired(true)  // Require encryption to be randomized

            // Specify the Elliptic Curve algorithm and curve name
            setAlgorithmParameterSpec(ECGenParameterSpec(SecurityConstant.ELLIPTIC_CURVE_STANDARD_NAME))

            // Check the Android version to set user authentication parameters
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                setUserAuthenticationParameters(
                    AUTHENTICATION_VALIDITY_DURATION,  // Specify the duration of authentication validity
                    KeyProperties.AUTH_BIOMETRIC_STRONG  // Set strong biometric authentication
                )
            } else {
                // For earlier Android versions, set authentication validity duration
                setUserAuthenticationValidityDurationSeconds(AUTHENTICATION_VALIDITY_DURATION)
            }

            // Check the Android version to set additional security features
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                setIsStrongBoxBacked(true)  // Use StrongBox for hardware-backed security
                setUnlockedDeviceRequired(true)  // Require an unlocked device for key usage
            }

            // Check the Android version to set invalidated by biometric enrollment
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                setInvalidatedByBiometricEnrollment(true)  // Invalidate the key if biometric data changes
            }

            this  // Return the builder instance
        }.build()  // Build and return the KeyGenParameterSpec

    // Sign data using the private key
    fun signData(plainMessage: String): String? = try {
        // Load the private key entry from the KeyStore
        loadKeyEntry()

        // Create a Signature instance using SHA256 with ECDSA algorithm
        Signature.getInstance(ALGORITHM_SHA256_WITH_ECDSA).run {
            // Initialize the Signature with the private key
            initSign(getPrivateKey())

            // Update the Signature with the message's bytes
            update(plainMessage.toByteArray())

            // Sign the message and return it as a base64-encoded string
            sign()
        }.run {
            base64Encode()
        }
    } catch (e: SignatureException) {
        // Handle an exception related to signature operations
        Log.d(SecurityConstant.APP_TAG, e.stackTraceToString())
        null
    } catch (e: KeyPermanentlyInvalidatedException) {
        // Handle an exception when the key is permanently invalidated
        Log.d(SecurityConstant.APP_TAG, e.stackTraceToString())
        null
    } catch (e: KeyStoreException) {
        // Handle an exception related to the KeyStore
        Log.d(SecurityConstant.APP_TAG, e.stackTraceToString())
        null
    } catch (e: CertificateException) {
        // Handle an exception related to certificates
        Log.d(SecurityConstant.APP_TAG, e.stackTraceToString())
        null
    } catch (e: UnrecoverableKeyException) {
        // Handle an exception when the key cannot be recovered
        Log.d(SecurityConstant.APP_TAG, e.stackTraceToString())
        null
    } catch (e: NoSuchAlgorithmException) {
        // Handle an exception when a required cryptographic algorithm is not found
        Log.d(SecurityConstant.APP_TAG, e.stackTraceToString())
        null
    } catch (e: InvalidKeyException) {
        // Handle an exception when the private key is invalid
        Log.d(SecurityConstant.APP_TAG, e.stackTraceToString())
        null
    } catch (e: IllegalStateException) {
        // Handle an exception related to the state of the system or security
        Log.d(SecurityConstant.APP_TAG, e.stackTraceToString())
        null
    } catch (e: ProviderException) {
        // Handle a general security provider exception
        Log.d(SecurityConstant.APP_TAG, e.stackTraceToString())
        null
    } catch (e: IOException) {
        // Handle an exception related to input/output operations
        Log.d(SecurityConstant.APP_TAG, e.stackTraceToString())
        null
    } catch (e: InvalidAlgorithmParameterException) {
        // Handle an exception related to invalid algorithm parameters
        Log.d(SecurityConstant.APP_TAG, e.stackTraceToString())
        null
    }

    // Verify data using the public key
    fun verifyData(plainMessage: String, base64Signature: String): Boolean = try {
        // Load the private key entry from the KeyStore
        loadKeyEntry()

        // Create a Signature instance using SHA256 with ECDSA algorithm
        Signature.getInstance(ALGORITHM_SHA256_WITH_ECDSA).run {
            // Initialize the Signature with the public key
            initVerify(getPublicKey())

            // Update the Signature with the message's bytes
            update(plainMessage.toByteArray())

            // Verify the base64-encoded signature and return the result as a boolean
            verify(base64Signature.base64Decode())
        }
    } catch (e: SignatureException) {
        // Handle an exception related to signature operations
        Log.d(SecurityConstant.APP_TAG, e.stackTraceToString())
        false
    } catch (e: KeyPermanentlyInvalidatedException) {
        // Handle an exception when the key is permanently invalidated
        Log.d(SecurityConstant.APP_TAG, e.stackTraceToString())
        false
    } catch (e: KeyStoreException) {
        // Handle an exception related to the KeyStore
        Log.d(SecurityConstant.APP_TAG, e.stackTraceToString())
        false
    } catch (e: CertificateException) {
        // Handle an exception related to certificates
        Log.d(SecurityConstant.APP_TAG, e.stackTraceToString())
        false
    } catch (e: UnrecoverableKeyException) {
        // Handle an exception when the key cannot be recovered
        Log.d(SecurityConstant.APP_TAG, e.stackTraceToString())
        false
    } catch (e: NoSuchAlgorithmException) {
        // Handle an exception when a required cryptographic algorithm is not found
        Log.d(SecurityConstant.APP_TAG, e.stackTraceToString())
        false
    } catch (e: InvalidKeyException) {
        // Handle an exception when the public key is invalid
        Log.d(SecurityConstant.APP_TAG, e.stackTraceToString())
        false
    } catch (e: IllegalStateException) {
        // Handle an exception related to the state of the system or security
        Log.d(SecurityConstant.APP_TAG, e.stackTraceToString())
        false
    } catch (e: ProviderException) {
        // Handle a general security provider exception
        Log.d(SecurityConstant.APP_TAG, e.stackTraceToString())
        false
    } catch (e: IOException) {
        // Handle an exception related to input/output operations
        Log.d(SecurityConstant.APP_TAG, e.stackTraceToString())
        false
    } catch (e: InvalidAlgorithmParameterException) {
        // Handle an exception related to invalid algorithm parameters
        Log.d(SecurityConstant.APP_TAG, e.stackTraceToString())
        false
    }

    // Get the public key from the loaded key entry, if available
    private fun getPublicKey(): PublicKey? = getCertificate()?.publicKey

    // Get the certificate from the loaded key entry, if available
    private fun getCertificate(): Certificate? = keyEntry?.certificate

    // Get the private key from the loaded key entry, if available
    private fun getPrivateKey(): PrivateKey? = keyEntry?.privateKey

}