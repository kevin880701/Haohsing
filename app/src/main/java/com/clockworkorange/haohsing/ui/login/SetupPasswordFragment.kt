package com.clockworkorange.haohsing.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.databinding.FragmentSetupPasswordBinding
import com.clockworkorange.haohsing.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SetupPasswordFragment : BaseFragment() {

    private var _binding: FragmentSetupPasswordBinding? = null
    private val binding get() = _binding!!

    private val viewModel by activityViewModels<RegisterViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSetupPasswordBinding.inflate(layoutInflater)
        initListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
    }

    private fun initListener() {
        binding.btBack.setOnClickListener { findNavController().navigateUp() }

        binding.etPassword1.setTextWatcher {
            checkPassword()
            checkAgreeButtonState()
        }

        binding.etPassword2.setTextWatcher {
            checkPassword()
            checkAgreeButtonState()
        }
        binding.cbAgreePrivacy.setOnCheckedChangeListener { buttonView, isChecked ->
            checkPassword()
            checkAgreeButtonState()
        }

        binding.tvPrivacy.setOnClickListener {
            setFragmentResultListener(PrivacyFragment.KEY_AGREE_PRIVACY) { key, bundle ->
                val isAgree = bundle.getBoolean(PrivacyFragment.KEY_AGREE_PRIVACY, false)
                binding.cbAgreePrivacy.isChecked = isAgree
            }
            val dest =
                SetupPasswordFragmentDirections.actionSetupPasswordFragmentToPrivacyFragment()
            findNavController().navigate(dest)
        }

        binding.btAgree.setOnClickListener {
            viewModel.registerWithPassword(binding.etPassword1.getInputText())
        }
    }

    private fun checkPassword() {
        val password1 = binding.etPassword1.getInputText()

        if (password1.length !in 6..18) {
            binding.etPassword1.setError(getString(R.string.password_need_six_eighteen))
        } else {
            binding.etPassword1.clearError()
        }

        val password2 = binding.etPassword2.getInputText()
        if (password2.isNotEmpty() && password2 != password1) {
            binding.etPassword2.setError(getString(R.string.password_not_same))
        } else {
            binding.etPassword2.clearError()
        }

    }

    private fun checkAgreeButtonState() {
        val isPasswordError =
            binding.etPassword1.isErrorShowing || binding.etPassword2.isErrorShowing
        val isAgreePrivacy = binding.cbAgreePrivacy.isChecked
        binding.btAgree.isEnabled = !isPasswordError && isAgreePrivacy
    }

    private fun bindViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.navAction.collect {
                        if (it is RegisterNav.NavToRegisterSuccess) {
                            val dest =
                                SetupPasswordFragmentDirections.actionSetupPasswordFragmentToRegisterSuccessFragment()
                            findNavController().navigate(dest)
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}