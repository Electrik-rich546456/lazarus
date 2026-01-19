package com.diafyt.lazarus.utils

import android.nfc.Tag
import android.nfc.tech.NfcV
import android.util.Log

/**
 * Enhanced NFC utility with Libre 2 "OpenFreeStyle" Unlock Support.
 */
object NFCUtil {
    private const val blocklen = 8
    private const val TAG = "NFCUtil-Lazarus"

    /**
     * Libre 2 Custom Unlock (Command A0)
     * This is the "Magic Key" sequence used in OpenFreeStyle to permit writes.
     */
    suspend fun unlockLibre2(tag: Tag): Boolean {
        val nfc = NfcV.get(tag)
        if (!nfc.isConnected) nfc.connect()

        // OpenFreeStyle Magic: Flags 0x02, Cmd 0xA0, Mfg 0x07, Key C2AD7521
        val unlockCmd = byteArrayOf(
            0x02.toByte(), 0xA0.toByte(), 0x07.toByte(),
            0xC2.toByte(), 0xAD.toByte(), 0x75.toByte(), 0x21.toByte()
        )

        return try {
            val resp = nfc.transceive(unlockCmd)
            // Success if response starts with 0x00 (No Error)
            resp.isNotEmpty() && resp[0] == 0.toByte()
        } catch (e: Exception) {
            Log.e(TAG, "Unlock failed: ${e.message}")
            false
        }
    }

    /**
     * Write a single block using the Libre 2 specific handshake.
     */
    suspend fun writeBlock(tag: Tag, pos: Byte, data: ByteArray): ByteArray? {
        if (data.size != blocklen) throw RuntimeException("Payload must be $blocklen bytes.")

        // 1. Establish the Secure Session (Libre 2 requirement)
        unlockLibre2(tag)

        // 2. Build the Write Command (Command 0x21)
        // Flag 0x22 = Addressed mode (UID included)
        val cmd = byteArrayOf(0x22, 0x21) + 
                  tag.id + // 8-byte UID
                  pos +    // block index
                  data     // 8-bytes data

        return AsyncNFCTask(tag).asyncRun(cmd)?.let { checkError(it) }
    }

    /**
     * Write multiple blocks (Command 0x24)
     */
    suspend fun writeMultipleBlocks(tag: Tag, pos: Byte, data: ByteArray): ByteArray? {
        if (data.size % blocklen != 0) throw RuntimeException("Payload size error.")

        unlockLibre2(tag)

        val cmd = byteArrayOf(0x22, 0x24) + 
                  tag.id + 
                  pos + 
                  (data.size / blocklen - 1).toByte() + 
                  data

        return AsyncNFCTask(tag).asyncRun(cmd)?.let { checkError(it) }
    }

    suspend fun readBlock(tag: Tag, pos: Byte): ByteArray? {
        unlockLibre2(tag)
        val cmd = byteArrayOf(0x22, 0x20) + tag.id + pos
        return AsyncNFCTask(tag).asyncRun(cmd)?.let { checkError(it) }
    }

    fun checkError(msg: ByteArray): ByteArray? {
        if (msg.isNotEmpty() && msg[0] == 0.toByte()) {
            return msg.sliceArray(1 until msg.size)
        }
        return null
    }
}
