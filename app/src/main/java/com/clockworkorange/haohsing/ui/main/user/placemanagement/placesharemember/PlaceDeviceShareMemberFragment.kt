package com.clockworkorange.haohsing.ui.main.user.placemanagement.placesharemember

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
import com.clockworkorange.haohsing.databinding.FragmentPlaceDeviceShareMemberBinding
import com.clockworkorange.haohsing.ui.base.BaseFragment
import com.clockworkorange.haohsing.ui.dialog.MessageDialog
import com.clockworkorange.haohsing.ui.dialog.MessageInputDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PlaceDeviceShareMemberFragment : BaseFragment(), PlaceDeviceShareMemberAdapter.Listener {

    private var _binding: FragmentPlaceDeviceShareMemberBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaceDeviceShareMemberViewModel by viewModels()

    private val args by navArgs<PlaceDeviceShareMemberFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaceDeviceShareMemberBinding.inflate(layoutInflater)
        initView()
        initListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
        viewModel.setPlaceId(args.placeId)
    }

    private fun initView() {
        binding.ilToolbar.tvTitle.text = "設備分享"
        binding.rvMembers.adapter = PlaceDeviceShareMemberAdapter(this)
        binding.rvMembers.setHasFixedSize(true)
    }

    private fun initListener() {
        binding.ilToolbar.btBack.setOnClickListener { findNavController().navigateUp() }
        binding.btAddMember.setOnClickListener { showAddMemberDialog() }
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
            (binding.rvMembers.adapter as? PlaceDeviceShareMemberAdapter)?.submitList(uiState.shareMemberList)
        }
    }

    private suspend fun observeNavAction() {

    }

    private fun showAddMemberDialog() {
        MessageInputDialog.newInstance(
            "新增分享成員",
            buttonTitle = "邀請",
            showClearButton = true,
            listener = { viewModel.addPlaceDeviceShareMember(it) }
        ).show(childFragmentManager, "invite_member")
    }

    override fun onDeletePlaceDeviceShareMember(member: UiPlaceShareMember) {
        MessageDialog.newInstance(
            MessageDialog.IconType.Alert,
            title = "確定移除權限嗎？",
            message = "成員移除後，\n將移除使用飲水機的權限",
            button1Style = MessageDialog.ButtonStyle.Outline,
            button1Title = getString(R.string.cancel),
            button2Style = MessageDialog.ButtonStyle.FillRed,
            button2Title = getString(R.string.confirm),
            button2Listener = { viewModel.deletePlaceDeviceMember(member) }
        ).show(childFragmentManager, "delete_member")
    }

    override fun onMemberShareDeviceClick(member: UiPlaceShareMember) {
        val dest = PlaceDeviceShareMemberFragmentDirections
            .actionPlaceDeviceShareMemberFragmentToUserPlaceDeviceShareManagementFragment(
                args.placeId,
                member.placeShareMember.member.userId
            )
        findNavController().navigate(dest)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}