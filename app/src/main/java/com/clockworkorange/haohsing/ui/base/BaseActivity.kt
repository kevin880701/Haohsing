package com.clockworkorange.haohsing.ui.base

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.clockworkorange.domain.data.remote.GlobalErrorHandler
import com.clockworkorange.domain.data.remote.model.ErrorResponse
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.ui.dialog.MessageDialog
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import timber.log.Timber

open class BaseActivity : AppCompatActivity(), GlobalErrorHandler {

    override fun onNetworkUnavailable() {
        runOnUiThread {
            Toast.makeText(this, "無網路連線", Toast.LENGTH_SHORT).show()
        }
        Timber.e("onNetworkUnavailable")
    }

    override fun onLoginSessionExpired() {
        runOnUiThread {
            Toast.makeText(this, "登入過期", Toast.LENGTH_SHORT).show()
        }
        Timber.e("onLoginSessionExpired")
    }

    override fun onErrorMessage(msg: ErrorResponse) {
        runOnUiThread {
            MessageDialog.newInstance(
                MessageDialog.IconType.Alert,
                msg.msg,
                msg.errorMsg,
                MessageDialog.ButtonStyle.FillRed,
                getString(R.string.confirm)
            ).show(supportFragmentManager, "error_dialog")
        }
    }

    interface RequestEnableGPSCallback {
        fun onRequestEnableGPSResult(enable: Boolean)
    }

    private var callback: RequestEnableGPSCallback? = null

    fun requestEnableGPS(callback: RequestEnableGPSCallback) {
        this.callback = callback
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(LocationRequest.create())
            .setNeedBle(true)

        val task = LocationServices.getSettingsClient(this)
            .checkLocationSettings(builder.build())

        task.addOnCompleteListener {
            try {
                val response = task.getResult(ApiException::class.java)
                Timber.d("all request are satisfied")
                callback.onRequestEnableGPSResult(true)
            } catch (e: ApiException) {
                when (e.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        Timber.d("RESOLUTION_REQUIRED")
                        try {
                            val resolvable = e as ResolvableApiException
                            resolvable.startResolutionForResult(this, 1001)
                        } catch (e: IntentSender.SendIntentException) {
                            //ignore
                        } catch (e: ClassCastException) {
                            //ignore
                        }
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        Timber.d("SETTINGS_CHANGE_UNAVAILABLE")
                        callback.onRequestEnableGPSResult(false)
                    }
                }

            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    Timber.d("RESULT_OK")
                    callback?.onRequestEnableGPSResult(true)
                }

                Activity.RESULT_CANCELED -> {
                    Timber.d("RESULT_CANCELED")
                    callback?.onRequestEnableGPSResult(false)

                }
            }
        }
    }

}