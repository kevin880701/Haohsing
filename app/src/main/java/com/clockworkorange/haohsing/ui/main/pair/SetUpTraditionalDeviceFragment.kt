package com.clockworkorange.haohsing.ui.main.pair

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.clockworkorange.domain.entity.AreaInfo
import com.clockworkorange.domain.entity.PlaceInfo
import com.clockworkorange.domain.usecase.task.ChangedFilter
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.databinding.FragmentSetUpTraditionalDeviceBinding
import com.clockworkorange.haohsing.ui.base.BaseFragment
import com.clockworkorange.haohsing.ui.dialog.*
import com.clockworkorange.haohsing.ui.main.engineer.main.taskform.AddFilterDialog
import com.clockworkorange.haohsing.ui.main.pair.engineer.EngineerPairViewModel
import com.clockworkorange.haohsing.ui.main.pair.user.UserManualPairViewModel
import com.clockworkorange.haohsing.ui.widget.setListener
import com.clockworkorange.haohsing.ui.widget.setTextAndArrow
import com.clockworkorange.haohsing.ui.widget.setTextOnly
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.internal.notifyAll

@AndroidEntryPoint
abstract class SetUpTraditionalDeviceFragment : BaseFragment() {

    private var _binding: FragmentSetUpTraditionalDeviceBinding? = null
    protected val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSetUpTraditionalDeviceBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initCommonView()
        initRespectiveView()
        bindViewModel()
    }

    private fun initCommonView() = binding.run {
        ilToolbar.tvTitle.text = "新增裝置"

        ilBrand.setTextAndArrow("廠牌", "請輸入")
        ilModel.setTextAndArrow("型號", "請輸入")
        ilName.setTextAndArrow("名稱")

        ilBrand.tvInfo.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorGray))
        ilModel.tvInfo.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorGray))
        ilName.tvInfo.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
        ilVendor.tvInfo.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorGray))

        ilBrand.root.setOnClickListener { showEditBrandDialog() }
        ilModel.root.setOnClickListener { showEditModelDialog() }
        ilName.root.setOnClickListener { showEditDeviceNameDialog() }
        ilToolbar.btBack.setOnClickListener { findNavController().navigateUp() }
    }

    private fun showEditDeviceNameDialog() {
        MessageInputDialog.newInstance(
            "編輯名稱",
            buttonTitle = getString(R.string.save)
        ) {
            setDeviceName(it)
        }.show(childFragmentManager, "edit_device_name")
    }

    private fun showEditBrandDialog() {
        MessageInputDialog.newInstance(
            "編輯廠牌",
            buttonTitle = getString(R.string.save)
        ) {
            setBrand(it)
        }.show(childFragmentManager, "edit_brand")
    }

    abstract fun setBrand(brand: String)

    abstract fun setDeviceName(name: String)

    abstract fun bindViewModel()

    private fun showEditModelDialog() {
        MessageInputDialog.newInstance(
            "編輯型號",
            buttonTitle = getString(R.string.save)
        ) {
            setModel(it)
        }.show(childFragmentManager, "edit_model")
    }

    abstract fun setModel(model: String)

    abstract fun initRespectiveView()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

@AndroidEntryPoint
class UserSetUpTraditionalDeviceFragment : SetUpTraditionalDeviceFragment() {

    private val viewModel by hiltNavGraphViewModels<UserManualPairViewModel>(R.id.user_manual_pair)

