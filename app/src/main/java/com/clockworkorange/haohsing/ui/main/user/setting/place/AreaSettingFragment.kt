package com.clockworkorange.haohsing.ui.main.user.setting.place

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
import com.clockworkorange.domain.data.DevStatusCode
import com.clockworkorange.domain.entity.PowerSchedule
import com.clockworkorange.domain.entity.ScheduleType
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.databinding.FragmentAreaSettingBinding
import com.clockworkorange.haohsing.ui.base.BaseFragment
import com.clockworkorange.haohsing.ui.dialog.SelectItemDialog
import com.clockworkorange.haohsing.ui.main.user.devicedetail.requireworkorder.AddEdit
import com.clockworkorange.haohsing.ui.main.user.setting.ScheduleAdapter
import com.clockworkorange.haohsing.ui.widget.SpacesItemDecoration
import com.clockworkorange.haohsing.ui.widget.setTextAndArrow
import com.clockworkorange.haohsing.utils.GenericAdapterListener
import com.clockworkorange.haohsing.utils.ScreenUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AreaSettingFragment : BaseFragment(), GenericAdapterListener<PowerSchedule> {

    private var _binding: FragmentAreaSettingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AreaSettingViewModel by viewModels()

    private val args by navArgs<AreaSettingFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAreaSettingBinding.inflate(layoutInflater)
        initView()
        initListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
        viewModel.setAreaId(args.areaId)
    }

    private fun initView() = binding.run {
        ilToolbar.tvTitle.text = "${args.areaName}設定"
        ilSwPower.tvTitle.text = "電源"
        ilSwHotWaterLock.tvTitle.text = "熱水出水解鎖"
        ilSwCold.tvTitle.text = "製冷器啟動"
        binding.ilSwReheat.tvTitle.text = "再加熱功能"
        binding.ilHeatTemp.setTextAndArrow("熱水保溫溫度")
        binding.ilColdTemp.setTextAndArrow("冰水保溫溫度")
        binding.rvSchedules.adapter = ScheduleAdapter(this@AreaSettingFragment, ScheduleType.Area)
        binding.rvSchedules.addItemDecoration(
            SpacesItemDecoration(
                bottom = ScreenUtils.dp2px(
                    requireContext(),
                    8
                )
            )
        )
    }

    private fun initListener() = binding.run {
        ilToolbar.btBack.setOnClickListener { findNavController().navigateUp() }
        ilSwPower.swEnable.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setFeatEnableOrNot(
                DevStatusCode.POWER,
                isChecked
            )
        }
        ilSwHotWaterLock.swEnable.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setFeatEnableOrNot(
                DevStatusCode.HOT_WATER_LOCK,
                isChecked
            )
        }
        ilSwCold.swEnable.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setFeatEnableOrNot(
                DevStatusCode.COLD_MAKE,
                isChecked
            )
        }
        binding.ilSwReheat.swEnable.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setFeatEnableOrNot(
                DevStatusCode.RE_HEAT,
                isChecked
            )
        }
        binding.ilHeatTemp.root.setOnClickListener { showSelectHotTempDialog() }
        binding.ilColdTemp.root.setOnClickListener { showSelectColdTempDialog() }
        binding.btAddSchedule.setOnClickListener {
            val dest =
                AreaSettingFragmentDirections.actionAreaSettingFragmentToPowerScheduleFragment(
                    addedit = AddEdit.Add,
                    subjectId = args.areaId,
                    scope = ScheduleType.Area
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
            setHotWaterTemp(it)
            viewModel.selectHotWaterTemp(it)
        }.show(childFragmentManager, "select_hot")
    }

    private fun setHotWaterTemp(temp: String) {
        binding.ilHeatTemp.tvInfo.text = "$temp°C"
    }

    private fun setHotColdTemp(temp: String) {
        binding.ilColdTemp.tvInfo.text = "$temp°C"
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
            setHotColdTemp(it)
            viewModel.selectColdWaterTemp(it)
        }.show(childFragmentManager, "select_cold")
    }

    private fun bindViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { observeUiState() }
                launch { viewModel.toastFlow.collect { showToast(it) } }
            }
        }
    }

    private suspend fun observeUiState() {
        viewModel.uiState.collect { uiState ->
            (binding.rvSchedules.adapter as? ScheduleAdapter)?.submitList(uiState.schedules)
        }
    }

    override fun onItemClick(item: PowerSchedule) {
        val dest =
            AreaSettingFragmentDirections.actionAreaSettingFragmentToPowerScheduleFragment(
                addedit = AddEdit.Edit,
                subjectId = args.areaId,
                scheduleId = item.id,
                scope = ScheduleType.Area
            )
        findNavController().navigate(dest)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}