package com.example.fashionsapp

import HomeFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.fashionsapp.tab.fragment.AccountFragment
import com.example.fashionsapp.tab.fragment.NotifyFragment
import com.example.fashionsapp.tab.fragment.ShoppingFragment

class FragmentPageAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle)
{
    override fun getItemCount(): Int {
       return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            1 -> ShoppingFragment()
            2 -> NotifyFragment()
            3 -> AccountFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}