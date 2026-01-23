package com.diafyt.lazarus.ui

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.diafyt.lazarus.R
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private var nfcAdapter: NfcAdapter? = null
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        viewPager = findViewById(R.id.pager)
        
        val adapter = MainFragmentStateAdapter(this)
        viewPager.adapter = adapter

        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when(position) {
                0 -> "Temperature"
                else -> "Programming"
            }
        }.attach()
    }

    override fun onResume() {
        super.onResume()
        val intent = Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        // Fixed: Removed FLAG_MUTABLE for compatibility with older SDKs
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, null, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        tag?.let {
            // Find the adapter to get the current fragment
            val adapter = viewPager.adapter as? MainFragmentStateAdapter
            // This calls the tag handler in your fragments
            (adapter?.fragmentStore?.get(1) as? ProgrammingFragment)?.onTagDetected(it)
        }
    }
}
