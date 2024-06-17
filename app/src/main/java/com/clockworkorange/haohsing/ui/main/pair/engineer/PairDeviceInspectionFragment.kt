package com.clockworkorange.haohsing.ui.main.pair.engineer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.clockworkorange.domain.usecase.device.DrainStatus
import com.clockworkorange.domain.usecase.task.ChangedFilter
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.databinding.FragmentPairDeviceInspectionBinding
import com.clockworkorange.haohsing.ui.base.BaseFragment
import com.clockworkorange.haohsing.ui.dialog.MessageDialog
import com.clockworkorange.haohsing.ui.dialog.MessageInputDialog
import com.clockworkorange.haohsing.ui.main.engineer.main.taskform.AddFilterDialog
import com.clockworkorange.haohsing.ui.widget.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PairDeviceInspectionFragment : BaseFragment() {

    private var _binding: FragmentPairDeviceInspectionBinding? = null
    private val binding get() = _binding!!

    private val pairViewModel: EngineerPairViewModel by hiltNavGraphViewModels(R.id.engineer_pair_device)
    private val viewModel: PairDeviceInspectionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPairDeviceInspectionBinding.inflate(layoutInflater)
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
            ilToolbar.tvTitle.text = "新增設備"
            ilTds.setTextAndArrow("TDS", "-- TDS")
            ilWaterPressure.setTextAndArrow("水壓", "-- psi")
            ilCheckReliefValve.set("安裝減壓閥")
            ilVoltage.setTextAndArrow("電壓")
            ilCheckGround.set("接地")
            ilSelectDrainSmooth.set("暢通")
            ilSelectDrainPuddle.set("積水")
            ilSelectDrainOverflow.set("溢水")
            ilInstallRoFilter.set("濾芯是否安裝")
            ilCheckRoWork.set("RO馬達正常啟動")
            ilFilter1.setTextAndArrow("新增濾芯-1", "設定壽命")
            ilFilter2.setTextAndArrow("新增濾芯-2", "設定壽命")
            ilFilter3.setTextAndArrow("新增濾芯-3", "設定壽命")
            ilFilter4.setTextAndArrow("新增濾芯-Ro", "設定壽命")
            ilFilter5.setTextAndArrow("新增濾芯-5", "設定壽命")
            ilCheckIntakeWaterWork.set("進水正常")
            ilCheckOutWaterWork.set("出水正常")
            ilCheckHeatWork.set("加熱正常")
            ilCheckCoolWork.set("製冷正常")
            ilCheckFunctionWork.set("功能正常")

            ilCheckPowerPlug.set("電源插座的位置")
            ilCheckPowerSwitch.set("電源開關的位置")
            ilCheckWaterSwitch.set("水源與水源開關的位置")
            ilCheckBasicFunction.set("產品基本功能說明")
            ilCheckBasicMaintain.set("產品基本保養說明")
            ilCheckConnectService.set("保養維修聯絡方式")
            ilCheckGuide.set("交付說明書與保證卡")

            ilPhotoProduct.set("主產品安裝狀態")
            ilPhotoFaucet.set("龍頭照")
            ilPhotoBreaker.set("漏水斷路照")
            ilPhotoWaterPressureGauge.set("水壓表")
        }
    }

    private fun initListener() {
        with(binding) {
            ilToolbar.btBack.setOnClickListener { showCancelDialog() }
            ilTds.setListener { showInputTDSDialog() }
            ilWaterPressure.setListener { showInputPsiDialog() }
            ilCheckReliefValve.setListener { viewModel.clickCheckReliefValve() }
            ilVoltage.setListener { showInputVoltageDialog() }
            ilCheckGround.setListener { viewModel.clickCheckGround() }
            ilSelectDrainSmooth.setListener { viewModel.setDrainStatus(DrainStatus.DrainSmooth) }
            ilSelectDrainPuddle.setListener { viewModel.setDrainStatus(DrainStatus.DrainPuddle) }
            ilSelectDrainOverflow.setListener { viewModel.setDrainStatus(DrainStatus.DrainOverflow) }
            ilInstallRoFilter.setListener { viewModel.clickInstallROFilter() }
            ilCheckRoWork.setListener { viewModel.clickRoWork() }
            btAddFilter.setOnClickListener { viewModel.addExtraFilter() }
            ilFilter1.setListener { showAddFilterDialog(ChangedFilter.FilterType.F1) }
            ilFilter2.setListener { showAddFilterDialog(ChangedFilter.FilterType.F2) }
            ilFilter3.setListener { showAddFilterDialog(ChangedFilter.FilterType.F3) }
            ilFilter4.setListener { showAddFilterDialog(ChangedFilter.FilterType.F4) }
            ilFilter5.setListener { showAddFilterDialog(ChangedFilter.FilterType.F5) }
            ilCheckIntakeWaterWork.setListener { viewModel.clickIntakeWaterWork() }
            ilCheckOutWaterWork.setListener { viewModel.clickOutWaterWork() }
            ilCheckHeatWork.setListener { viewModel.clickHeatWork() }
            ilCheckCoolWork.setListener { viewModel.clickCoolWork() }
            ilCheckFunctionWork.setListener { viewModel.clickFunctionWork() }
            ilCheckPowerPlug.setListener { viewModel.clickPowerPlug() }
            ilCheckPowerSwitch.setListener { viewModel.clickPowerSwitch() }
            ilCheckWaterSwitch.setListener { viewModel.clickWaterSwitch() }
            ilCheckBasicFunction.setListener { viewModel.clickBasicFunction() }
            ilCheckBasicMaintain.setListener { viewModel.clickBasicMaintain() }
            ilCheckConnectService.setListener { viewModel.clickConnectService() }
            ilCheckGuide.setListener { viewModel.clickGuide() }
            ilPhotoProduct.setListener { pickProductLauncher.launch(arrayOf("image/*")) }
            ilPhotoFaucet.setListener { pickFaucetLauncher.launch(arrayOf("image/*")) }
            ilPhotoBreaker.setListener { pickBreakerLauncher.launch(arrayOf("image/*")) }
            ilPhotoWaterPressureGauge.setListener { pickWaterPressureGaugeLauncher.launch(arrayOf("image/*")) }

            ilPhotoProduct.setRemoveListener { viewModel.removeProductImage() }
            ilPhotoFaucet.setRemoveListener { viewModel.removeFaucetImage() }
            ilPhotoBreaker.setRemoveListener { viewModel.removeBreakerImage() }
            ilPhotoWaterPressureGauge.setRemoveListener { viewModel.removeWaterPressureGaugeImage() }

            btCancel.setOnClickListener { showCancelDialog() }
            btNext.setOnClickListener { clickNext() }
        }
    }

    private fun bindViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { observeUiState() }
                launch { viewModel.toastFlow.collect { showToast(it) } }
                launch {
                    pairViewModel.uiState.collect {
                        binding.ilLoading.root.isVisible = it.isLoading
                        if (it.isPairSuccess) {
                            findNavController().navigate(PairDeviceInspectionFragmentDirections.actionPairDeviceInspectionFragmentToEngineerPairSuccessFragment())
                        }
                    }
                }
            }
        }
    }

    private suspend fun observeUiState() {
        viewModel.uiState.collect { uiState ->
            with(binding) {
                ilTds.tvInfo.text = "${uiState.tds ?: "--"} TDS"
                ilWaterPressure.tvInfo.text = "${uiState.psi ?: "--"} psi"
                ilCheckReliefValve.setCheckStatus(uiState.checkReliefValve)
                ilVoltage.tvInfo.text = "${uiState.voltage ?: "--"} V"
                ilCheckGround.setCheckStatus(uiState.checkGround)
                setDrainStatus(uiState.drainStatus)
                ilInstallRoFilter.setCheckStatus(uiState.installROFilter)
                ilCheckRoWork.setCheckStatus(uiState.checkROWork)
                setExtraFilter(uiState.extraFilterCount)
                ilFilter1.tvInfo.text =
                    uiState.changedFilter1?.lifeMonth?.let { "%d個月".format(it) } ?: "設定壽命"
                ilFilter2.tvInfo.text =
                    uiState.changedFilter2?.lifeMonth?.let { "%d個月".format(it) } ?: "設定壽命"
                ilFilter3.tvInfo.text =
                    uiState.changedFilter3?.lifeMonth?.let { "%d個月".format(it) } ?: "設定壽命"
                ilFilter4.tvInfo.text =
                    uiState.changedFilter4?.lifeMonth?.let { "%d個月".format(it) } ?: "設定壽命"
                ilFilter5.tvInfo.text =
                    uiState.changedFilter5?.lifeMonth?.let { "%d個月".format(it) } ?: "設定壽命"
                ilCheckIntakeWaterWork.setCheckStatus(uiState.checkIntakeWaterWork)
                ilCheckOutWaterWork.setCheckStatus(uiState.checkOutWaterWork)
                ilCheckHeatWork.setCheckStatus(uiState.checkHeatWork)
                ilCheckCoolWork.setCheckStatus(uiState.checkCoolWork)
                ilCheckFunctionWork.setCheckStatus(uiState.checkFunctionWork)

                ilCheckPowerPlug.setCheckStatus(uiState.checkPowerPlug)
                ilCheckPowerSwitch.setCheckStatus(uiState.checkPowerSwitch)
                ilCheckWaterSwitch.setCheckStatus(uiState.checkWaterSwitch)
                ilCheckBasicFunction.setCheckStatus(uiState.checkBasicFunction)
                ilCheckBasicMaintain.setCheckStatus(uiState.checkBasicMaintain)
                ilCheckConnectService.setCheckStatus(uiState.checkConnectService)
                ilCheckGuide.setCheckStatus(uiState.checkGuide)

                ilPhotoProduct.setImageUri(uiState.productImageUri)
                ilPhotoFaucet.setImageUri(uiState.faucetImageUri)
                ilPhotoBreaker.setImageUri(uiState.breakerImageUri)
                ilPhotoWaterPressureGauge.setImageUri(uiState.waterPressureGaugeImageUri)

                if (uiState.pairDeviceInspection != null) {
                    pairViewModel.finishDeviceInspection(uiState.pairDeviceInspection)
                }
            }
        }
    }

    private fun setDrainStatus(drainStatus: DrainStatus?) {
        drainStatus ?: return
        with(binding) {
            ilSelectDrainSmooth.setSelectStatus(drainStatus == DrainStatus.DrainSmooth)
            ilSelectDrainPuddle.setSelectStatus(drainStatus == DrainStatus.DrainPuddle)
            ilSelectDrainOverflow.setSelectStatus(drainStatus == DrainStatus.DrainOverflow)
        }
    }

    private fun setExtraFilter(extraFilterCount: Int) {
        with(binding) {
            ilFilter2.root.isVisible = extraFilterCount > 0
            ilFilter3.root.isVisible = extraFilterCount > 1
            ilFilter4.root.isVisible = extraFilterCount > 2
            ilFilter5.root.isVisible = extraFilterCount > 3
        }
    }

    private fun showAddFilterDialog(filterType: ChangedFilter.FilterType) {
        AddFilterDialog.newInstance(filterType) { filter ->
            when (filter.type) {
                ChangedFilter.FilterType.F1 -> viewModel.setChangedFilter1(filter)
                ChangedFilter.FilterType.F2 -> viewModel.setChangedFilter2(filter)
                ChangedFilter.FilterType.F3 -> viewModel.setChangedFilter3(filter)
                ChangedFilter.FilterType.F4 -> viewModel.setChangedFilter4(filter)
                ChangedFilter.FilterType.F5 -> viewModel.setChangedFilter5(filter)
            }
        }.show(childFragmentManager, "add_filter_dialog")
    }

    private fun showInputTDSDialog() {
        MessageInputDialog.newInstance(
            "TDS",
            inputType = EditorInfo.TYPE_CLASS_NUMBER
        ) { tds ->
            viewModel.setTDS(tds)
        }.show(childFragmentManager, "tds")
    }

    private fun showInputPsiDialog() {
        MessageInputDialog.newInstance(
            "水壓",
            inputType = EditorInfo.TYPE_CLASS_NUMBER
        ) { psi ->
            viewModel.setPsi(psi)
        }.show(childFragmentManager, "psi")
    }

    private fun showInputVoltageDialog() {
        MessageInputDialog.newInstance(
            "電壓",
            inputType = EditorInfo.TYPE_CLASS_NUMBER
        ) { voltage ->
            viewModel.setVoltage(voltage)
        }.show(childFragmentManager, "voltage")
    }

    private fun showCancelDialog() {
        MessageDialog.newInstance(
            MessageDialog.IconType.Alert,
            "確定要取消新增飲水機嗎？",
            "確定取消後，將不儲存當前資料",
            button1Style = MessageDialog.ButtonStyle.Outline,
            button1Title = getString(R.string.cancel),
            button2Style = MessageDialog.ButtonStyle.FillRed,
            button2Title = getString(R.string.confirm),
            button2Listener = {
                findNavController().popBackStack(R.id.engineerMainNavFragment, false)
            }
        ).show(childFragmentManager, "cancel")
    }


    private fun clickNext() {
        if (!viewModel.uiState.value.checkReliefValve) {
            showToast("尚未勾選安裝減壓閥")
            return
        }
        viewModel.clickNext()
    }

    private val pickProductLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { fileUri ->
            fileUri ?: return@registerForActivityResult
            viewModel.setProductImage(fileUri)
        }

    private val pickFaucetLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { fileUri ->
            fileUri ?: return@registerForActivityResult
            viewModel.setFaucetImage(fileUri)
        }

    private val pickBreakerLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { fileUri ->
            fileUri ?: return@registerForActivityResult
            viewModel.setBreakerImage(fileUri)
        }

    private val pickWaterPressureGaugeLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { fileUri ->
            fileUri ?: return@registerForActivityResult
            viewModel.setWaterPressureGaugeImage(fileUri)
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}