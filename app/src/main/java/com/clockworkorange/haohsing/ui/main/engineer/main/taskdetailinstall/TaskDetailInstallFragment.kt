package com.clockworkorange.haohsing.ui.main.engineer.main.taskdetailinstall

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
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
import com.clockworkorange.domain.usecase.task.TaskDetail
import com.clockworkorange.domain.usecase.task.TaskStatusCategory
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.databinding.FragmentTaskDetailInstallBinding
import com.clockworkorange.haohsing.ui.base.BaseFragment
import com.clockworkorange.haohsing.ui.dialog.MessageDialog
import com.clockworkorange.haohsing.ui.dialog.MessageInputDialog
import com.clockworkorange.haohsing.ui.dialog.SignatureDialog
import com.clockworkorange.haohsing.ui.widget.setBitmapFile
import com.clockworkorange.haohsing.ui.widget.setTextAndArrow
import com.clockworkorange.haohsing.ui.widget.setTextOnly
import com.clockworkorange.haohsing.ui.widget.setUrlImage
import com.clockworkorange.haohsing.utils.FileUtil
import com.clockworkorange.haohsing.utils.IntentUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class TaskDetailInstallFragment : BaseFragment() {

    private var _binding: FragmentTaskDetailInstallBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskDetailInstallViewModel by viewModels()

    private val args by navArgs<TaskDetailInstallFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskDetailInstallBinding.inflate(layoutInflater)
        initView()
        initListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
        viewModel.setId(args.taskId)
    }

    private fun initView() {
        binding.ilToolbar.tvTitle.text = "任務資訊"
        binding.ilTaskId.setTextOnly("單號")
        binding.ilDeliveryDatetime.setTextOnly("到府時間")
        binding.ilRequirement.setTextOnly("需求")
        binding.ilContent.tvTitle.text = "內容"
        binding.ilVendorNote.tvTitle.text = "備註說明"
        binding.ilFee.setTextOnly("費用")
        binding.ilCustomerSignature.setTextOnly("客戶簽名")
        binding.ilEngineerId.setTextOnly("服務人員編號")


        val textColor = ContextCompat.getColor(requireContext(), R.color.colorLightText)
        binding.ilFee.tvInfo.setTextColor(textColor)

    }

    private fun initListener() {
        binding.ilToolbar.btBack.setOnClickListener { findNavController().navigateUp() }
        binding.btFinish.setOnClickListener { finishTask() }
        binding.btReportError.setOnClickListener { reportErrorTask() }
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
            binding.ilFee.tvInfo.text = uiState.fee
            binding.ilCustomerSignature.setBitmapFile(uiState.customerSignature)
            processTaskDetail(uiState.taskDetail)
            if (uiState.isDoneTask) {
                findNavController().navigateUp()
            }
        }
    }

    private fun processTaskDetail(taskDetail: TaskDetail?) {
        taskDetail ?: return
        with(binding) {
            ilTaskId.tvInfo.text = taskDetail.taskId.toString()
            ilCustomerInfo.tvPlace.text = taskDetail.customerInfo.agency
            ilCustomerInfo.tvCustomerName.text = taskDetail.customerInfo.name
            ilCustomerInfo.tvPhone.text = taskDetail.customerInfo.phone
            ilCustomerInfo.tvPhone.setOnClickListener {
                requireActivity().startActivity(IntentUtils.createPhoneCallIntent(taskDetail.customerInfo.phone))
            }
            ilCustomerInfo.tvAddress.text = taskDetail.customerInfo.address
            ilCustomerInfo.tvAddress.setOnClickListener {
                try {
                    requireActivity().startActivity(IntentUtils.createMapIntent(taskDetail.customerInfo.address))
                } catch (e: Exception) {
                }
            }

            ilRequirement.tvInfo.text = taskDetail.requirement.displayString
            ilContent.tvContent.text = taskDetail.note
            binding.ilVendorNote.tvContent.text = taskDetail.vendorNote
            ilEngineerId.tvInfo.text = taskDetail.engineerInfo?.id?.toString()


            when (taskDetail.status) {
                TaskStatusCategory.UnFinish -> {
                    if (LocalDateTime.now() > taskDetail.appointedDatetime) {//逾期
                        binding.ilDeliveryDatetime.tvInfo.text = buildSpannedString {
                            append(taskDetail.appointedDatetime.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")))
                            append("\n")
                            color(ContextCompat.getColor(requireContext(), R.color.colorRed)) {
                                append(
                                    taskDetail.appointedDatetime.format(
                                        DateTimeFormatter.ofPattern(
                                            "HH:mm 逾期"
                                        )
                                    )
                                )
                            }
                        }
                    } else {
                        binding.ilDeliveryDatetime.tvInfo.text =
                            taskDetail.appointedDatetime.format(
                                DateTimeFormatter.ofPattern("yyyy年MM月dd日\nHH:mm")
                            )
                    }

                    ilFee.setTextAndArrow("費用")
                    ilFee.root.setOnClickListener { showInputFeeDialog() }
                    ilCustomerSignature.root.setOnClickListener { showConfirmSignatureDialog() }
                }
                TaskStatusCategory.Finish -> {
                    binding.ilDeliveryDatetime.tvInfo.text = taskDetail.deliveryTime?.format(
                        DateTimeFormatter.ofPattern("yyyy年MM月dd日\nHH:mm")
                    ) ?: "--"
                    ilFee.setTextOnly("費用", taskDetail.confirmRecord?.fee?.toString() ?: "--")
                    binding.ilCustomerSignature.setUrlImage(taskDetail.confirmRecord?.customerSign)
                    ilFee.root.setOnClickListener { }
                    ilCustomerSignature.root.setOnClickListener { }
                    btFinish.isVisible = false
                    btReportError.isVisible = false
                }
                TaskStatusCategory.Error -> {
                    binding.ilDeliveryDatetime.tvInfo.text = taskDetail.deliveryTime?.format(
                        DateTimeFormatter.ofPattern("yyyy年MM月dd日\nHH:mm")
                    ) ?: "--"
                    ilFee.setTextOnly("費用", taskDetail.confirmRecord?.fee?.toString() ?: "--")
                    ilFee.root.setOnClickListener { }
                    ilCustomerSignature.root.setOnClickListener { }
                    btFinish.isVisible = false
                    btReportError.isVisible = false
                }
            }
        }
    }

    private fun showConfirmSignatureDialog() {
        MessageDialog.newInstance(
            MessageDialog.IconType.Info,
            "確定收費金額嗎?", "讓顧客簽名後，將無法修改費用",
            button1Style = MessageDialog.ButtonStyle.Outline,
            button1Title = getString(R.string.cancel),
            button2Style = MessageDialog.ButtonStyle.Fill,
            button2Title = getString(R.string.confirm)
        ) { showCustomerSignatureBoard() }.show(childFragmentManager, "confirm_signature")
    }

    private suspend fun observeNavAction() {

    }

    private fun showInputFeeDialog() {
        MessageInputDialog.newInstance(
            "費用",
            inputType = EditorInfo.TYPE_CLASS_NUMBER
        ) { fee ->
            viewModel.setFee(fee)
        }.show(childFragmentManager, "fee")
    }

    private fun showCustomerSignatureBoard() {
        SignatureDialog. newInstance("費用 : ${viewModel.uiState.value.fee ?: ""}") { signatureBitmap ->
            val file = FileUtil.writeBitmapToFile(requireContext(), signatureBitmap, "sign.jpg")
            viewModel.setCustomerSignature(file)
            disableFeeInput()
        }.show(childFragmentManager, "signature")
    }

    private fun disableFeeInput() {
        binding.ilFee.run {
            root.isEnabled = false
            setTextOnly()
        }
    }

    private fun finishTask() {
        if (!isCustomerSignature()) {
            return
        }

        MessageDialog.newInstance(
            MessageDialog.IconType.Info,
            "確定回報結案嗎？", "回報結案後即不能再修改",
            button1Style = MessageDialog.ButtonStyle.Outline,
            button1Title = getString(R.string.cancel),
            button2Style = MessageDialog.ButtonStyle.Fill,
            button2Title = getString(R.string.confirm)
        ) {
            viewModel.doneTask()
        }.show(childFragmentManager, "finish")
    }

    private fun reportErrorTask() {
        if (!isCustomerSignature()) {
            return
        }

        MessageInputDialog.newInstance(
            "異常原因"
        ) { reason ->
            viewModel.reportErrorTask(reason)
        }.show(childFragmentManager, "report_error")
    }

    private fun isCustomerSignature(): Boolean {
        return if (viewModel.uiState.value.customerSignature == null) {
            binding.ilCustomerSignature.flRoot.setBackgroundResource(R.drawable.shape_r10_white_red_border)
            showToast("客戶尚未簽名")
            false
        } else {
            binding.ilCustomerSignature.flRoot.setBackgroundResource(R.drawable.shape_r10_white)
            true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}