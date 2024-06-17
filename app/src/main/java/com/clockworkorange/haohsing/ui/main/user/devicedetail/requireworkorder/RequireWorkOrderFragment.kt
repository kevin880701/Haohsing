package com.clockworkorange.haohsing.ui.main.user.devicedetail.requireworkorder

import android.net.Uri
import android.os.Bundle
import android.text.SpannedString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.core.view.isVisible
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.clockworkorange.domain.data
import com.clockworkorange.domain.entity.WorkOrderRequirement
import com.clockworkorange.domain.usecase.task.FormOption
import com.clockworkorange.domain.usecase.workorder.GetWorkOrderErrorOptionUseCase
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.databinding.FragmentRequireWorkOrderBinding
import com.clockworkorange.haohsing.ui.base.BaseFragment
import com.clockworkorange.haohsing.ui.dialog.MessageDialog
import com.clockworkorange.haohsing.ui.dialog.MessageInputDialog
import com.clockworkorange.haohsing.ui.dialog.SelectItemDialog
import com.clockworkorange.haohsing.ui.main.PhotoVideoAdapter
import com.clockworkorange.haohsing.ui.widget.GridSpacingItemDecoration
import com.clockworkorange.haohsing.ui.widget.setTextAndArrow
import com.clockworkorange.haohsing.utils.ScreenUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RequireWorkOrderFragment : BaseFragment(), PhotoVideoAdapter.Listener {

    private var _binding: FragmentRequireWorkOrderBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RequireWorkOrderViewModel by viewModels()

    private val args by navArgs<RequireWorkOrderFragmentArgs>()

    @Inject
    lateinit var getWorkOrderErrorOptionUseCase: GetWorkOrderErrorOptionUseCase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRequireWorkOrderBinding.inflate(layoutInflater)
        initView()
        initListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
        viewModel.set(args.addEdit, args.deviceId, args.mId)
    }

    private fun initView() {
        binding.ilToolbar.tvTitle.text = "新增報修與保養"
        binding.ilUserName.setTextAndArrow("客戶名稱", "請填寫")
        binding.ilUserPhone.setTextAndArrow("電話", "請填寫")
        binding.ilRequirement.setTextAndArrow("需求", "請選擇")
        binding.ilDeliveryTime.setTextAndArrow("期望聯絡時間", "請選擇")
        binding.ilFailScenario.setTextAndArrow("新增故障狀況", "請選擇")
        binding.ilSelectPhotosVideos.setTextAndArrow("上傳影片/照片", "拍攝異常狀況")
        viewModel.setNote(args.note)

        val gridLayoutManager = GridLayoutManager(requireContext(), 3)
        val decoration = GridSpacingItemDecoration(3, ScreenUtils.dp2px(requireContext(), 8), false)
        binding.rvPhotosVideos.layoutManager = gridLayoutManager
        binding.rvPhotosVideos.addItemDecoration(decoration)
        binding.rvPhotosVideos.adapter = PhotoVideoAdapter(false, this)
        binding.rvPhotosVideos.itemAnimator = null

    }

    private fun initListener() {
        binding.ilToolbar.btBack.setOnClickListener { findNavController().navigateUp() }
        binding.ilUserName.root.setOnClickListener { showEditUserNameDialog() }
        binding.ilUserPhone.root.setOnClickListener { showEditUserPhoneDialog() }
        binding.ilRequirement.root.setOnClickListener { selectRequirementTypeDialog() }
        binding.ilDeliveryTime.root.setOnClickListener { selectDeliveryTimeDialog() }
        binding.ilFailScenario.root.setOnClickListener { selectFailScenarioDialog() }
        binding.ilSelectPhotosVideos.root.setOnClickListener {
            pickContentLauncher.launch(arrayOf("image/*", "video/*"))
        }

        binding.btSubmit.setOnClickListener {
            val note = binding.etNote.editableText.toString()
            viewModel.submit(note)
        }

        binding.ilLoading.root.setOnClickListener { }
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
            with(binding) {
                ilUserName.tvInfo.text = uiState.userName
                ilUserPhone.tvInfo.text = uiState.userPhone
                ilRequirement.tvInfo.text = uiState.requirement?.displayString ?: "請選擇"

                ilFailScenario.tvInfo.text =
                    convertFailScenarioToDisplayString(uiState.errorReasonList)
                ilSelectPhotosVideos.tvInfo.text =
                    convertPhotoVideosUriToDisplayString(uiState.photoVideos)
                binding.rvPhotosVideos.isVisible = uiState.photoVideos.isNotEmpty()
                (binding.rvPhotosVideos.adapter as? PhotoVideoAdapter)?.submitList(uiState.photoVideos.toList())
                ilDeliveryTime.tvInfo.text = uiState.deliveryTime ?: "請選擇"
                ilManufacturerInfo.tvName.text = uiState.manufacturerInfo?.name ?: ""
                ilManufacturerInfo.tvPhone.text = uiState.manufacturerInfo?.phone ?: ""
                btSubmit.isEnabled = uiState.isFillEnough()
                ilLoading.root.isVisible = uiState.isLoading
                etNote.setText(uiState.note)

                if (uiState.isAddWorkOrderSuccess) {
                    showAddWorkOrderSuccessDialog()
                }

                if (uiState.isEditWorkOrderSuccess) {
                    showToast("編輯成功")
                    findNavController().navigateUp()
                }
            }

        }
    }


    private fun convertFailScenarioToDisplayString(failScenarioList: HashSet<FormOption>): SpannedString {
        if (failScenarioList.isEmpty()) {
            return buildSpannedString {
                color(ContextCompat.getColor(requireContext(), R.color.colorGray)) { append("請選擇") }
            }
        }

        val size = failScenarioList.size
        return buildSpannedString {
            color(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorPrimary
                )
            ) { append("${size}項異常") }
        }
    }

    private fun convertPhotoVideosUriToDisplayString(items: HashSet<Uri>): SpannedString {
        if (items.isEmpty()) {
            return buildSpannedString {
                color(ContextCompat.getColor(requireContext(), R.color.colorGray)) { append("請選擇") }
            }
        }

        val size = items.size
        return buildSpannedString {
            color(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorPrimary
                )
            ) { append("${size}個項目") }
        }
    }

    private suspend fun observeNavAction() {

    }

    private fun showEditUserNameDialog() {
        MessageInputDialog.newInstance(
            "客戶名稱"
        ) { name ->
            viewModel.setUserName(name)
        }.show(childFragmentManager, "edit_name")
    }

    private fun showEditUserPhoneDialog() {
        MessageInputDialog.newInstance(
            "電話"
        ) { phone ->
            viewModel.setUserPhone(phone)
        }.show(childFragmentManager, "edit_phone")
    }

    private fun selectRequirementTypeDialog() {
        val requirementOptions = arrayOf("保養", "維修")
        SelectItemDialog.newInstance(
            "需求",
            requirementOptions,
            getString(R.string.finished)
        ) { requirement ->
            when (requirement) {
                requirementOptions[0] -> viewModel.setRequirement(WorkOrderRequirement.Maintain)
                requirementOptions[1] -> viewModel.setRequirement(WorkOrderRequirement.Repair)
            }

        }.show(childFragmentManager, "select_requirement")
    }

    private fun selectDeliveryTimeDialog() {
        val deliveryTimeOptions = arrayOf(
            "上午 9:00 - 12:00",
            "下午 1:00 - 3:00",
            "下午 3:00 - 5:00"
        )
        SelectItemDialog.newInstance(
            "時間",
            deliveryTimeOptions,
            getString(R.string.finished)
        ) { deliveryTime ->
            viewModel.setDeliveryTime(deliveryTime)
        }.show(childFragmentManager, "select_requirement")

    }

    private fun selectFailScenarioDialog() {
        lifecycleScope.launch {
            val result = getWorkOrderErrorOptionUseCase.invoke(Unit)
            result.data?.let {
                SelectErrorReasonFragment.newInstance(it) { selectedErrorReason ->
                    viewModel.selectFailScenario(selectedErrorReason)
                }.show(childFragmentManager, "select_fail_scenario")
            }
        }
    }

    private val pickContentLauncher =
        registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { fileUris ->
            if (fileUris.isEmpty()) {
                return@registerForActivityResult
            }

            fileUris.forEach {
                val doc = DocumentFile.fromSingleUri(requireContext(), it)
                val type = doc?.type
                if (type?.contains("video") == true && !type.contains("mp4")) {
                    showToast("影片僅支援mp4")
                    return@forEach
                }
                viewModel.addSelectedPhotoVideos(it)
            }
        }

    override fun onRemoveItem(itemUri: Uri) {
        viewModel.removeSelectedPhotoVideo(itemUri)
    }

    private fun showAddWorkOrderSuccessDialog() {
        MessageDialog.newInstance(
            MessageDialog.IconType.Check,
            "成功送出",
            "服務單位會儘速與您聯繫，\n確認到府日期與時間",
            button1Style = MessageDialog.ButtonStyle.Fill,
            button1Title = getString(R.string.confirm),
            button1Listener = {
                findNavController().navigateUp()
            }
        ).show(childFragmentManager, "add_work_order")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}