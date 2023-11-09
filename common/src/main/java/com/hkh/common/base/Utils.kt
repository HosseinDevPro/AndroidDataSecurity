package com.hkh.common.base

import android.widget.Toast
import androidx.fragment.app.Fragment

object Utils {

    fun Fragment.showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

}