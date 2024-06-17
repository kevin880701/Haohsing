package com.clockworkorange.haohsing.ui.main.pair

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.databinding.FragmentPairSuccessBinding
import com.clockworkorange.haohsing.ui.base.BaseFragment
import com.clockworkorange.haohsing.ui.main.pair.engineer.EngineerPairViewModel
import com.clockworkorange.haohsing.ui.main.pair.user.UserPairViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
abstract class PairSuccessFragment : BaseFragment() {

    private var _binding: FragmentPairSuccessBinding? = null
    protected val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPairSuccessBinding.inflate(layoutInflater)
        initView()
        initListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    onBackPress()
                }
            })
        bindViewModel()
    }


    abstract fun initView()
    abstract fun initListener()
    abstract fun bindViewModel()

    open fun onBackPress() {}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

@AndroidEntryPoint
class UserPairSuccessFragment : PairSuccessFragment() {

    private val viewModel: UserPairViewModel by hiltNavGraphViewModels(R.id.user_pair_device)

    override fun initView() {
        binding.ilToolbar.tvTitle.text = "新增成功"
        binding.ilToolbar.btBack.isVisible = false
    }

    override fun initListener() {

    }

    override fun bindViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { uiState ->
                        binding.tvName.text = uiState.deviceName
                    }
                }
                launch {
                    delay(3000)
                    findNavController().popBackStack(R.id.userMainFragment, false)
                }
            }
        }
    }
}

@AndroidEntryPoint
class EngineerPairSuccessFragment : PairSuccessFragment() {

    private val viewModel: EngineerPairViewModel by hiltNavGraphViewModels(R.id.engineer_pair_device)

    override fun initView() {
        binding.ilToolbar.tvTitle.text = "新增成功"
        binding.ilToolbar.btBack.isVisible = false
    }

    override fun initListener() {

    }

    override fun bindViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { uiState ->
                        binding.tvName.text = uiState.name
                    }
                }
                launch {
                    delay(3000)
                    findNavController().popBackStack(R.id.engineerMainNavFragment, false)
                }
            }
        }
    }
}