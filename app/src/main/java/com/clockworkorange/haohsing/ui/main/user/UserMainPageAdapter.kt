package com.clockworkorange.haohsing.ui.main.user

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.clockworkorange.haohsing.ui.main.user.account.UserAccountFragment
import com.clockworkorange.haohsing.ui.main.user.analysis.place.PlaceStatisticsFragment
import com.clockworkorange.haohsing.ui.main.user.main.UserMainFragment
import com.clockworkorange.haohsing.ui.main.user.notification.UserNotificationFragment

class UserMainPageAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> UserMainFragment.newInstance()
            1 -> PlaceStatisticsFragment.newInstance()
            2 -> UserNotificationFragment.newInstance()
            3 -> UserAccountFragment.newInstance()
            else -> throw IllegalArgumentException("wrong position")
        }
    }
}