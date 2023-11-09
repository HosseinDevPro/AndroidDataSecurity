package com.hkh.common.hash

import com.hkh.common.Base64Utils.base64Decode
import com.hkh.common.Base64Utils.base64Encode
import com.hkh.common.SecurityConstant
import java.security.MessageDigest

class HashUtil() {

    // Generate a SHA-256 hashed and base64-encoded message
    fun generateHashedMessage(message: String): String {
        // Compute the SHA-256 hash of the input message
        return message.sha256().run {
            // Encode the SHA-256 hash as a base64 string
            base64Encode()
        }
    }

    // Verify if the plain data matches a given SHA-256 hashed and base64-encoded data
    fun verifyHashedMessage(plainData: String, sha256HashedData: String): Boolean {
        // Decode the base64-encoded SHA-256 hashed data
        val hashedData: ByteArray = sha256HashedData.base64Decode()
        // Compare the decoded hashed data to the SHA-256 hash of the plain data
        return hashedData.contentEquals(plainData.sha256())
    }

    // Compute the SHA-256 hash of a string and return it as a byte array
    private fun String.sha256(): ByteArray {
        // Create a MessageDigest instance for SHA-256 hashing
        val digest = MessageDigest.getInstance(SecurityConstant.SHA_256)
        // Compute the SHA-256 hash of the input string
        return digest.digest(this.toByteArray())
    }

}