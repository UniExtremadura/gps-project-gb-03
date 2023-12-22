package com.unex.musicgo.espresso

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.unex.musicgo.ui.activities.HomeActivity
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.AllOf.allOf
import org.junit.Rule
import org.junit.Test
import com.unex.musicgo.R

class CUNE11UITest {

    @get:Rule
    val activityRule = ActivityScenarioRule(HomeActivity::class.java)

    @Test
    fun testFilteredSong (){
        Espresso.onView(ViewMatchers.withId(R.id.discoverButton)).perform(ViewActions.click())
        // Interaction with searchBar and ApplyFilter button
        sleepThread(1)

        Espresso.onView(ViewMatchers.withId(R.id.searchBar)).perform(ViewActions.click())
        sleepThread(1)
        Espresso.onView(ViewMatchers.withId(R.id.searchBar)).perform(ViewActions.typeText("shakira"), ViewActions.pressImeActionButton())

        sleepThread(3)
        // Hacer clic en un elemento en una posición específica dentro del RecyclerView
        Espresso.onView(allOf(ViewMatchers.withId(R.id.rv_song_list), withHeight(1831)))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(1, ViewActions.click()))
		
		Espresso.onView(ViewMatchers.withId(R.id.btn_apply_filters)).perform(ViewActions.click())
    }

    private fun sleepThread(seconds: Int) {
        try {
            val milliseconds = seconds * 1000L
            Thread.sleep(milliseconds)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    private fun withHeight(height: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("with height: $height")
            }

            override fun matchesSafely(view: View): Boolean {
                return view.height == height
            }
        }
    }

}