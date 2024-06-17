package com.clockworkorange.haohsing.ui.main.pair

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.databinding.FragmentPairGuideBinding
import com.clockworkorange.haohsing.ui.base.BaseFragment
import com.clockworkorange.haohsing.ui.main.pair.engineer.EngineerPairViewModel
import com.clockworkorange.haohsing.ui.main.pair.user.UserPairViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
abstract class PairGuideFragment : BaseFragment() {

    private var _binding: FragmentPairGuideBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPairGuideBinding.inflate(layoutInflater)
        initView()
        initListener()
        return binding.root
    }

    private fun initView() {
        binding.ilToolbar.tvTitle.text = "搜尋設備"
    }

    private fun initListener() {
        binding.ilToolbar.btBack.setOnClickListener { findNavController().navigateUp() }
        binding.btStartScan.setOnClickListener { onStartScanClick() }
        binding.btInputModel.setOnClickListener { onStartAddDeviceManually() }
    }

    abstract fun onStartScanClick()

    abstract fun onStartAddDeviceManually()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

@AndroidEntryPoint
class UserPairGuideFragment : PairGuideFragment() {

    private val viewModel: UserPairViewModel by hiltNavGraphViewModels(R.id.user_pair_device)

    override fun onStartScanClick() {
        findNavController().navigate(UserPairGuideFragmentDirections.actionUserPairGuideFragmentToScanQRCodeFragment())
    }

    override fun onStartAddDeviceManually() {
        findNavController().navigate(R.id.action_userPairGuideFragment_to_setUpTraditionalDeviceFragment)
    }
}

@AndroidEntryPoint
class EngineerPairGuideFragment : PairGuideFragment() {

    private val viewModel: EngineerPairViewModel by hiltNavGraphViewModels(R.id.engineer_pair_device)

    override fun onStartScanClick() {
        findNavController().navigate(EngineerPairGuideFragmentDirections.actionEngineerPairGuideFragmentToEngineerScanQRCodeFragment())
    }

    override fun onStartAddDeviceManually() {
        findNavController().navigate(R.id.action_engineerPairGuideFragment_to_engineerSetUpTraditionalDeviceFragment)
    }

}