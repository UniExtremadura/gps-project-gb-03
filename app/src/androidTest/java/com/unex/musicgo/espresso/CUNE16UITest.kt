package com.unex.musicgo.espresso

import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.unex.musicgo.R
import com.unex.musicgo.ui.activities.LoginActivity
import org.hamcrest.core.AllOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CUNE16UITest {

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
    fun testGenerateStatistics() {
        //First Song
        playSong(5, "mi gran noche")
        //Second Song
        playSong(15, "que gueno que estoy")
        //Third Song
        playSong(10, "vino tinto")

        Espresso.onView(ViewMatchers.withId(R.id.profile)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.check_statistics_btn)).perform(ViewActions.click())
        sleepThread(3)
    }

    private fun playSong(time: Int, song:String) {
        sleepThread(1)
        //First Song
        Espresso.onView(ViewMatchers.withId(R.id.searchView)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.isAssignableFrom(EditText::class.java))
            .perform(ViewActions.typeText(song), ViewActions.pressImeActionButton())
        sleepThread(3)

        Espresso.onView(AllOf.allOf(ViewMatchers.withId(R.id.rv_song_list)))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0,
                    ViewActions.click()
                ))
        sleepThread(1)

        Espresso.onView(ViewMatchers.withId(R.id.play_button)).perform(ViewActions.click())
        sleepThread(time)
        Espresso.onView(ViewMatchers.withId(R.id.stop_button)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.home_icon)).perform(ViewActions.click())
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
