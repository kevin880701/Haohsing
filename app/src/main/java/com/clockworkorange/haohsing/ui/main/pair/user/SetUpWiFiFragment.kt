package com.clockworkorange.haohsing.ui.main.pair.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.HiltViewModelFactory
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.databinding.FragmentSetUpWiFiBinding
import com.clockworkorange.haohsing.ui.base.BaseFragment
import com.clockworkorange.haohsing.ui.dialog.MessageDialog
import com.clockworkorange.haohsing.ui.main.pair.engineer.EngineerPairViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
abstract class SetUpWiFiFragment : BaseFragment(), SelectOtherWiFiDialog.Listener {

    private var _binding: FragmentSetUpWiFiBinding? = null
    protected val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSetUpWiFiBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initCommonView()
        initSpecificView()
        initListener()
        bindViewModel()
    }

    private fun initCommonView() {
        with(binding) {
            ilToolbar.tvTitle.text = "啟用設定模式"
            ilToolbar.btBack.isVisible = false
        }
    }

    private fun initListener() {
        binding.tvCancel.setOnClickListener {
            MessageDialog.newInstance(
                MessageDialog.IconType.Alert,
                "確定要取消新增飲水機嗎？",
                button1Style = MessageDialog.ButtonStyle.Outline,
                button1Title = getString(R.string.cancel),
                button2Style = MessageDialog.ButtonStyle.FillRed,
                button2Title = getString(R.string.confirm),
                button2Listener = {
                    onCancel()
                }
            ).show(childFragmentManager, "cancel")
        }

        binding.flSelectedWifi.setOnClickListener {
            val wifiList = getWifiList() ?: return@setOnClickListener
            SelectOtherWiFiDialog.newInstance(wifiList, this)
                .show(childFragmentManager, SelectOtherWiFiDialog.TAG)
        }

        binding.etPassword.addTextChangedListener {
            binding.btNext.isEnabled = it?.toString()?.isNotEmpty() == true
        }

        binding.btNext.setOnClickListener {
            val selectedWifi = getSelectedWifi() ?: return@setOnClickListener
            val ssid = selectedWifi.ssid
            val password = binding.etPassword.editableText.toString()
            setWifi(ssid, password)
        }
    }

    abstract fun getSelectedWifi(): SimpleWiFiInfo?

    fun showPairWiFiFailDialog() {
        MessageDialog.newInstance(
            MessageDialog.IconType.Alert,
            "網路連線失敗，請再試一次",
            "可能原因：Wi-Fi 密碼錯誤/訊號不穩",
            button1Style = MessageDialog.ButtonStyle.Fill,
            button1Title = getString(R.string.confirm)
        ).show(childFragmentManager, "pair_wifi_fail")
    }

    abstract fun selectWifi(wiFiInfo: SimpleWiFiInfo)

    fun setLoading(visible: Boolean) {
        binding.ilLoading.root.isVisible = visible
    }

    override fun onWifiSelected(wifi: SimpleWiFiInfo) {
        selectWifi(wifi)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    abstract fun setWifi(ssid: String, password: String)

    abstract fun getWifiList(): List<SimpleWiFiInfo>?

    abstract fun onCancel()

    abstract fun bindViewModel()

    abstract fun initSpecificView()

    abstract fun onActiveSuccess()
}

@AndroidEntryPoint
class EngineerSetUpWiFiFragment : SetUpWiFiFragment() {

    private val viewModel: EngineerPairViewModel by hiltNavGraphViewModels(R.id.engineer_pair_device)

    override fun initSpecificView() {}

    override fun setWifi(ssid: String, password: String) {
        viewModel.setWiFi(ssid, password)
    }

    override fun getSelectedWifi(): SimpleWiFiInfo? = viewModel.uiState.value.selectedWifi

    override fun selectWifi(wiFiInfo: SimpleWiFiInfo) {
        viewModel.selectWifi(wiFiInfo)
    }

    override fun getWifiList(): List<SimpleWiFiInfo>? = viewModel.uiState.value.scanWiFiList

    override fun onCancel() {
        findNavController().popBackStack(R.id.engineerMainNavFragment, false)
    }

    override fun bindViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { observeUiState() }
                launch { viewModel.toastFlow.collect { showToast(it) } }
            }
        }
    }

    private suspend fun observeUiState() {
        viewModel.uiState.collect { uiState ->

            binding.tvSelectedWifiName.text = uiState.selectedWifi?.ssid

            setLoading(uiState.isLoading)

            if (uiState.isPairWiFiFail) {
                showPairWiFiFailDialog()
            }

            if (uiState.isActiveSuccess) {
                onActiveSuccess()
            }
        }
    }

    override fun onActiveSuccess() {

        findNavController().navigate(EngineerSetUpWiFiFragmentDirections.actionEngineerSetUpWiFiFragmentToEngineerSetUpDeviceFragment())
    }
}

