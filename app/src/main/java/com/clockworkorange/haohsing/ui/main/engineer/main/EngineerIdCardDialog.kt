package com.clockworkorange.haohsing.ui.main.engineer.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.clockworkorange.domain.Result
import com.clockworkorange.domain.entity.UserInfo
import com.clockworkorange.domain.usecase.user.GetUserInfoUseCase
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.databinding.FragmentEngineerIdCardDialogBinding
import com.clockworkorange.haohsing.utils.loadImage
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class EngineerIdCardDialog : BottomSheetDialogFragment() {

    companion object {
        fun newInstance() = EngineerIdCardDialog()
    }

    private var _binding: FragmentEngineerIdCardDialogBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var getUserInfoUseCase: GetUserInfoUseCase

    override fun getTheme(): Int {
        return R.style.CustomRoundBottomSheetDialogStyle
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEngineerIdCardDialogBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                getUserInfoUseCase.invoke(Unit).collect { result ->
                    when (result) {
                        is Result.Success -> {
                            processUserInfo(result.data)
                        }
                        is Result.Error -> {
                            Toast.makeText(requireContext(), "無法取得資訊", Toast.LENGTH_LONG).show()
                            dismiss()
                        }
                    }
                }
            }
        }
    }

    private fun processUserInfo(data: UserInfo) {
        with(binding) {
            ivUser.loadImage(data.image)
            tvName.text = data.name
            tvVendorName.text = data.vendorName
            tvPhone.text = data.phone
            tvId.text = "員工編號：%d".format(data.id)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}