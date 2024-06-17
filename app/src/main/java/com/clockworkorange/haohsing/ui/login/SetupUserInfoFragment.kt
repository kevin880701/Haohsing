package com.clockworkorange.haohsing.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.clockworkorange.haohsing.databinding.FragmentSetupUserInfoBinding
import com.clockworkorange.haohsing.ui.base.BaseFragment

class SetupUserInfoFragment : BaseFragment() {

    private var _binding: FragmentSetupUserInfoBinding? = null
    private val binding get() = _binding!!

    private val viewModel by activityViewModels<RegisterViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSetupUserInfoBinding.inflate(layoutInflater)
        initListener()
        return binding.root
    }

    private fun initListener() {
        binding.btBack.setOnClickListener { findNavController().navigateUp() }

        binding.etName.setTextWatcher {
            if (it.isNotEmpty()) {
                binding.etName.setChecked()
            } else {
                binding.etName.clearCheck()
            }
        }

        binding.etPhone.setTextWatcher {

        }

        binding.etLocation.setOnClickListener {

        }


        binding.btFinish.setOnClickListener {
            val name = binding.etName.getInputText()
            val phone = binding.etPhone.getInputText()
            val location = binding.etLocation.getInputText()
//            viewModel.setUpUserInfo(name, phone, location)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}