package com.clockworkorange.haohsing.ui.main.engineer.main

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import com.clockworkorange.domain.entity.WorkOrderRequirement
import com.clockworkorange.domain.usecase.task.TaskStatusCategory
import com.clockworkorange.domain.usecase.task.TaskSummary
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.databinding.FragmentEngineerMainBinding
import com.clockworkorange.haohsing.ui.base.BaseFragment
import com.clockworkorange.haohsing.ui.main.engineer.EngineerMainNavFragment
import com.clockworkorange.haohsing.ui.main.engineer.EngineerMainNavFragmentDirections
import com.clockworkorange.haohsing.ui.main.engineer.SelectEngineerTabReceiver
import com.clockworkorange.haohsing.ui.main.engineer.main.calendar.MoreEventDecorator
import com.clockworkorange.haohsing.ui.main.engineer.main.calendar.OneEventDecorator
import com.clockworkorange.haohsing.ui.main.engineer.main.calendar.TwoEventDecorator
import com.clockworkorange.haohsing.ui.widget.SpacesItemDecoration
import com.clockworkorange.haohsing.utils.GenericAdapterListener
import com.clockworkorange.haohsing.utils.ScreenUtils
import com.clockworkorange.haohsing.utils.loadImage
import com.prolificinteractive.materialcalendarview.CalendarDay
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import okhttp3.internal.platform.android.AndroidLogHandler.setFilter
import timber.log.Timber


