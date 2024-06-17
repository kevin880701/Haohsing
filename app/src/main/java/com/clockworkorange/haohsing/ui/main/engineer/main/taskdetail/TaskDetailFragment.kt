package com.clockworkorange.haohsing.ui.main.engineer.main.taskdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.clockworkorange.domain.entity.WorkOrderRequirement
import com.clockworkorange.domain.usecase.task.FormOption
import com.clockworkorange.domain.usecase.task.TaskDetail
import com.clockworkorange.domain.usecase.task.TaskStatusCategory
import com.clockworkorange.domain.usecase.task.toDisplayString
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.databinding.FragmentTaskDetailBinding
import com.clockworkorange.haohsing.ui.base.BaseFragment
import com.clockworkorange.haohsing.ui.widget.GridSpacingItemDecoration
import com.clockworkorange.haohsing.ui.widget.setTextOnly
import com.clockworkorange.haohsing.ui.widget.setUrlImage
import com.clockworkorange.haohsing.utils.GenericAdapterListener
import com.clockworkorange.haohsing.utils.IntentUtils
import com.clockworkorange.haohsing.utils.ScreenUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class TaskDetailFragment : BaseFragment(), GenericAdapterListener<String> {

    private var _binding: FragmentTaskDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskDetailViewModel by viewModels()

    private val args by navArgs<TaskDetailFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskDetailBinding.inflate(layoutInflater)
        initView()
        initListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
        viewModel.setTaskId(args.taskId)
        Timber.d("taskId: ${args.taskId}")
    }

    private fun initView() {
        with(binding) {
            ilToolbar.tvTitle.text = ""
            ilTaskId.setTextOnly("單號")
            ilDeliveryDatetime.setTextOnly("到府時間")

            ilRequirement.setTextOnly("需求")
            ilErrorScenario.tvTitle.text = "故障狀況"
            ilPhotoVideos.setTextOnly("影片/照片")
            rvPhotoVideos.layoutManager = GridLayoutManager(requireContext(), 3)
            rvPhotoVideos.addItemDecoration(
                GridSpacingItemDecoration(
                    3,
                    ScreenUtils.dp2px(requireContext(), 8),
                    false
                )
            )
            rvPhotoVideos.adapter = RemotePhotoVideoAdapter(this@TaskDetailFragment)

            ilNote.tvTitle.text = "備註說明"
            ilVendorNote.tvTitle.text = "備註說明"

            ilTds.setTextOnly("TDS")
            ilCheckTds.setTextOnly("驗水")
            ilChangeFilter.tvTitle.text = "更換濾心"
            ilBasicMaintain.tvTitle.text = "基礎保養"

            ilErrorCode.tvTitle.text = "異常原因"
            ilRepairParts.tvTitle.text = "維修內容"
            ilChangeParts.tvTitle.text = "更換零件"

            ilFee.setTextOnly("費用")
            ilCustomerSignature.setTextOnly("客戶簽名")
            ilEngineerId.setTextOnly("服務人員編號")
            ilReportError.tvTitle.text = "回報異常原因"
            ilReportError.tvTitle.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorRed
                )
            )
        }

    }

    private fun initListener() {
        binding.ilToolbar.btBack.setOnClickListener { findNavController().navigateUp() }

        binding.btFillMaintain.setOnClickListener {
            viewModel.startWork(args.taskId)
            val dest =
                TaskDetailFragmentDirections.actionTaskDetailFragmentToTaskFormFragment(args.taskId)
            findNavController().navigate(dest)
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
            processTaskDetail(uiState.taskDetail)
        }
    }

    private fun processTaskDetail(taskDetail: TaskDetail?) {
        taskDetail ?: return
        //客戶資訊
        with(binding.ilCustomerInfo) {
            tvPlace.text = taskDetail.customerInfo.agency
            tvCustomerName.text = taskDetail.customerInfo.name
            tvPhone.text = taskDetail.customerInfo.phone
            tvPhone.setOnClickListener {
                requireActivity().startActivity(IntentUtils.createPhoneCallIntent(taskDetail.customerInfo.phone))
            }
            tvAddress.text = taskDetail.customerInfo.address
            tvAddress.setOnClickListener {
                try {
                    requireActivity().startActivity(IntentUtils.createMapIntent(taskDetail.customerInfo.address))
                } catch (e: Exception) {
                }
            }
        }
        //客戶反應內容
        if (taskDetail.hasCustomerFeedback) {
            binding.ilRequirement.tvInfo.text = taskDetail.requirement.displayString
            binding.ilErrorScenario.tvContent.text = taskDetail.errorReason.toDisplayString()
            binding.ilNote.tvContent.text = taskDetail.note
            binding.ilPhotoVideos.tvInfo.text = "${taskDetail.photoVideos.size} 個項目"
            binding.rvPhotoVideos.isVisible = taskDetail.photoVideos.isNotEmpty()
            (binding.rvPhotoVideos.adapter as? RemotePhotoVideoAdapter)?.submitList(taskDetail.photoVideos)
        } else {
            hideCustomerFeedback()
        }
        // 經銷商備註
        binding.ilVendorNote.tvContent.text = taskDetail.vendorNote

        when (taskDetail.status) {
            TaskStatusCategory.UnFinish -> {
                binding.ilToolbar.tvTitle.text = "任務資訊"
                binding.btFillMaintain.isVisible = true
                hideDetailRecord()
                binding.ilTaskId.tvInfo.text = taskDetail.taskId.toString()
                binding.ilTaskId.tvInfo.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorPrimary
                    )
                )
                if (LocalDateTime.now() > taskDetail.appointedDatetime) {//逾期
                    binding.ilDeliveryDatetime.tvInfo.text = buildSpannedString {
                        append(taskDetail.appointedDatetime.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")))
                        append("\n")
                        color(ContextCompat.getColor(requireContext(), R.color.colorRed)) {
                            append(taskDetail.appointedDatetime.format(DateTimeFormatter.ofPattern("HH:mm 逾期")))
                        }
                    }
                } else {
                    binding.ilDeliveryDatetime.tvInfo.text = buildSpannedString {
                        append(taskDetail.appointedDatetime.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")))
                        append("\n")
                        append(taskDetail.appointedDatetime.format(DateTimeFormatter.ofPattern("HH:mm")))
                    }
                }
            }
            TaskStatusCategory.Finish -> {
                binding.ilToolbar.tvTitle.text = "已完成任務"

                binding.ilTaskId.tvInfo.text = taskDetail.taskId.toString()
                binding.ilTaskId.tvInfo.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorPrimary
                    )
                )

                val deliveryTimeString =
                    (taskDetail.deliveryTime ?: taskDetail.appointedDatetime).format(
                        DateTimeFormatter.ofPattern("yyyy年MM月dd日\nHH:mm")
                    )
                binding.ilDeliveryDatetime.tvInfo.text = deliveryTimeString
                binding.btFillMaintain.isVisible = false
                binding.ilReportError.root.isVisible = false
            }
            TaskStatusCategory.Error -> {
                binding.ilToolbar.tvTitle.text = "回報異常的任務"

                if (taskDetail.isErrorNeedResend) {
                    binding.ilTaskId.tvInfo.text = "%d-需重派".format(taskDetail.taskId)
                    binding.ilTaskId.tvInfo.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorYellow
                        )
                    )
                } else {
                    binding.ilTaskId.tvInfo.text = "%d-異常".format(taskDetail.taskId)
                    binding.ilTaskId.tvInfo.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorRed
                        )
                    )
                }
                val deliveryTimeString =
                    (taskDetail.deliveryTime ?: taskDetail.appointedDatetime).format(
                        DateTimeFormatter.ofPattern("yyyy年MM月dd日\nHH:mm")
                    )
                binding.ilDeliveryDatetime.tvInfo.text = deliveryTimeString
                binding.btFillMaintain.isVisible = false
            }
        }


        //保養紀錄
        binding.ilTds.tvInfo.text = taskDetail.maintainRecord?.tds ?: "--"
        binding.ilCheckTds.tvInfo.text = taskDetail.maintainRecord?.testTDS ?: "--"
        binding.ilChangeFilter.tvContent.text =
            taskDetail.maintainRecord?.filterChanged?.toDisplayString(false)
        binding.ilBasicMaintain.tvContent.text =
            taskDetail.maintainRecord?.basicMaintain?.toDisplayString(false)

        //維修紀錄
        binding.ilErrorCode.tvContent.text =
            convertFormOptionToDisplayString(taskDetail.repairRecord?.errorCode)
        binding.ilRepairParts.tvContent.text =
            convertFormOptionToDisplayString(taskDetail.repairRecord?.repairParts, false)
        binding.ilChangeParts.tvContent.text =
            taskDetail.repairRecord?.changeParts?.toDisplayString(showDot = false)

        //確認項目
        binding.ilFee.tvInfo.text = taskDetail.confirmRecord?.fee.toString()
        binding.ilEngineerId.tvInfo.text = taskDetail.confirmRecord?.engineerId.toString()
        binding.ilCustomerSignature.setUrlImage(taskDetail.confirmRecord?.customerSign)
        binding.ilReportError.tvContent.text =
            taskDetail.confirmRecord?.errorReason?.ifEmpty { "未填寫" }

        when (taskDetail.requirement) {
            WorkOrderRequirement.Install -> {
                binding.ilErrorScenario.root.isVisible = false
                binding.ilPhotoVideos.root.isVisible = false
                binding.rvPhotoVideos.isVisible = false
                binding.ilNote.tvTitle.text = "內容"
                binding.btFillMaintain.isVisible = false
            }
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
        }
    }

    private fun hideCustomerFeedback() = with(binding) {
        tvTitleCustomer.isVisible = false
        ilRequirement.root.isVisible = false
        ilErrorScenario.root.isVisible = false
        ilPhotoVideos.root.isVisible = false
        rvPhotoVideos.isVisible = false
        ilNote.root.isVisible = false
    }

    private fun hideDetailRecord() {
        with(binding) {
            tvMaintainRecord.isVisible = false
            ilTds.root.isVisible = false
            ilCheckTds.root.isVisible = false
            ilChangeFilter.root.isVisible = false
            ilBasicMaintain.root.isVisible = false

            tvRepairRecord.isVisible = false
            ilErrorCode.root.isVisible = false
            ilRepairParts.root.isVisible = false
            ilChangeParts.root.isVisible = false

            tvConfirm.isVisible = false
            ilFee.root.isVisible = false
            ilCustomerSignature.root.isVisible = false
            ilEngineerId.root.isVisible = false
            ilReportError.root.isVisible = false
        }
    }

    private suspend fun observeNavAction() {

    }

    override fun onItemClick(item: String) {
        PreviewPhotoVideoDialog.newInstance(item)
            .show(childFragmentManager, "preview")
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
        _binding = null
    }


}