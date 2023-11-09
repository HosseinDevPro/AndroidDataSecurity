package com.hkh.security.hash

import com.hkh.security.Base64Utils.base64Decode
import com.hkh.security.Base64Utils.base64Encode
import com.hkh.security.SecurityConstant
import java.security.MessageDigest

class HashUtil() {

    fun generateHashedMessage(message: String): String {
        return message.sha256().run {
            base64Encode()
        }
    }

    fun verifyHashedMessage(plainData: String, sha256HashedData: String): Boolean {
        val hashedData: ByteArray = sha256HashedData.base64Decode()
        return hashedData.contentEquals(plainData.sha256())
    }

    private fun String.sha256(): ByteArray {
        val digest = MessageDigest.getInstance(SecurityConstant.SHA_256)
        return digest.digest(this.toByteArray())
    }

}