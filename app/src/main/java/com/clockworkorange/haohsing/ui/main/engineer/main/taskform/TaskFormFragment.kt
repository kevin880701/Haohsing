package com.clockworkorange.haohsing.ui.main.engineer.main.taskform

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.clockworkorange.domain.entity.WorkOrderRequirement
import com.clockworkorange.domain.usecase.task.ChangedFilter
import com.clockworkorange.domain.usecase.task.DoneTaskType
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.databinding.FragmentTaskFormBinding
import com.clockworkorange.haohsing.ui.base.BaseFragment
import com.clockworkorange.haohsing.ui.dialog.MessageDialog
import com.clockworkorange.haohsing.ui.dialog.MessageInputDialog
import com.clockworkorange.haohsing.ui.dialog.SelectItemDialog
import com.clockworkorange.haohsing.ui.dialog.SignatureDialog
import com.clockworkorange.haohsing.ui.main.PhotoVideoAdapter
import com.clockworkorange.haohsing.ui.main.engineer.EngineerMainActivity
import com.clockworkorange.haohsing.ui.widget.GridSpacingItemDecoration
import com.clockworkorange.haohsing.ui.widget.setBitmapFile
import com.clockworkorange.haohsing.ui.widget.setTextAndArrow
import com.clockworkorange.haohsing.ui.widget.setTextOnly
import com.clockworkorange.haohsing.utils.FileUtil
import com.clockworkorange.haohsing.utils.ScreenUtils
import com.clockworkorange.haohsing.utils.toDisplay
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class TaskFormFragment : BaseFragment() {

    private var _binding: FragmentTaskFormBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskFormViewModel by viewModels()

    private val args by navArgs<TaskFormFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskFormBinding.inflate(layoutInflater)
        initView()
        initListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
        viewModel.setTaskId(args.taskId)
    }

    private fun initView() {
        (requireActivity() as? EngineerMainActivity)?.setLightStatusBar()
        with(binding) {
            ilToolbar.tvTitle.text = "維修保養單"
            ilTds.setTextAndArrow("TDS", "--TDS")
            ilCheckTds.setTextAndArrow("驗水", "--TDS")
            ilSelectChangeFilter.tvTitle.text = "更換濾心"
            ilFilter1.setTextAndArrow("濾芯-1", "設定壽命")
            ilFilter2.setTextAndArrow("濾芯-2", "設定壽命")
            ilFilter3.setTextAndArrow("濾芯-3", "設定壽命")
            ilFilterRo.setTextAndArrow("濾芯-ro", "設定壽命")
            ilFilter5.setTextAndArrow("濾芯-5", "設定壽命")

            val textColor = ContextCompat.getColor(requireContext(), R.color.colorLightText)

            ilFilter1.tvInfo.setTextColor(textColor)
            ilFilter2.tvInfo.setTextColor(textColor)
            ilFilter3.tvInfo.setTextColor(textColor)
            ilFilterRo.tvInfo.setTextColor(textColor)
            ilFilter5.tvInfo.setTextColor(textColor)

            ilSelectBasicMaintain.tvTitle.text = "基礎保養"
            ilPhotoOldFilter.setTextAndArrow("舊濾芯照", "選擇照片")
            ilPhotoOldFilter.tvInfo.setTextColor(textColor)

            val dp8 = ScreenUtils.dp2px(requireContext(), 8)
            rvPhotosOldFilter.layoutManager = GridLayoutManager(requireContext(), 3)
            rvPhotosOldFilter.addItemDecoration(GridSpacingItemDecoration(3, dp8, false))
            rvPhotosOldFilter.adapter = PhotoVideoAdapter { viewModel.removeOldFilterPhotos() }

            ilPhotoNewFilter.setTextAndArrow("換好的濾芯照", "選擇照片")
            ilPhotoNewFilter.tvInfo.setTextColor(textColor)

            rvPhotosNewFilter.layoutManager = GridLayoutManager(requireContext(), 3)
            rvPhotosNewFilter.addItemDecoration(GridSpacingItemDecoration(3, dp8, false))
            rvPhotosNewFilter.adapter = PhotoVideoAdapter { viewModel.removeNewFilterPhotos() }

            ilSelectErrorCode.tvTitle.text = "異常原因-故障代碼"
            ilSelectErrorCode.etOther.isVisible = true

            ilSelectRepairContent.tvTitle.text = "維修內容"
            ilSelectRepairContent.etOther.isVisible = true
            ilPhotoNewParts.setTextAndArrow("設備完好照", "選擇照片")
            ilPhotoNewParts.tvInfo.setTextColor(textColor)


            rvPhotosNewParts.layoutManager = GridLayoutManager(requireContext(), 3)
            rvPhotosNewParts.addItemDecoration(GridSpacingItemDecoration(3, dp8, false))
            rvPhotosNewParts.adapter = PhotoVideoAdapter { viewModel.removeNewPartsPhotos() }

            ilPhotoOldParts.setTextAndArrow("更換下的零件照", "選擇照片")
            ilPhotoOldParts.tvInfo.setTextColor(textColor)

            rvPhotosOldParts.layoutManager = GridLayoutManager(requireContext(), 3)
            rvPhotosOldParts.addItemDecoration(GridSpacingItemDecoration(3, dp8, false))
            rvPhotosOldParts.adapter = PhotoVideoAdapter { viewModel.removeOldPartsPhotos() }

            ilQuotation.setTextAndArrow("報價單 (須簽名)", "PDF")
            ilQuotation.tvInfo.setTextColor(textColor)

            ilIsRepair.setTextAndArrow("是否執行維修")
            ilIsRepair.tvInfo.setTextColor(textColor)

            ilFee.setTextAndArrow("費用")
            ilFee.tvInfo.setTextColor(textColor)

            ilCustomerSignature.setTextAndArrow("客戶簽名")
            ilEngineerId.setTextOnly("服務人員編號")

        }
    }

    private fun initListener() {
        with(binding) {
            ilToolbar.btBack.setOnClickListener { findNavController().navigateUp() }

            btExpandMaintain.setOnClickListener {
                llMaintain.isVisible = !llMaintain.isVisible
                btExpandMaintain.setImageResource(if (llMaintain.isVisible) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down)
            }

            btExpandRepair.setOnClickListener {
                llRepair.isVisible = !llRepair.isVisible
                btExpandRepair.setImageResource(if (llRepair.isVisible) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down)
            }

            btExpandConfirm.setOnClickListener {
                llConfirm.isVisible = !llConfirm.isVisible
                btExpandConfirm.setImageResource(if (llConfirm.isVisible) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down)
            }

            ilTds.root.setOnClickListener { showInputTDSDialog() }

            ilCheckTds.root.setOnClickListener { showInputCheckTDSDialog() }

            ilPhotoOldFilter.root.setOnClickListener { pickOldFilterLauncher.launch(arrayOf("image/*")) }
            ilPhotoNewFilter.root.setOnClickListener { pickNewFilterLauncher.launch(arrayOf("image/*")) }

            ilPhotoNewParts.root.setOnClickListener { pickNewPartsLauncher.launch(arrayOf("image/*")) }
            ilPhotoOldParts.root.setOnClickListener { pickOldPartsLauncher.launch(arrayOf("image/*")) }

            cgRepairType.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.chip_error_reason -> {
                        ilSelectErrorCode.root.isVisible = true
                        ilSelectRepairContent.root.isVisible = false
                        ilSelectChangeParts.root.isVisible = false
                    }
                    R.id.chip_repair_content -> {
                        ilSelectErrorCode.root.isVisible = false
                        ilSelectRepairContent.root.isVisible = true
                        ilSelectChangeParts.root.isVisible = false
                    }
                    R.id.chip_change_parts -> {
                        ilSelectErrorCode.root.isVisible = false
                        ilSelectRepairContent.root.isVisible = false
                        ilSelectChangeParts.root.isVisible = true
                    }
                }
            }

            ilIsRepair.root.setOnClickListener { showIsRepairDialog() }
            ilFee.root.setOnClickListener { showInputFeeDialog() }
            ilCustomerSignature.root.setOnClickListener { showConfirmSignatureDialog() }

            btFinish.setOnClickListener { finishTask() }
            btNeedQuotation.setOnClickListener { quotationTask() }
            btReportError.setOnClickListener { reportErrorTask() }

            ilFilter1.root.setOnClickListener { showAddFilterDialog(ChangedFilter.FilterType.F1) }
            ilFilter2.root.setOnClickListener { showAddFilterDialog(ChangedFilter.FilterType.F2) }
            ilFilter3.root.setOnClickListener { showAddFilterDialog(ChangedFilter.FilterType.F3) }
            ilFilterRo.root.setOnClickListener { showAddFilterDialog(ChangedFilter.FilterType.F4) }
            ilFilter5.root.setOnClickListener { showAddFilterDialog(ChangedFilter.FilterType.F5) }

            ilSelectChangeFilter.flItems.setListener { option -> viewModel.clickFilterOption(option) }
            ilSelectBasicMaintain.flItems.setListener { option ->
                viewModel.clickBasicMaintainOptions(
                    option
                )
            }
            ilSelectErrorCode.flItems.setListener { option -> viewModel.clickErrorCodeOptions(option) }
            ilSelectRepairContent.flItems.setListener { option ->
                viewModel.clickRepairContentOptions(
                    option
                )
            }
            with(ilSelectChangeParts) {
                flItemsControl.setListener { option -> viewModel.clickControlOptions(option) }
                flItemsHeat.setListener { option -> viewModel.clickHeatOptions(option) }
                flItemsCool.setListener { option -> viewModel.clickCoolOptions(option) }
                flItemsPipeline.setListener { option -> viewModel.clickPipelineOptions(option) }
                flItemsRo.setListener { option -> viewModel.clickRoOptions(option) }
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

            when (uiState.requirement) {
                WorkOrderRequirement.Maintain -> {
                    binding.flRepair.isVisible = false
                    binding.llRepair.isVisible = false
                    binding.dividerRepair.isVisible = false
                    processMaintainUi(uiState)
                }
                WorkOrderRequirement.Repair -> {
                    binding.flMaintain.isVisible = false
                    binding.llMaintain.isVisible = false
                    binding.dividerMaintain.isVisible = false
                    processRepairUi(uiState)
                }
            }

            processConfirmUi(uiState)

            if (uiState.isDoneTask) {
                findNavController().navigateUp()
            }
        }
    }


    private fun processMaintainUi(uiState: TaskFormViewModel.UiState) {
        with(binding) {
            ilTds.tvInfo.text = "${uiState.tds ?: "--"} TDS"
            ilCheckTds.tvInfo.text = "${uiState.checkTDS ?: "--"} TDS"

            ilSelectChangeFilter.flItems.setItems(uiState.changeFiltersOptions)
            ilSelectBasicMaintain.flItems.setItems(uiState.basicMaintainOptions)

            ilFilter1.root.isVisible =
                uiState.changeFiltersOptions.firstOrNull { it.option.code == "BB1" }?.isSelect == true
            ilFilter2.root.isVisible =
                uiState.changeFiltersOptions.firstOrNull { it.option.code == "BB2" }?.isSelect == true
            ilFilter3.root.isVisible =
                uiState.changeFiltersOptions.firstOrNull { it.option.code == "BB3" }?.isSelect == true
            ilFilterRo.root.isVisible =
                uiState.changeFiltersOptions.firstOrNull { it.option.code == "BB4" }?.isSelect == true
            ilFilter5.root.isVisible =
                uiState.changeFiltersOptions.firstOrNull { it.option.code == "BB5" }?.isSelect == true

            ilPhotoOldFilter.tvInfo.text = uiState.photoOldFilter.toDisplay("選擇照片", "已選擇")

            rvPhotosOldFilter.isVisible = uiState.photoOldFilter != null
            (rvPhotosOldFilter.adapter as? PhotoVideoAdapter)?.submitList(uiState.photoOldFilter?.let {
                listOf(
                    it
                )
            })

            ilPhotoNewFilter.tvInfo.text = uiState.photoNewFilter.toDisplay("選擇照片", "已選擇")
            rvPhotosNewFilter.isVisible = uiState.photoNewFilter != null
            (rvPhotosNewFilter.adapter as? PhotoVideoAdapter)?.submitList(uiState.photoNewFilter?.let {
                listOf(
                    it
                )
            })

            uiState.changedFilter.forEach {
                when (it.type) {
                    ChangedFilter.FilterType.F1 -> ilFilter1.tvInfo.text =
                        "%d個月".format(it.lifeMonth)
                    ChangedFilter.FilterType.F2 -> ilFilter2.tvInfo.text =
                        "%d個月".format(it.lifeMonth)
                    ChangedFilter.FilterType.F3 -> ilFilter3.tvInfo.text =
                        "%d個月".format(it.lifeMonth)
                    ChangedFilter.FilterType.F4 -> ilFilterRo.tvInfo.text =
                        "%d個月".format(it.lifeMonth)
                    ChangedFilter.FilterType.F5 -> ilFilter5.tvInfo.text =
                        "%d個月".format(it.lifeMonth)
                }
            }
        }

    }

    private fun processRepairUi(uiState: TaskFormViewModel.UiState) {
        with(binding) {
            ilSelectErrorCode.flItems.setItems(uiState.errorCodeOptions)
            ilSelectRepairContent.flItems.setItems(uiState.repairContentOptions)
            with(ilSelectChangeParts) {
                flItemsControl.setItems(uiState.controlOptions)
                flItemsHeat.setItems(uiState.heatOptions)
                flItemsCool.setItems(uiState.coolOptions)
                flItemsPipeline.setItems(uiState.pipelineOptions)
                flItemsRo.setItems(uiState.roOptions)
            }


            ilPhotoOldParts.tvInfo.text = uiState.photoOldParts.toDisplay("選擇照片", "已選擇")
            rvPhotosOldParts.isVisible = uiState.photoOldParts != null
            (rvPhotosOldParts.adapter as? PhotoVideoAdapter)?.submitList(uiState.photoOldParts?.let {
                listOf(
                    it
                )
            })

            ilPhotoNewParts.tvInfo.text = uiState.photoNewParts.toDisplay("選擇照片", "已選擇")
            rvPhotosNewParts.isVisible = uiState.photoNewParts != null
            (rvPhotosNewParts.adapter as? PhotoVideoAdapter)?.submitList(uiState.photoNewParts?.let {
                listOf(
                    it
                )
            })
        }

    }

    private fun processConfirmUi(uiState: TaskFormViewModel.UiState) {
        with(binding) {
            llRejectRepairReason.isVisible = uiState.isRepair == false
            ilIsRepair.tvInfo.text = when (uiState.isRepair) {
                true -> "是"
                false -> "否"
                null -> ""
            }

            ilFee.tvInfo.text = uiState.fee
            ilCustomerSignature.setBitmapFile(uiState.customerSignature)
            ilEngineerId.tvInfo.text = uiState.engineerId?.toString() ?: ""
        }

    }

    private suspend fun observeNavAction() {

    }

    private fun showInputTDSDialog() {
        MessageInputDialog.newInstance(
            "TDS",
            inputType = EditorInfo.TYPE_CLASS_NUMBER
        ) { tds ->
            viewModel.setTDS(tds)
        }.show(childFragmentManager, "tds")
    }

    private fun showInputCheckTDSDialog() {
        MessageInputDialog.newInstance(
            "TDS",
            inputType = EditorInfo.TYPE_CLASS_NUMBER
        ) { tds ->
            viewModel.setCheckTDS(tds)
        }.show(childFragmentManager, "check_tds")
    }

    private fun showIsRepairDialog() {
        val options = arrayOf("是", "否")
        SelectItemDialog.newInstance(
            "完成維修",
            options,
            getString(R.string.finished)
        ) {
            binding.ilIsRepair.tvInfo.text = it
            when (it) {
                options[0] -> viewModel.setIsRepair(true)
                options[1] -> viewModel.setIsRepair(false)
            }

        }.show(childFragmentManager, "is_repair")
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
        SignatureDialog.newInstance("費用 : ${viewModel.uiState.value.fee ?: ""}") { signatureBitmap ->
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
            when (viewModel.uiState.value.requirement) {
                WorkOrderRequirement.Maintain -> {
                    viewModel.doneMaintainTask(
                        DoneTaskType.Finish
                    )
                }
                WorkOrderRequirement.Repair -> {
                    val errorCodeOtherMessage =
                        binding.ilSelectErrorCode.etOther.editableText.toString()
                    val repairContentOtherMessage =
                        binding.ilSelectRepairContent.etOther.editableText.toString()
                    viewModel.doneRepairTask(
                        errorCodeOtherMessage,
                        repairContentOtherMessage,
                        DoneTaskType.Finish
                    )
                }
            }

        }.show(childFragmentManager, "finish")
    }

    private fun quotationTask() {
        if (!isCustomerSignature()) {
            return
        }

        MessageDialog.newInstance(
            MessageDialog.IconType.Info,
            "將回報需重新派單需求", "廠商上傳估價單後將重新派單",
            button1Style = MessageDialog.ButtonStyle.Outline,
            button1Title = getString(R.string.cancel),
            button2Style = MessageDialog.ButtonStyle.Fill,
            button2Title = getString(R.string.confirm)
        ) {
            when (viewModel.uiState.value.requirement) {
                WorkOrderRequirement.Maintain -> {
                    viewModel.doneMaintainTask(DoneTaskType.NeedReSent)
                }
                WorkOrderRequirement.Repair -> {
                    val errorCodeOtherMessage =
                        binding.ilSelectErrorCode.etOther.editableText.toString()
                    val repairContentOtherMessage =
                        binding.ilSelectRepairContent.etOther.editableText.toString()
                    viewModel.doneRepairTask(
                        errorCodeOtherMessage,
                        repairContentOtherMessage,
                        DoneTaskType.NeedReSent
                    )
                }
            }
        }.show(childFragmentManager, "finish")
    }

    private fun reportErrorTask() {
        if (!isCustomerSignature()) {
            return
        }

        MessageInputDialog.newInstance(
            "異常原因"
        ) { reason ->
            when (viewModel.uiState.value.requirement) {
                WorkOrderRequirement.Maintain -> {
                    viewModel.doneMaintainTask(DoneTaskType.ReportError, reason)
                }
                WorkOrderRequirement.Repair -> {
                    val errorCodeOtherMessage =
                        binding.ilSelectErrorCode.etOther.editableText.toString()
                    val repairContentOtherMessage =
                        binding.ilSelectRepairContent.etOther.editableText.toString()
                    viewModel.doneRepairTask(
                        errorCodeOtherMessage,
                        repairContentOtherMessage,
                        DoneTaskType.ReportError,
                        reason
                    )
                }
            }
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

    private fun showAddFilterDialog(filterType: ChangedFilter.FilterType) {
        AddFilterDialog.newInstance(filterType) { filter ->
            viewModel.addChangedFilter(filter)
        }.show(childFragmentManager, "add_filter_dialog")
    }

    private val pickOldFilterLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { fileUri ->
            fileUri ?: return@registerForActivityResult
            viewModel.setOldFilterPhotos(fileUri)
        }

    private val pickNewFilterLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { fileUri ->
            fileUri ?: return@registerForActivityResult
            viewModel.setNewFilterPhotos(fileUri)
        }

    private val pickOldPartsLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { fileUri ->
            fileUri ?: return@registerForActivityResult
            viewModel.setOldPartsPhotos(fileUri)
        }

    private val pickNewPartsLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { fileUri ->
            fileUri ?: return@registerForActivityResult
            viewModel.setNewPartsPhotos(fileUri)
        }


    override fun onDestroyView() {
        super.onDestroyView()
        (requireActivity() as? EngineerMainActivity)?.setDarkStatusBar()
        _binding = null
    }

}