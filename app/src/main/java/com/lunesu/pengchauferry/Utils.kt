package com.lunesu.pengchauferry

import android.os.Build
import java.util.*

object Utils {
    val isEmulator = Build.FINGERPRINT.contains("generic")
}
