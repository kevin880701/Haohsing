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
import androidx.recyclerview.widget.ConcatAdapter
import com.clockworkorange.domain.usecase.notification.Duration
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.databinding.FragmentEngineerInstallRecordBinding
import com.clockworkorange.haohsing.ui.base.BaseFragment
import com.clockworkorange.haohsing.ui.dialog.MessageInputDialog
import com.clockworkorange.haohsing.ui.dialog.SelectItemDialog
import com.clockworkorange.haohsing.ui.main.user.notification.DurationSelectorAdapter
import com.clockworkorange.haohsing.ui.widget.SpacesItemDecoration
import com.clockworkorange.haohsing.utils.ScreenUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class EngineerInstallRecordFragment : BaseFragment(), DurationSelectorAdapter.Listener {

    private var _binding: FragmentEngineerInstallRecordBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EngineerInstallRecordViewModel by viewModels()

    private var durationSelectorAdapter: DurationSelectorAdapter? = null
    private var installRecordAdapter: UiInstallRecordAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEngineerInstallRecordBinding.inflate(layoutInflater)
        initView()
        initListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
    }

    private fun initView() {
        binding.ilToolbar.tvTitle.text = "飲水機安裝紀錄"
        binding.ilToolbar.btBack.isVisible = false

        durationSelectorAdapter = DurationSelectorAdapter(this)
        installRecordAdapter = UiInstallRecordAdapter()

        binding.rvInstallRecord.addItemDecoration(
            SpacesItemDecoration(
                bottom = ScreenUtils.dp2px(
                    requireContext(),
                    8
                )
            )
        )
        binding.rvInstallRecord.adapter =
            ConcatAdapter(durationSelectorAdapter, installRecordAdapter)

        binding.ilSearchbar.tvSearchKeyword.hint = "關鍵字"
    }

    private fun initListener() {
        with(binding) {
            ilSearchbar.root.setOnClickListener { showInputSearchKeywordDialog() }
            ilSearchbar.btClearSearch.setOnClickListener { viewModel.clearSearch() }
            tvTransfer.setOnClickListener { findNavController().navigate(R.id.action_engineerMainNavFragment_to_engineerTransferOwnerFragment) }
        }
    }

    private fun showInputSearchKeywordDialog() {
        MessageInputDialog.createSearchDialog(requireContext()) {
            viewModel.search(it)
        }.show(childFragmentManager, "keyword")
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
        viewModel.uiState.collect {
            binding.ilSearchbar.tvSearchKeyword.text = it.searchKeyword ?: "關鍵字"
            binding.ilSearchbar.btClearSearch.isVisible = it.searchKeyword != null

            binding.rvInstallRecord.smoothScrollToPosition(0)
            installRecordAdapter?.submitList(it.installRecord)
            durationSelectorAdapter?.duration = it.duration
        }
    }

    private suspend fun observeNavAction() {

    }

    override fun onDurationSelectorClick() {
        val items = arrayOf(
            "近一個月",
            "近兩個月",
            "近三個月"
        )

        SelectItemDialog.newInstance("時間", items, getString(R.string.finished)) {
            val duration = when (it) {
                items[0] -> Duration.Recent1Month
                items[1] -> Duration.Recent2Month
                items[2] -> Duration.Recent3Month
                else -> throw IllegalArgumentException("")
            }
            viewModel.selectDuration(duration)
        }.show(childFragmentManager, "duration_selector")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = EngineerInstallRecordFragment()
    }

}