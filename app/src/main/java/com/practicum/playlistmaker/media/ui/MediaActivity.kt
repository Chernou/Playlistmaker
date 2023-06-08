package com.practicum.playlistmaker.media.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.utils.NavigationRouter
import com.practicum.playlistmaker.utils.ResourceProvider
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class MediaActivity : AppCompatActivity() {

    private lateinit var tabMediator: TabLayoutMediator
    private val resourceProvider: ResourceProvider by inject()
    private val router: NavigationRouter by inject {
        parametersOf(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)

        val toolbar = findViewById<Toolbar>(R.id.search_toolbar)
        toolbar.setNavigationOnClickListener {
            router.goBack()
        }

        val viewPager = findViewById<ViewPager2>(R.id.view_pager)
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        viewPager.adapter = MediaPagerAdapter(supportFragmentManager, lifecycle)
        tabMediator = TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = resourceProvider.getString(R.string.favorite_tracks)
                1 -> tab.text = resourceProvider.getString(R.string.playlists)
            }
        }
        tabMediator.attach()
    }

    override fun onDestroy() {
        super.onDestroy()
        tabMediator.detach()
    }
}