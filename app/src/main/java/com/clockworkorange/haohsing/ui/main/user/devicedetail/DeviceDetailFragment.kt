package com.clockworkorange.haohsing.ui.main.user.devicedetail

import android.content.Intent
import android.net.Uri
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
import com.clockworkorange.domain.entity.DeviceDetail
import com.clockworkorange.domain.entity.DeviceValue
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.databinding.FragmentDeviceDetailBinding
import com.clockworkorange.haohsing.ui.base.BaseFragment
import com.clockworkorange.haohsing.ui.dialog.MessageDialog
import com.clockworkorange.haohsing.ui.dialog.MessageInputDialog
import com.clockworkorange.haohsing.ui.widget.SpacesItemDecoration
import com.clockworkorange.haohsing.ui.widget.setArrowOnly
import com.clockworkorange.haohsing.ui.widget.setTextAndArrow
import com.clockworkorange.haohsing.ui.widget.setTextOnly
import com.clockworkorange.haohsing.utils.Filter
import com.clockworkorange.haohsing.utils.ScreenUtils
import com.clockworkorange.haohsing.utils.loadImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DeviceDetailFragment : BaseFragment(), DeviceSettingAdapter.Listener {

    private var _binding: FragmentDeviceDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DeviceDetailViewModel by viewModels()

    private val args: DeviceDetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 於 onCreate 初始化，避免切回此頁重新呼叫方法
        viewModel.setDevice(args.deviceId, args.deviceIdList)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeviceDetailBinding.inflate(layoutInflater)
        initView()
        initListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
    }

    private fun initView() {
        with(binding) {
            ilToolbar.tvTitle.text = ""
            ilToolbar.btBack.isVisible = true
            tvDeviceStatus.isVisible = false

            ilFilterWash.swEnable.isChecked = false
            ilFilterWash.tvTitle.text = "濾芯清洗"
            rvFilters.addItemDecoration(
                SpacesItemDecoration(
                    bottom = ScreenUtils.dp2px(
                        requireContext(),
                        8
                    )
                )
            )
            rvFilters.adapter = FilterAdapter()

            rvNormalSetting.addItemDecoration(
                SpacesItemDecoration(
                    bottom = ScreenUtils.dp2px(
                        requireContext(),
                        8
                    )
                )
            )
            rvNormalSetting.adapter = DeviceSettingAdapter(this@DeviceDetailFragment)

            ilAdvancedSetting.setArrowOnly("進階設定")
            ilMaintenanceLog.setArrowOnly("維修保養紀錄")
            ilEnergyLog.setArrowOnly("數據分析")
            ilInfoName.setTextAndArrow("名稱")
            ilModelName.setTextOnly("型號")
            ilConnectStatus.setTextAndArrow("狀態")

            ilShareDevice.setArrowOnly("設備分享")
            ilManufacturerInfo.setArrowOnly("廠商資訊")
            ilRequireMaintenance.setArrowOnly("報修與保養需求")
            ilReset.setArrowOnly("飲水機恢復原廠設定")
            ilWarranty.setArrowOnly("保固資訊")
            ilManual.setArrowOnly("使用說明書")
        }
    }

    private fun initListener() {
        binding.ilToolbar.btBack.setOnClickListener { findNavController().navigateUp() }
        binding.btTroubleShooting.setOnClickListener {
            val dest =
                DeviceDetailFragmentDirections.actionDeviceDetailFragmentToTroubleShootingFragment(
                    args.deviceId
                )
            findNavController().navigate(dest)
        }
        binding.btPreviousDevice.setOnClickListener { viewModel.previousDevice() }
        binding.btNextDevice.setOnClickListener { viewModel.nextDevice() }

        binding.ilAdvancedSetting.root.setOnClickListener {
            val dest =
                DeviceDetailFragmentDirections.actionDeviceDetailFragmentToAdvancedSettingFragment(
                    args.deviceId
                )
            findNavController().navigate(dest)
        }

        binding.ilMaintenanceLog.root.setOnClickListener {
            val dest =
                DeviceDetailFragmentDirections.actionDeviceDetailFragmentToWorkOrderLogFragment(args.deviceId)
            findNavController().navigate(dest)
        }
        binding.ilEnergyLog.root.setOnClickListener {
            findNavController().navigate(DeviceDetailFragmentDirections.actionDeviceDetailFragmentToDeviceStatisticsFragment(viewModel.uiState.value.device!!.id))
        }
        binding.ilInfoName.root.setOnClickListener { showEditNameDialog() }
        binding.ilConnectStatus.root.setOnClickListener {
            findNavController().navigate(
                DeviceDetailFragmentDirections.actionDeviceDetailFragmentToResetWifiFragment(
                    viewModel.uiState.value.device?.mac ?: ""
                )
            )
        }
        binding.flPlaceArea.setOnClickListener {
            val dest =
                DeviceDetailFragmentDirections.actionDeviceDetailFragmentToPlaceAreaFragment(args.deviceId)
            findNavController().navigate(dest)
        }
        binding.ilShareDevice.root.setOnClickListener {
            val dest =
                DeviceDetailFragmentDirections.actionDeviceDetailFragmentToShareMemberFragment(args.deviceId)
            findNavController().navigate(dest)
        }

        binding.ilManufacturerInfo.root.setOnClickListener {
            val dest =
                DeviceDetailFragmentDirections.actionDeviceDetailFragmentToManufacturerInfoFragment(
                    args.deviceId
                )
            findNavController().navigate(dest)
        }

        binding.ilRequireMaintenance.root.setOnClickListener {
            val dest =
                DeviceDetailFragmentDirections.actionDeviceDetailFragmentToRequireMaintainFragment(
                    args.deviceId
                )
            findNavController().navigate(dest)
        }
        binding.btNotifyManufacturer.setOnClickListener {
            // 預先代入的備註
            var note = viewModel.uiState.value.device?.errorCode?.takeIf { it.isNotEmpty() }?.let { "故障編碼#$it" } ?: ""
            viewModel.uiState.value.deviceFilterStatus.forEach {
                if (Filter.needChange(it.health)) {
                    if (note.isNotEmpty()) note += "\n"
                    note += "${it.name}需更換"
                }
            }
            val dest =
                DeviceDetailFragmentDirections.actionDeviceDetailFragmentToRequireMaintainFragment(
                    deviceId = args.deviceId, note = note
                )
            findNavController().navigate(dest)
        }
        binding.ilReset.root.setOnClickListener { showRestDialog() }
        binding.ilWarranty.root.setOnClickListener {
            val dest =
                DeviceDetailFragmentDirections.actionDeviceDetailFragmentToWarrantyInfoFragment(args.deviceId)
            findNavController().navigate(dest)
        }
        binding.ilManual.root.setOnClickListener { showManual() }
        binding.btQuit.setOnClickListener {
            showConfirmQuitDevice()
        }
    }

    private fun showManual() {
        viewModel.uiState.value.device?.manualUrl?.let {
            val intent = Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(it) }
            startActivity(intent)
        } ?: showToast("沒有該機型的說明書")
    }

    private fun showConfirmQuitDevice() {
        MessageDialog.newInstance(
            MessageDialog.IconType.Alert,
            "確定退出飲水機？",
            "退出後，\n" +
                    "將移除使用飲水機的權限，\n" +
                    "以及清除歷史資料和分享成員",
            button1Style = MessageDialog.ButtonStyle.Outline,
            button1Title = getString(R.string.cancel),
            button2Style = MessageDialog.ButtonStyle.FillRed,
            button2Title = getString(R.string.confirm),
            button2Listener = {
                viewModel.quitDevice()
            }
        ).show(childFragmentManager, "quit_device")
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
            binding.btPreviousDevice.isVisible = uiState.previousButtonVisible
            binding.btNextDevice.isVisible = uiState.nextButtonVisible
            (binding.rvNormalSetting.adapter as? DeviceSettingAdapter)?.submitList(uiState.device?.deviceSetting)
            processDevice(uiState.device)
            processManagerItemVisible(uiState.device?.isOwner ?: false)
            processDeviceValue(uiState.device?.deviceValue)
            (binding.rvFilters.adapter as? FilterAdapter)?.submitList(uiState.deviceFilterStatus)
            if (uiState.isDeviceDeleted) findNavController().navigateUp()
        }
    }

    private fun processManagerItemVisible(isManager: Boolean) {
        binding.ilAdvancedSetting.root.isVisible = isManager
        binding.ilEnergyLog.root.isVisible = isManager
        if (isManager) {
            binding.ilInfoName.setTextAndArrow()
            binding.ilInfoName.root.isClickable = true
            binding.flPlaceArea.isClickable = true
            binding.ivPlaceAreaArrow.isVisible = true
        } else {
            binding.ilInfoName.setTextOnly()
            binding.ilInfoName.root.isClickable = false
            binding.flPlaceArea.isClickable = false
            binding.ivPlaceAreaArrow.isVisible = false
        }
        binding.ilShareDevice.root.isVisible = isManager
        binding.ilRequireMaintenance.root.isVisible = isManager
        binding.ilReset.root.isVisible = isManager
        binding.btNotifyManufacturer.isVisible = isManager
    }

    private fun processDevice(device: DeviceDetail?) {
        device ?: return
        binding.ilToolbar.tvTitle.text = device.name
        binding.tvDeviceModel.text = device.modelInfo.name
        binding.ivDevice.loadImage(device.modelInfo.imageUrl)

        binding.ilInfoName.tvInfo.text = device.name
        binding.ilModelName.tvInfo.text = device.modelInfo.name
        binding.ilConnectStatus.tvInfo.text = "..."

        binding.tvPlaceArea.text =
            "${if (device.placeInfo.name == "所有") "未選擇" else device.placeInfo.name} | " +
                    "${if (device.areaInfo?.name == null || device.areaInfo?.name == "所有") "未選擇" else device.areaInfo?.name}"

        binding.ilConnectStatus.tvInfo.text = device.status.displayString

        binding.tvOwnerName.text = device.ownerInfo.name
        binding.tvOwnerAddress.text = device.ownerInfo.address
        binding.tvNoMaintainTime.text = String.format("距上次保養%d天", device.noMaintainTime)

        if (!device.errorCode.isNullOrEmpty()) {
            binding.tvDeviceStatus.isVisible = true
            binding.tvDeviceStatus.text = device.errorCode
        } else {
            binding.tvDeviceStatus.isVisible = false
        }
    }

    private fun processDeviceValue(deviceValue: DeviceValue?) {
        deviceValue ?: return
        binding.ilWaterInfo.tvWaterLevel.text = when (deviceValue.waterLevel) {
            DeviceValue.WaterLeveL.High -> "高"
            DeviceValue.WaterLeveL.Middle -> "中"
            DeviceValue.WaterLeveL.Low -> "低"
            DeviceValue.WaterLeveL.UnKnown -> "--"
        }

        val colorWaterLevel = when (deviceValue.waterLevel) {
            DeviceValue.WaterLeveL.High -> ContextCompat.getColor(requireContext(), R.color.white)
            DeviceValue.WaterLeveL.Middle -> ContextCompat.getColor(
                requireContext(),
                R.color.colorYellow
            )
            DeviceValue.WaterLeveL.Low -> ContextCompat.getColor(requireContext(), R.color.colorRed)
            DeviceValue.WaterLeveL.UnKnown -> ContextCompat.getColor(
                requireContext(),
                R.color.colorRed
            )
        }
        binding.ilWaterInfo.tvWaterLevel.setTextColor(colorWaterLevel)

        binding.ilWaterInfo.tvCleanTds.text = deviceValue.cleanWaterTDS.toString()
        val cleanTdsColor = if (deviceValue.cleanWaterTDS >= 100) {
            ContextCompat.getColor(requireContext(), R.color.colorRed)
        } else {
            ContextCompat.getColor(requireContext(), R.color.white)
        }
        binding.ilWaterInfo.tvCleanTdsWarning.isVisible = deviceValue.cleanWaterTDS >= 100
        binding.ilWaterInfo.tvCleanTds.setTextColor(cleanTdsColor)
        binding.ilWaterInfo.tvOriginTds.text = deviceValue.originWaterTDS.toString()

        binding.ilWaterTemp.tvCoolTemp.text = deviceValue.tempCool.toString()
        binding.ilWaterTemp.tvHotTemp.text = deviceValue.tempHot.toString()
        binding.ilWaterTemp.tvNormalTemp.text = deviceValue.tempNormal.toString()
    }

    private suspend fun observeNavAction() {

    }

    private fun showRestDialog() {
        MessageDialog.newInstance(
            MessageDialog.IconType.Alert,
            "確定恢復原廠設定嗎？",
            "恢復原廠設定後，\n先前的設定將不存在。",
            button1Style = MessageDialog.ButtonStyle.Outline,
            button1Title = getString(R.string.cancel),
            button2Style = MessageDialog.ButtonStyle.FillRed,
            button2Title = getString(R.string.confirm),
            button2Listener = { viewModel.setDeviceValue("h00a", true) }
        ).show(childFragmentManager, "rest_dialog")
    }

    private fun showEditNameDialog() {
        MessageInputDialog.newInstance(
            "編輯名稱",
            buttonTitle = getString(R.string.save)
        ) { newName ->
            viewModel.editDeviceName(newName)
        }.show(childFragmentManager, "edit_name")
    }

    override fun onSettingChange(code: String, enable: Boolean) {
        viewModel.setDeviceValue(code, enable)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}