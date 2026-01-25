package com.diafyt.lazarus.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.diafyt.lazarus.R

class TutorialFragment : Fragment(R.layout.fragment_tutorial) {
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    // Fixes "Unresolved reference: showInfoSnack"
    private fun showInfoSnack(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
