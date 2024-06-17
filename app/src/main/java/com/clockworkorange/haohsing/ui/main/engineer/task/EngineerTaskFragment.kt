package com.clockworkorange.haohsing.ui.main.engineer.task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import com.clockworkorange.domain.entity.WorkOrderRequirement
import com.clockworkorange.domain.usecase.notification.Duration
import com.clockworkorange.domain.usecase.task.TaskSummary
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.databinding.FragmentEngineerTaskBinding
import com.clockworkorange.haohsing.ui.base.BaseFragment
import com.clockworkorange.haohsing.ui.dialog.MessageInputDialog
import com.clockworkorange.haohsing.ui.dialog.SelectItemDialog
import com.clockworkorange.haohsing.ui.main.engineer.main.taskdetail.TaskDetailFragmentArgs
import com.clockworkorange.haohsing.ui.main.engineer.main.taskdetailinstall.TaskDetailInstallFragmentArgs
import com.clockworkorange.haohsing.ui.main.user.notification.DurationSelectorAdapter
import com.clockworkorange.haohsing.ui.widget.SpacesItemDecoration
import com.clockworkorange.haohsing.utils.GenericAdapterListener
import com.clockworkorange.haohsing.utils.ScreenUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EngineerTaskFragment : BaseFragment(), GenericAdapterListener<TaskSummary>,
    DurationSelectorAdapter.Listener {

    private var _binding: FragmentEngineerTaskBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EngineerTaskViewModel by viewModels()

    private var errorTaskAdapter: UiTaskSummaryAdapter? = null
    private var durationSelectorAdapter: DurationSelectorAdapter? = null
    private var normalTaskAdapter: UiTaskSummaryAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEngineerTaskBinding.inflate(layoutInflater)
        initView()
        initListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
    }

    private fun initView() {
        binding.ilToolbar.tvTitle.text = "任務紀錄"
        binding.ilToolbar.btBack.isVisible = false
        errorTaskAdapter = UiTaskSummaryAdapter(this)
        durationSelectorAdapter = DurationSelectorAdapter(this)
        normalTaskAdapter = UiTaskSummaryAdapter(this)

        binding.rvTaskHistory.addItemDecoration(
            SpacesItemDecoration(
                bottom = ScreenUtils.dp2px(
                    requireContext(),
                    8
                )
            )
        )
        binding.rvTaskHistory.adapter =
            ConcatAdapter(errorTaskAdapter, durationSelectorAdapter, normalTaskAdapter)

        binding.ilSearchbar.tvSearchKeyword.hint = "搜尋單號、姓名"
    }

    private fun initListener() {
        with(binding) {
            cgFilterRequirement.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.chip_all -> viewModel.clearFilterRequirement()
                    R.id.chip_repair -> viewModel.filterRequirement(WorkOrderRequirement.Repair)
                    R.id.chip_maintain -> viewModel.filterRequirement(WorkOrderRequirement.Maintain)
                    R.id.chip_install -> viewModel.filterRequirement(WorkOrderRequirement.Install)
                }
            }
            ilSearchbar.root.setOnClickListener { showInputSearchKeywordDialog() }
            ilSearchbar.btClearSearch.setOnClickListener { viewModel.clearSearch() }

        }
    }

    private fun showInputSearchKeywordDialog() {
        MessageInputDialog.createSearchDialog(requireContext()) {
            viewModel.searchTaskIdOrName(it)
        }.show(childFragmentManager, "keyword")
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
        viewModel.uiState.collect {
            errorTaskAdapter?.submitList(it.errorTaskList)
            durationSelectorAdapter?.duration = it.duration
            normalTaskAdapter?.submitList(it.normalTaskList)
            binding.tvEmpty.isVisible = it.normalTaskList.isEmpty()
            binding.ilSearchbar.tvSearchKeyword.text = it.searchKeyword ?: "搜尋單號、姓名"
            binding.ilSearchbar.btClearSearch.isVisible = it.searchKeyword != null

            binding.rvTaskHistory.smoothScrollToPosition(0)
        }
    }

    private suspend fun observeNavAction() {

    }

    override fun onItemClick(item: TaskSummary) {
        when (item.requirement) {
            WorkOrderRequirement.Install -> {
                findNavController().navigate(
                    R.id.taskDetailInstallFragment,
                    TaskDetailInstallFragmentArgs(item.taskId).toBundle()
                )
            }
            WorkOrderRequirement.Maintain, WorkOrderRequirement.Repair -> {
                findNavController().navigate(
                    R.id.taskDetailFragment,
                    TaskDetailFragmentArgs(item.taskId).toBundle()
                )
            }
            else -> {}
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
            viewModel.selectDuration(duration)
        }.show(childFragmentManager, "duration_selector")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = EngineerTaskFragment()
    }
}