package com.clockworkorange.haohsing.ui.main.user.analysis.device

import android.os.Bundle
import android.text.Html
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
import com.clockworkorange.haohsing.databinding.FragmentDeviceStatisticsBinding
import com.clockworkorange.haohsing.ui.base.BaseFragment
import com.clockworkorange.haohsing.ui.widget.init
import com.clockworkorange.haohsing.ui.widget.setData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DeviceStatisticsFragment : BaseFragment() {

    private var _binding: FragmentDeviceStatisticsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<DeviceStatisticsViewModel>()
    private val args by navArgs<DeviceStatisticsFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeviceStatisticsBinding.inflate(layoutInflater)
        initView()
        initListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
        viewModel.getStatistics(args.deviceId)
    }

    private fun initView() = binding.run {
        ilToolbar.tvTitle.text = "數據分析"
        ilHourUsageChart.init()
    }

    private fun initListener() = binding.run {
        ilToolbar.btBack.setOnClickListener { findNavController().navigateUp() }
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
        with(binding) {
            viewModel.uiState.collect { uiState ->
                uiState.deviceStatistics?.let {
                    ilPanelPowerSummary.setData(it.powerDayMonthUsage)
                    ilPanelWaterSummary.setData(it.waterDayMonthUsage)
                    ilHourUsageChart.setData(powerHourUsage = it.powerHourValues, waterHourUsage = it.waterHourValues)
                    it.suggest?.let { suggest ->
                        llSuggest.isVisible = true
                        tvSuggest.text = suggest
                    }
                }
                ilLoading.root.isVisible = uiState.isLoading
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}