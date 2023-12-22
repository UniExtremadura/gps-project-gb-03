package com.unex.musicgo.espresso

import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.Matcher

class ClickViewWithId(private val id: Int) : ViewAction {
    override fun getConstraints(): Matcher<View> {
        return ViewMatchers.withId(id)
    }

    override fun getDescription(): String {
        return "Click on a view with specific id."
    }

    override fun perform(uiController: UiController, view: View) {
        val v = view.findViewById<View>(id)
        v.performClick()
    }
}