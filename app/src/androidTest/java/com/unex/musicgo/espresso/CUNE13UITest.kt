package com.unex.musicgo.espresso

import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.pressImeActionButton
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.hamcrest.core.AllOf.allOf
import org.junit.Rule
import org.junit.Test
import com.unex.musicgo.R
import com.unex.musicgo.ui.activities.LoginActivity
import org.junit.Before

class CUNE13UITest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @Before
    fun login() {
        sleepThread(1)
        // Valid Credentials
        onView(withId(R.id.username)).perform(
            ViewActions.clearText(),
            typeText("music@go.com"),
            ViewActions.closeSoftKeyboard()
        )
        onView(withId(R.id.password_tv)).perform(
            ViewActions.clearText(),
            typeText("123456"),
            ViewActions.closeSoftKeyboard()
        )
        onView(withId(R.id.login_btn)).perform(click())
        sleepThread(2)
    }

    @Test
    fun testSongStop() {
        onView(withId(R.id.searchView)).perform(click())
        onView(isAssignableFrom(EditText::class.java))
            .perform(typeText("mi gran noche"), pressImeActionButton())
        sleepThread(3)

        onView(allOf(withId(R.id.rv_song_list)))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
        sleepThread(1)

        onView(withId(R.id.play_button)).perform(click())
        sleepThread(4)
        onView(withId(R.id.stop_button)).perform(click())
        sleepThread(1)
        onView(withId(R.id.play_button)).perform(click())
        sleepThread(2)
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