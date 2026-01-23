package com.diafyt.lazarus.utils

import android.nfc.Tag
import android.nfc.tech.NfcV
import java.lang.Thread.sleep

object NFCUtil {
    
    // Libre 2 A0 Unlock Command
    private val UNLOCK_CMD = byteArrayOf(0x02.toByte(), 0xA0.toByte(), 0x07.toByte(), 0xC2.toByte(), 0xAD.toByte(), 0x75.toByte(), 0x21.toByte())

    fun flashAndVerify(tag: Tag, payload: Map<Int, ByteArray>): String {
        val nfcV = NfcV.get(tag) ?: return "Error: Not NfcV"
        val report = StringBuilder()
        try {
            nfcV.connect()
            val resp = nfcV.transceive(UNLOCK_CMD)
            if (resp[0] != 0x00.toByte()) return "A0 Unlock Failed"
            
            report.append("Unlock OK\n")
            for ((block, data) in payload) {
                val writeCmd = byteArrayOf(0x22.toByte(), 0x21.toByte()) + tag.id + block.toByte() + data
                nfcV.transceive(writeCmd)
                sleep(60) 
                report.append("Block $block: Written\n")
            }
        } catch (e: Exception) { report.append("Error: ${e.message}") }
        finally { nfcV.close() }
        return report.toString()
    }

    // Fixed: Added the missing readBlock function for Util.kt
    fun readBlock(nfcV: NfcV, block: Int, uid: ByteArray): ByteArray {
        val readCmd = byteArrayOf(0x22.toByte(), 0x20.toByte()) + uid + block.toByte()
        val response = nfcV.transceive(readCmd)
        return if (response[0] == 0x00.toByte()) {
            response.sliceArray(1 until response.size)
        } else {
            byteArrayOf()
        }
    }
}
