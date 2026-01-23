package com.diafyt.lazarus.utils

object HexUtils {
    fun hexToBytes(hex: String): ByteArray {
        val s = hex.replace(" ", "").replace(":", "")
        val result = ByteArray(s.length / 2)
        for (i in result.indices) {
            result[i] = s.substring(i * 2, i * 2 + 2).toInt(16).toByte()
        }
        return result
    }
}
