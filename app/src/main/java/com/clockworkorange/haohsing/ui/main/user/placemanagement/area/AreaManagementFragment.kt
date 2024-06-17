package com.clockworkorange.haohsing.ui.main.user.placemanagement.area

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
import com.clockworkorange.domain.entity.AreaInfo
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.databinding.FragmentAreaManagementBinding
import com.clockworkorange.haohsing.ui.base.BaseFragment
import com.clockworkorange.haohsing.ui.dialog.MessageDialog
import com.clockworkorange.haohsing.ui.dialog.MessageInputDialog
import com.clockworkorange.haohsing.ui.widget.SpacesItemDecoration
import com.clockworkorange.haohsing.utils.ScreenUtils
import com.clockworkorange.haohsing.utils.scrollToBottom
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AreaManagementFragment : BaseFragment(), AreaManagementAdapter.Listener {

    private var _binding: FragmentAreaManagementBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AreaManagementViewModel by viewModels()

    private val arg: AreaManagementFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAreaManagementBinding.inflate(layoutInflater)
        initView()
        initListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
        viewModel.setPlaceId(arg.placeId)
    }

    private fun initView() {
        binding.ilToolbar.tvTitle.text = "區域管理"
        binding.ilEmpty.tvEmptyNote.text = "尚無區域"
        binding.rvAreaList.adapter = AreaManagementAdapter(this)
        binding.rvAreaList.addItemDecoration(
            SpacesItemDecoration(
                bottom = ScreenUtils.dp2px(
                    requireContext(),
                    8
                )
            )
        )
    }

    private fun initListener() {
        binding.ilToolbar.btBack.setOnClickListener { findNavController().navigateUp() }
        binding.btAddArea.setOnClickListener { showAddAreaDialog() }
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
            binding.ilEmpty.root.isVisible = uiState.areaList.isEmpty()
            binding.tvAreaCount.isVisible = uiState.areaList.isNotEmpty()
            (binding.rvAreaList.adapter as? AreaManagementAdapter)?.submitList(uiState.areaList)
            binding.tvAreaCount.text = "${uiState.areaList.size} 個區域"
        }
    }

    private suspend fun observeNavAction() {

    }

    override fun onDeleteClick(item: AreaInfo) {
        MessageDialog.newInstance(
            MessageDialog.IconType.Alert,
            "確定要刪除此區域？\n刪除後，區域內的裝置將移到\n「所有」標籤內。",
            button1Style = MessageDialog.ButtonStyle.Outline,
            button1Title = getString(R.string.cancel),
            button2Style = MessageDialog.ButtonStyle.FillRed,
            button2Title = getString(R.string.delete),
            button2Listener = {
                viewModel.deleteArea(item.id)
            }
        ).show(childFragmentManager, "delete_area")
    }

    override fun onItemClick(item: AreaInfo) {
        fun showConfirmDialog(newName: String) {
            MessageDialog.newInstance(
                MessageDialog.IconType.Info,
                title = "確定要更新此標籤？所有相關紀錄將會一併更新",
                button1Style = MessageDialog.ButtonStyle.Outline,
                button1Title = getString(R.string.cancel),
                button2Style = MessageDialog.ButtonStyle.Fill,
                button2Title = getString(R.string.confirm),
                button2Listener = { viewModel.editAreaName(item, newName) }
            ).show(childFragmentManager, "edit_area_confirm")
        }

        MessageInputDialog.newInstance(
            "編輯名稱",
            listener = { showConfirmDialog(it) }
        ).show(childFragmentManager, "edit_area")

    }

    private fun showAddAreaDialog() {
        MessageInputDialog.newInstance(
            "新增區域",
            listener = { viewModel.addArea(it) { binding.rvAreaList.scrollToBottom() } }
        ).show(childFragmentManager, "add_area")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}