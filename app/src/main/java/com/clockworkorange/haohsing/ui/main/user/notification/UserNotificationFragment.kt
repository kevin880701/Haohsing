package com.clockworkorange.haohsing.ui.main.user.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import com.clockworkorange.domain.entity.UserInfo
import com.clockworkorange.domain.usecase.notification.Duration
import com.clockworkorange.domain.usecase.notification.Notification
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.databinding.FragmentUserNotificationBinding
import com.clockworkorange.haohsing.ui.base.BaseFragment
import com.clockworkorange.haohsing.ui.dialog.SelectItemDialog
import com.clockworkorange.haohsing.ui.main.engineer.EngineerMainNavFragment
import com.clockworkorange.haohsing.ui.main.engineer.SelectEngineerTabReceiver
import com.clockworkorange.haohsing.ui.main.engineer.main.taskdetail.TaskDetailFragmentArgs
import com.clockworkorange.haohsing.ui.main.user.SelectUserTabReceiver
import com.clockworkorange.haohsing.ui.main.user.UserMainNavFragment
import com.clockworkorange.haohsing.ui.main.user.devicedetail.DeviceDetailFragmentArgs
import com.clockworkorange.haohsing.ui.main.user.devicedetail.workorderlog.scheduled.WorkOrderDetailScheduledFragmentArgs
import com.clockworkorange.haohsing.ui.widget.SpacesItemDecoration
import com.clockworkorange.haohsing.utils.GenericAdapterListener
import com.clockworkorange.haohsing.utils.ScreenUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserNotificationFragment : BaseFragment(), DurationSelectorAdapter.Listener,
    GenericAdapterListener<UserNotificationViewModel.UiNotification> {

    private var _binding: FragmentUserNotificationBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserNotificationViewModel by viewModels()

    private var inviteHeaderAdapter: InviteHeaderAdapter? = null
    private var inviteAdapter: InviteAdapter? = null
    private var durationSelectorAdapter: DurationSelectorAdapter? = null
    private var notificationAdapter: NotificationAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserNotificationBinding.inflate(layoutInflater)
        initView()
        initListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
    }

    private fun initView() {
        binding.ilToolbar.tvTitle.text = "通知"
        binding.ilToolbar.btBack.isVisible = false
        inviteHeaderAdapter = InviteHeaderAdapter()
        inviteAdapter = InviteAdapter()
        durationSelectorAdapter = DurationSelectorAdapter(this)
        notificationAdapter = NotificationAdapter(this)
        binding.rvItem.adapter = ConcatAdapter(
            inviteHeaderAdapter,
            inviteAdapter,
            durationSelectorAdapter,
            notificationAdapter
        )
        binding.rvItem.addItemDecoration(
            SpacesItemDecoration(
                bottom = ScreenUtils.dp2px(
                    requireContext(),
                    8
                )
            )
        )
    }

    private fun initListener() {
        binding.btMarkRead.setOnClickListener {
            viewModel.markAllNotificationAsRead()
        }
    }

    override fun onDurationSelectorClick() {
        val items = arrayOf(
            "近一個月",
            "近兩個月",
            "近三個月"
        )

        SelectItemDialog.newInstance("時間", items, getString(R.string.finished)) {
            val duration = when (it) {
                items[0] -> Duration.Recent1Month
                items[1] -> Duration.Recent2Month
                items[2] -> Duration.Recent3Month
                else -> throw IllegalArgumentException("")
            }
            viewModel.setNotificationDuration(duration)
        }.show(childFragmentManager, "duration_selector")
    }

    private fun bindViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { observeUiState() }
                launch { observeNavAction() }
                launch { viewModel.toastFlow.collect { showToast(it) } }
            }
        }
    }

    private suspend fun observeUiState() {
        viewModel.uiState.collect { uiState ->
            binding.ilLoading.root.isVisible = uiState.isLoading
            durationSelectorAdapter?.duration = uiState.duration
            inviteHeaderAdapter?.isVisible = uiState.inviteList.isNotEmpty()
            inviteAdapter?.submitList(uiState.inviteList)
            notificationAdapter?.submitList(uiState.notificationList)
        }
    }

    private suspend fun observeNavAction() {

    }

    override fun onItemClick(item: UserNotificationViewModel.UiNotification) {
        val isEngineer = viewModel.uiState.value.role == UserInfo.Role.Engineer
        when (item.notification.type) {
            Notification.Type.Alert -> {
                val deviceId = item.notification.deviceId
                if (deviceId != null) {
                    val deviceIdList = viewModel.uiState.value.devicesIds
                    findNavController().navigate(
                        R.id.deviceDetailFragment,
                        DeviceDetailFragmentArgs(deviceIdList.toIntArray(), deviceId).toBundle()
                    )
                }
            }
            Notification.Type.DataAnalysis -> {
                LocalBroadcastManager.getInstance(requireContext().applicationContext)
                    .sendBroadcast(SelectUserTabReceiver.createIntent(UserMainNavFragment.Tab.DataAnalysis))
            }
            Notification.Type.Maintenance -> {
                if (isEngineer) {
                    navigateToHome(item)
                } else {
                    val orderId = item.notification.taskId
                    if (orderId != null) {
                        findNavController().navigate(
                            R.id.workOrderDetailScheduledFragment,
                            WorkOrderDetailScheduledFragmentArgs(orderId).toBundle()
                        )
                    }
                }
            }
            Notification.Type.TaskDelay -> {
                navigateToHome(item)
            }
            else -> {}
        }
    }

    private fun navigateToHome(item: UserNotificationViewModel.UiNotification) {
        val taskId = item.notification.taskId
        if (taskId != null) {

            // 透過 EngineerMainNavFragment 的 receiver 回到 home page
            // 透過 EngineerMainFragment 的 receiver 切換至特定日期
            LocalBroadcastManager.getInstance(requireContext().applicationContext)
                .sendBroadcast(
                    SelectEngineerTabReceiver.createTaskIntent(
                        EngineerMainNavFragment.Tab.Home,
                        taskId
                    )
                )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        inviteHeaderAdapter = null
        inviteAdapter = null
        durationSelectorAdapter = null
        notificationAdapter = null
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = UserNotificationFragment()
    }
}