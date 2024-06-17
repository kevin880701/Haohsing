package com.clockworkorange.haohsing.ui.main.user.analysis.place

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.clockworkorange.domain.entity.AreaInfo
import com.clockworkorange.domain.entity.PlaceInfo
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.databinding.FragmentPlaceStatisticsBinding
import com.clockworkorange.haohsing.databinding.ViewAreaSuggestBinding
import com.clockworkorange.haohsing.ui.base.BaseFragment
import com.clockworkorange.haohsing.ui.main.user.setting.place.AreaSettingFragmentArgs
import com.clockworkorange.haohsing.ui.widget.init
import com.clockworkorange.haohsing.ui.widget.setData
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.tabs.TabLayout
import com.whiteelephant.monthpicker.MonthPickerDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.YearMonth
import java.util.*

@AndroidEntryPoint
class PlaceStatisticsFragment : BaseFragment() {

    private var _binding: FragmentPlaceStatisticsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<PlaceStatisticsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaceStatisticsBinding.inflate(layoutInflater)
        initView()
        initListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()

    }

    private fun initView() {
        binding.tvCount.text = "能源統計 - 共--台"

        binding.ilPanelPieChartWater.init(true)
        binding.ilPanelPieChartPower.init(false)
        binding.ilHourUsageChart.init()
    }

    private fun initListener() {
        binding.tlPlace.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val place = tab?.tag as? PlaceInfo
                viewModel.selectPlace(place)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        binding.cgTag.setOnCheckedChangeListener { group, checkedId ->
            val chip: Chip? = group.findViewById<Chip>(checkedId)
            val area = (chip?.tag as? AreaInfo)
            area?.let {
                viewModel.selectArea(area)
                binding.flSetupDevices.isVisible = it.id != -1
            }
        }

        binding.btPreviousMonth.setOnClickListener { viewModel.previousMonth() }
        binding.btNextMonth.setOnClickListener { viewModel.nextMonth() }

        binding.flSetupDevices.setOnClickListener {
            val area = viewModel.getArea() ?: return@setOnClickListener
            findNavController().navigate(R.id.areaSettingFragment, AreaSettingFragmentArgs(area.id, area.name).toBundle())
        }

        binding.tvCurrentMonth.setOnClickListener {
            val today = Calendar.getInstance()
            MonthPickerDialog.Builder(
                requireActivity(),
                { selectedMonth, selectedYear ->
                    val newYearMonth =
                        YearMonth.of(selectedYear, selectedMonth + 1)//0-based to 1-based
                    if (newYearMonth > YearMonth.now()) {
                        showToast("不能選擇未來時間")
                    } else {
                        viewModel.setCurrentYearMonth(newYearMonth)
                    }
                },
                today.get(Calendar.YEAR), today.get(Calendar.MONTH)
            ).apply {
                setMinYear(1990)
                setMaxYear(today.get(Calendar.YEAR))
                setTitle("選擇年月")
            }.build().show()
        }

    }


    private fun bindViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.map { it.placeList }
                        .distinctUntilChanged()
                        .collect { processPlaceList(it) }
                }
                launch {
                    viewModel.uiState.map { it.areaList }
                        .distinctUntilChanged()
                        .collect { processAreaList(it) }
                }
                launch { observeUiState() }
                launch { viewModel.toastFlow.collect { showToast(it) } }
            }
        }
    }

    private suspend fun observeUiState() {
        viewModel.uiState.collect { uiState ->
            with(binding) {
                tvCurrentMonth.text =
                    "%d年%d月".format(uiState.yearMonth.year, uiState.yearMonth.monthValue)

                ilPanelWaterDayMonth.setData(uiState.placeStatistics?.waterDayMonthUsage)
                ilPanelPieChartWater.setData(uiState.placeStatistics?.waterAreas, true)
                ilPanelPowerDayMonth.setData(uiState.placeStatistics?.powerDayMonthUsage)
                ilPanelPieChartPower.setData(uiState.placeStatistics?.powerAreas, false)
                ilHourUsageChart.setData(uiState.waterHourValues, uiState.powerHourValues)

                tvCount.text = uiState.placeStatistics?.deviceCount?.let { "能源統計 - 共%d台".format(it) }
                    ?: "能源統計 - 共--台"
                tvFilterSuggest.text = uiState.placeStatistics?.filterLifeSuggest
                tvFilterLifeDays.text =
                    uiState.placeStatistics?.avgFilterLifeInDays?.let { "%d".format(it) } ?: "--"
                ilLoading.root.isVisible = uiState.isLoading

                llSuggestRoot.isVisible = uiState.suggestMsg != null

                uiState.suggestMsg?.let { msgs ->
                    llSuggestContainer.removeAllViews()
                    for (msg in msgs) {
                        val suggestBinding = ViewAreaSuggestBinding.inflate(layoutInflater)
                        suggestBinding.tvSuggest.text = msg
                        llSuggestContainer.addView(suggestBinding.root)
                    }
                }
            }
        }
    }

    private fun processPlaceList(places: List<PlaceInfo>) {
        binding.tlPlace.removeAllTabs()
        places.forEach { place ->
            val tab = binding.tlPlace.newTab()
            tab.text = place.name
            tab.tag = place
            binding.tlPlace.addTab(tab)
        }
    }

    private fun processAreaList(areas: List<AreaInfo>) {
        binding.svTag.isGone = areas.isEmpty()
        binding.cgTag.removeAllViews()
        areas.forEach {
            binding.cgTag.addView(createChip(it))
        }
        (binding.cgTag.getChildAt(0) as? Chip)?.isChecked = true
    }

    private fun createChip(area: AreaInfo): Chip {
        return createChip(area.name, area)
    }

    private fun createChip(name: String, aTag: Any): Chip {
        return Chip(requireContext()).apply {
            text = name
            tag = aTag
            val drawable = ChipDrawable.createFromAttributes(
                requireContext(),
                null,
                0,
                R.style.Widget_SelectTagChip
            )
            setChipDrawable(drawable)
            setTextAppearanceResource(R.style.TextAppearance_HAOHSING_HeadLine4)
            setTextColor(
                ContextCompat.getColorStateList(
                    requireContext(),
                    R.color.chip_text_color
                )
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = PlaceStatisticsFragment()
    }
}