package com.diafyt.lazarus.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class MainFragmentStateAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

    // This checks matches your log error: "Unresolved reference: fragmentStore"
    val fragmentStore = mutableMapOf<Int, Fragment>()

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        val fragment = when (position) {
            0 -> TemperatureFragment()
            1 -> ProgrammingFragment()
            else -> TutorialFragment()
        }
        fragmentStore[position] = fragment
        return fragment
    }
}
