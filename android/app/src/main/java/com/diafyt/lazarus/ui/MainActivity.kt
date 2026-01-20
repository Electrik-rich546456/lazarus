package com.diafyt.lazarus

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.diafyt.lazarus.ui.ProgrammingFragment

class MainActivity : AppCompatActivity() {

    private var nfcAdapter: NfcAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        
        // Load the Programming screen by default for testing
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, ProgrammingFragment())
            .commit()
    }

    override fun onResume() {
        super.onResume()
        // This ensures the app grabs the NFC tag before any other app
        val intent = Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, null, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    // This is called when you tap a sensor
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        tag?.let {
            // Find the ProgrammingFragment and give it the tag
            val fragment = supportFragmentManager.findFragmentById(R.id.container) as? ProgrammingFragment
            fragment?.onTagDetected(it)
        }
    }
}
