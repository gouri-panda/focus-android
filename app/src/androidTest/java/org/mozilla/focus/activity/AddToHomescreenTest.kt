/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.mozilla.focus.activity

import android.os.Build
import android.os.Build.VERSION
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mozilla.focus.activity.robots.browserScreen
import org.mozilla.focus.activity.robots.homeScreen
import org.mozilla.focus.activity.robots.searchScreen
import org.mozilla.focus.helpers.MainActivityFirstrunTestRule
import org.mozilla.focus.helpers.TestHelper.readTestAsset
import org.mozilla.focus.helpers.TestHelper.webPageLoadwaitingTime
import java.io.IOException

/**
 * Tests to verify the functionality of Add to homescreen from the main menu
 */
@RunWith(AndroidJUnit4ClassRunner::class)
class AddToHomescreenTest {
    private lateinit var webServer: MockWebServer

    @get: Rule
    var mActivityTestRule = MainActivityFirstrunTestRule(showFirstRun = false)

    @Before
    fun setup() {
        webServer = MockWebServer()
        try {
            webServer.enqueue(
                MockResponse()
                    .setBody(readTestAsset("plain_test.html"))
            )
            webServer.start()
        } catch (e: IOException) {
            throw AssertionError("Could not start web server", e)
        }
    }

    @After
    fun tearDown() {
        try {
            webServer.shutdown()
        } catch (e: IOException) {
            throw AssertionError("Could not stop web server", e)
        }
    }

    @Test
    fun addToHomeScreenTest() {
        val pageUrl = webServer.url("").toString()
        val pageTitle = "test1"

        // Open website, and click 'Add to homescreen'
        searchScreen {
        }.loadPage(pageUrl) {
            progressBar.waitUntilGone(webPageLoadwaitingTime)
        }.openThreeDotMenu {
            openAddToHSDialog()
            addShortcutWithTitle(pageTitle)
            if (VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                handleAddAutomaticallyDialog()
            }
        }
        homeScreen {
        }.searchAndOpenHomeScreenShortcut(pageTitle) {
            verifyPageURL(pageUrl)
        }
    }

    @Test
    fun noNameShortcutTest() {
        val pageUrl = webServer.url("").toString()

        // Open website, and click 'Add to homescreen'
        searchScreen {
        }.loadPage(pageUrl) {
        }.openThreeDotMenu {
            // leave shortcut title empty and add it to HS
            openAddToHSDialog()
            addShortcutNoTitle()
            if (VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                handleAddAutomaticallyDialog()
            }
        }
        homeScreen {
        }.searchAndOpenHomeScreenShortcut(webServer.hostName) {
            verifyPageURL(pageUrl)
        }
    }

    @Test
    fun eraseDataAndOpenShortcut() {
        val pageUrl = webServer.url("").toString()
        val pageTitle = "test2"

        // Open website, and click 'Add to homescreen'
        searchScreen {
        }.loadPage(pageUrl) {
            progressBar.waitUntilGone(webPageLoadwaitingTime)
        }.openThreeDotMenu {
            openAddToHSDialog()
            addShortcutWithTitle(pageTitle)
            if (VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                handleAddAutomaticallyDialog()
            }
        }
        // clear data and open the page shortcut
        browserScreen {
        }.clearBrowsingData {
        }.searchAndOpenHomeScreenShortcut(pageTitle) {
            verifyPageURL(pageUrl)
        }
    }
}