@AndroidEntryPoint
class EngineerMainFragment : BaseFragment(), GenericAdapterListener<TaskSummary>,
    SelectEngineerTabReceiver.Listener {

    private var _binding: FragmentEngineerMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EngineerMainViewModel by viewModels()

    private val receiver = SelectEngineerTabReceiver(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocalBroadcastManager.getInstance(requireContext().applicationContext)
            .registerReceiver(receiver, SelectEngineerTabReceiver.createIntentFilter())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEngineerMainBinding.inflate(layoutInflater)
        initView()
        initListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
        binding.tvUnfinished.callOnClick()
        binding.btToday.callOnClick()
    }

    private fun initView() {
        binding.calendarView.setTitleFormatter { day ->
            return@setTitleFormatter "%04d-%02d".format(day.year, day.month)
        }
        binding.rvTask.adapter = TaskSummaryAdapter(this)
        binding.rvTask.addItemDecoration(
            SpacesItemDecoration(
                bottom = ScreenUtils.dp2px(
                    requireContext(),
                    8
                )
            )
        )
    }

    private fun initListener() {
        binding.calendarView.setOnDateChangedListener { _, date, _ ->
            Timber.d("setOnDateChangedListener")
            viewModel.selectDay(date)
        }
        binding.calendarView.setOnMonthChangedListener { widget, date ->
            Timber.d("setOnMonthChangedListener")
            viewModel.selectFirstDayInMonth(date)
        }

        binding.btToday.setOnClickListener {
            val today = CalendarDay.today()
            binding.calendarView.currentDate = today
            viewModel.selectDay(today)
        }

        binding.tvUnfinished.setOnClickListener {
            viewModel.filterTaskStatus(TaskStatusCategory.UnFinish)
        }
        binding.tvFinished.setOnClickListener {
            viewModel.filterTaskStatus(TaskStatusCategory.Finish)
        }
        binding.tvError.setOnClickListener {
            viewModel.filterTaskStatus(TaskStatusCategory.Error)
        }

        binding.ivImage.setOnClickListener {
            EngineerIdCardDialog.newInstance()
                .show(childFragmentManager, "id_card")
        }

        binding.btAdd.setOnClickListener {
            findNavController().navigate(R.id.action_nav_to_engineer_pair_device)
        }
    }

    private fun unSelectAllTaskType() {
        with(binding) {
            tvUnfinished.setBackgroundResource(R.drawable.shape_round_white)
            tvFinished.setBackgroundResource(R.drawable.shape_round_white)
            tvError.setBackgroundResource(R.drawable.shape_round_white)

            val color = ContextCompat.getColor(requireContext(), R.color.colorLightText)
            tvUnfinished.setTextColor(color)
            tvFinished.setTextColor(color)
            tvError.setTextColor(color)
        }
    }

    private fun bindViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { observeUiState() }
                launch { observeNavAction() }
                launch { viewModel.toastFlow.collect { showToast(it) } }

                launch {
                    viewModel.uiState.mapNotNull { it.selectedDay }
                        .distinctUntilChanged()
                        .collect {
                            binding.run {
                                tvTaskListDate.text =
                                    "%d.%02d.%02d".format(it.year, it.month, it.day)
                                calendarView.currentDate = it   // 可滑動日曆
                                calendarView.selectedDate = it
                            }
                        }
                }

                launch {
                    viewModel.uiState.mapNotNull { it.monthTaskCount }
                        .distinctUntilChanged()
                        .collect { processCalendarEventDot(it) }
                }
            }
        }
    }

    private suspend fun observeUiState() {
        viewModel.uiState.collect { uiState ->
            binding.tvName.text = uiState.userName
            binding.ivImage.loadImage(uiState.userImage)
            (binding.rvTask.adapter as? TaskSummaryAdapter)?.submitList(uiState.selectedDayTaskSummaryList)
            setFilterView(uiState.filter)
        }
    }

    private fun setFilterView(filter: TaskStatusCategory) {
        when(filter) {
            TaskStatusCategory.UnFinish -> {
                unSelectAllTaskType()
                binding.tvUnfinished.setBackgroundResource(R.drawable.shape_round_008ed3)
                binding.tvUnfinished.setTextColor(Color.WHITE)
            }
            TaskStatusCategory.Finish -> {
                unSelectAllTaskType()
                binding.tvFinished.setBackgroundResource(R.drawable.shape_round_008ed3)
                binding.tvFinished.setTextColor(Color.WHITE)
            }
            TaskStatusCategory.Error -> {
                unSelectAllTaskType()
                binding.tvError.setBackgroundResource(R.drawable.shape_round_008ed3)
                binding.tvError.setTextColor(Color.WHITE)
            }
        }
    }

    private fun processCalendarEventDot(monthTaskCount: List<EngineerMainViewModel.MonthTaskCount>) {
        if (monthTaskCount.isEmpty()) return
        val dot1Days = monthTaskCount.filter { it.taskCount == 1 }.map { it.day }
        val dot2Days = monthTaskCount.filter { it.taskCount == 2 }.map { it.day }
        val dotMoreDays = monthTaskCount.filter { it.taskCount > 2 }.map { it.day }

        binding.calendarView.apply {
            removeDecorators()
            addDecorator(OneEventDecorator(dot1Days))
            addDecorator(TwoEventDecorator(dot2Days))
            addDecorator(MoreEventDecorator(dotMoreDays))
            invalidateDecorators()
        }
    }

    private suspend fun observeNavAction() {

    }

    override fun onItemClick(item: TaskSummary) {
        val dest = if (item.requirement == WorkOrderRequirement.Install) {
            EngineerMainNavFragmentDirections.actionEngineerMainNavFragmentToTaskDetailInstallFragment(
                item.taskId
            )
        } else {
            EngineerMainNavFragmentDirections.actionEngineerMainNavFragmentToTaskDetailFragment(item.taskId)
        }
        findNavController().navigate(dest)
    }

    override fun onSelectTab(tab: EngineerMainNavFragment.Tab?, intent: Intent?) {
        if (tab == EngineerMainNavFragment.Tab.Home) {
            val taskId =
                intent?.getIntExtra(SelectEngineerTabReceiver.KEY_TASK_ID, -1)
            if (taskId != null) {
                viewModel.selectDayByTaskId(taskId)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(requireContext().applicationContext)
            .unregisterReceiver(receiver)
    }

    companion object {
        fun newInstance() = EngineerMainFragment()
    }

}