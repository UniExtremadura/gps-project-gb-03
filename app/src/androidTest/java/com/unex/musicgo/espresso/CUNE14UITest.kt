package com.unex.musicgo.espresso

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.unex.musicgo.ui.activities.HomeActivity
import org.hamcrest.core.AllOf
import org.junit.Rule
import org.junit.Test
import com.unex.musicgo.R
import org.junit.Before

class CUNE14UITest {

    @get:Rule
    val activityRule = ActivityScenarioRule(HomeActivity::class.java)

    @Before
    fun setup() {
        Espresso.onView(ViewMatchers.withId(R.id.top_list)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.create_playlist_btn)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.brush_icon_playlist)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.playlist_name))
            .perform(ViewActions.typeText("Test Playlist"), ViewActions.closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.brush_icon_description))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.playlist_description))
            .perform(ViewActions.typeText("Test Description"), ViewActions.closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.brush_icon_playlist)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.brush_icon_description))
            .perform(ViewActions.click())
        sleepThread(1)

        Espresso.onView(ViewMatchers.withId(R.id.top_list)).perform(ViewActions.click())
        sleepThread(1)
    }

    private fun sleepThread(seconds: Int) {
        try {
            val milliseconds = seconds * 1000L
            Thread.sleep(milliseconds)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
    @Test
    fun testDeletePlaylist(){
        Espresso.onView(AllOf.allOf(ViewMatchers.withId(R.id.recycler_view)))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, ViewActions.click()))
        sleepThread(1)

        Espresso.onView(ViewMatchers.withId(R.id.trash_icon)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.confirm_button))
            .inRoot(RootMatchers.isDialog())
            .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.top_list)).perform(ViewActions.click())
        sleepThread(1)

    }

}