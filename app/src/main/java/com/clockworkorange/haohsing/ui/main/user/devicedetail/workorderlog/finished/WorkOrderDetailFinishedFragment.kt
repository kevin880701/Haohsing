package com.clockworkorange.haohsing.ui.main.user.devicedetail.workorderlog.finished

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.clockworkorange.domain.entity.WorkOrderRequirement
import com.clockworkorange.domain.usecase.task.FormOption
import com.clockworkorange.domain.usecase.task.toDisplayString
import com.clockworkorange.domain.usecase.workorder.FinishedWorkOrderDetail
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.databinding.FragmentWorkOrderDetailFinishedBinding
import com.clockworkorange.haohsing.ui.base.BaseFragment
import com.clockworkorange.haohsing.ui.main.user.MainActivity
import com.clockworkorange.haohsing.ui.widget.setTextAndArrow
import com.clockworkorange.haohsing.ui.widget.setTextOnly
import com.clockworkorange.haohsing.ui.widget.setUrlImage
import com.clockworkorange.haohsing.utils.IntentUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter


@AndroidEntryPoint
class WorkOrderDetailFinishedFragment : BaseFragment() {
    private var _binding: FragmentWorkOrderDetailFinishedBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WorkOrderDetailFinishedViewModel by viewModels()

    private val args by navArgs<WorkOrderDetailFinishedFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkOrderDetailFinishedBinding.inflate(layoutInflater)
        initView()
        initListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
        viewModel.setId(args.deviceId, args.orderId)
    }

    private fun initView() {
        with(binding) {
            ilToolbar.tvTitle.text = "保養維修履歷"
            ilWorkOrderId.setTextOnly("單號")
            ilDeliveryDatetime.setTextOnly("到府時間")
            ilRequirement.setTextOnly("需求")
            ilErrorScenario.tvTitle.text = "故障狀況"
            ilNote.tvTitle.text = "備註說明"
            ilTds.tvTitle.text = "TDS"
            ilCheckTds.tvTitle.text = "驗水"
            ilChangeFilter.tvTitle.text = "更換濾心"
            ilBasicMaintain.tvTitle.text = "基礎保養"
            ilErrorCode.tvTitle.text = "異常原因"
            ilRepairParts.tvTitle.text = "維修內容"
            ilChangeParts.tvTitle.text = "更換零件"
            ilQuotation.setTextAndArrow("報價單", "PDF")
            ilIsRepair.setTextOnly("是否完成維修")
            ilFee.setTextOnly("費用")
            ilCustomerSignature.setTextOnly("客戶簽名")
            ilEngineerId.setTextOnly("服務人員編號")

        }
        (requireActivity() as? MainActivity)?.setLightStatusBar()
    }

    private fun initListener() {
        binding.ilToolbar.btBack.setOnClickListener { findNavController().navigateUp() }
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
            processWorkOrder(uiState.finishedWorkOrderDetail)
        }
    }

    private fun processWorkOrder(detail: FinishedWorkOrderDetail?) {
        detail ?: return
        //廠商資訊
        with(binding.ilEngineerInfo) {
            tvPlace.text = detail.engineerInfo.place
            tvEngineerName.text = detail.engineerInfo.name
            tvPhone.text = detail.engineerInfo.phone
        }
        //客戶資訊
        with(binding.ilCustomerInfo) {
            tvPlace.text = detail.customerInfo.agency
            tvCustomerName.text = detail.customerInfo.name
            tvPhone.text = detail.customerInfo.phone
            tvAddress.text = detail.customerInfo.address
        }
        //客戶反應內容
        binding.ilRequirement.tvInfo.text = detail.requirement.displayString
        binding.ilErrorScenario.tvContent.text = detail.errorReason.toDisplayString()
        binding.ilNote.tvContent.text = detail.note


        binding.ilWorkOrderId.tvInfo.text = detail.orderId.toString()
        binding.ilWorkOrderId.tvInfo.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorPrimary
            )
        )

        val deliveryTimeString = detail.deliveryTime?.format(
            DateTimeFormatter.ofPattern("yyyy年MM月dd日\nHH:mm")
        )
        binding.ilDeliveryDatetime.tvInfo.text = deliveryTimeString ?: "--"


        //保養紀錄
        binding.ilTds.tvInfo.text = detail.maintainRecord?.tds ?: "--"
        binding.ilCheckTds.tvInfo.text = detail.maintainRecord?.testTDS ?: "--"
        binding.ilChangeFilter.tvContent.text =
            detail.maintainRecord?.filterChanged?.toDisplayString(false)
        binding.ilBasicMaintain.tvContent.text =
            detail.maintainRecord?.basicMaintain?.toDisplayString(false)

        //維修紀錄
        binding.ilErrorCode.tvContent.text =
            convertFormOptionToDisplayString(detail.repairRecord?.errorCode)
        binding.ilRepairParts.tvContent.text =
            convertFormOptionToDisplayString(detail.repairRecord?.repairParts, false)
        binding.ilChangeParts.tvContent.text =
            detail.repairRecord?.changeParts?.toDisplayString(showDot = false)

        //確認項目
        binding.ilFee.tvInfo.text = detail.confirmRecord?.fee.toString()
        binding.ilEngineerId.tvInfo.text = detail.confirmRecord?.engineerId.toString()
        binding.ilCustomerSignature.setUrlImage(detail.confirmRecord?.customerSign)
        binding.ilIsRepair.tvInfo.text =
            if (detail.confirmRecord?.isRepairDone == true) "是" else "否"
        binding.ilQuotation.root.setOnClickListener {
            val url = detail.confirmRecord?.quotationPDF
            if (url != null && url.startsWith("http")) {
                val intent = IntentUtils.createViewUrlIntent(url)
                try {
                    requireActivity().startActivity(intent)
                } catch (e: Exception) {
                }
            }
        }

        when (detail.requirement) {
            WorkOrderRequirement.Install -> {}
            WorkOrderRequirement.Maintain -> {
                binding.tvRepairRecord.isVisible = false
                binding.ilErrorCode.root.isVisible = false
                binding.ilRepairParts.root.isVisible = false
                binding.ilChangeParts.root.isVisible = false
            }
            WorkOrderRequirement.Repair -> {
                binding.tvMaintainRecord.isVisible = false
                binding.ilTds.root.isVisible = false
                binding.ilCheckTds.root.isVisible = false
                binding.ilChangeFilter.root.isVisible = false
                binding.ilBasicMaintain.root.isVisible = false
            }
            else -> {}
        }
    }

    private suspend fun observeNavAction() {

    }

    private fun convertFormOptionToDisplayString(
        option: List<FormOption>?,
        showDot: Boolean = true
    ): String {
        if (option == null || option.isEmpty()) return "未填寫"

        return buildString {
            option.forEach {
                if (showDot) append("•")
                append(it.displayName)
                append("\n")
            }
        }.dropLast(1)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (requireActivity() as? MainActivity)?.setDarkStatusBar()
        _binding = null
    }
}