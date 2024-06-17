package com.clockworkorange.haohsing.ui.main.pair.user

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.clockworkorange.domain.entity.AreaInfo
import com.clockworkorange.domain.entity.PlaceInfo
import com.clockworkorange.domain.usecase.device.SearchedDevice
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.databinding.FragmentSetUpDeviceBinding
import com.clockworkorange.haohsing.ui.base.BaseFragment
import com.clockworkorange.haohsing.ui.dialog.*
import com.clockworkorange.haohsing.ui.main.pair.engineer.EngineerPairViewModel
import com.clockworkorange.haohsing.ui.widget.setTextAndArrow
import com.clockworkorange.haohsing.ui.widget.setTextOnly
import com.clockworkorange.haohsing.utils.loadImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
abstract class SetUpDeviceFragment : BaseFragment() {

    private var _binding: FragmentSetUpDeviceBinding? = null
    protected val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSetUpDeviceBinding.inflate(layoutInflater)
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
            ilToolbar.btBack.isVisible = false
            ilName.setTextAndArrow("名稱")
            ilName.tvInfo.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorPrimary
                )
            )
            ilService.setTextOnly("服務經銷商")
            ilService.tvInfo.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorGray
                )
            )
            ilAddress.tvInfo.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorGray
                )
            )
            ilCustomer.tvInfo.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorGray
                )
            )
        }
        initOtherView()
    }

    private fun initListener() {
        with(binding) {
            ilName.root.setOnClickListener { showEditDeviceNameDialog() }
            btAdd.setOnClickListener { onAdd() }
        }
        initOtherListener()
    }

    private fun bindViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { observeUiState() }
                launch { toast() }
            }
        }
    }

    abstract fun initOtherView()

    abstract fun onAdd()

    abstract fun initOtherListener()

    abstract suspend fun toast()

    abstract suspend fun observeUiState()

    protected fun processSearchedDevice(device: SearchedDevice?) {
        device ?: return
        with(binding) {
            tvModelName.text = device.name
            tvMac.text = "Mac:${device.mac}"
            tvSn.text = "序號: ${device.sn}"
            ivDevice.loadImage(device.modelImage)
        }
    }

    abstract fun setDeviceName(name: String)

    private fun showEditDeviceNameDialog() {
        MessageInputDialog.newInstance(
            "編輯名稱",
            buttonTitle = getString(R.string.save)
        ) { name ->
            setDeviceName(name)
        }.show(childFragmentManager, "input_name")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

@AndroidEntryPoint
class UserSetUpDeviceFragment : SetUpDeviceFragment() {
    private val viewModel: UserPairViewModel by hiltNavGraphViewModels(R.id.user_pair_device)
    override fun initOtherView() = binding.run {
        ilToolbar.tvTitle.text = "新增裝置"
        ilPlace.setTextAndArrow("單位")
        ilPlace.tvInfo.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorGray))
        ilArea.setTextAndArrow("區域")
        ilArea.tvInfo.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorGray))
        ilAddress.setTextOnly("安裝地址")
        ilCustomer.setTextOnly("地址所屬機構")

    }

    override fun onAdd() {
        viewModel.addDevice()
    }

    override fun initOtherListener() = binding.run {
        ilPlace.root.setOnClickListener { viewModel.clickEditPlace() }
        ilArea.root.setOnClickListener { viewModel.clickEditArea() }
        tvFill.setOnClickListener { viewModel.fillWarranty() }
        flOwner.setOnClickListener { showEditOwnerNameDialog() }
        flEmail.setOnClickListener { showEditOwnerEmailDialog() }
        flPhone.setOnClickListener { showEditOwnerPhoneDialog() }
        flDate.setOnClickListener { showEditBuyDateDialog() }
    }

    override suspend fun toast() {
        viewModel.toastFlow.collect { showToast(it) }
    }

    override suspend fun observeUiState() {
        viewModel.uiState.collect { uiState ->
            processSearchedDevice(uiState.searchedDevice)

            with(binding) {
                ilName.tvInfo.text = uiState.deviceName ?: "請輸入"

                ilPlace.tvInfo.text = uiState.place?.name ?: "請選擇"
                ilArea.tvInfo.text = uiState.area?.name ?: "請選擇"


                ilService.tvInfo.text = uiState.searchedDevice?.vendorName


                ilAddress.tvInfo.text = uiState.searchedDevice?.installAddress
                ilCustomer.tvInfo.text = uiState.searchedDevice?.customerAgency?.name

                flWarrantyInfo.isVisible = uiState.searchedDevice?.isRegisterWarranty == false
                llWarrantyInfoForm.isVisible = uiState.searchedDevice?.isRegisterWarranty == false
                tvWarrantyNote.isVisible = uiState.searchedDevice?.isRegisterWarranty == false

                tvOwnerName.text = uiState.ownerName ?: "請填寫"
                tvOwnerMail.text = uiState.ownerEmail ?: "請填寫"
                tvOwnerPhone.text = uiState.ownerPhone ?: "請填寫"
                tvDate.text = uiState.buyDate?.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日"))
                    ?: "--年--月--日"

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

            if (uiState.isPairSuccess) {
                findNavController().navigate(UserSetUpDeviceFragmentDirections.actionSetUpDeviceFragmentToUserPairSuccessFragment())
            }

        }
    }

    override fun setDeviceName(name: String) {
        viewModel.setDeviceName(name)
    }

    private fun showEditOwnerNameDialog() {
        MessageInputDialog.newInstance(
            "編輯名稱",
            buttonTitle = getString(R.string.save),
            listener = { viewModel.editOwnerName(it) }
        ).show(childFragmentManager, "edit_owner_name")
    }

    private fun showEditOwnerEmailDialog() {
        MessageInputDialog.newInstance(
            "編輯Email",
            buttonTitle = getString(R.string.save),
            listener = { viewModel.editOwnerEmail(it) }
        ).show(childFragmentManager, "edit_owner_name")
    }

    private fun showEditOwnerPhoneDialog() {
        MessageInputDialog.newInstance(
            "編輯電話",
            inputType = EditorInfo.TYPE_CLASS_PHONE,
            buttonTitle = getString(R.string.save),
            listener = { viewModel.editOwnerPhone(it) }
        ).show(childFragmentManager, "edit_owner_name")
    }

    private fun showEditBuyDateDialog() {
        DatePickerDialog(requireContext()).apply {
            setOnDateSetListener { view, year, month, dayOfMonth ->
                val localDate = LocalDate.of(year, month, dayOfMonth)
                viewModel.setBuyDate(localDate)
            }
        }.show()
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
}

@AndroidEntryPoint
class EngineerSetUpDeviceFragment : SetUpDeviceFragment() {
    private val viewModel: EngineerPairViewModel by hiltNavGraphViewModels(R.id.engineer_pair_device)
    override fun initOtherView() = binding.run {
        ilToolbar.tvTitle.text = "連上裝置"
        ilAddress.setTextAndArrow("安裝地址")
        ilCustomer.setTextAndArrow("地址所屬機構")

        ilPlace.root.isVisible = false
        ilArea.root.isVisible = false
        flWarrantyInfo.isVisible = false
        llWarrantyInfoForm.isVisible = false
        tvWarrantyNote.isVisible = false
    }

    override fun onAdd() {
        findNavController().navigate(R.id.action_engineerSetUpDeviceFragment_to_pairDeviceInspectionFragment)
    }

    override fun initOtherListener() = binding.run {
        ilAddress.root.setOnClickListener { showSelectAddressDialog() }
        ilCustomer.root.setOnClickListener {
            findNavController().navigate(R.id.action_engineerSetUpDeviceFragment_to_selectCustomerPlaceFragment)
        }
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
                    viewModel.setAddress(fullAddress)
                }.show(childFragmentManager, "input_address")
            }
        }).show(childFragmentManager, "select_city_region")
    }

    override suspend fun toast() {
        viewModel.toastFlow.collect { showToast(it) }
    }

    override suspend fun observeUiState() {
        viewModel.uiState.collect { uiState ->
            processSearchedDevice(uiState.qrcodeSearchedDevice)

            with(binding) {
                ilService.tvInfo.text = uiState.userInfo?.vendorName
                ilCustomer.tvInfo.text = uiState.customerAgency?.name
                ilAddress.tvInfo.text = uiState.address
                ilName.tvInfo.text = uiState.name
                ilLoading.root.isVisible = uiState.isLoading
            }
        }
    }

    override fun setDeviceName(name: String) {
        viewModel.setDeviceName(name)
    }
}