package com.unex.musicgo.espresso

import android.view.View
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.pressImeActionButton
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.unex.musicgo.ui.activities.LoginActivity
import org.hamcrest.Matcher
import org.hamcrest.core.AllOf.allOf
import org.junit.Rule
import org.junit.Test
import com.unex.musicgo.R

class CUE9UITest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @Test
    fun testFavorite (){
        // Valid Credentials
        onView(withId(R.id.username)).perform(typeText("music@go.com"), closeSoftKeyboard())
        onView(withId(R.id.password_tv)).perform(typeText("123456"), closeSoftKeyboard())

        // Click on login and verify the transition
        onView(withId(R.id.login_btn)).perform(click())
        sleepThread(2)

        testRateSongs("Herald of Darkness", "Masterpiece")


    }

    private fun testRateSongs (songName: String, comment: String){

        onView(withId(R.id.searchView)).perform(click())
        onView(isAssignableFrom(EditText::class.java))
            .perform(typeText(songName), pressImeActionButton())

        sleepThread(3)

        onView(allOf(withId(R.id.rv_song_list)))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0,
                    click()
                ))
        sleepThread(1)

        // Write a comment
        onView(withId(R.id.scroll_view)).perform(scrollToBottom())
        onView(withId(R.id.new_comment_field)).perform(typeText(comment), closeSoftKeyboard())
        sleepThread(4)

        onView(withId(R.id.home_icon)).perform(click())
    }




    private fun sleepThread(seconds: Int) {
        try {
            val milliseconds = seconds * 1000L
            Thread.sleep(milliseconds)
        } catch (e: InterruptedException) {
            e.printStackTrace()
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