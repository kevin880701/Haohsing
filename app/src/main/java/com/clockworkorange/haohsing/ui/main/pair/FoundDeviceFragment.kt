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
import com.clockworkorange.domain.usecase.device.SearchedDevice
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.databinding.FragmentFoundDeviceBinding
import com.clockworkorange.haohsing.ui.base.BaseFragment
import com.clockworkorange.haohsing.ui.dialog.MessageDialog
import com.clockworkorange.haohsing.ui.main.pair.engineer.EngineerPairViewModel
import com.clockworkorange.haohsing.ui.main.pair.user.UserPairViewModel
import com.clockworkorange.haohsing.utils.loadImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber


@AndroidEntryPoint
abstract class FoundDeviceFragment : BaseFragment() {

    private var _binding: FragmentFoundDeviceBinding? = null
    protected val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFoundDeviceBinding.inflate(layoutInflater)
        initView()
        initListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    onBackPress()
                }
            })

        viewDidLoad()
    }

    abstract fun initListener()

    abstract fun initView()

    abstract fun bindViewModel()

    abstract fun viewDidLoad()

    abstract fun onBackPress()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

@AndroidEntryPoint
class UserFoundDeviceFragment : FoundDeviceFragment() {

    private val viewModel: UserPairViewModel by hiltNavGraphViewModels(R.id.user_pair_device)

    override fun initView() {
        binding.ilToolbar.tvTitle.text = "找到裝置"
        binding.ilToolbar.btBack.isVisible = false
    }

    override fun initListener() {

    }

    override fun bindViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { observeUiState() }
                launch { viewModel.toastFlow.collect { showToast(it) } }
            }
        }
    }

    suspend fun observeUiState() {
        viewModel.uiState.collect { uiState ->
            processSearchedDevice(uiState.searchedDevice)
            binding.ilLoading.root.isVisible = uiState.isLoading
            uiState.scanWiFiList?.joinToString { it.ssid }?.let {
                Timber.d(it)
                showToast("掃描到wifi")
            }

            if (uiState.isBlePairFail) {
                showRetryDialog()
            }

            if (uiState.scanWiFiList != null) {
                findNavController().navigate(UserFoundDeviceFragmentDirections.actionUserFoundDeviceFragmentToSetUpWiFiFragment())
            }
        }
    }

    private fun showRetryDialog() {
        MessageDialog.newInstance(
            MessageDialog.IconType.Alert,
            "藍牙配對失敗",
            button1Style = MessageDialog.ButtonStyle.Outline,
            button1Title = "重試",
            button1Listener = { viewModel.startPair() },
            button2Style = MessageDialog.ButtonStyle.FillRed,
            button2Title = getString(R.string.cancel),
            button2Listener = { findNavController().popBackStack(R.id.userMainFragment, false) }
        ).show(childFragmentManager, "pair_fail")
    }

    private fun processSearchedDevice(device: SearchedDevice?) {
        device ?: return
        with(binding) {
            tvModelName.text = device.name
            tvMac.text = "Mac:${device.mac}"
            tvSn.text = "序號: ${device.sn}"
            ivDevice.loadImage(device.modelImage)
        }
    }

    override fun viewDidLoad() {
        viewModel.startPair()
    }

    override fun onBackPress() {
        findNavController().popBackStack(R.id.userMainFragment, false)
    }
}

@AndroidEntryPoint
class EngineerFoundDeviceFragment : FoundDeviceFragment() {

    private val viewModel: EngineerPairViewModel by hiltNavGraphViewModels(R.id.engineer_pair_device)

    override fun initView() {
        binding.ilToolbar.tvTitle.text = "找到裝置"
        binding.ilToolbar.btBack.isVisible = false
    }

    override fun initListener() {

    }

    override fun bindViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { observeUiState() }
                launch { viewModel.toastFlow.collect { showToast(it) } }
            }
        }
    }

    suspend fun observeUiState() {
        viewModel.uiState.collect { uiState ->
            processSearchedDevice(uiState.qrcodeSearchedDevice)
            binding.ilLoading.root.isVisible = uiState.isLoading

            if (uiState.isBlePairFail) {
                showRetryDialog()
            }

            if (uiState.scanWiFiList != null) {
                findNavController().navigate(EngineerFoundDeviceFragmentDirections.actionEngineerFoundDeviceFragmentToEngineerSetUpWiFiFragment())
            }
        }
    }

    private fun showRetryDialog() {
        MessageDialog.newInstance(
            MessageDialog.IconType.Alert,
            "藍牙配對失敗",
            button1Style = MessageDialog.ButtonStyle.Outline,
            button1Title = "重試",
            button1Listener = { viewModel.startPair() },
            button2Style = MessageDialog.ButtonStyle.FillRed,
            button2Title = getString(R.string.cancel),
            button2Listener = { findNavController().popBackStack(R.id.userMainFragment, false) }
        ).show(childFragmentManager, "pair_fail")
    }

    private fun processSearchedDevice(device: SearchedDevice?) {
        device ?: return
        with(binding) {
            tvModelName.text = device.name
            tvMac.text = "Mac:${device.mac}"
            tvSn.text = "序號: ${device.sn}"
            ivDevice.loadImage(device.modelImage)
        }
    }

    override fun viewDidLoad() {
        viewModel.startPair()
    }

    override fun onBackPress() {
        findNavController().popBackStack(R.id.engineerMainNavFragment, false)
    }
}