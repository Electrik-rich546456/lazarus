package com.diafyt.lazarus.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.diafyt.lazarus.R
import kotlinx.android.synthetic.main.fragment_temperature.*

class TemperatureFragment : Fragment(R.layout.fragment_temperature) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    // Called by BLE handler
    fun updateData(data: ByteArray) {
        if (checkError(data)) {
            showInfoSnack("Sensor Error")
            return
        }
        
        val rawTemp = littleEndianDecode(data)
        val celsius = rawTemp / 10.0
        val fahrenheit = (celsius * 1.8) + 32
        
        activity?.runOnUiThread {
            // Now calling the local function below
            val hex = bytesToHex(data) 
            // Update your UI components here if they exist in your XML
        }
    }

    // --- SELF-CONTAINED HELPERS (No external files needed) ---

    private fun bytesToHex(bytes: ByteArray?): String {
        if (bytes == null) return ""
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun checkError(data: ByteArray): Boolean {
        if (data.isEmpty()) return true
        return false
    }

    private fun littleEndianDecode(data: ByteArray): Int {
        if (data.size < 2) return 0
        return (data[0].toInt() and 0xFF) or ((data[1].toInt() and 0xFF) shl 8)
    }

    private fun showInfoSnack(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
    
    // Placeholder functions to satisfy remaining legacy references
    private fun showInfoDialog(title: String, msg: String) {}
    private fun retrieveProgramKey() {}
    private fun checkError(): Boolean = false
    var thermometerProgramKey: String = ""
    
    private fun celsiusToFahrenheit(celsius: Double): Double {
        return (celsius * 1.8) + 32
    }
}
