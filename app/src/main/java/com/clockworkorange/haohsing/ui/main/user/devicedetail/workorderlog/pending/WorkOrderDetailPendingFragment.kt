package com.clockworkorange.haohsing.ui.main.user.devicedetail.workorderlog.pending

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
import com.clockworkorange.domain.entity.ManufacturerInfo
import com.clockworkorange.domain.usecase.task.toDisplayString
import com.clockworkorange.domain.usecase.workorder.PendingWorkOrderDetail
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.databinding.FragmentWorkOrderDetailPendingBinding
import com.clockworkorange.haohsing.ui.base.BaseFragment
import com.clockworkorange.haohsing.ui.base.NavUp
import com.clockworkorange.haohsing.ui.dialog.MessageDialog
import com.clockworkorange.haohsing.ui.main.user.MainActivity
import com.clockworkorange.haohsing.ui.main.user.devicedetail.requireworkorder.AddEdit
import com.clockworkorange.haohsing.ui.widget.setTextOnly
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WorkOrderDetailPendingFragment : BaseFragment() {

    private var _binding: FragmentWorkOrderDetailPendingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WorkOrderDetailPendingViewModel by viewModels()

    private val args by navArgs<WorkOrderDetailPendingFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkOrderDetailPendingBinding.inflate(layoutInflater)
        initView()
        initListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
        viewModel.setOrderId(args.orderId)
        viewModel.setDeviceId(args.deviceId)
    }

    private fun initView() {
        binding.ilToolbar.tvTitle.text = "待處理"
        binding.ilRequirement.setTextOnly("需求")
        binding.ilDeliveryDate.setTextOnly("期望到府日")
        binding.ilDeliveryTime.setTextOnly("期望到府時間")

        (requireActivity() as? MainActivity)?.setLightStatusBar()
    }

    private fun initListener() {
        binding.ilToolbar.btBack.setOnClickListener { findNavController().navigateUp() }

        binding.btEdit.setOnClickListener {
            val dest =
                WorkOrderDetailPendingFragmentDirections.actionWorkOrderDetailPendingFragmentToRequireMaintainFragment(
                    args.deviceId,
                    AddEdit.Edit,
                    args.orderId
                )
            findNavController().navigate(dest)
        }
        binding.btCancel.setOnClickListener { showCancelWorkOrderDialog() }
    }

    private fun showCancelWorkOrderDialog() {
        MessageDialog.newInstance(
            MessageDialog.IconType.Alert,
            "確定取消此單嗎？",
            message = "取消後，廠商將不進一步處理",
            button1Style = MessageDialog.ButtonStyle.Outline,
            button1Title = "保留",
            button2Style = MessageDialog.ButtonStyle.FillRed,
            button2Title = getString(R.string.cancel),
            button2Listener = { viewModel.cancelWorkOrder() }
        ).show(childFragmentManager, "confirm_cancel")
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
            uiState.pendingWorkOrderDetail?.let { fillWorkOrderDetail(it) }
            uiState.manufacturerInfo?.let { fillManufacturerInfo(it) }
            binding.btEdit.isVisible = uiState.isOwner
            binding.btCancel.isVisible = uiState.isOwner
        }
    }

    private fun fillWorkOrderDetail(workOrder: PendingWorkOrderDetail) {
        binding.ilRequirement.tvInfo.text = workOrder.requirement.displayString
        binding.ilDeliveryDate.tvInfo.text =
            convertSelectWeekDaysToDisplayString(workOrder.deliveryDate)
        binding.ilDeliveryTime.tvInfo.text = workOrder.deliveryTime
        binding.tvFailScenario.text = workOrder.errorReasons.toDisplayString()
        binding.tvNote.text = workOrder.note?.takeIf { it.isNotEmpty() } ?: "未填寫"
    }

    private fun convertSelectWeekDaysToDisplayString(deliveryDate: List<Int>): String {
        return buildString {
            append("星期")
            deliveryDate.forEachIndexed { index, weekDay ->
                when (weekDay) {
                    1 -> append("一")
                    2 -> append("二")
                    3 -> append("三")
                    4 -> append("四")
                    5 -> append("五")
                    6 -> append("六")
                    7 -> append("日")
                }
                if (index != deliveryDate.size - 1) {
                    append("、")
                }
            }
        }
    }


    private fun fillManufacturerInfo(manufacturerInfo: ManufacturerInfo) {
        binding.ilManufacturerInfo.tvName.text = manufacturerInfo.name
        binding.ilManufacturerInfo.tvPhone.text = manufacturerInfo.phone
    }

    private suspend fun observeNavAction() {
        viewModel.navAction.collect {
            if (it is NavUp) {
                findNavController().navigateUp()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (requireActivity() as? MainActivity)?.setDarkStatusBar()
        _binding = null
    }
}