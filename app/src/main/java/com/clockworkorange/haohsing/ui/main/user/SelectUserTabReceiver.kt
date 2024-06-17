package com.clockworkorange.haohsing.ui.main.user

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter


class SelectUserTabReceiver(private val listener: Listener) : BroadcastReceiver() {

    companion object {
        private const val ACTION_SELECT_USER_TAB = "ACTION_SELECT_USER_TAB"
        private const val KEY_TAB = "TAB"

        fun createIntentFilter(): IntentFilter = IntentFilter(ACTION_SELECT_USER_TAB)

        fun createIntent(tab: UserMainNavFragment.Tab) = Intent(ACTION_SELECT_USER_TAB).apply {
            putExtra(KEY_TAB, tab)
        }
    }

    interface Listener {
        fun onSelectTab(tab: UserMainNavFragment.Tab?, intent: Intent?)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val tab = intent?.getSerializableExtra(KEY_TAB) as? UserMainNavFragment.Tab
        listener.onSelectTab(tab, intent)
    }
}