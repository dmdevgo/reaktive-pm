package dev.garage.rpm.navigation

import android.app.Activity

class ActivityNavigationMessageDispatcher(
    activity: Activity
) : NavigationMessageDispatcher(activity) {

    override fun getParent(node: Any?): Any? = null
}