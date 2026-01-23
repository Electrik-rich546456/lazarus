package com.diafyt.lazarus.ui

import android.nfc.Tag
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.diafyt.lazarus.R
import com.diafyt.lazarus.utils.DeliveryPlan
import com.diafyt.lazarus.utils.NFCUtil
import kotlinx.android.synthetic.main.fragment_programming.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProgrammingFragment : Fragment(R.layout.fragment_programming) {

    // Main entry point for NFC Tags
    fun onTagDetected(tag: Tag) {
        statusTitle.text = "Patching Sensor..."
        lifecycleScope.launch(Dispatchers.IO) {
            val result = NFCUtil.flashAndVerify(tag, DeliveryPlan.getLazarusPayload())
            withContext(Dispatchers.Main) {
                logOutput.text = result
                if (result.contains("Unlock OK")) {
                    statusTitle.text = "Flash Success"
                } else {
                    statusTitle.text = "Flash Failed"
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        statusTitle.text = "Ready for Libre 2"
    }
}
