package com.clockworkorange.haohsing.ui.main.engineer

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.clockworkorange.haohsing.ui.main.engineer.install.EngineerInstallRecordFragment
import com.clockworkorange.haohsing.ui.main.engineer.main.EngineerMainFragment
import com.clockworkorange.haohsing.ui.main.engineer.task.EngineerTaskFragment
import com.clockworkorange.haohsing.ui.main.user.account.UserAccountFragment
import com.clockworkorange.haohsing.ui.main.user.notification.UserNotificationFragment

class EngineerMainPageAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 5

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> EngineerMainFragment.newInstance()
            1 -> EngineerTaskFragment.newInstance()
            2 -> EngineerInstallRecordFragment.newInstance()
            3 -> UserNotificationFragment.newInstance()
            4 -> UserAccountFragment.newInstance()
            else -> throw IllegalArgumentException("wrong position")
        }
    }
}