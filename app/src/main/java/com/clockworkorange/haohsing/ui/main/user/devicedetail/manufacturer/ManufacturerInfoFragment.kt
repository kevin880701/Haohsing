package com.clockworkorange.haohsing.ui.main.user.devicedetail.manufacturer

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
import com.clockworkorange.haohsing.databinding.FragmentManufacturerInfoBinding
import com.clockworkorange.haohsing.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ManufacturerInfoFragment : BaseFragment() {

    private var _binding: FragmentManufacturerInfoBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ManufacturerInfoViewModel by viewModels()

    private val args by navArgs<ManufacturerInfoFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentManufacturerInfoBinding.inflate(layoutInflater)
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
        binding.ilToolbar.tvTitle.text = "廠商資訊"
    }

    private fun initListener() {
        binding.ilToolbar.btBack.setOnClickListener { findNavController().navigateUp() }
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
            binding.ilManufacturerInfo.tvName.text = uiState.manufacturerInfo?.name ?: "--"
            binding.ilManufacturerInfo.tvPhone.text = uiState.manufacturerInfo?.phone ?: "--"
        }
    }

    private suspend fun observeNavAction() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}