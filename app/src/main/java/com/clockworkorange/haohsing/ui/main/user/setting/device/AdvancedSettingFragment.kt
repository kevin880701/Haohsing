package com.clockworkorange.haohsing.ui.main.user.setting.device

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.clockworkorange.domain.entity.AdvancedSetting
import com.clockworkorange.domain.entity.PowerSchedule
import com.clockworkorange.domain.entity.ScheduleType
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.databinding.FragmentAdvancedSettingBinding
import com.clockworkorange.haohsing.ui.base.BaseFragment
import com.clockworkorange.haohsing.ui.dialog.SelectItemDialog
import com.clockworkorange.haohsing.ui.main.user.devicedetail.requireworkorder.AddEdit
import com.clockworkorange.haohsing.ui.main.user.setting.ScheduleAdapter
import com.clockworkorange.haohsing.ui.widget.SpacesItemDecoration
import com.clockworkorange.haohsing.ui.widget.setTextAndArrow
import com.clockworkorange.haohsing.utils.GenericAdapterListener
import com.clockworkorange.haohsing.utils.ScreenUtils
import com.clockworkorange.haohsing.utils.setCheckWithoutEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AdvancedSettingFragment : BaseFragment(), GenericAdapterListener<PowerSchedule> {

    private var _binding: FragmentAdvancedSettingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AdvancedSettingViewModel by viewModels()

    private val args by navArgs<AdvancedSettingFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdvancedSettingBinding.inflate(layoutInflater)
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
        binding.ilToolbar.tvTitle.text = "進階設定"
        binding.ilSwReheat.tvTitle.text = "再加熱功能"
        binding.ilHeatTemp.setTextAndArrow("熱水保溫溫度")
        binding.ilColdTemp.setTextAndArrow("冰水保溫溫度")
        binding.rvSchedules.adapter = ScheduleAdapter(this, ScheduleType.Device)
        binding.rvSchedules.addItemDecoration(
            SpacesItemDecoration(
                bottom = ScreenUtils.dp2px(
                    requireContext(),
                    8
                )
            )
        )
    }

    private fun initListener() {
        binding.ilToolbar.btBack.setOnClickListener { findNavController().navigateUp() }
        binding.ilSwReheat.root.setOnClickListener { viewModel.toggleReheat() }
        binding.ilSwReheat.swEnable.isClickable = false
        binding.ilHeatTemp.root.setOnClickListener { showSelectHotTempDialog() }
        binding.ilColdTemp.root.setOnClickListener { showSelectColdTempDialog() }
        binding.btAddSchedule.setOnClickListener {
            val dest =
                AdvancedSettingFragmentDirections.actionAdvancedSettingFragmentToPowerScheduleFragment(
                    addedit = AddEdit.Add,
                    subjectId = args.deviceId,
                    scope = ScheduleType.Device
                )
            findNavController().navigate(dest)
        }
    }


    private fun showSelectHotTempDialog() {
        val options = viewModel.uiState.value.hotWaterTempOptions
        if (options.isEmpty()) {
            showToast("無法取得熱水溫度選項")
            return
        }

        SelectItemDialog.newInstance(
            "溫度",
            options.toTypedArray(),
            getString(R.string.finished)
        ) {
            viewModel.selectHotWaterTemp(it)
        }.show(childFragmentManager, "select_hot")
    }

    private fun showSelectColdTempDialog() {
        val options = viewModel.uiState.value.coldWaterTempOptions
        if (options.isEmpty()) {
            showToast("無法取得冰水溫度選項")
            return
        }

        SelectItemDialog.newInstance(
            "溫度",
            options.toTypedArray(),
            getString(R.string.finished)
        ) {
            viewModel.selectColdWaterTemp(it)
        }.show(childFragmentManager, "select_cold")
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
            processSetting(uiState.deviceDetail?.advancedSetting)
            (binding.rvSchedules.adapter as? ScheduleAdapter)?.submitList(uiState.deviceDetail?.schedules)
        }
    }

    private fun processSetting(advancedSetting: AdvancedSetting?) {
        advancedSetting ?: return
        with(binding) {
            ilSwReheat.swEnable.setCheckWithoutEvent(advancedSetting.reheatEnable)
            ilHeatTemp.tvInfo.text = "${advancedSetting.hotWaterTemp ?: "--"}°C"
            ilColdTemp.tvInfo.text = "${advancedSetting.coolWaterTemp ?: "--"}°C"
        }
    }

    private suspend fun observeNavAction() {

    }

    override fun onItemClick(item: PowerSchedule) {
        when(item.getScheduleType()) {
            ScheduleType.Device -> {
                val dest =
                    AdvancedSettingFragmentDirections.actionAdvancedSettingFragmentToPowerScheduleFragment(
                        addedit = AddEdit.Edit,
                        subjectId = args.deviceId,
                        scheduleId = item.id,
                        scope = ScheduleType.Device
                    )
                findNavController().navigate(dest)
            }
            ScheduleType.Area -> {
                val dest = AdvancedSettingFragmentDirections.actionAdvancedSettingFragmentToAreaSettingFragment(
                    item.areaId!!,
                    viewModel.uiState.value.deviceDetail?.areaName!!
                )
                findNavController().navigate(dest)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}