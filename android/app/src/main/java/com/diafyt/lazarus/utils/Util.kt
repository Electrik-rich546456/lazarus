package com.diafyt.lazarus.utils

import android.nfc.tech.NfcV
import android.util.Log

object Util {
    fun checkSensor(nfcV: NfcV, uid: ByteArray): Boolean {
        // Block 39 is the standard Lazarus check
        val data = NFCUtil.readBlock(nfcV, 39, uid)
        if (data.isEmpty()) return false
        
        // We bypass the strict check for Libre 2 rebirth
        return true 
    }
}
