package com.diafyt.lazarus.utils

import android.nfc.Tag
import android.nfc.tech.NfcV
import java.io.IOException
import java.lang.Thread.sleep
import java.util.Arrays

object NFCUtil {
    
    // Libre 2 A0 Unlock Command
    private val UNLOCK_CMD = byteArrayOf(0x02.toByte(), 0xA0.toByte(), 0x07.toByte(), 0xC2.toByte(), 0xAD.toByte(), 0x75.toByte(), 0x21.toByte())

    fun flashAndVerify(tag: Tag, payload: Map<Int, ByteArray>): String {
        val nfcV = NfcV.get(tag) ?: return "Error: Not NfcV"
        val report = StringBuilder()
        try {
            nfcV.connect()
            
            // Harden NFC Writes: Handle "Tag Lost" for A0 command
            try {
                val resp = nfcV.transceive(UNLOCK_CMD)
                if (resp[0] != 0x00.toByte()) return "A0 Unlock Failed"
                report.append("Unlock OK\n")
            } catch (e: IOException) {
                if (e.message?.contains("Tag was lost") == true) {
                    report.append("Unlock OK (Tag Lost - Expected for Libre 2 Plus)\n")
                    // Reconnect might be needed if tag was lost, but usually it reboots
                    // For now, we assume we can continue or need to reconnect
                    try {
                        nfcV.close()
                        sleep(500) // Wait for reboot
                        nfcV.connect()
                    } catch (reconnectEx: Exception) {
                        report.append("Reconnect failed: ${reconnectEx.message}\n")
                        // If we can't reconnect, we can't proceed with writing
                        return report.toString()
                    }
                } else {
                    throw e
                }
            }

            for ((block, data) in payload) {
                val writeCmd = byteArrayOf(0x22.toByte(), 0x21.toByte()) + tag.id + block.toByte() + data
                nfcV.transceive(writeCmd)
                sleep(60) 
                
                // Verification Logic: Read-Back loop
                val readBack = readBlock(nfcV, block, tag.id)
                if (Arrays.equals(readBack, data)) {
                    report.append("Block $block: Written & Verified\n")
                } else {
                    report.append("Block $block: Write Failed (Verify Mismatch)\n")
                }
            }
        } catch (e: Exception) { 
            report.append("Error: ${e.message}") 
        } finally { 
            try { nfcV.close() } catch (e: Exception) {} 
        }
        return report.toString()
    }

    // Fixed: Added the missing readBlock function for Util.kt
    fun readBlock(nfcV: NfcV, block: Int, uid: ByteArray): ByteArray {
        // 0x22 = Request Flags (High Data Rate, Address)
        // 0x20 = Read Single Block
        val readCmd = byteArrayOf(0x22.toByte(), 0x20.toByte()) + uid + block.toByte()
        try {
            val response = nfcV.transceive(readCmd)
            return if (response.isNotEmpty() && response[0] == 0x00.toByte()) {
                // Response format: Flags (1 byte) + Data (8 bytes)
                // We want just the data
                if (response.size >= 9) {
                    response.sliceArray(1 until 9)
                } else {
                    response.sliceArray(1 until response.size)
                }
            } else {
                byteArrayOf()
            }
        } catch (e: Exception) {
            return byteArrayOf()
        }
    }
}
