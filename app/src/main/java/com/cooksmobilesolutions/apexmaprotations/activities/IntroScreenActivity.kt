package com.cooksmobilesolutions.apexmaprotations.activities

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.cooksmobilesolutions.apexmaprotations.R
import com.cooksmobilesolutions.apexmaprotations.databinding.IntroScreenLayoutBinding
import com.cooksmobilesolutions.apexmaprotations.fragments.introScreens.NavigationIntroFragment
import com.cooksmobilesolutions.apexmaprotations.fragments.introScreens.RefreshDataIntroFragment
import com.cooksmobilesolutions.apexmaprotations.fragments.introScreens.SetAlarmsIntroFragment
import com.cooksmobilesolutions.apexmaprotations.util.SHOW_INTO_SCREEN_KEY
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val INTRO_SCREEN_NUM_PAGES = 3

@AndroidEntryPoint
class IntroScreenActivity : AppCompatActivity() {
    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private val binding: IntroScreenLayoutBinding by lazy {
        IntroScreenLayoutBinding.inflate(this.layoutInflater)
    }

    private val viewPager: ViewPager2 by lazy {
        findViewById(R.id.IntroScreenViewPager)
    }

    override fun onBackPressed() {
        if (viewPager.currentItem != 0) {
            viewPager.currentItem = viewPager.currentItem - 1
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val pagerAdapter = IntroScreenViewPageAdapter(this)
        viewPager.adapter = pagerAdapter
        binding.finishIntroButton.setOnClickListener {
            sharedPreferences.edit().putBoolean(SHOW_INTO_SCREEN_KEY, false).apply()
            finish()
        }
    }

    inner class IntroScreenViewPageAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

        override fun getItemCount(): Int = INTRO_SCREEN_NUM_PAGES

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> {
                    NavigationIntroFragment()
                }
                1 -> {
                    RefreshDataIntroFragment()
                }
                2 -> {
                    SetAlarmsIntroFragment()
                }
                else -> NavigationIntroFragment()
            }
        }
    }


}