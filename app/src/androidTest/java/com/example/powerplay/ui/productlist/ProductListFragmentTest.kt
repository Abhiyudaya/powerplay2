package com.example.powerplay.ui.productlist

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.powerplay.MainActivity
import com.example.powerplay.R
import com.example.powerplay.ui.adapter.ProductAdapter
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class ProductListFragmentTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testRecyclerViewIsDisplayed() {
        onView(withId(R.id.recyclerViewProducts))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testSwipeRefreshLayoutIsDisplayed() {
        onView(withId(R.id.swipeRefreshLayout))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testClickOnRecyclerViewItem() {
        // Wait for data to load and then click on first item
        Thread.sleep(3000) // Simple wait for network call
        
        onView(withId(R.id.recyclerViewProducts))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<ProductAdapter.ProductViewHolder>(
                    0,
                    click()
                )
            )
        
        // Verify that we navigated to detail screen (activity should change)
        // This is a basic test - in a real scenario you'd verify the detail screen content
    }

    @Test
    fun testRetryButtonFunctionality() {
        // This test would need to mock network failure first
        // For now, just verify the retry button exists
        onView(withId(R.id.buttonRetry))
            .check(matches(isDisplayed()))
    }
}