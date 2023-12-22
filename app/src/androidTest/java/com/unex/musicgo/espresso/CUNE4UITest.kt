package com.unex.musicgo.espresso

import android.view.View
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.pressImeActionButton
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.AllOf.allOf
import org.junit.Rule
import org.junit.Test
import com.unex.musicgo.R
import com.unex.musicgo.ui.activities.LoginActivity
import org.junit.Before

class CUNE4UITest {

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
    fun testSongDetails() {
        onView(withId(R.id.searchView)).perform(click())
        onView(isAssignableFrom(EditText::class.java))
            .perform(typeText("despacito"), pressImeActionButton())
        sleepThread(2)

        onView(allOf(withId(R.id.rv_song_list)))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
        sleepThread(1)
        scrollToBottom()
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

    private fun scrollToBottom(): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return isAssignableFrom(View::class.java)
            }

            override fun getDescription(): String {
                return "Scrolling to bottom of the view"
            }

            override fun perform(uiController: UiController, view: View) {
                view.scrollTo(0, view.bottom)
                uiController.loopMainThreadUntilIdle()
            }
        }
    }
}