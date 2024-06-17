package com.clockworkorange.haohsing.ui.main.pair

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.databinding.FragmentScanQRCodeBinding
import com.clockworkorange.haohsing.ui.base.BaseActivity
import com.clockworkorange.haohsing.ui.base.BaseFragment
import com.clockworkorange.haohsing.ui.dialog.MessageDialog
import com.clockworkorange.haohsing.ui.main.pair.engineer.EngineerPairViewModel
import com.clockworkorange.haohsing.ui.main.pair.user.UserPairViewModel
import com.clockworkorange.haohsing.utils.RequireEnableBluetoothContract
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
abstract class ScanQRCodeFragment : BaseFragment() {

    private var _binding: FragmentScanQRCodeBinding? = null
    private val binding get() = _binding!!

    private var lastText: String = ""

    @Inject
    lateinit var gson: Gson

    private val requestCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGrant ->
            if (isGrant) {
                setupScan()
            } else {
                showToast("需要相機權限用以掃描QR Code")
                findNavController().navigateUp()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScanQRCodeBinding.inflate(layoutInflater)
        initView()
        initListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_DENIED
        ) {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        } else {
            setupScan()
        }
    }

    abstract fun bindViewModel()

    private fun initView() {
        binding.ilToolbar.tvTitle.text = "搜尋設備"
    }

    private fun setupScan() {
        bindViewModel()

        lastText = ""
        binding.viewQrcode.barcodeView.decoderFactory =
            DefaultDecoderFactory(listOf(BarcodeFormat.QR_CODE))
        binding.viewQrcode.setStatusText("")
        binding.viewQrcode.decodeContinuous {
            if (it.text == null || it.text == lastText) {
                return@decodeContinuous
            }
            lastText = it.text

            try {
                val content = gson.fromJson(lastText, QrCodeContent::class.java)
                onScanResult(content)
            } catch (e: Exception) {
                showScanFailDialog()
            }
        }
    }

    abstract fun onScanResult(content: QrCodeContent)

    private fun showScanFailDialog() {
        MessageDialog.newInstance(
            MessageDialog.IconType.Alert,
            "掃描失敗",
            "此類型的行動條碼無法讀取",
            button1Style = MessageDialog.ButtonStyle.Fill,
            button1Title = "重新掃描",
            button1Listener = { lastText = "" }
        ).show(childFragmentManager, "scan_fail")
    }

    protected fun showConfirmOwnershipDialog(confirmCallback: (() -> Unit)) {
        MessageDialog.newInstance(
            MessageDialog.IconType.Alert,
            "確認將該設備轉移至您的帳號嗎？",
            "該設備以所屬至其他用戶，點擊確定後，將移轉帳號",
            button1Style = MessageDialog.ButtonStyle.Outline,
            button1Title = getString(R.string.cancel),
            button1Listener = { findNavController().navigateUp() },
            button2Style = MessageDialog.ButtonStyle.Fill,
            button2Title = getString(R.string.confirm),
            button2Listener = confirmCallback
        ).show(childFragmentManager, "ownership")
    }

    override fun onResume() {
        super.onResume()
        binding.viewQrcode.resume()
    }

    override fun onPause() {
        super.onPause()
        binding.viewQrcode.pause()
    }

    private fun initListener() {
        binding.ilToolbar.btBack.setOnClickListener { findNavController().navigateUp() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

data class QrCodeContent(
    @SerializedName("id")
    val mac: String
)

@AndroidEntryPoint
class UserScanQRCodeFragment : ScanQRCodeFragment() {

    private val viewModel: UserPairViewModel by hiltNavGraphViewModels(R.id.user_pair_device)

    override fun onScanResult(content: QrCodeContent) {
        viewModel.searchDevice(content.mac)
    }

    override fun bindViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { observeUiState() }
                launch { viewModel.toastFlow.collect { showToast(it) } }
            }
        }

        viewModel.checkPermission()
    }

    private suspend fun observeUiState() {
        viewModel.uiState.collect { uiState ->

            if (uiState.isRequestBluetoothPermission) {
                bluetoothPermissionRequest.launch(
                    arrayOf(
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT
                    )
                )
            }

            if (uiState.isRequestEnableBluetooth) {
                enableBluetoothRequest.launch(Unit)
            }

            if (uiState.isRequestGPSPermission) {
                gpsPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }

            if (uiState.isRequestEnableGPS) {
                requestEnableGPS()
            }

            if (uiState.showConfirmTransferPermissionDialog) {
                showConfirmOwnershipDialog { viewModel.continueWork() }
            }

            if (uiState.searchedDevice != null) {
                findNavController().navigate(UserScanQRCodeFragmentDirections.actionUserScanQRCodeFragmentToUserFoundDeviceFragment())
            }
        }
    }

    private fun requestEnableGPS() {
        (requireActivity() as? BaseActivity)?.requestEnableGPS(object :
            BaseActivity.RequestEnableGPSCallback {
            override fun onRequestEnableGPSResult(enable: Boolean) {
                viewModel.continueWork()
            }
        })
    }

    private val bluetoothPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (it.values.all { grant -> grant }) {
                viewModel.continueWork()
            } else {
                showToast("需要藍牙權限才能掃描裝置")
                findNavController().navigateUp()
            }
        }

    private val enableBluetoothRequest =
        registerForActivityResult(RequireEnableBluetoothContract()) {
            if (it) {
                viewModel.continueWork()
            } else {
                showToast("需要開啟藍牙才能掃描裝置")
                findNavController().navigateUp()
            }
        }

    private val gpsPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                viewModel.continueWork()
            } else {
                showToast("需要定位權限才能掃描裝置")
                findNavController().navigateUp()
            }
        }
}

