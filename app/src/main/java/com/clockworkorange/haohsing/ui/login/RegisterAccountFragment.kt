package com.clockworkorange.haohsing.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.databinding.FragmentRegisterAccountBinding
import com.clockworkorange.haohsing.ui.base.BaseFragment
import com.clockworkorange.haohsing.ui.dialog.MessageDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterAccountFragment : BaseFragment() {

    private val viewModel: RegisterViewModel by activityViewModels()

    private var _binding: FragmentRegisterAccountBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterAccountBinding.inflate(layoutInflater)
        initListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
    }

    private fun initListener() {
        binding.btBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.etMail.setTextWatcher {
            binding.btRegister.isEnabled = it.isNotEmpty()
        }

        binding.btRegister.setOnClickListener {
            val mail = binding.etMail.getInputText()
            viewModel.createAccount(mail)
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
        viewModel.uiState.collect {
            if (it.isShowAccountExistDialog) {
                MessageDialog.newInstance(
                    MessageDialog.IconType.Info,
                    "此帳號已被使用",
                    button1Style = MessageDialog.ButtonStyle.Fill,
                    button1Title = getString(R.string.confirm),
                    button1Listener = { viewModel.onAccountExistDialogShow() }
                ).show(childFragmentManager, "account_exist_dialog")
            }

            if (it.accountFieldError != null) {
                binding.etMail.setError(it.accountFieldError)
            } else {
                binding.etMail.clearError()
            }
        }
    }

    private suspend fun observeNavAction() {
        viewModel.navAction.collect {
            when (it) {
                is RegisterNav.NavToSetupPassword -> {
                    val dest =
                        RegisterAccountFragmentDirections.actionRegisterAccountFragmentToSetupPasswordFragment()
                    findNavController().navigate(dest)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}