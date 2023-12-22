package com.unex.musicgo.espresso

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.unex.musicgo.ui.activities.LoginActivity
import org.junit.Rule
import org.junit.Test
import com.unex.musicgo.R

class CUE1UITest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @Test
    fun testLogin() {

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
        sleepThread(2)

    }
    @Test
    fun invalidTestLogin() {

        // Credentials
        Espresso.onView(ViewMatchers.withId(R.id.username)).perform(
            ViewActions.clearText(),
            ViewActions.typeText("notregistered@email.com"),
            ViewActions.closeSoftKeyboard()
        )
        Espresso.onView(ViewMatchers.withId(R.id.password_tv)).perform(
            ViewActions.clearText(),
            ViewActions.typeText("123456"),
            ViewActions.closeSoftKeyboard()
        )

        Espresso.onView(ViewMatchers.withId(R.id.login_btn)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.login_btn)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

    }

    @Test
    fun testRegistrationEmptyName() {

        Espresso.onView(ViewMatchers.withId(R.id.register_btn)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.username))
            .perform(ViewActions.typeText(""), ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withId(R.id.usersurname))
            .perform(ViewActions.typeText("test_surname"), ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withId(R.id.usermail)).perform(
            ViewActions.typeText("test_email@example.com"),
            ViewActions.closeSoftKeyboard()
        )
        Espresso.onView(ViewMatchers.withId(R.id.userpassword))
            .perform(ViewActions.typeText("test_pass"), ViewActions.closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.login_btn)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.login_btn)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testRegistrationEmptySurname() {

        Espresso.onView(ViewMatchers.withId(R.id.register_btn)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.username))
            .perform(ViewActions.typeText("test_name"), ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withId(R.id.usersurname))
            .perform(ViewActions.typeText(""), ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withId(R.id.usermail)).perform(
            ViewActions.typeText("test_email@example.com"),
            ViewActions.closeSoftKeyboard()
        )
        Espresso.onView(ViewMatchers.withId(R.id.userpassword))
            .perform(ViewActions.typeText("test_pass"), ViewActions.closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.login_btn)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.login_btn)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testRegistrationEmptyEmail() {

        Espresso.onView(ViewMatchers.withId(R.id.register_btn)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.username))
            .perform(ViewActions.typeText("test_name"), ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withId(R.id.usersurname))
            .perform(ViewActions.typeText("test_surname"), ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withId(R.id.usermail))
            .perform(ViewActions.typeText(""), ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withId(R.id.userpassword))
            .perform(ViewActions.typeText("test_pass"), ViewActions.closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.login_btn)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.login_btn)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testRegistrationRight() {

        Espresso.onView(ViewMatchers.withId(R.id.register_btn)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.username))
            .perform(ViewActions.typeText("test_name1"), ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withId(R.id.usersurname))
            .perform(ViewActions.typeText("test_surname1"), ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withId(R.id.usermail)).perform(
            ViewActions.typeText("test_email@example1.com"),
            ViewActions.closeSoftKeyboard()
        )
        Espresso.onView(ViewMatchers.withId(R.id.userpassword))
            .perform(ViewActions.typeText("test_pass1"), ViewActions.closeSoftKeyboard())


        Espresso.onView(ViewMatchers.withId(R.id.login_btn)).perform(ViewActions.click())
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