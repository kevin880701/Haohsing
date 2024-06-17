package com.clockworkorange.haohsing.ui.login

import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.clockworkorange.domain.data.remote.GlobalErrorHandler
import com.clockworkorange.domain.data.remote.model.ErrorResponse
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.ui.GlobalErrorHandlerImpl
import com.clockworkorange.haohsing.ui.dialog.MessageDialog
import com.clockworkorange.haohsing.utils.doOnApplyWindowInsets
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity(), GlobalErrorHandler {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val container = findViewById<FrameLayout>(R.id.container)
        ViewCompat.getWindowInsetsController(container)?.isAppearanceLightStatusBars = true
        WindowCompat.setDecorFitsSystemWindows(window, false)
        container.doOnApplyWindowInsets { v, insets, padding ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val bottomNavBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            v.updatePadding(
                top = padding.top + systemBars.top,
                bottom = padding.bottom + bottomNavBars.bottom
            )

        }

        GlobalErrorHandlerImpl.registerErrorHandler(this)

    }

    override fun onNetworkUnavailable() {

    }

    override fun onLoginSessionExpired() {

    }

    override fun onErrorMessage(msg: ErrorResponse) {
        runOnUiThread {
            MessageDialog.newInstance(
                MessageDialog.IconType.Alert,
                msg.msg,
                null,
                MessageDialog.ButtonStyle.FillRed,
                getString(R.string.confirm)
            ).show(supportFragmentManager, "error_dialog")
        }
    }
}