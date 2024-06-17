package com.clockworkorange.haohsing.ui.main.user.devicedetail.troubleshooting

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
import com.clockworkorange.haohsing.databinding.FragmentTroubleShootingBinding
import com.clockworkorange.haohsing.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TroubleShootingFragment : BaseFragment() {

    private var _binding: FragmentTroubleShootingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TroubleShootingViewModel by viewModels()

    private val args: TroubleShootingFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTroubleShootingBinding.inflate(layoutInflater)
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
        binding.ilToolbar.tvTitle.text = "簡易故障排除"
    }

    private fun initListener() {
        binding.ilToolbar.btBack.setOnClickListener { findNavController().navigateUp() }
        binding.byRequireMaintain.setOnClickListener {
            val dest =
                TroubleShootingFragmentDirections.actionTroubleShootingFragmentToRequireMaintainFragment(
                    args.deviceId
                )
            findNavController().navigate(dest)
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

    }

    private suspend fun observeNavAction() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}