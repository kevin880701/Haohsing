package com.clockworkorange.haohsing.ui.main.user.devicedetail.placearea

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
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.databinding.FragmentPlaceAreaBinding
import com.clockworkorange.haohsing.ui.base.BaseFragment
import com.clockworkorange.haohsing.ui.dialog.MessageInputDialog
import com.clockworkorange.haohsing.ui.dialog.SelectItemDialog
import com.clockworkorange.haohsing.ui.widget.setTextAndArrow
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PlaceAreaFragment : BaseFragment() {

    private var _binding: FragmentPlaceAreaBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaceAreaViewModel by viewModels()

    private val args by navArgs<PlaceAreaFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaceAreaBinding.inflate(layoutInflater)
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
        binding.ilToolbar.tvTitle.text = "單位與區域"
        binding.ilPlace.setTextAndArrow("單位")
        binding.ilArea.setTextAndArrow("區域")
    }

    private fun initListener() {
        binding.ilToolbar.btBack.setOnClickListener { findNavController().navigateUp() }

        binding.btAddPlace.setOnClickListener { showAddPlaceDialog() }
        binding.btAddArea.setOnClickListener { showAddAreaDialog() }
        binding.ilPlace.root.setOnClickListener { showSelectPlaceDialog() }
        binding.ilArea.root.setOnClickListener { showSelectAreaDialog() }
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
            binding.ilPlace.tvInfo.text = if (uiState.place == "所有") "未選擇" else uiState.place
            binding.ilArea.tvInfo.text = if (uiState.area == "所有") "未選擇" else uiState.area
        }
    }

    private suspend fun observeNavAction() {

    }

    private fun showAddPlaceDialog() {
        MessageInputDialog.newInstance(
            "新增單位",
            hint = "輸入名稱"
        ) { name ->
            viewModel.addPlace(name)
        }.show(childFragmentManager, "add_place_dialog")
    }

    private fun showAddAreaDialog() {
        MessageInputDialog.newInstance(
            "新增區域",
            hint = "輸入名稱"
        ) { name ->
            viewModel.addArea(name)
        }.show(childFragmentManager, "add_area_dialog")
    }

    private fun showSelectPlaceDialog() {
        lifecycleScope.launch {
            val placeList = viewModel.getPlaceList()
            if (placeList.isEmpty()) {
                showToast("無單位資料")
                return@launch
            }

            SelectItemDialog.newInstance(
                "單位",
                placeList.map { it.name }.toTypedArray(),
                getString(R.string.finished)
            ) { selectPlaceName ->
                placeList.firstOrNull { it.name == selectPlaceName }?.let {
                    viewModel.updateDevicePlace(it)
                }
            }.show(childFragmentManager, "select_place")
        }
    }

    private fun showSelectAreaDialog() {
        lifecycleScope.launch {
            val areaList = viewModel.getAreaList()
            if (areaList.isEmpty()) {
                showToast("無區域資料")
                return@launch
            }

            SelectItemDialog.newInstance(
                "區域",
                areaList.map { it.name }.toTypedArray(),
                getString(R.string.finished)
            ) { selectAreaName ->
                areaList.firstOrNull { it.name == selectAreaName }?.let {
                    viewModel.updateDeviceArea(it)
                }
            }.show(childFragmentManager, "select_area")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}