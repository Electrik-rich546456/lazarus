package com.diafyt.lazarus.utils

import android.nfc.Tag
import android.util.Log
import kotlinx.coroutines.delay

/**
 * Executes a sequence of NFC instructions to flash the sensor.
 */
class DeliveryPlan(private val instructions: List<Instruction>) {

    enum class InstructionType {
        WRITE, READ, UNLOCK
    }

    data class Instruction(val type: InstructionType, val block: Byte, val data: ByteArray)

    /**
     * Execute the plan on a given tag.
     */
    suspend fun deliver(tag: Tag): Boolean {
        Log.i(javaClass.name, "Starting delivery of ${instructions.size} instructions.")
        
        // OpenFreeStyle handshake: Ensure the Libre 2 is unlocked before we start the loop
        NFCUtil.unlockLibre2(tag)
        delay(50) // Wait for the sensor to enter the "session"

        instructions.forEachIndexed { index, instruction ->
            Log.d(javaClass.name, "Executing instruction $index: ${instruction.type} at block ${instruction.block}")
            
            val success = when (instruction.type) {
                InstructionType.WRITE -> {
                    // Small delay between blocks is crucial for Libre 2 stability
                    delay(20) 
                    NFCUtil.writeBlock(tag, instruction.block, instruction.data) != null
                }
                InstructionType.READ -> {
                    NFCUtil.readBlock(tag, instruction.block) != null
                }
                InstructionType.UNLOCK -> {
                    NFCUtil.unlockLibre2(tag)
                }
            }

            if (!success) {
                Log.e(javaClass.name, "Instruction $index failed! Aborting plan.")
                return false
            }
        }
        
        Log.i(javaClass.name, "Delivery plan completed successfully.")
        return true
    }

    companion object {
        /**
         * Create a plan from a string representation (usually thermometer-payload.txt).
         */
        fun create(text: String): DeliveryPlan {
            val instructions = mutableListOf<Instruction>()
            text.lines().forEach { line ->
                val parts = line.split(" ")
                if (parts.size >= 2) {
                    val type = when (parts[0]) {
                        "W" -> InstructionType.WRITE
                        "R" -> InstructionType.READ
                        "U" -> InstructionType.UNLOCK
                        else -> null
                    }
                    if (type != null) {
                        val block = parts[1].toInt(16).toByte()
                        val data = if (parts.size > 2) Util.hexToBytes(parts[2]) ?: byteArrayOf() else byteArrayOf()
                        instructions.add(Instruction(type, block, data))
                    }
                }
            }
            return DeliveryPlan(instructions)
        }
    }
}
