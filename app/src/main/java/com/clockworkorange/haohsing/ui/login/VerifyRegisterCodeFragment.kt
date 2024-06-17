package com.clockworkorange.haohsing.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.clockworkorange.haohsing.databinding.FragmentVerifyRegisterCodeBinding
import com.clockworkorange.haohsing.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class VerifyRegisterCodeFragment : BaseFragment() {

    private var _binding: FragmentVerifyRegisterCodeBinding? = null
    private val binding get() = _binding!!

    private val viewModel by activityViewModels<RegisterViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVerifyRegisterCodeBinding.inflate(layoutInflater)
        initListener()
        return binding.root
    }

    private fun initListener() {
        binding.btBack.setOnClickListener { findNavController().navigateUp() }
        binding.etVerifyCode.addTextChangedListener {
            binding.btNext.isEnabled = !it.isNullOrEmpty()
        }
        binding.tvResentCode.setOnClickListener { viewModel.resentVerifyCode() }
        binding.btNext.setOnClickListener {
            val code = binding.etVerifyCode.editableText.toString()
            viewModel.verifyCode(code)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
    }

    private fun bindViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { observeUiStatus() }
                launch { observeNavAction() }
                launch { viewModel.toastFlow.collect { showToast(it) } }
            }
        }
    }

    private suspend fun observeUiStatus() {
        viewModel.uiState.collect {
            binding.tvMail.text = it.toCreateAccount
        }
    }

    private suspend fun observeNavAction() {
        viewModel.navAction.collect {
            if (it is RegisterNav.NavToSetupPassword) {
                findNavController().navigate(VerifyRegisterCodeFragmentDirections.actionVerifyRegisterCodeFragmentToSetupPasswordFragment())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}