package com.cooksmobilesolutions.apexmaprotations.testUtils

import android.view.View
import androidx.annotation.Nullable
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import org.hamcrest.Matcher


object ViewPager2Actions {
    private const val DEFAULT_SMOOTH_SCROLL = false
    /**
     * Moves [ViewPager2] to the right by one page.
     */
    /**
     * Moves [ViewPager2] to the right by one page.
     */
    @JvmOverloads
    fun scrollRight(smoothScroll: Boolean = DEFAULT_SMOOTH_SCROLL): ViewAction {
        return object : ViewPagerScrollAction() {
            override fun getDescription(): String {
                return "ViewPager2 move one page to the right"
            }

            override fun performScroll(viewPager: ViewPager2?) {
                val current = viewPager?.currentItem
                if (current != null) {
                    viewPager.setCurrentItem(current + 1, smoothScroll)
                }
            }
        }
    }
    /**
     * Moves [ViewPager2] to the left by one page.
     */
    /**
     * Moves [ViewPager2] to the left be one page.
     */
    @JvmOverloads
    fun scrollLeft(smoothScroll: Boolean = DEFAULT_SMOOTH_SCROLL): ViewAction {
        return object : ViewPagerScrollAction() {
            override fun getDescription(): String {
                return "ViewPager2 move one page to the left"
            }

            override fun performScroll(viewPager: ViewPager2?) {
                val current = viewPager?.currentItem
                if (current != null) {
                    viewPager.setCurrentItem(current - 1, smoothScroll)
                }
            }
        }
    }
    /**
     * Moves [ViewPager2] to the last page.
     */
    /**
     * Moves [ViewPager2] to the last page.
     */
    @JvmOverloads
    fun scrollToLast(smoothScroll: Boolean = DEFAULT_SMOOTH_SCROLL): ViewAction {
        return object : ViewPagerScrollAction() {
            override fun getDescription(): String {
                return "ViewPager2 move to last page"
            }

            override fun performScroll(viewPager: ViewPager2?) {
                val size = viewPager?.adapter!!.itemCount
                if (size > 0) {
                    viewPager.setCurrentItem(size - 1, smoothScroll)
                }
            }
        }
    }
    /**
     * Moves [ViewPager2] to the first page.
     */
    /**
     * Moves [ViewPager2] to the first page.
     */
    @JvmOverloads
    fun scrollToFirst(smoothScroll: Boolean = DEFAULT_SMOOTH_SCROLL): ViewAction {
        return object : ViewPagerScrollAction() {
            override fun getDescription(): String {
                return "ViewPager2 move to first page"
            }

            override fun performScroll(viewPager: ViewPager2?) {
                val size = viewPager?.adapter!!.itemCount
                if (size > 0) {
                    viewPager.setCurrentItem(0, smoothScroll)
                }
            }
        }
    }
    /**
     * Moves [ViewPager2] to specific page.
     */
    /**
     * Moves [ViewPager2] to a specific page.
     */
    @JvmOverloads
    fun scrollToPage(page: Int, smoothScroll: Boolean = DEFAULT_SMOOTH_SCROLL): ViewAction {
        return object : ViewPagerScrollAction() {
            override fun getDescription(): String {
                return "ViewPager2 move to page"
            }

            override fun performScroll(viewPager: ViewPager2?) {
                viewPager?.setCurrentItem(page, smoothScroll)
            }
        }
    }

    /**
     * [ViewPager2] listener that serves as Espresso's [IdlingResource] and notifies the
     * registered callback when the [ViewPager2] gets to SCROLL_STATE_IDLE state.
     */
    private class CustomViewPager2Listener : OnPageChangeCallback(),
        IdlingResource {
        private var mCurrState = ViewPager2.SCROLL_STATE_IDLE

        @Nullable
        private var mCallback: IdlingResource.ResourceCallback? = null
        var mNeedsIdle = false
        override fun registerIdleTransitionCallback(resourceCallback: IdlingResource.ResourceCallback) {
            mCallback = resourceCallback
        }

        override fun getName(): String {
            return "ViewPager2 listener"
        }

        override fun isIdleNow(): Boolean {
            return if (!mNeedsIdle) {
                true
            } else {
                mCurrState == ViewPager2.SCROLL_STATE_IDLE
            }
        }

        override fun onPageSelected(position: Int) {
            if (mCurrState == ViewPager2.SCROLL_STATE_IDLE) {
                if (mCallback != null) {
                    mCallback!!.onTransitionToIdle()
                }
            }
        }

        override fun onPageScrollStateChanged(state: Int) {
            mCurrState = state
            if (mCurrState == ViewPager2.SCROLL_STATE_IDLE) {
                if (mCallback != null) {
                    mCallback!!.onTransitionToIdle()
                }
            }
        }

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
        }
    }

    private abstract class ViewPagerScrollAction : ViewAction {
        override fun getConstraints(): Matcher<View> {
            return isDisplayed()
        }

        override fun perform(uiController: UiController, view: View) {
            val viewPager = view as ViewPager2

            // Add a custom tracker listener
            val customListener = CustomViewPager2Listener()
            viewPager.registerOnPageChangeCallback(customListener)

            // Note that we're running the following block in a try-finally construct.
            // This is needed since some of the actions are going to throw (expected) exceptions.
            // If that happens, we still need to clean up after ourselves
            // to leave the system (Espresso) in a good state.
            try {
                // Register our listener as idling resource so that Espresso waits until the
                // wrapped action results in the ViewPager2 getting to the SCROLL_STATE_IDLE state
                IdlingRegistry.getInstance().register(customListener)
                uiController.loopMainThreadUntilIdle()
                performScroll(view)
                uiController.loopMainThreadUntilIdle()
                customListener.mNeedsIdle = true
                uiController.loopMainThreadUntilIdle()
                customListener.mNeedsIdle = false
            } finally {
                // Unregister our idling resource
                IdlingRegistry.getInstance().unregister(customListener)
                // And remove our tracker listener from ViewPager2
                viewPager.unregisterOnPageChangeCallback(customListener)
            }
        }

        protected abstract fun performScroll(viewPager: ViewPager2?)
    }
}