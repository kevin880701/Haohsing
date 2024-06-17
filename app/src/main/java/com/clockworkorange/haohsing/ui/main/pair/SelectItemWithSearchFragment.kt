package com.clockworkorange.haohsing.ui.main.pair

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.clockworkorange.domain.usecase.customer.CustomerAgency
import com.clockworkorange.domain.usecase.vendor.Vendor
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.databinding.FragmentSelectWithSearchBinding
import com.clockworkorange.haohsing.ui.base.BaseFragment
import com.clockworkorange.haohsing.ui.dialog.MessageInputDialog
import com.clockworkorange.haohsing.ui.main.pair.engineer.CustomerAdapter
import com.clockworkorange.haohsing.ui.main.pair.engineer.EngineerPairViewModel
import com.clockworkorange.haohsing.ui.main.pair.engineer.SelectCustomerViewModel
import com.clockworkorange.haohsing.ui.main.pair.user.SelectVendorViewModel
import com.clockworkorange.haohsing.ui.main.pair.user.UserManualPairViewModel
import com.clockworkorange.haohsing.ui.main.pair.user.VendorAdapter
import com.clockworkorange.haohsing.ui.widget.SpacesItemDecoration
import com.clockworkorange.haohsing.utils.GenericAdapterListener
import com.clockworkorange.haohsing.utils.ScreenUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
abstract class SelectItemWithSearchFragment : BaseFragment() {

    private var _binding: FragmentSelectWithSearchBinding? = null
    protected val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectWithSearchBinding.inflate(layoutInflater)
        initCommonView()
        initRespectiveView()
        initListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
    }

    private fun initCommonView() {
        binding.ilSearchbar.tvSearchKeyword.hint = "搜尋"
        binding.rvCustomers.addItemDecoration(
            SpacesItemDecoration(
                bottom = ScreenUtils.dp2px(
                    requireContext(),
                    8
                )
            )
        )
        binding.rvCustomers.setHasFixedSize(true)
    }

    abstract fun initRespectiveView()

    private fun initListener() {
        binding.ilSearchbar.root.setOnClickListener { showInputKeywordDialog() }
        binding.ilSearchbar.btClearSearch.setOnClickListener { clearSearch() }

        binding.ilToolbar.btBack.setOnClickListener { findNavController().navigateUp() }
    }

    private fun showInputKeywordDialog() {
        MessageInputDialog.createSearchDialog(requireContext()) {
            search(it)
        }.show(childFragmentManager, "keyword")
    }

    private fun bindViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { observeUiState() }
            }
        }
    }

    abstract fun clearSearch()

    abstract fun search(input: String)

    abstract suspend fun observeUiState()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

@AndroidEntryPoint
class SelectVendorFragment : SelectItemWithSearchFragment(), GenericAdapterListener<Vendor> {

    private val pairViewModel by hiltNavGraphViewModels<UserManualPairViewModel>(R.id.user_manual_pair)

    private val viewModel by viewModels<SelectVendorViewModel>()

    override fun initRespectiveView() {
        binding.ilToolbar.tvTitle.text = "選擇經銷商"
        binding.rvCustomers.adapter = VendorAdapter(this)
    }

    override fun clearSearch() {
        viewModel.clearSearch()
    }

    override fun search(input: String) {
        viewModel.search(input)
    }

    override suspend fun observeUiState() {
        viewModel.uiState.collect { uiState ->
            binding.ilSearchbar.tvSearchKeyword.text = uiState.keyword
            binding.ilSearchbar.btClearSearch.isVisible = uiState.keyword.isNotEmpty()
            (binding.rvCustomers.adapter as? VendorAdapter)?.submitList(uiState.filteredCustomers)
            binding.ilLoading.root.isVisible = uiState.isLoading
        }
    }

    override fun onItemClick(item: Vendor) {
        pairViewModel.selectVendor(item)
        findNavController().navigateUp()
    }
}

@AndroidEntryPoint
class SelectCustomerPlaceFragment : SelectItemWithSearchFragment(), GenericAdapterListener<CustomerAgency> {

    private val pairViewModel: EngineerPairViewModel by hiltNavGraphViewModels(R.id.engineer_pair_device)

    private val viewModel by viewModels<SelectCustomerViewModel>()

    override fun initRespectiveView() {
        binding.ilToolbar.tvTitle.text = "選擇機構"

        binding.rvCustomers.adapter = CustomerAdapter(this)
    }

    override fun clearSearch() {
        viewModel.clearSearch()
    }

    override fun search(input: String) {
        viewModel.search(input)
    }

    override suspend fun observeUiState() {
        viewModel.uiState.collect { uiState ->
            binding.ilSearchbar.tvSearchKeyword.text = uiState.keyword
            binding.ilSearchbar.btClearSearch.isVisible = uiState.keyword.isNotEmpty()
            (binding.rvCustomers.adapter as? CustomerAdapter)?.submitList(uiState.filteredCustomerAgencies)
            binding.ilLoading.root.isVisible = uiState.isLoading
        }
    }

    override fun onItemClick(item: CustomerAgency) {
        pairViewModel.selectCustomer(item)
        findNavController().navigateUp()
    }
}