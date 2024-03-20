package com.example.fashionsapp


import HomeFragment
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.fashionsapp.databinding.ActivityMainBinding
import com.example.fashionsapp.databinding.LayoutTitleTabBinding
import com.example.fashionsapp.tab.fragment.AccountFragment
import com.example.fashionsapp.tab.fragment.NotifyFragment
import com.example.fashionsapp.tab.fragment.ShoppingFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding

    private lateinit var adapter: FragmentPageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        adapter = FragmentPageAdapter(supportFragmentManager, lifecycle)

        binding?.viewPager?.adapter = adapter

        binding?.tabLayout?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    binding?.viewPager?.currentItem = tab.position
                    tab.customView?.findViewById<TextView>(R.id.tv_number)?.visibility = View.VISIBLE
//                    updateViewTabbar(tab.position)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.customView?.findViewById<TextView>(R.id.tv_number)?.visibility = View.GONE
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })
        binding?.viewPager?.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding?.tabLayout?.selectTab(binding?.tabLayout?.getTabAt(position))
            }
        })

        setNavigationTabbar(
            R.drawable.home,
            R.drawable.shopping,
            R.drawable.notify,
            R.drawable.account
        )
    }

    private fun updateViewTabbar(position: Int) {
        when (position) {
            0 -> {
                setNavigationTabbar(
                    R.drawable.home_tab,
                    R.drawable.shopping,
                    R.drawable.notify,
                    R.drawable.account
                )
            }

            1 -> {
                setNavigationTabbar(
                    R.drawable.home,
                    R.drawable.shop_tab,
                    R.drawable.notify,
                    R.drawable.account
                )
            }

            2 -> {
                setNavigationTabbar(
                    R.drawable.home,
                    R.drawable.shopping,
                    R.drawable.notify_tab,
                    R.drawable.account
                )
            }

            3 -> {
                setNavigationTabbar(
                    R.drawable.home,
                    R.drawable.shopping,
                    R.drawable.notify,
                    R.drawable.account_tab
                )
            }
        }
    }

    private fun setNavigationTabbar(
        iconHome: Int,
        iconShop: Int,
        iconNotify: Int,
        iconAccount: Int
    ) {
        TabLayoutMediator(binding?.tabLayout!!, binding?.viewPager!!) { tab, position ->
            when (position) {
                0 -> {
                    val tabHome =
                        LayoutTitleTabBinding.inflate(LayoutInflater.from(applicationContext))
                    tabHome.ivIcon.setImageResource(iconHome)
                    tab.customView = tabHome.root
                }

                1 -> {
                    val tabShop =
                        LayoutTitleTabBinding.inflate(LayoutInflater.from(applicationContext))
                    tabShop.ivIcon.setImageResource(iconShop)
                    tab.customView = tabShop.root
                }

                2 -> {
                    val tabNotification =
                        LayoutTitleTabBinding.inflate(LayoutInflater.from(applicationContext))
                    tabNotification.ivIcon.setImageResource(iconNotify)
                    tab.customView = tabNotification.root
                }

                3 -> {
                    val tabAccount =
                        LayoutTitleTabBinding.inflate(LayoutInflater.from(applicationContext))
                    tabAccount.ivIcon.setImageResource(iconAccount)
                    tab.customView = tabAccount.root
                }
            }
        }.attach()
    }

}