@AndroidEntryPoint
class UserSetUpWiFiFragment : SetUpWiFiFragment() {

    private val viewModel: UserPairViewModel by hiltNavGraphViewModels(R.id.user_pair_device)

    override fun initSpecificView() {}

    override fun getSelectedWifi(): SimpleWiFiInfo? = viewModel.uiState.value.selectedWifi

    override fun setWifi(ssid: String, password: String) {
        viewModel.setWiFi(ssid, password)

    }

    override fun getWifiList(): List<SimpleWiFiInfo>? = viewModel.uiState.value.scanWiFiList

    override fun onCancel() {
        findNavController().popBackStack(R.id.userMainFragment, false)
    }

    override fun bindViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { observeUiState() }
                launch { viewModel.toastFlow.collect { showToast(it) } }
            }
        }
    }

    override fun selectWifi(wiFiInfo: SimpleWiFiInfo) {
        viewModel.selectWifi(wiFiInfo)
    }

    private suspend fun observeUiState() {
        viewModel.uiState.collect { uiState ->

            binding.tvSelectedWifiName.text = uiState.selectedWifi?.ssid

            setLoading(uiState.isLoading)

            if (uiState.isPairWiFiFail) {
                showPairWiFiFailDialog()
            }

            if (uiState.isActiveSuccess) {
                onActiveSuccess()
            }
        }
    }

    override fun onActiveSuccess() {
        findNavController().navigate(UserSetUpWiFiFragmentDirections.actionSetUpWiFiFragmentToSetUpDeviceFragment())
    }
}

/**
 * page : 重設 wifi
 *
 * 進入點 : 詳細資訊頁 -> 狀態 -> 重設 wifi
 */
@AndroidEntryPoint
class ResetWifiFragment : SetUpWiFiFragment() {

    private val viewModel: UserPairViewModel by viewModels()

    val args by navArgs<ResetWifiFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showConfirmResetDialog()
    }

    override fun setWifi(ssid: String, password: String) {
        viewModel.setWiFi(ssid, password)
    }

    override fun getWifiList(): List<SimpleWiFiInfo>? = viewModel.uiState.value.scanWiFiList

    override fun getSelectedWifi(): SimpleWiFiInfo? = viewModel.uiState.value.selectedWifi

    override fun onCancel() {
    }

    override fun bindViewModel() {

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { observeUiState() }
                launch { viewModel.toastFlow.collect { showToast(it) } }
            }
        }
    }

    private suspend fun observeUiState() {
        viewModel.uiState.collect { uiState ->

            binding.tvSelectedWifiName.text = uiState.selectedWifi?.ssid

            setLoading(uiState.isLoading)

            if (uiState.isPairWiFiFail) {
                showPairWiFiFailDialog()
            }

            if (uiState.isActiveSuccess) {
                onActiveSuccess()
            }

            if (uiState.isBlePairFail) {
                showRetryDialog()
            }
        }
    }

    override fun initSpecificView() {
        binding.tvCancel.isVisible = false
        binding.btNext.text = "確認"
    }

    private fun showConfirmResetDialog() {
        MessageDialog.newInstance(
            MessageDialog.IconType.Alert,
            "確定要重新建立連線嗎?",
            "點選確認可重新設定 WiFi",
            button1Style = MessageDialog.ButtonStyle.Outline,
            button1Title = "取消",
            button1Listener = { findNavController().navigateUp() },
            button2Style = MessageDialog.ButtonStyle.FillRed,
            button2Title = getString(R.string.confirm),
            button2Listener = { viewModel.startPair(args.mac) }
        ).show(childFragmentManager, "pair_fail")
    }

    private fun showRetryDialog() {
        MessageDialog.newInstance(
            MessageDialog.IconType.Alert,
            "請確認已經安下機面板的重置Wifi按鈕", "如需說明請洽經銷商",
            button1Style = MessageDialog.ButtonStyle.Outline,
            button1Title = "重試",
            button1Listener = { viewModel.startPair(args.mac) },
            button2Style = MessageDialog.ButtonStyle.FillRed,
            button2Title = getString(R.string.cancel),
            button2Listener = { findNavController().navigateUp() }
        ).show(childFragmentManager, "pair_fail")
    }

    override fun onActiveSuccess() {
        findNavController().navigateUp()
    }

    override fun selectWifi(wiFiInfo: SimpleWiFiInfo) {
        viewModel.selectWifi(wiFiInfo)
    }
}