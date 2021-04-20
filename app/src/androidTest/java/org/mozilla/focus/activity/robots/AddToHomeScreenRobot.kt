package org.mozilla.focus.activity.robots

import androidx.test.uiautomator.UiScrollable
import androidx.test.uiautomator.UiSelector
import org.mozilla.focus.helpers.TestHelper
import org.mozilla.focus.helpers.TestHelper.mDevice
import org.mozilla.focus.helpers.TestHelper.packageName

class AddToHomeScreenRobot {

    fun handleAddAutomaticallyDialog() {
        addAutomaticallyBtn.waitForExists(TestHelper.waitingTime)
        addAutomaticallyBtn.click()
        addAutomaticallyBtn.waitUntilGone(TestHelper.waitingTime)
    }

    fun addShortcutWithTitle(title: String) {
        shortcutTitle.waitForExists(TestHelper.waitingTime)
        shortcutTitle.clearTextField()
        shortcutTitle.text = title
        addToHSOKBtn.click()
    }

    fun addShortcutNoTitle() {
        shortcutTitle.waitForExists(TestHelper.waitingTime)
        shortcutTitle.clearTextField()
        addToHSOKBtn.click()
    }

    class Transition {
        // Searches a page shortcut on the device homescreen
        fun searchAndOpenHomeScreenShortcut(title: String, interact: BrowserRobot.() -> Unit): BrowserRobot.Transition {
            mDevice.pressHome()

            fun deviceHomeScreen() =
                UiScrollable(UiSelector().resourceId("com.google.android.apps.nexuslauncher:id/workspace"))
            deviceHomeScreen().setAsHorizontalList()
            deviceHomeScreen().scrollTextIntoView(title)

            fun shortcut() = mDevice.findObject(UiSelector().text(title))
            shortcut().clickAndWaitForNewWindow()

            BrowserRobot().interact()
            return BrowserRobot.Transition()
        }
    }
}

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
        .resourceId("${packageName}:id/edit_title")
)