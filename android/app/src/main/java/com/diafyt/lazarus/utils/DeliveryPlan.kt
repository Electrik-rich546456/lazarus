package com.diafyt.lazarus.utils

object DeliveryPlan {
    // These delays are crucial for the MSP430 FRAM to process writes
    const val PRE_LOOP_DELAY = 100L
    const val INTER_BLOCK_DELAY = 50L

    fun getLazarusPayload(): Map<Int, ByteArray> {
        return mapOf(
            // Block 39: Signature that tells the app "I am a Lazarus Sensor"
            39 to byteArrayOf(0x01.toByte(), 0x80.toByte(), 0x00.toByte(), 0x00.toByte(), 
                               0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()),
            
            // Block 40: Your custom Patch data
            40 to byteArrayOf(0xDE.toByte(), 0xAD.toByte(), 0xBE.toByte(), 0xEF.toByte(), 
                               0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte())
        )
    }
}
