package com.lunesu.pengchauferry.ui.ferry

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class FerryPierAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = STRINGS.size

    override fun createFragment(position: Int): Fragment {
        // Return a NEW fragment instance in createFragment(int)
        val fragment = FerryFragment.newInstance()
        fragment.arguments = Bundle().apply {
            putInt(FerryFragment.ARG_OBJECT, position)
        }
        return fragment
    }
}
