/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.focus.navigation

import org.mozilla.focus.R
import org.mozilla.focus.activity.MainActivity
import org.mozilla.focus.biometrics.BiometricAuthenticationDialogFragment
import org.mozilla.focus.fragment.BrowserFragment
import org.mozilla.focus.fragment.FirstrunFragment
import org.mozilla.focus.fragment.UrlInputFragment
import org.mozilla.focus.session.ui.TabSheetFragment
import org.mozilla.focus.utils.ViewUtils

/**
 * Class performing the actual navigation in [MainActivity] by performing fragment transactions if
 * needed.
 */
class MainActivityNavigation(
    private val activity: MainActivity
) {
    /**
     * Home screen.
     */
    fun home() {
        val fragmentManager = activity.supportFragmentManager
        val browserFragment = fragmentManager.findFragmentByTag(BrowserFragment.FRAGMENT_TAG) as BrowserFragment?

        val isShowingBrowser = browserFragment != null
        val crashReporterIsVisible = browserFragment?.crashReporterIsVisible() ?: false

        if (isShowingBrowser && !crashReporterIsVisible) {
            ViewUtils.showBrandedSnackbar(activity.findViewById(android.R.id.content),
                R.string.feedback_erase,
                activity.resources.getInteger(R.integer.erase_snackbar_delay))
        }

        // We add the url input fragment to the layout if it doesn't exist yet.
        val transaction = fragmentManager
            .beginTransaction()

        // We only want to play the animation if a browser fragment is added and resumed.
        // If it is not resumed then the application is currently in the process of resuming
        // and the session was removed while the app was in the background (e.g. via the
        // notification). In this case we do not want to show the content and remove the
        // browser fragment immediately.
        val shouldAnimate = isShowingBrowser && browserFragment!!.isResumed

        if (shouldAnimate) {
            transaction.setCustomAnimations(0, R.anim.erase_animation)
        }

        // Currently this callback can get invoked while the app is in the background. Therefore we are using
        // commitAllowingStateLoss() here because we can't do a fragment transaction while the app is in the
        // background - like we already do in showBrowserScreenForCurrentSession().
        // Ideally we'd make it possible to pause observers while the app is in the background:
        // https://github.com/mozilla-mobile/android-components/issues/876
        transaction
            .replace(R.id.container, UrlInputFragment.createWithoutSession(), UrlInputFragment.FRAGMENT_TAG)
            .commitAllowingStateLoss()
    }

    /**
     * Show browser for tab with the given [tabId].
     */
    fun browser(tabId: String, showTabs: Boolean) {
        val fragmentManager = activity.supportFragmentManager

        val urlInputFragment = fragmentManager.findFragmentByTag(UrlInputFragment.FRAGMENT_TAG) as UrlInputFragment?
        if (urlInputFragment != null) {
            fragmentManager
                .beginTransaction()
                .remove(urlInputFragment)
                .commitAllowingStateLoss()
        }

        val browserFragment = fragmentManager.findFragmentByTag(BrowserFragment.FRAGMENT_TAG) as BrowserFragment?
        if (browserFragment == null || browserFragment.tab.id != tabId) {
            fragmentManager
                .beginTransaction()
                .replace(R.id.container, BrowserFragment.createForTab(tabId), BrowserFragment.FRAGMENT_TAG)
                .commitAllowingStateLoss()
        }

        val tabsTrayFragment = fragmentManager.findFragmentByTag(TabSheetFragment.FRAGMENT_TAG) as TabSheetFragment?
        if (tabsTrayFragment == null && showTabs) {
            fragmentManager
                .beginTransaction()
                .add(R.id.container, TabSheetFragment(), TabSheetFragment.FRAGMENT_TAG)
                .commitAllowingStateLoss()
        } else if (tabsTrayFragment != null && !showTabs) {
            fragmentManager
                .beginTransaction()
                .remove(tabsTrayFragment)
                .commitAllowingStateLoss()
        }
    }

    /**
     * Edit URL of tab with the given [tabId].
     */
    fun edit(
        tabId: String,
        x: Int,
        y: Int,
        width: Int,
        height: Int
    ) {
        val urlFragment = UrlInputFragment.createWithTab(tabId, x, y, width, height)

        activity.supportFragmentManager
            .beginTransaction()
            .add(R.id.container, urlFragment, UrlInputFragment.FRAGMENT_TAG)
            .commit()
    }

    /**
     * Show first run onboarding.
     */
    fun firstRun() {
        activity.supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, FirstrunFragment.create(), FirstrunFragment.FRAGMENT_TAG)
            .commit()
    }

    /**
     * Lock app.
     */
    fun lock() {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {
            throw IllegalStateException("Trying to lock unsupported device")
        }

        val fragmentManager = activity.supportFragmentManager
        if (fragmentManager.findFragmentByTag(BiometricAuthenticationDialogFragment.FRAGMENT_TAG) != null) {
            return
        }

        val transaction = fragmentManager
            .beginTransaction()

        fragmentManager.fragments.forEach { fragment ->
            transaction.remove(fragment)
        }

        BiometricAuthenticationDialogFragment()
            .show(transaction, BiometricAuthenticationDialogFragment.FRAGMENT_TAG)
    }
}
