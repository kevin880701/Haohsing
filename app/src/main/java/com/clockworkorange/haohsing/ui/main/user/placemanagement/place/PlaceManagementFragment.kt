package com.clockworkorange.haohsing.ui.main.user.placemanagement.place

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
import com.clockworkorange.domain.usecase.palcearea.PlaceSummary
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.databinding.FragmentPlaceManagementBinding
import com.clockworkorange.haohsing.ui.base.BaseFragment
import com.clockworkorange.haohsing.ui.dialog.MessageDialog
import com.clockworkorange.haohsing.ui.dialog.MessageInputDialog
import com.clockworkorange.haohsing.utils.scrollToBottom
import com.clockworkorange.haohsing.utils.setDivider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class PlaceManagementFragment : BaseFragment(), PlaceManagementAdapter.Listener {

    private var _binding: FragmentPlaceManagementBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaceManagementViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaceManagementBinding.inflate(layoutInflater)
        initView()
        initListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
    }

    override fun onStart() {
        super.onStart()
        viewModel.refresh()
    }

    private fun initView() {
        binding.ilToolbar.tvTitle.text = "單位管理"
        binding.rvPlace.adapter = PlaceManagementAdapter(this)
        binding.rvPlace.setDivider(R.drawable.divider_line)
        binding.ilEmpty.tvEmptyNote.text = "尚無單位"
    }

    private fun initListener() {
        binding.ilToolbar.btBack.setOnClickListener { findNavController().navigateUp() }
        binding.btAddPlace.setOnClickListener {
            viewModel.addNewPlace(successCallback = {
                binding.rvPlace.scrollToBottom()
            })
        }
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
            binding.ilEmpty.root.isVisible = uiState.placeInfoList.isEmpty()
            (binding.rvPlace.adapter as? PlaceManagementAdapter)?.submitList(uiState.placeInfoList)
        }
    }

    private suspend fun observeNavAction() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPlaceNameClick(placeInfo: PlaceSummary) {
        MessageInputDialog.newInstance(
            "編輯名稱",
            listener = { text ->
                viewModel.editPlaceName(placeInfo.toPlaceInfo(), text)
            }
        ).show(childFragmentManager, "editName")
    }

    override fun onPlaceAreaClick(placeInfo: PlaceSummary) {
        findNavController().navigate(
            PlaceManagementFragmentDirections.actionPlaceManagementFragmentToAreaManagementFragment(
                placeInfo.id
            )
        )
    }

    override fun onPlaceShareClick(placeInfo: PlaceSummary) {
        val dest =
            PlaceManagementFragmentDirections.actionPlaceManagementFragmentToPlaceDeviceShareMemberFragment(
                placeInfo.id
            )
        findNavController().navigate(dest)
    }

    override fun onDeletePlaceClick(placeInfo: PlaceSummary) {
        MessageDialog.newInstance(
            MessageDialog.IconType.Alert,
            "所有此單位的設定與裝置將被移除，要馬上移除嗎？",
            button1Style = MessageDialog.ButtonStyle.Outline,
            button1Title = getString(R.string.cancel),
            button2Style = MessageDialog.ButtonStyle.FillRed,
            button2Title = "移除",
            button2Listener = { viewModel.removePlace(placeInfo.toPlaceInfo()) }
        ).show(childFragmentManager, "remove_place")
    }
}