package com.hkh.androiddatasecurity.common

import android.util.Base64

object Utils {

    fun ByteArray.base64Encode(): String = Base64.encodeToString(
        this,
        Base64.DEFAULT
    )

    fun String.base64Decode(): ByteArray = Base64.decode(
        this,
        Base64.DEFAULT
    )

}