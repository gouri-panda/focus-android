/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.focus.activity.robots

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.uiautomator.UiSelector
import org.mozilla.focus.R
import org.mozilla.focus.helpers.TestHelper.mDevice
import org.mozilla.focus.helpers.TestHelper.packageName
import org.mozilla.focus.helpers.TestHelper.waitingTime

class ThreeDotMainMenuRobot {

    fun openAddToHSDialog() {
        addToHSmenuItem.waitForExists(waitingTime)
        // If the menu item is not clickable, wait and retry
        while (!addToHSmenuItem.isClickable) {
            mDevice.pressBack()
            threeDotMenuButton.perform(click())
        }
        addToHSmenuItem.click()
    }

    fun handleAddAutomaticallyDialog() {
        addAutomaticallyBtn.waitForExists(waitingTime)
        addAutomaticallyBtn.click()
        addAutomaticallyBtn.waitUntilGone(waitingTime)
    }

    fun addShortcutWithTitle(title: String) {
        shortcutTitle.waitForExists(waitingTime)
        shortcutTitle.clearTextField()
        shortcutTitle.text = title
        addToHSOKBtn.click()
    }

    fun addShortcutNoTitle() {
        shortcutTitle.waitForExists(waitingTime)
        shortcutTitle.clearTextField()
        addToHSOKBtn.click()
    }

    class Transition {
        fun openSettings(interact: SettingsRobot.() -> Unit): SettingsRobot.Transition {
            mDevice.findObject(UiSelector().text("Settings")).waitForExists(waitingTime)
            settingsMenuButton
                .check(matches(isDisplayed()))
                .perform(click())

            SettingsRobot().interact()
            return SettingsRobot.Transition()
        }
    }
}

private val settingsMenuButton = onView(ViewMatchers.withId(R.id.settings))

private val threeDotMenuButton = onView(ViewMatchers.withId(R.id.menuView))

private val addToHSmenuItem = mDevice.findObject(
    UiSelector()
        .resourceId("$packageName:id/add_to_homescreen")
)

private val addToHSOKBtn = mDevice.findObject(
    UiSelector()
        .resourceId("$packageName:id/addtohomescreen_dialog_add")
        .enabled(true)
)

private val addAutomaticallyBtn = mDevice.findObject(
    UiSelector()
        .className("android.widget.Button")
        .textContains("Add automatically")
)

private val shortcutTitle = mDevice.findObject(
    UiSelector()
        .resourceId("$packageName:id/edit_title")
)

