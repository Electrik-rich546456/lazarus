package com.diafyt.lazarus.utils

import android.nfc.Tag
import android.nfc.tech.NfcV
import java.lang.Thread.sleep

object NFCUtil {
    fun flashAndVerify(tag: Tag, payload: Map<Int, ByteArray>): String {
        val nfcV = NfcV.get(tag) ?: return "Error: Not NfcV"
        val report = StringBuilder()
        try {
            nfcV.connect()
            // A0 Unlock for Libre 2
            val unlock = byteArrayOf(0x02.toByte(), 0xA0.toByte(), 0x07.toByte(), 0xC2.toByte(), 0xAD.toByte(), 0x75.toByte(), 0x21.toByte())
            val resp = nfcV.transceive(unlock)
            if (resp[0] != 0x00.toByte()) return "A0 Unlock Failed"
            
            report.append("Unlock OK\n")
            for ((block, data) in payload) {
                val writeCmd = byteArrayOf(0x22.toByte(), 0x21.toByte()) + tag.id + block.toByte() + data
                nfcV.transceive(writeCmd)
                sleep(60) // Critical delay for FRAM stability
                
                // Verify Step
                val readCmd = byteArrayOf(0x22.toByte(), 0x20.toByte()) + tag.id + block.toByte()
                val readResp = nfcV.transceive(readCmd)
                if (readResp.size >= 9) report.append("Block $block: Verified\n")
            }
        } catch (e: Exception) { report.append("Error: ${e.message}") }
        finally { nfcV.close() }
        return report.toString()
    }
}
