package com.lunesu.pengchauferry.ui.ferry

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

import com.lunesu.pengchauferry.R

class PagerFragment : Fragment() {

    companion object {
        fun newInstance() = PagerFragment()
    }

    private lateinit var ferryPierAdapter: FerryPierAdapter
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.pager_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ferryPierAdapter = FerryPierAdapter(this)
        viewPager = view.findViewById(R.id.pager)
        viewPager.adapter = ferryPierAdapter

        val piers = STRINGS.values.toList()
        val tabLayout = view.findViewById<TabLayout>(R.id.tab_layout_from)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = getString(piers[position])
        }.attach()
    }

}

