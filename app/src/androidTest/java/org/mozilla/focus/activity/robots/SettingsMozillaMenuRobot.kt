/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.focus.activity.robots

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.uiautomator.UiScrollable
import androidx.test.uiautomator.UiSelector
import org.mozilla.focus.helpers.TestHelper
import org.mozilla.focus.helpers.TestHelper.waitingTime

class SettingsMozillaMenuRobot {
    fun verifyMozillaMenuItems() {
        mozillaSettingsList.waitForExists(waitingTime)
        showTipsSwitch.check(matches(isDisplayed()))
        aboutFocusPageLink.check(matches(isDisplayed()))
        helpPageLink.check(matches(isDisplayed()))
        rightsPageLink.check(matches(isDisplayed()))
        privacyNoticeLink.check(matches(isDisplayed()))
    }

    class Transition
}

private val mozillaSettingsList =
    UiScrollable(UiSelector().resourceId("${TestHelper.packageName}:id/recycler_view"))

private val showTipsSwitch = onView(withText("Show home screen tips"))

private val aboutFocusPageLink = onView(withText("About Firefox Focus"))

private val helpPageLink = onView(withText("Help"))

private val rightsPageLink = onView(withText("Your Rights"))

private val privacyNoticeLink = onView(withText("Privacy Notice"))
