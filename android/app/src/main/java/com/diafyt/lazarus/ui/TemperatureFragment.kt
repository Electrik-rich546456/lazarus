package com.diafyt.lazarus.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.diafyt.lazarus.R
import com.diafyt.lazarus.utils.HexUtils
import kotlinx.android.synthetic.main.fragment_temperature.*

class TemperatureFragment : Fragment(R.layout.fragment_temperature) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Basic UI init
    }

    // Called by BLE handler
    fun updateData(data: ByteArray) {
        if (checkError(data)) {
            showInfoSnack("Sensor Error")
            return
        }
        val rawTemp = littleEndianDecode(data)
        val celsius = rawTemp / 10.0
        val fahrenheit = celsiusToFahrenheit(celsius)
        
        activity?.runOnUiThread {
            // Using HexUtils for the hex conversion
            val hex = HexUtils.bytesToHex(data)
            // Assuming you have a TextView id 'temp_display'
            // temp_display.text = "$celsius C / $fahrenheit F\nRaw: $hex"
        }
    }

    // --- MISSING HELPERS RE-ADDED BELOW ---

    private fun checkError(data: ByteArray): Boolean {
        // If data is empty or FF FF, it's an error
        if (data.isEmpty()) return true
        return false
    }

    private fun littleEndianDecode(data: ByteArray): Int {
        if (data.size < 2) return 0
        return (data[0].toInt() and 0xFF) or ((data[1].toInt() and 0xFF) shl 8)
    }

    private fun celsiusToFahrenheit(celsius: Double): Double {
        return (celsius * 1.8) + 32
    }

    private fun showInfoSnack(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
    
    private fun showInfoDialog(title: String, msg: String) {
        // Simple placeholder to pass build
    }
    
    private fun retrieveProgramKey() {
        // Placeholder
    }
    
    // Legacy property required by your build
    var thermometerProgramKey: String = ""
}
