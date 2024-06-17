package com.clockworkorange.haohsing.ui.main.user.account

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.databinding.FragmentAccountSettingBinding
import com.clockworkorange.haohsing.ui.base.BaseFragment
import com.clockworkorange.haohsing.ui.dialog.MessageDialog
import com.clockworkorange.haohsing.ui.dialog.MessageInputDialog
import com.clockworkorange.haohsing.ui.dialog.SelectCityRegionDialog
import com.clockworkorange.haohsing.ui.login.LoginActivity
import com.clockworkorange.haohsing.ui.widget.setTextAndArrow
import com.clockworkorange.haohsing.ui.widget.setTextOnly
import com.clockworkorange.haohsing.utils.FileUtil
import com.clockworkorange.haohsing.utils.loadImage
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import kotlin.coroutines.resume

@AndroidEntryPoint
class AccountSettingFragment : BaseFragment() {

    private var _binding: FragmentAccountSettingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AccountSettingViewModel by viewModels()

    private val pickPictureLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { imageUri ->
            try {
                if (imageUri == null) return@registerForActivityResult
                val inputStream = requireContext().contentResolver.openInputStream(imageUri)
                val inputBitmap = BitmapFactory.decodeStream(inputStream)
                val file = FileUtil.writeBitmapToFile(requireContext(), inputBitmap)
                viewModel.updateUserImage(file)
                inputBitmap.recycle()
            } catch (e: Exception) {
                showToast(e.message ?: "")
                Timber.e(e)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountSettingBinding.inflate(layoutInflater)
        initView()
        initListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
    }

    private fun initView() {
        binding.ilToolbar.tvTitle.text = "個人資料"
        binding.ilName.setTextAndArrow("名稱")
        binding.ilMail.setTextOnly("Email")
        binding.ilChangePassword.setTextAndArrow("變更密碼")
        binding.ilPhone.setTextAndArrow("電話")
        binding.ilRegion.setTextAndArrow("地區")
    }

    private fun initListener() {
        binding.ilToolbar.btBack.setOnClickListener { findNavController().navigateUp() }
        binding.ivUserImage.setOnClickListener { showChangeUserImageDialog() }
        binding.ilName.root.setOnClickListener { showChangeNameDialog() }
        binding.ilChangePassword.root.setOnClickListener { showChangePasswordDialog() }
        binding.ilPhone.root.setOnClickListener { showChangePhoneDialog() }
        binding.ilRegion.root.setOnClickListener { showChangeCityRegionDialog() }
        binding.btLogout.setOnClickListener { showLogoutDialog() }
    }

    private fun showChangeUserImageDialog() {
        MessageDialog.newInstance(
            MessageDialog.IconType.Info,
            "更換大頭貼?",
            button1Title = getString(R.string.cancel),
            button1Style = MessageDialog.ButtonStyle.FillRed,
            button2Title = getString(R.string.confirm),
            button2Style = MessageDialog.ButtonStyle.Fill,
            button2Listener = { pickImage() }
        ).show(childFragmentManager, "change_user_img")
    }

    private fun pickImage() {
        pickPictureLauncher.launch("image/*")
    }

    private fun showChangeNameDialog() {
        MessageInputDialog.newInstance(
            title = "修改名稱",
            inputType = EditorInfo.TYPE_CLASS_TEXT,
            listener = { viewModel.updateUserName(it) }
        ).show(childFragmentManager, "change_name")
    }

    private fun showChangePasswordDialog() {
        EditPasswordDialog.newInstance { oldPwd, newPwd ->
            viewModel.updateUserPassword(oldPwd, newPwd)
        }.show(childFragmentManager, "change_password")
    }

    private fun showChangePhoneDialog() {
        MessageInputDialog.newInstance(
            title = "修改電話",
            inputType = EditorInfo.TYPE_CLASS_PHONE,
            listener = { viewModel.updateUserPhone(it) }
        ).show(childFragmentManager, "change_name")
    }

    private fun showChangeCityRegionDialog() {
        SelectCityRegionDialog.newInstance(object : SelectCityRegionDialog.Listener {
            override fun selectResult(city: String, region: String) {
                viewModel.updateUserCityRegion(city, region)
            }
        }).show(childFragmentManager, "select_city_region")
    }

    private fun showLogoutDialog() {
        MessageDialog.newInstance(
            MessageDialog.IconType.Alert,
            "確定登出帳號嗎？",
            button1Style = MessageDialog.ButtonStyle.Outline,
            button1Title = getString(R.string.cancel),
            button2Style = MessageDialog.ButtonStyle.Fill,
            button2Title = getString(R.string.confirm),
            button2Listener = {
                FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val token = task.result
                        viewModel.logout(token)
                    }
                }
            }
        ).show(childFragmentManager, "logout_dialog")
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
            with(binding) {
                tvHeadName.text = uiState.name
                tvHeadMail.text = uiState.mail
                ilName.tvInfo.text = uiState.name
                ilMail.tvInfo.text = uiState.mail
                ilPhone.tvInfo.text = uiState.phone
                ilRegion.tvInfo.text = uiState.region
                ivUserImage.loadImage(uiState.image)

                if (uiState.isEngineer) {
                    setLayoutEngineer()
                }
            }
        }
    }

    private fun setLayoutEngineer() {
        with(binding) {
            tvHeadMail.isInvisible = true
            ilName.setTextOnly()
            ilPhone.setTextOnly()
            ilChangePassword.root.isVisible = false
            ilRegion.root.isVisible = false
            val color = ContextCompat.getColor(requireContext(), R.color.colorLightGray)
            ilName.tvInfo.setTextColor(color)
            ilMail.tvInfo.setTextColor(color)
            ilPhone.tvInfo.setTextColor(color)

            ilName.root.isClickable = false
            ilChangePassword.root.isClickable = false
            ilPhone.root.isClickable = false
        }
    }

    private suspend fun observeNavAction() {
        viewModel.navFlow.collect {
            lifecycleScope.launch {
                logoutGoogle()

                val intent = Intent(requireActivity(), LoginActivity::class.java)
                requireActivity().startActivity(intent)
                requireActivity().finish()
            }
        }
    }

    private suspend fun logoutGoogle() = suspendCancellableCoroutine<Unit> { continuation ->
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(getString(R.string.backend_server_oauth_client_id))
            .requestServerAuthCode(getString(R.string.backend_server_oauth_client_id))
            .build()

        val client = GoogleSignIn.getClient(requireActivity(), gso)

        client.signOut()
            .addOnCompleteListener {
                continuation.resume(Unit)
            }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}