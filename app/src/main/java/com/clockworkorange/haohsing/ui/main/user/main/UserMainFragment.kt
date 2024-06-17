package com.clockworkorange.haohsing.ui.main.user.main

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
import androidx.recyclerview.widget.GridLayoutManager
import com.clockworkorange.domain.entity.AreaInfo
import com.clockworkorange.domain.entity.Device
import com.clockworkorange.domain.entity.PlaceInfo
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.databinding.FragmentUserMainBinding
import com.clockworkorange.haohsing.ui.base.BaseFragment
import com.clockworkorange.haohsing.ui.main.user.devicedetail.DeviceDetailFragmentArgs
import com.clockworkorange.haohsing.ui.widget.GridSpacingItemDecoration
import com.clockworkorange.haohsing.utils.GenericAdapterListener
import com.clockworkorange.haohsing.utils.ScreenUtils
import com.clockworkorange.haohsing.utils.loadImage
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserMainFragment : BaseFragment(), GenericAdapterListener<Device> {

    private var _binding: FragmentUserMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserMainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserMainBinding.inflate(layoutInflater)
        initView()
        initListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
    }

    private fun initView() {
        val gridLayoutManager = GridLayoutManager(requireContext(), 3)
        val decoration = GridSpacingItemDecoration(3, ScreenUtils.dp2px(requireContext(), 8), false)
        binding.rvDevices.layoutManager = gridLayoutManager
        binding.rvDevices.addItemDecoration(decoration)
        binding.rvDevices.adapter = DeviceAdapter(this)
        binding.ilEmpty.tvEmptyNote.text = "尚無飲水機"
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
            viewModel.selectArea(area)
        }

        binding.btAreaMenu.setOnClickListener {
            findNavController().navigate(R.id.placeManagementFragment)
        }

        binding.btAddDevice.setOnClickListener {
            findNavController().navigate(R.id.action_nav_to_user_pair_device)
        }

        binding.refreshView.setOnRefreshListener {
            viewModel.swipeRefresh()
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

                viewModel.uiState.collect { uiState ->
                    with(binding) {
                        tvUserGreeting.text = uiState.userGreeting
                        ivUserImage.loadImage(uiState.userImage)
                        processDeviceList(uiState.devices)
                        if (!uiState.isSwipeRefreshing) refreshView.isRefreshing = false
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

    private fun processDeviceList(devices: List<Device>) {
        binding.ilEmpty.root.isVisible = devices.isEmpty()
        (binding.rvDevices.adapter as? DeviceAdapter)?.submitList(devices)
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

    override fun onItemClick(item: Device) {
        val deviceIdList = viewModel.uiState.value.devices.map { it.id }.toIntArray()
        val clickDeviceId = item.id
        val arg = DeviceDetailFragmentArgs(deviceIdList, clickDeviceId)
        findNavController().navigate(R.id.deviceDetailFragment, arg.toBundle())
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = UserMainFragment()
    }

}