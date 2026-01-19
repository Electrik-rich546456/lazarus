package com.diafyt.lazarus.utils

import android.nfc.Tag
import android.nfc.tech.NfcV

/**
 * Provide easy to use NFC commands with Libre 2 support.
 */
object NFCUtil {
    private const val blocklen = 8

    /**
     * Libre 2 Unlock Sequence (The "Magic Key")
     */
    suspend fun unlockLibre2(tag: Tag) {
        // Flags: 0x02 (High Data Rate), Command: 0xA0 (Custom), Manufacturer: 0x07 (TI)
        // Magic Key: C2 AD 75 21
        val unlockCmd = byteArrayOf(
            0x02.toByte(), 0xA0.toByte(), 0x07.toByte(),
            0xC2.toByte(), 0xAD.toByte(), 0x75.toByte(), 0x21.toByte()
        )
        // We send the unlock but don't worry if it fails (might be a Libre 1)
        AsyncNFCTask(tag).asyncRun(unlockCmd)
    }

    /**
     * Write a single block via the corresponding NFC command.
     */
    suspend fun writeBlock(tag: Tag, pos: Byte, data: ByteArray): ByteArray? {
        if (data.size != blocklen) {
            throw RuntimeException("Wrong size of payload (must be $blocklen bytes).")
        }

        // 1. Send the Unlock handshake for Libre 2
        unlockLibre2(tag)

        // 2. Prepare the actual write command
        val cmd = byteArrayOf(
            0x22, // flags: addressed (= UID field present) and high data rate mode
            0x21, // write single block
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, // placeholder for tag UID
            pos
        ) + data
        System.arraycopy(tag.id, 0, cmd, 2, 8)

        // 3. Execute the write
        AsyncNFCTask(tag).asyncRun(cmd)?.let {
            return checkError(it)
        }
        return null
    }

    /**
     * Write multiple blocks via the corresponding NFC command.
     */
    suspend fun writeMultipleBlocks(tag: Tag, pos: Byte, data: ByteArray): ByteArray? {
        if (data.size < blocklen || data.size % blocklen != 0) {
            throw RuntimeException("Wrong size of payload (must be divisible by $blocklen bytes).")
        }

        // 1. Send the Unlock handshake for Libre 2
        unlockLibre2(tag)

        val cmd = byteArrayOf(
            0x22, // flags: addressed (= UID field present) and high data rate mode
            0x24, // write multiple blocks
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, // placeholder for tag UID
            pos,
            (data.size / blocklen - 1).toByte()
        ) + data
        System.arraycopy(tag.id, 0, cmd, 2, 8)

        AsyncNFCTask(tag).asyncRun(cmd)?.let {
            return checkError(it)
        }
        return null
    }

    /**
     * Read a single block via the corresponding NFC command.
     */
    suspend fun readBlock(tag: Tag, pos: Byte): ByteArray? {
        // Unlock first for reading too
        unlockLibre2(tag)

        val cmd = byteArrayOf(
             0x22, // flags: addressed (= UID field present) and high data rate mode
             0x20, // read single block
             0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, // placeholder for tag UID
             pos)
        System.arraycopy(tag.id, 0, cmd, 2, 8)
        AsyncNFCTask(tag).asyncRun(cmd)?.let {
            return checkError(it)
        }
        return null
    }

    /**
     * Read multiple blocks via the corresponding NFC command.
     */
    suspend fun readMultipleBlocks(tag: Tag, pos: Byte, count: Byte): ByteArray? {
        // Unlock first
        unlockLibre2(tag)

        val cmd = byteArrayOf(
            0x22, // flags: addressed (= UID field present) and high data rate mode
            0x23, // read multiple blocks
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, // placeholder for tag UID
            pos,
            (count - 1).toByte()
        )
        System.arraycopy(tag.id, 0, cmd, 2, 8)
        AsyncNFCTask(tag).asyncRun(cmd)?.let {
            return checkError(it)
        }
        return null
    }

    /**
     * Check the status flags of a raw NFC response.
     */
    fun checkError(msg: ByteArray): ByteArray? {
        if (msg.isNotEmpty() && msg[0] == 0.toByte()) {
            return msg.sliceArray(1 until msg.size)
        }
        return null
    }
}