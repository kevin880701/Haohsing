package com.clockworkorange.haohsing.ui.main.user.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.databinding.FragmentUserAccountBinding
import com.clockworkorange.haohsing.ui.base.BaseFragment
import com.clockworkorange.haohsing.utils.loadImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserAccountFragment : BaseFragment() {

    private var _binding: FragmentUserAccountBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserAccountViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserAccountBinding.inflate(layoutInflater)
        initView()
        initListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
    }

    private fun initView() {
        binding.ilToolbar.btBack.isVisible = false
        binding.ilToolbar.tvTitle.text = "設定"
    }

    private fun initListener() {

        binding.btAccount.setOnClickListener {
            findNavController().navigate(R.id.accountSettingFragment)
        }

        binding.flAbout.setOnClickListener {
            showToast("關於")
        }
        binding.flTerm.setOnClickListener {
            val args = TermFragmentArgs("服務條款", "http://cipw.cwoiot.com:8180/haohsing/privacy.html")
            findNavController().navigate(R.id.termFragment, args.toBundle())
        }
        binding.flPrivacy.setOnClickListener {
            val args = TermFragmentArgs("隱私權條款", "http://cipw.cwoiot.com:8180/haohsing/privacy.html")
            findNavController().navigate(R.id.termFragment, args.toBundle())

        }
        binding.flCopyright.setOnClickListener {
            val args = TermFragmentArgs("著作權條款","http://cipw.cwoiot.com:8180/haohsing/privacy.html")
            findNavController().navigate(R.id.termFragment, args.toBundle())
        }
    }

    private fun bindViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    binding.tvVersion.text = "版本:${uiState.version}"
                    binding.tvName.text = uiState.userName ?: ""
                    binding.ivUserImage.loadImage(uiState.userImage)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = UserAccountFragment()
    }
}