package com.unex.musicgo.espresso

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.unex.musicgo.ui.activities.SignupActivity
import org.junit.Rule
import org.junit.Test
import com.unex.musicgo.R
import org.junit.Before


class CUNE2UItest {

    @get:Rule
    val activityRule = ActivityScenarioRule(SignupActivity::class.java)

    @Before
    fun createAccount() {
        Espresso.onView(ViewMatchers.withId(R.id.username))
            .perform(ViewActions.typeText("dummy_name1"), ViewActions.closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.usersurname))
            .perform(ViewActions.typeText("dummy_surname1"), ViewActions.closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.usermail)).perform(
            ViewActions.typeText("test_email@dummy1.com"), ViewActions.closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.userpassword))
            .perform(ViewActions.typeText("dummy_pass1"), ViewActions.closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.login_btn)).perform(ViewActions.scrollTo(), ViewActions.click())
    }

    @Test
    fun deleteAccount (){
        sleepThread(1)

        Espresso.onView(ViewMatchers.withId(R.id.settings_icon)).perform(ViewActions.click())
        sleepThread(1)

        Espresso.onView(ViewMatchers.withId(R.id.delete_account_info)).perform(ViewActions.click())
        sleepThread(1)

        Espresso.onView(withText(R.string.dialog_delete_account_title)).inRoot(isDialog()).check(matches(isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.confirm_button)).inRoot(isDialog()).perform(ViewActions.click())
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