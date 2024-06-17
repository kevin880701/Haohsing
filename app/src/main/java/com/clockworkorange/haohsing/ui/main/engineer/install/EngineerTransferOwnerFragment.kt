package com.clockworkorange.haohsing.ui.main.engineer.install

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.databinding.FragmentEngineerTransferOwnerBinding
import com.clockworkorange.haohsing.ui.base.BaseFragment
import com.clockworkorange.haohsing.ui.dialog.MessageDialog
import com.clockworkorange.haohsing.ui.dialog.MessageInputDialog
import com.clockworkorange.haohsing.ui.widget.SpacesItemDecoration
import com.clockworkorange.haohsing.utils.ScreenUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber


@AndroidEntryPoint
class EngineerTransferOwnerFragment : BaseFragment() {

    private var _binding: FragmentEngineerTransferOwnerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EngineerTransferOwnerViewModel by viewModels()

    private var transferableDeviceAdapter: UiTransferableDeviceAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEngineerTransferOwnerBinding.inflate(layoutInflater)
        initView()
        initListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
    }

    private fun initView() {
        binding.ilToolbar.tvTitle.text = "轉移裝置"
        binding.ilToolbar.btBack.isVisible = false

        transferableDeviceAdapter = UiTransferableDeviceAdapter()

        binding.rvDevices.addItemDecoration(
            SpacesItemDecoration(
                bottom = ScreenUtils.dp2px(
                    requireContext(),
                    8
                )
            )
        )
        binding.rvDevices.adapter = transferableDeviceAdapter
    }

    private fun initListener() {
        with(binding) {
            btCancel.setOnClickListener { findNavController().navigateUp() }
            btOk.setOnClickListener { showInputReceiverDialog() }
        }
    }

    private fun showInputReceiverDialog() {
        MessageInputDialog.newInstance(
            title = "接收人的帳號",
            buttonTitle = "轉移裝置"
        ) { account ->
            MessageDialog.newInstance(
                MessageDialog.IconType.Info,
                "確定要轉移這些裝置?", "確認後，將無法收回",
                button1Style = MessageDialog.ButtonStyle.Outline,
                button1Title = "否",
                button2Style = MessageDialog.ButtonStyle.Fill,
                button2Title = "是"
            ) {
                viewModel.transferOwner(account)
            }.show(childFragmentManager, "confirm_transfer")
        }.show(childFragmentManager, "receiver")
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
        viewModel.uiState.collect {

            it.devices.let { devices ->
                binding.tvNoDevice.isVisible = devices.isEmpty()
                binding.rvDevices.isVisible = devices.isNotEmpty()
                binding.btOk.isEnabled = devices.isNotEmpty()
                transferableDeviceAdapter?.submitList(devices)
            }

            if (it.isTransferSuccess) {
                findNavController().navigateUp()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = EngineerTransferOwnerFragment()
    }

}