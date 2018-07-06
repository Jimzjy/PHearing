package io.github.phearing.phearing.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.viewpager.widget.ViewPager
import io.github.phearing.phearing.R
import io.github.phearing.phearing.common.ViewPagerFragmentAdapter
import io.github.phearing.phearing.ui.history.HistoryActivity
import io.github.phearing.phearing.ui.headphone.HeadphoneActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
        requestPermission()
    }

    private fun init() {
        val viewPagerAdapter = ViewPagerFragmentAdapter(supportFragmentManager)
        viewPagerAdapter.addFragment(TestFragment.newInstance(), resources.getString(R.string.audiometry))
        viewPagerAdapter.addFragment(NewsFragment.newInstance(), resources.getString(R.string.news))
        main_view_pager.adapter = viewPagerAdapter

        main_menu_iv.setOnClickListener {
            main_drawer_layout.openDrawer(GravityCompat.START)
        }

        main_navigation_view.setNavigationItemSelectedListener {
            //main_drawer_layout.closeDrawers()
            when(it.itemId) {
                R.id.menu_headphone -> {
                    startActivity(Intent(this@MainActivity, HeadphoneActivity::class.java))
                }
                R.id.menu_history -> {
                    startActivity(Intent(this@MainActivity, HistoryActivity::class.java))
                }
            }
            true
        }

        val barIcon = arrayOf(main_test_iv, main_news_iv)
        for (i in 1..(barIcon.size - 1)) {
            barIcon[i].setColorFilter(ContextCompat.getColor(applicationContext, R.color.colorPrimaryTrans))
        }
        main_view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                for (i in 0..(barIcon.size - 1)) {
                    if (i != position) {
                        barIcon[i].setColorFilter(ContextCompat.getColor(applicationContext, R.color.colorPrimaryTrans))
                    } else {
                        barIcon[i].colorFilter = null
                    }
                }
            }
        })

        for (i in 0..(barIcon.size - 1)) {
            barIcon[i].setOnClickListener {
                main_view_pager.currentItem = i
            }
        }
    }

    private fun requestPermission() {
        val permissions = arrayOf(Manifest.permission.RECORD_AUDIO)
        val notGetPermissionList = mutableListOf<String>()

        for (p in permissions) {
            if (ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) {
                notGetPermissionList.add(p)
            }
        }

        if (notGetPermissionList.size > 0) {
            ActivityCompat.requestPermissions(this, notGetPermissionList.toTypedArray(), 0)
        }
    }
}