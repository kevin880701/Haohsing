package com.clockworkorange.haohsing.ui.main.pair.user

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import blufi.espressif.BlufiClient
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class BluetoothController @Inject constructor(@ApplicationContext private val context: Context) {

    fun isNotEnable(): Boolean {
        return !BluetoothAdapter.getDefaultAdapter().isEnabled
    }

    fun isPermissionAllow(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.BLUETOOTH_SCAN
        ) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.BLUETOOTH_CONNECT
        ) == PackageManager.PERMISSION_GRANTED
    }

    //HAOHSING-IoT-XXXXXX，XXXXXX是指裝置MAC後六碼
    private var scanCallback: ScanCallback? = null

    suspend fun searchDevice(mac: String) = withTimeout(10_000) {
        val pairName = "HAOHSING-IoT-%s".format(mac.takeLast(6))
        Timber.d("搜尋名稱 $pairName")
        val bluetoothDevice = searchBLEDevice(pairName)
        return@withTimeout GatewayDevice(BlufiClient(context, bluetoothDevice))
    }

    private suspend fun searchBLEDevice(prefix: String) =
        suspendCancellableCoroutine<BluetoothDevice> {
            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            val bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner

            val scanSettings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build()

            var isFind = false

            scanCallback = object : ScanCallback() {
                override fun onScanResult(callbackType: Int, result: ScanResult?) {
                    val name = result?.device?.name
                    name?.let {
                        Timber.d("search device name: $name")
                    }
                    if (name?.startsWith(prefix) == true && !isFind) {
                        isFind = true
                        bluetoothLeScanner.stopScan(scanCallback)
                        it.resume(result.device)
                    }
                }

                override fun onScanFailed(errorCode: Int) {
                    super.onScanFailed(errorCode)
                    val msg: String = when (errorCode) {
                        SCAN_FAILED_ALREADY_STARTED -> "SCAN_FAILED_ALREADY_STARTED"
                        SCAN_FAILED_APPLICATION_REGISTRATION_FAILED -> "SCAN_FAILED_APPLICATION_REGISTRATION_FAILED"
                        SCAN_FAILED_INTERNAL_ERROR -> "SCAN_FAILED_INTERNAL_ERROR"
                        SCAN_FAILED_FEATURE_UNSUPPORTED -> "SCAN_FAILED_FEATURE_UNSUPPORTED"
                        else -> "unKnown"
                    }
                    it.resumeWithException(RuntimeException(msg))
                }
            }

            Timber.d("搜尋裝置中...")
            bluetoothLeScanner.startScan(null, scanSettings, scanCallback)
            it.invokeOnCancellation { bluetoothLeScanner.stopScan(scanCallback) }
        }

}