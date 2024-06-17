package com.clockworkorange.haohsing.ui.login

import android.content.Intent
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
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.databinding.FragmentLoginBinding
import com.clockworkorange.haohsing.ui.base.BaseFragment
import com.clockworkorange.haohsing.ui.dialog.MessageDialog
import com.clockworkorange.haohsing.ui.main.engineer.EngineerMainActivity
import com.clockworkorange.haohsing.ui.main.user.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber


@AndroidEntryPoint
class LoginFragment : BaseFragment(), AppleLoginDialog.Listener {

    private val RC_GOOGLE_SOGN_IN = 1001

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(layoutInflater)
        initListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
    }

    private fun initListener() {
        binding.etMail.setTextWatcher { mail ->
            val password = binding.etPassword.getInputText()
            binding.btLogin.isEnabled = mail.isNotEmpty() && password.isNotEmpty()
        }

        binding.etPassword.setTextWatcher { password ->
            val mail = binding.etMail.getInputText()
            binding.btLogin.isEnabled = mail.isNotEmpty() && password.isNotEmpty()
        }

        binding.btLogin.setOnClickListener {
            val mail = binding.etMail.getInputText()
            val password = binding.etPassword.getInputText()
            val keepLoginStatus = binding.cbKeepLogin.isChecked
            viewModel.login(mail, password, keepLoginStatus)
        }

        binding.btGoogleLogin.setOnClickListener {
            startLoginWithGoogle()
        }

        binding.tvForgetPassword.setOnClickListener {
            val mail = binding.etMail.getInputText()
            viewModel.forgetPassword(mail)
        }

        binding.tvRegister.setOnClickListener {
            viewModel.onRegisterClick()
        }

        binding.btAppleLogin.setOnClickListener {
            AppleLoginDialog.newInstance(this)
                .show(childFragmentManager, "apple_login")
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
            binding.ilLoading.root.isVisible = it.isLoading

            if (it.accountFieldError != null) {
                binding.etMail.setError(it.accountFieldError)
            } else {
                binding.etMail.clearError()
            }

            if (it.passwordFieldError != null) {
                binding.etPassword.setError(it.passwordFieldError)
            } else {
                binding.etPassword.clearError()
            }

            it.loginErrorDialog?.let { type -> showDialog(type) }

        }
    }

    private suspend fun observeNavAction() {
        viewModel.navAction.collect {
            when (it) {
                LoginNav.NavToMain -> {
                    val intent = Intent(requireActivity(), MainActivity::class.java)
                    requireActivity().startActivity(intent)
                    requireActivity().finish()
                }
                is LoginNav.NavToForgetPassword -> {
                    findNavController().navigate(
                        LoginFragmentDirections.actionLoginFragmentToForgetPasswordFragment(
                            it.mail
                        )
                    )
                }

                LoginNav.NavToRegister -> {
                    findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegisterAccountFragment())
                }
                LoginNav.NavToEngineerMain -> {
                    val intent = Intent(requireActivity(), EngineerMainActivity::class.java)
                    requireActivity().startActivity(intent)
                    requireActivity().finish()
                }
            }
        }
    }

    private fun showDialog(type: LoginViewModel.UiState.LoginErrorDialog) = when (type) {
        LoginViewModel.UiState.LoginErrorDialog.AccountNotActive -> {
            MessageDialog.newInstance(
                MessageDialog.IconType.Info,
                getString(R.string.not_verify_yet), getString(R.string.goto_mail_active_account),
                button1Style = MessageDialog.ButtonStyle.Outline,
                button1Title = getString(R.string.back),
                button1Listener = { viewModel.onDialogDismiss() },
                button2Style = MessageDialog.ButtonStyle.FillRed,
                button2Title = getString(R.string.resend_email),
                button2Listener = {
                    viewModel.onDialogDismiss()
                    viewModel.resendActiveEmail(binding.etMail.getInputText())
                }
            ).show(childFragmentManager, "not_active_dialog")
        }
        LoginViewModel.UiState.LoginErrorDialog.SystemMaintain -> {
            MessageDialog.newInstance(
                MessageDialog.IconType.Alert,
                getString(R.string.login_fail), getString(R.string.system_maintain_login_later),
                button1Style = MessageDialog.ButtonStyle.Fill,
                button1Title = getString(R.string.confirm),
                button1Listener = { viewModel.onDialogDismiss() }
            ).show(childFragmentManager, "system_maintain_dialog")
        }
        LoginViewModel.UiState.LoginErrorDialog.WrongAccountPassword -> {
            MessageDialog.newInstance(
                MessageDialog.IconType.Alert,
                getString(R.string.login_fail), getString(R.string.wrong_account_password),
                button1Style = MessageDialog.ButtonStyle.Fill,
                button1Title = getString(R.string.confirm),
                button1Listener = { viewModel.onDialogDismiss() }
            ).show(childFragmentManager, "wrong_account_password_dialog")
        }
        LoginViewModel.UiState.LoginErrorDialog.NoNetwork -> {
            MessageDialog.newInstance(
                MessageDialog.IconType.Alert,
                getString(R.string.login_fail), "無網路連線",
                button1Style = MessageDialog.ButtonStyle.Fill,
                button1Title = getString(R.string.confirm),
                button1Listener = { viewModel.onDialogDismiss() }
            ).show(childFragmentManager, "no_network_dialog")
        }
    }

    /**
     * 需在 google cloud console API 設定 OAuth 憑證，指定 keystore 產生的 SHA1
     * 且用來設定的 google cloud 專案需與後端 OAuth 使用的專案相同 : office
     */
    private fun startLoginWithGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(getString(R.string.backend_server_oauth_client_id))
            .requestServerAuthCode(getString(R.string.backend_server_oauth_client_id))
            .build()

        val client = GoogleSignIn.getClient(requireActivity(), gso)

        val existedAccount = GoogleSignIn.getLastSignedInAccount(requireActivity())
        if (existedAccount != null && existedAccount.idToken != null) {
            Timber.d("existedAccount idToken:${existedAccount.idToken}")
            viewModel.googleLogin(existedAccount.idToken!!)
            return
        }

        val intent = client.signInIntent
        startActivityForResult(intent, RC_GOOGLE_SOGN_IN)
    }

    override fun onAppleLoginSuccess(authCode: String) {
        viewModel.appleLogin(authCode)
    }

    override fun onAppleLoginFail() {
        showToast("Apple登入失敗")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_GOOGLE_SOGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken ?: throw RuntimeException("google login error")
                viewModel.googleLogin(idToken)
            }
            catch (e: ApiException) {
                // status code 10 : 通常是 OAuth 憑證指定的 SHA1 與 app 不同
                Timber.e("ApiException ${e.status}")
                showToast("登入失敗: ${e.statusCode}")
            }
            catch (e: Exception) {
                Timber.e(e)
                showToast("登入失敗:${e.message}")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}