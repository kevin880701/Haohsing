package com.clockworkorange.haohsing.ui.main.user.devicedetail.workorderlog

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
import androidx.navigation.fragment.navArgs
import com.clockworkorange.domain.usecase.workorder.WorkOrderInfo
import com.clockworkorange.domain.usecase.workorder.WorkOrderStatus
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.databinding.FragmentWorkOrderLogBinding
import com.clockworkorange.haohsing.ui.base.BaseFragment
import com.clockworkorange.haohsing.ui.widget.SpacesItemDecoration
import com.clockworkorange.haohsing.utils.GenericAdapterListener
import com.clockworkorange.haohsing.utils.ScreenUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WorkOrderLogFragment : BaseFragment(), GenericAdapterListener<WorkOrderInfo> {

    private var _binding: FragmentWorkOrderLogBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WorkOrderLogViewModel by viewModels()

    private val args by navArgs<WorkOrderLogFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkOrderLogBinding.inflate(layoutInflater)
        initView()
        initListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
        viewModel.setDeviceId(args.deviceId)
    }

    private fun initView() {
        binding.ilToolbar.tvTitle.text = "維修保養紀錄"
        binding.ilEmpty.tvEmptyNote.text = "尚無紀錄"
        binding.rvItems.addItemDecoration(
            SpacesItemDecoration(
                bottom = ScreenUtils.dp2px(
                    requireContext(),
                    8
                )
            )
        )
        binding.rvItems.adapter = WorkOrderInfoAdapter(this)
    }

    private fun initListener() {
        binding.ilToolbar.btBack.setOnClickListener { findNavController().navigateUp() }
        binding.cgType.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.chip_pending -> viewModel.selectState(WorkOrderStatus.Pending)
                R.id.chip_schedule -> viewModel.selectState(WorkOrderStatus.Schedule)
                R.id.chip_finished -> viewModel.selectState(
                    WorkOrderStatus.Finished,
                    WorkOrderStatus.Cancel
                )
            }
        }
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
            (binding.rvItems.adapter as? WorkOrderInfoAdapter)?.submitList(uiState.workOrderInfoList)
            binding.ilEmpty.root.isVisible = uiState.workOrderInfoList.isEmpty()
        }
    }

    private suspend fun observeNavAction() {

    }

    override fun onItemClick(item: WorkOrderInfo) {
        val dest = when (item.status) {
            WorkOrderStatus.Pending -> WorkOrderLogFragmentDirections.actionWorkOrderLogFragmentToWorkOrderDetailPendingFragment(
                args.deviceId,
                item.id
            )
            WorkOrderStatus.Schedule -> WorkOrderLogFragmentDirections.actionWorkOrderLogFragmentToWorkOrderDetailScheduledFragment(
                item.id
            )
            WorkOrderStatus.Finished,
            WorkOrderStatus.Cancel -> WorkOrderLogFragmentDirections.actionWorkOrderLogFragmentToWorkOrderDetailFinishedFragment(
                args.deviceId,
                item.id
            )
        }
        findNavController().navigate(dest)
    }

    override fun onStart() {
        super.onStart()
        viewModel.refresh()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}