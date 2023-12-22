package com.unex.musicgo.espresso

import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.hamcrest.core.AllOf
import org.junit.Rule
import org.junit.Test
import com.unex.musicgo.R
import com.unex.musicgo.ui.activities.LoginActivity
import org.junit.Before

class CUE6UITest {
    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @Before
    fun login() {
        sleepThread(1)
        // Valid Credentials
        Espresso.onView(ViewMatchers.withId(R.id.username)).perform(
            ViewActions.clearText(),
            ViewActions.typeText("music@go.com"),
            ViewActions.closeSoftKeyboard()
        )
        Espresso.onView(ViewMatchers.withId(R.id.password_tv)).perform(
            ViewActions.clearText(),
            ViewActions.typeText("123456"),
            ViewActions.closeSoftKeyboard()
        )
        Espresso.onView(ViewMatchers.withId(R.id.login_btn)).perform(ViewActions.click())
        sleepThread(2)
    }

    @Test
    fun testAddSongToPlaylist() {
        Espresso.onView(ViewMatchers.withId(R.id.top_list)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.create_playlist_btn)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.brush_icon_playlist)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.playlist_name))
            .perform(ViewActions.typeText("Test Playlist"), ViewActions.closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.brush_icon_description)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.playlist_description))
            .perform(ViewActions.typeText("Test Description"), ViewActions.closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.brush_icon_playlist)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.brush_icon_description)).perform(ViewActions.click())
        sleepThread(1)


        Espresso.onView(ViewMatchers.withId(R.id.home_icon)).perform(ViewActions.click())
        sleepThread(1)

        Espresso.onView(ViewMatchers.withId(R.id.searchView)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.isAssignableFrom(EditText::class.java))
            .perform(ViewActions.typeText("herald of darkness"), ViewActions.pressImeActionButton())
        sleepThread(2)

        sleepThread(1)
        Espresso.onView(AllOf.allOf(ViewMatchers.withId(R.id.rv_song_list)))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, ClickViewWithId(R.id.song_options)))
        sleepThread(1)

        Espresso.onView(ViewMatchers.withText(R.string.save_on_playlist_menu))
            .inRoot(RootMatchers.isPlatformPopup())
            .perform(ViewActions.click())

        sleepThread(2)

        Espresso.onView(AllOf.allOf(ViewMatchers.withId(R.id.recycler_view)))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, ViewActions.click()))
        sleepThread(1)

        Espresso.onView(ViewMatchers.withId(R.id.top_list)).perform(ViewActions.click())
        Espresso.onView(AllOf.allOf(ViewMatchers.withId(R.id.recycler_view)))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, ViewActions.click()))
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

}

