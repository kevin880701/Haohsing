package com.clockworkorange.haohsing.ui.main.user.devicedetail.warranty

import android.app.DatePickerDialog
import android.app.ProgressDialog.show
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.databinding.FragmentWarrantyInfoBinding
import com.clockworkorange.haohsing.ui.base.BaseFragment
import com.clockworkorange.haohsing.ui.dialog.MessageDialog
import com.clockworkorange.haohsing.ui.dialog.MessageInputDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class WarrantyInfoFragment : BaseFragment() {

    private var _binding: FragmentWarrantyInfoBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WarrantyInfoViewModel by viewModels()

    private val args by navArgs<WarrantyInfoFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWarrantyInfoBinding.inflate(layoutInflater)
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
        binding.ilToolbar.tvTitle.text = "保固"
    }

    private fun initListener() = binding.run {
        ilToolbar.btBack.setOnClickListener { findNavController().navigateUp() }
        tvFill.setOnClickListener { viewModel.fillWarranty() }
        ivArrow1.setOnClickListener { showEditOwnerNameDialog() }
        ivArrow2.setOnClickListener { showEditOwnerEmailDialog() }
        ivArrow3.setOnClickListener { showEditOwnerPhoneDialog() }
        ivArrow4.setOnClickListener { showEditBuyDateDialog() }
        btOk.setOnClickListener { if (viewModel.isDataFilled()) showConfirmDialog() }
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

            binding.run {
                tvFill.isVisible = uiState.shouldShowEditButton
                ivArrow1.isVisible = uiState.shouldShowEditButton
                ivArrow2.isVisible = uiState.shouldShowEditButton
                ivArrow3.isVisible = uiState.shouldShowEditButton
                ivArrow4.isVisible = uiState.shouldShowEditButton
                btOk.isVisible = uiState.shouldShowEditButton

                tvOwnerName.text = uiState.ownerName ?: "請輸入"
                tvOwnerMail.text = uiState.ownerEmail ?: "請輸入"
                tvOwnerPhone.text = uiState.ownerPhone ?: "請輸入"
                tvDate.text = uiState.date?.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")) ?: "--年--月--日"
            }
        }
    }

    private suspend fun observeNavAction() {

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

    private fun showConfirmDialog() {
        MessageDialog.newInstance(
            MessageDialog.IconType.Info,
            "確定送出資料嗎？",
            "送出後，將無法修改。",
            MessageDialog.ButtonStyle.Outline,
            getString(R.string.cancel),
            button2Style = MessageDialog.ButtonStyle.Fill,
            button2Title = getString(R.string.confirm),
        ) {
            viewModel.updateWarranty()
        }.show(childFragmentManager, "confirm_warranty")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}