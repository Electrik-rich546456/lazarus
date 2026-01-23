package com.diafyt.lazarus.utils

object HexUtils {
    fun bytesToHex(bytes: ByteArray?): String {
        if (bytes == null) return ""
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun hexToBytes(hex: String): ByteArray {
        val s = hex.replace(" ", "").replace(":", "")
        val result = ByteArray(s.length / 2)
        for (i in result.indices) {
            val index = i * 2
            result[i] = s.substring(index, index + 2).toInt(16).toByte()
        }
        return result
    }
}
