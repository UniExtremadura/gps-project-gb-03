package com.unex.musicgo.espresso

import android.widget.EditText
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.unex.musicgo.ui.activities.HomeActivity
import org.junit.Rule
import org.junit.Test
import com.unex.musicgo.R

class CUNE8UITest {

    @get:Rule
    val activityRule = ActivityScenarioRule(HomeActivity::class.java)

    @Test
    fun testSongSearch() {

        sleepThread(1)

        onView(withId(R.id.searchView)).perform(ViewActions.click())
        onView(ViewMatchers.isAssignableFrom(EditText::class.java))
            .perform(ViewActions.typeText("despacito"), ViewActions.pressImeActionButton())
        sleepThread(4)
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