@AndroidEntryPoint
class EngineerScanQRCodeFragment : ScanQRCodeFragment() {

    private val viewModel: EngineerPairViewModel by hiltNavGraphViewModels(R.id.engineer_pair_device)

    override fun onScanResult(content: QrCodeContent) {
        viewModel.searchDevice(content.mac)
    }

    override fun bindViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { observeUiState() }
                launch { viewModel.toastFlow.collect { showToast(it) } }
            }
        }

        viewModel.checkPermission()
    }

    private suspend fun observeUiState() {
        viewModel.uiState.collect { uiState ->

            if (uiState.isRequestBluetoothPermission) {
                bluetoothPermissionRequest.launch(
                    arrayOf(
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT
                    )
                )
            }

            if (uiState.isRequestEnableBluetooth) {
                enableBluetoothRequest.launch(Unit)
            }

            if (uiState.isRequestGPSPermission) {
                gpsPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }

            if (uiState.isRequestEnableGPS) {
                requestEnableGPS()
            }

            if (uiState.showConfirmTransferPermissionDialog) {
                showConfirmOwnershipDialog { viewModel.continueWork() }
            }

            if (uiState.qrcodeSearchedDevice != null) {
                findNavController().navigate(EngineerScanQRCodeFragmentDirections.actionEngineerScanQRCodeFragmentToEngineerFoundDeviceFragment())
            }
        }
    }

    private fun requestEnableGPS() {
        (requireActivity() as? BaseActivity)?.requestEnableGPS(object :
            BaseActivity.RequestEnableGPSCallback {
            override fun onRequestEnableGPSResult(enable: Boolean) {
                viewModel.continueWork()
            }
        })
    }

    private val bluetoothPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (it.values.all { grant -> grant }) {
                viewModel.continueWork()
            } else {
                showToast("需要藍牙權限才能掃描裝置")
                findNavController().navigateUp()
            }
        }

    private val enableBluetoothRequest =
        registerForActivityResult(RequireEnableBluetoothContract()) {
            if (it) {
                viewModel.continueWork()
            } else {
                showToast("需要開啟藍牙才能掃描裝置")
                findNavController().navigateUp()
            }
        }

    private val gpsPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                viewModel.continueWork()
            } else {
                showToast("需要定位權限才能掃描裝置")
                findNavController().navigateUp()
            }
        }
}