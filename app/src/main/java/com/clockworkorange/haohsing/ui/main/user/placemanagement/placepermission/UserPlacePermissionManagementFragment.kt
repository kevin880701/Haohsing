package com.clockworkorange.haohsing.ui.main.user.placemanagement.placepermission

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
import com.clockworkorange.haohsing.databinding.FragmentUserPlaceDeviceShareManagementBinding
import com.clockworkorange.haohsing.ui.base.BaseFragment
import com.clockworkorange.haohsing.ui.dialog.MessageDialog
import com.clockworkorange.haohsing.utils.GenericAdapterListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserPlacePermissionManagementFragment : BaseFragment(),
    GenericAdapterListener<UiCheckStatusDevice> {
    private var _binding: FragmentUserPlaceDeviceShareManagementBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserPlacePermissionManagementViewModel by viewModels()

    private val args by navArgs<UserPlacePermissionManagementFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserPlaceDeviceShareManagementBinding.inflate(layoutInflater)
        initView()
        initListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
        viewModel.setPlaceUserId(args.placeId, args.userId)
    }

    private fun initView() {
        binding.ilToolbar.tvTitle.text = "權限分享"
        binding.rvDevices.adapter = AreaDeviceAdapter(this)
    }

    private fun initListener() {
        binding.ilToolbar.btBack.setOnClickListener { findNavController().navigateUp() }
        binding.btCancel.setOnClickListener { findNavController().navigateUp() }
        binding.btSave.setOnClickListener { showConfirmSaveDialog() }
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
            (binding.rvDevices.adapter as? AreaDeviceAdapter)?.submitList(uiState.areaDevices)
        }
    }

    private suspend fun observeNavAction() {

    }

    private fun showConfirmSaveDialog() {
        MessageDialog.newInstance(
            MessageDialog.IconType.Info,
            "請問是否儲存此變更？",
            button1Style = MessageDialog.ButtonStyle.Outline,
            button1Title = "否",
            button2Style = MessageDialog.ButtonStyle.Fill,
            button2Title = "是",
            button2Listener = { viewModel.saveChange(args.placeId, args.userId) }
        ).show(childFragmentManager, "confirm_save")
    }

    override fun onItemClick(item: UiCheckStatusDevice) {
        viewModel.toggleDevice(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}