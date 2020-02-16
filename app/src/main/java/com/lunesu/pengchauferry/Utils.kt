package com.lunesu.pengchauferry

import android.os.Build

object Utils {
    val isEmulator = Build.FINGERPRINT.contains("generic")
}