    override fun initRespectiveView() = binding.run {
        ilPlace.root.isVisible = true
        ilArea.root.isVisible = true
        llFilter.isVisible = true

        ilPlace.setTextAndArrow("單位", "請輸入")
        ilArea.setTextAndArrow("區域", "請輸入")
        ilVendor.setTextAndArrow("服務經銷商")
        ilFilter1.setTextAndArrow("新增濾芯-1", "設定壽命")
        ilFilter2.setTextAndArrow("新增濾芯-2", "設定壽命")
        ilFilter3.setTextAndArrow("新增濾芯-3", "設定壽命")
        ilFilter4.setTextAndArrow("新增濾芯-Ro", "設定壽命")
        ilFilter5.setTextAndArrow("新增濾芯-5", "設定壽命")

        ilPlace.tvInfo.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorGray))
        ilArea.tvInfo.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorGray))
        ilFilter1.tvInfo.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorGray))
        ilFilter2.tvInfo.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorGray))
        ilFilter3.tvInfo.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorGray))
        ilFilter4.tvInfo.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorGray))
        ilFilter5.tvInfo.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorGray))

        ilPlace.root.setOnClickListener { viewModel.clickEditPlace() }
        ilArea.root.setOnClickListener { viewModel.clickEditArea() }
        ilVendor.root.setOnClickListener { findNavController().navigate(R.id.action_setUpTraditionalDeviceFragment_to_selectVendorFragment) }
        btAddFilter.setOnClickListener { viewModel.addExtraFilter() }
        ilFilter1.setListener { showAddFilterDialog(ChangedFilter.FilterType.F1) }
        ilFilter2.setListener { showAddFilterDialog(ChangedFilter.FilterType.F2) }
        ilFilter3.setListener { showAddFilterDialog(ChangedFilter.FilterType.F3) }
        ilFilter4.setListener { showAddFilterDialog(ChangedFilter.FilterType.F4) }
        ilFilter5.setListener { showAddFilterDialog(ChangedFilter.FilterType.F5) }
        btAdd.setOnClickListener { viewModel.addDeviceManually() }
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

    override fun setBrand(brand: String) {
        viewModel.setBrand(brand)
    }

    override fun setDeviceName(name: String) {
        viewModel.setDeviceName(name)
    }

    override fun bindViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { observeUiState() }
                launch { viewModel.toastFlow.collect { showToast(it) } }
            }
        }
    }

    override fun setModel(model: String) {
        viewModel.setModel(model)
    }

    private suspend fun observeUiState() {
        viewModel.uiState.collect { uiState ->
            with(binding) {
                ilBrand.tvInfo.text = uiState.brand ?: "請輸入"
                ilModel.tvInfo.text = uiState.model ?: "請輸入"
                ilName.tvInfo.text = uiState.deviceName ?: "新飲水機"
                ilPlace.tvInfo.text = uiState.place?.name ?: "請選擇"
                ilArea.tvInfo.text = uiState.area?.name ?: "請選擇"
                ilVendor.tvInfo.text = uiState.vendor?.name ?: "請選擇"

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

                ilLoading.root.isVisible = uiState.isLoading
            }

            when (uiState.placeDialogType) {
                PlaceDialogType.Add -> showAddPlaceDialog()
                is PlaceDialogType.Select -> showSelectPlaceDialog(uiState.placeDialogType.place)
                null -> {}
            }

            when (uiState.areaDialogType) {
                AreaDialogType.Add -> showAddAreaDialog()
                is AreaDialogType.Select -> showSelectAreaDialog(uiState.areaDialogType.area)
                null -> {}
            }

            if (uiState.isAddDeviceSuccess) {
                findNavController().popBackStack(R.id.userMainFragment, false)
            }
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

    private fun showSelectAreaDialog(area: List<AreaInfo>) {
        SelectItemDialog.newInstance(
            "區域",
            area.map { it.name }.toTypedArray(),
            getString(R.string.finished)
        ) { selectedName ->
            val selectedArea = area.first { it.name == selectedName }
            viewModel.selectArea(selectedArea)
        }.show(childFragmentManager, "select_area")
    }

    private fun showAddAreaDialog() {
        MessageInputDialog.newInstance(
            "新增區域",
            hint = "輸入名稱",
            buttonTitle = getString(R.string.save),
            listener = { name ->
                viewModel.addArea(name)
            }
        ).show(childFragmentManager, "add_area")
    }

    private fun showSelectPlaceDialog(place: List<PlaceInfo>) {
        SelectItemDialog.newInstance(
            "單位",
            place.map { it.name }.toTypedArray(),
            getString(R.string.finished)
        ) { selectedName ->
            val selectedPlace = place.first { it.name == selectedName }
            viewModel.selectPlace(selectedPlace)
        }.show(childFragmentManager, "select_place")
    }

    private fun showAddPlaceDialog() {
        MessageInputDialog.newInstance(
            "新增單位",
            hint = "輸入名稱",
            buttonTitle = getString(R.string.save),
            listener = { name ->
                viewModel.addPlace(name)
            }
        ).show(childFragmentManager, "add_place")
    }

}

@AndroidEntryPoint
class EngineerSetUpTraditionalDeviceFragment : SetUpTraditionalDeviceFragment() {

    private val pairViewModel: EngineerPairViewModel by hiltNavGraphViewModels(R.id.engineer_pair_device)

    override fun setBrand(brand: String) {
        pairViewModel.setBrand(brand)
    }

    override fun setDeviceName(name: String) {
        pairViewModel.setDeviceName(name)
    }

    override fun bindViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { observeUiState() }
                launch { pairViewModel.toastFlow.collect { showToast(it) } }
            }
        }
    }

    private suspend fun observeUiState() {
        pairViewModel.uiState.collect { uiState ->
            with(binding) {
                ilVendor.tvInfo.text = uiState.userInfo?.vendorName
                ilName.tvInfo.text = uiState.name
                ilAddress.tvInfo.text = uiState.address
                ilCustomerAgency.tvInfo.text = uiState.customerAgency?.name
                binding.ilBrand.tvInfo.text = uiState.brand ?: "請輸入"
                binding.ilModel.tvInfo.text = uiState.model ?: "請輸入"
            }

            if (uiState.manualPairDeviceId != null) {
                findNavController().navigate(R.id.action_engineerSetUpTraditionalDeviceFragment_to_pairDeviceInspectionFragment)
            }
        }
    }

    override fun setModel(model: String) {
        pairViewModel.setModel(model)
    }

    override fun initRespectiveView() = binding.run {
        ilVendor.setTextOnly("服務經銷商")

        ilAddress.root.isVisible = true
        ilCustomerAgency.root.isVisible = true

        ilAddress.setTextAndArrow("安裝地址")
        ilCustomerAgency.setTextAndArrow("地址所屬機構")

        ilAddress.setListener { showSelectAddressDialog() }
        ilCustomerAgency.setListener { findNavController().navigate(R.id.action_engineerSetUpTraditionalDeviceFragment_to_selectCustomerPlaceFragment) }
        btAdd.setOnClickListener { pairViewModel.addDeviceManually() }
    }

    private fun showSelectAddressDialog() {
        SelectCityRegionDialog.newInstance(object : SelectCityRegionDialog.Listener {
            override fun selectResult(city: String, region: String) {
                MessageInputDialog.newInstance(
                    "填寫完整地址",
                    hint = "詳細地址",
                    buttonTitle = getString(R.string.confirm),
                    showClearButton = true
                ) { address ->
                    val fullAddress = "$city$region$address"
                    pairViewModel.setAddress(fullAddress)
                }.show(childFragmentManager, "input_address")
            }
        }).show(childFragmentManager, "select_city_region")
    }
}