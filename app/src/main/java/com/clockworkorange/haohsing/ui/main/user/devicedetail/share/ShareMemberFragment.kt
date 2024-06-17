package com.clockworkorange.haohsing.ui.main.user.devicedetail.share

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
import androidx.navigation.fragment.navArgs
import com.clockworkorange.domain.usecase.device.ShareMember
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.databinding.FragmentShareMemberBinding
import com.clockworkorange.haohsing.ui.base.BaseFragment
import com.clockworkorange.haohsing.ui.dialog.MessageDialog
import com.clockworkorange.haohsing.ui.dialog.MessageInputDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ShareMemberFragment : BaseFragment(), ShareMemberAdapter.Listener {

    private var _binding: FragmentShareMemberBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ShareMemberViewModel by viewModels()

    private val args by navArgs<ShareMemberFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShareMemberBinding.inflate(layoutInflater)
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
        binding.ilToolbar.tvTitle.text = "設備分享"
        binding.rvMembers.adapter = ShareMemberAdapter(this)
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
            (binding.rvMembers.adapter as? ShareMemberAdapter)?.submitList(uiState.shareMemberList)
            binding.tvEmptyNote.isVisible = uiState.shareMemberList.isEmpty()
        }
    }

    private suspend fun observeNavAction() {

    }

    private fun showAddMemberDialog() {
        MessageInputDialog.newInstance(
            "新增分享成員",
            buttonTitle = "邀請",
            showClearButton = true,
            listener = { viewModel.addShareMember(it) }
        ).show(childFragmentManager, "invite_member")
    }

    override fun onDeleteMemberClick(member: ShareMember) {
        MessageDialog.newInstance(
            MessageDialog.IconType.Alert,
            title = "確定移除此成員使用權限嗎？",
            message = "此成員將無法使用此設備",
            button1Style = MessageDialog.ButtonStyle.Outline,
            button1Title = getString(R.string.cancel),
            button2Style = MessageDialog.ButtonStyle.FillRed,
            button2Title = getString(R.string.confirm),
            button2Listener = { viewModel.deleteMember(member) }
        ).show(childFragmentManager, "delete_member")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}