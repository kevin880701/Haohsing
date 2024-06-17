package com.clockworkorange.haohsing.ui.main.engineer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter


class SelectEngineerTabReceiver(private val listener: Listener) : BroadcastReceiver() {

    companion object {
        private const val ACTION_SELECT_ENGINEER_TAB = "ACTION_SELECT_ENGINEER_TAB"
        private const val KEY_TAB = "TAB"
        const val KEY_TASK_ID = "TASK_ID"
        fun createIntentFilter(): IntentFilter = IntentFilter(ACTION_SELECT_ENGINEER_TAB)

        fun createTaskIntent(tab: EngineerMainNavFragment.Tab, taskId: Int) =
            Intent(ACTION_SELECT_ENGINEER_TAB).apply {
                putExtra(KEY_TAB, tab)
                putExtra(KEY_TASK_ID, taskId)
            }
    }

    interface Listener {
        fun onSelectTab(tab: EngineerMainNavFragment.Tab?, intent: Intent?)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val tab = intent?.getSerializableExtra(KEY_TAB) as? EngineerMainNavFragment.Tab
        listener.onSelectTab(tab, intent)
    }
}