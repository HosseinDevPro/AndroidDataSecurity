package com.hkh.symmetric.util

import com.hkh.common.Base64Utils.base64Encode

data class SealedData(
    val cipheredBytes: ByteArray,
    val initialVector: ByteArray
) {

    fun getBase64CipherData() = cipheredBytes.base64Encode()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SealedData

        if (!cipheredBytes.contentEquals(other.cipheredBytes)) return false
        if (!initialVector.contentEquals(other.initialVector)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = cipheredBytes.contentHashCode()
        result = 31 * result + initialVector.contentHashCode()
        return result
    }

}
