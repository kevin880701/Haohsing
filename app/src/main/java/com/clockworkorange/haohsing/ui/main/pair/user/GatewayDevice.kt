package com.clockworkorange.haohsing.ui.main.pair.user

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.text.TextUtils
import blufi.espressif.BlufiCallback
import blufi.espressif.BlufiClient
import com.google.gson.Gson
import kotlinx.coroutines.*
import org.json.JSONObject
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class GatewayDevice(private val deviceClient: BlufiClient) {


    private val gson by lazy { Gson() }

    private var isConnect = false

    private var deviceInfo: DeviceInfo? = null
    private var wifiList = mutableListOf<SimpleWiFiInfo>()
    private var provResponse: BLEGenericResponse? = null
    private var activeDeviceResponse: BLEGenericResponse? = null

    suspend fun connect() = withContext(Dispatchers.IO) {
        suspendCancellableCoroutine<Boolean> { continuation ->
            Timber.d("連線中... thread:${Thread.currentThread().name}")
            deviceClient.setBlufiCallback(blufiCallback)
            deviceClient.connect()

            runBlocking {
                repeat(15) {
                    Timber.d("檢查連線 第${it + 1}次 thread:${Thread.currentThread().name}")
                    if (isConnect) {
                        //多等兩秒再回傳，使藍牙準備好傳送資料
                        delay(2000)
                        Timber.d("連線成功")
                        continuation.resume(true)
                        return@runBlocking
                    }
                    delay(5_000)
                }
            }

            if (!isConnect) {
                Timber.d("連線失敗")
                continuation.resume(false)
            }
        }
    }

    suspend fun getDeviceInfo() = withContext(Dispatchers.IO) {
        suspendCancellableCoroutine<DeviceInfo> { continuation ->
            deviceInfo = null

            val cmd = """
            {"cmd":"info"}
        """.trimIndent()

            deviceClient.postCustomData(cmd.toByteArray())

            runBlocking {
                repeat(15) {
                    if (deviceInfo != null) {
                        continuation.resume(deviceInfo!!)
                        return@runBlocking
                    }
                    delay(5_000)
                }
            }
            if (deviceInfo == null) {
                continuation.resumeWithException(RuntimeException("無法取得裝置資訊"))
            }
        }
    }

    suspend fun scanWiFi() = withContext(Dispatchers.IO) {
        suspendCancellableCoroutine<List<SimpleWiFiInfo>> { continuation ->
            wifiList.clear()

            val cmd = """
            {"cmd":"scan"}
        """.trimIndent()

            deviceClient.postCustomData(cmd.toByteArray())

            runBlocking {
                repeat(15) {
                    if (wifiList.isNotEmpty()) {
                        continuation.resume(wifiList)
                        return@runBlocking
                    }
                    delay(5_000)
                }
            }

            if (wifiList.isEmpty()) {
                continuation.resumeWithException(RuntimeException("no wifi"))
            }
        }
    }

    suspend fun pairWiFi(ssid: String, psw: String) = withContext(Dispatchers.IO) {
        suspendCancellableCoroutine<Boolean> { continuation ->
            provResponse = null

            val cmd = """
                {"cmd":"prov", "ssid":"$ssid", "passwd":"$psw"}
            """.trimIndent()
            deviceClient.postCustomData(cmd.toByteArray())

            runBlocking {
                var checkIpCount = 0
                repeat(15) {
                    if (provResponse != null) {
                        val deviceInfo = getDeviceInfo()
                        if (deviceInfo.ip.isNotEmpty()) {
                            continuation.resume(true)
                            return@runBlocking
                        } else {
                            checkIpCount++
                            if (checkIpCount > 2) {
                                continuation.resume(false)
                                return@runBlocking
                            }
                        }
                    }
                    delay(5_000)
                }
            }

            if (provResponse == null) {
                continuation.resume(false)
            }
        }
    }

    suspend fun activeDevice(token: String): Boolean = withContext(Dispatchers.IO) {
        suspendCancellableCoroutine<Boolean> { continuation ->
            activeDeviceResponse = null

            val name = if (!TextUtils.isEmpty(deviceInfo?.id)) deviceInfo?.id else "gateway"

            val cmd = """
                {"cmd":"activeDevice", "name":"$name", "token":"$token", "homeid":""}
            """.trimIndent()

            deviceClient.postCustomData(cmd.toByteArray())

            runBlocking {
                repeat(15) {
                    if (activeDeviceResponse != null) {
                        continuation.resume(activeDeviceResponse!!.isSuccess())
                        return@runBlocking
                    }
                    delay(5_000)
                }
            }

            if (activeDeviceResponse == null) {
                continuation.resume(false)
            }

        }
    }

    fun getMAC(): String {
        return deviceInfo?.id ?: "--"
    }

    fun disconnect() {
        deviceClient.close()
    }

    private val blufiCallback = object : BlufiCallback() {
        override fun onGattPrepared(
            client: BlufiClient?,
            gatt: BluetoothGatt?,
            service: BluetoothGattService?,
            writeChar: BluetoothGattCharacteristic?,
            notifyChar: BluetoothGattCharacteristic?
        ) {
            super.onGattPrepared(client, gatt, service, writeChar, notifyChar)
            Timber.d("onGattPrepared thread:${Thread.currentThread().name}")
            isConnect = true
        }

        override fun onPostCustomDataResult(client: BlufiClient?, status: Int, data: ByteArray?) {
            super.onPostCustomDataResult(client, status, data)
            Timber.d("send cmd: %s, status: %d", String(data!!), status)
        }

        override fun onReceiveCustomData(client: BlufiClient?, status: Int, data: ByteArray?) {
            super.onReceiveCustomData(client, status, data)
            val receiveData = String(data!!)
            Timber.d("receive cmd %s, status: %d", receiveData, status)
            val json = JSONObject(receiveData)
            when (json.getString("cmd")) {
                "info" -> {
                    deviceInfo = gson.fromJson(receiveData, DeviceInfo::class.java)
                }
                "scan" -> {
                    val scanWiFiResponse =
                        gson.fromJson(receiveData, BLEScanWiFiResponse::class.java)
                    wifiList.clear()
                    wifiList.addAll(scanWiFiResponse.wifiList)
                }
                "prov" -> {
                    provResponse = gson.fromJson(receiveData, BLEGenericResponse::class.java)
                }
                "activeDevice" -> {
                    activeDeviceResponse =
                        gson.fromJson(receiveData, BLEGenericResponse::class.java)

                }
                "ble_close" -> {

                }
            }

        }
    }


}