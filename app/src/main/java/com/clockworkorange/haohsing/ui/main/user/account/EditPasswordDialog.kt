package com.clockworkorange.haohsing.ui.main.user.account

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.clockworkorange.domain.data
import com.clockworkorange.domain.usecase.user.PasswordFormatCheckerUseCase
import com.clockworkorange.haohsing.databinding.FragmentEditPasswordDialogBinding
import com.clockworkorange.haohsing.ui.widget.CustomEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class EditPasswordDialog : DialogFragment() {

    fun interface Listener {
        fun onFillPasswordDone(oldPwd: String, newPwd: String)
    }

    companion object {
        const val TAG = "EditPasswordDialog"
        fun newInstance(listener: Listener) =
            EditPasswordDialog().apply {
                this.listener = listener
            }
    }

    private var _binding: FragmentEditPasswordDialogBinding? = null
    private val binding get() = _binding!!

    private var listener: Listener? = null

    @Inject
    lateinit var passwordFormatCheckerUseCase: PasswordFormatCheckerUseCase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditPasswordDialogBinding.inflate(layoutInflater)
        binding.etPassword.setInputType(CustomEditText.InputType.Password)
        binding.etNewPassword.setInputType(CustomEditText.InputType.Password)
        binding.etNewPassword2.setInputType(CustomEditText.InputType.Password)
        initListener()
        return binding.root
    }

    private fun initListener() {
        binding.btCancel.setOnClickListener { dismiss() }

        binding.btConfirm.setOnClickListener {
            lifecycleScope.launch {
                val oldPassword = binding.etPassword.getInputText()

                if (TextUtils.isEmpty(oldPassword)) {
                    binding.etPassword.setError("密碼為空")
                    return@launch
                }
                binding.etPassword.clearError()

                if (passwordFormatCheckerUseCase.invoke(oldPassword).data == false) {
                    binding.etPassword.setError("密碼格式錯誤，需要6-18碼")
                    return@launch
                }
                binding.etPassword.clearError()

                val newPassword = binding.etNewPassword.getInputText()

                if (TextUtils.isEmpty(newPassword)) {
                    binding.etNewPassword.setError("密碼為空")
                    return@launch
                }
                binding.etNewPassword.clearError()

                if (passwordFormatCheckerUseCase.invoke(newPassword).data == false) {
                    binding.etNewPassword.setError("密碼格式錯誤，需要6-18碼")
                    return@launch
                }
                binding.etNewPassword.clearError()

                val newPassword2 = binding.etNewPassword2.getInputText()

                if (TextUtils.isEmpty(newPassword2)) {
                    binding.etNewPassword2.setError("密碼為空")
                    return@launch
                }
                binding.etNewPassword2.clearError()

                if (passwordFormatCheckerUseCase.invoke(newPassword2).data == false) {
                    binding.etNewPassword2.setError("密碼格式錯誤，需要6-18碼")
                    return@launch
                }
                binding.etNewPassword2.clearError()

                if (!newPassword.contentEquals(newPassword2)) {
                    binding.etNewPassword2.setError("密碼不一致")
                    return@launch
                }

                listener?.onFillPasswordDone(oldPassword, newPassword)
                dismiss()
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}