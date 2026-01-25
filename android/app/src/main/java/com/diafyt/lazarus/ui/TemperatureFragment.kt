package com.diafyt.lazarus.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.diafyt.lazarus.R
import com.diafyt.lazarus.utils.HexUtils // Import is crucial
import kotlinx.android.synthetic.main.fragment_temperature.*

class TemperatureFragment : Fragment(R.layout.fragment_temperature) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    // Called by BLE handler or UI updates
    fun updateData(data: ByteArray) {
        if (checkError(data)) {
            showInfoSnack("Sensor Error")
            return
        }
        val rawTemp = littleEndianDecode(data)
        val celsius = rawTemp / 10.0
        val fahrenheit = celsiusToFahrenheit(celsius)
        
        activity?.runOnUiThread {
            // FIX: Added 'HexUtils.' before bytesToHex
            val hex = HexUtils.bytesToHex(data) 
            
            // If you have a text view for this, uncomment below:
            // temp_display.text = "Temp: $celsius°C\nData: $hex"
        }
    }

    // --- HELPER FUNCTIONS ---

    private fun checkError(data: ByteArray): Boolean {
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
    
    // Legacy placeholders to satisfy the compiler
    private fun showInfoDialog(title: String, msg: String) {}
    private fun retrieveProgramKey() {}
    var thermometerProgramKey: String = ""
}
