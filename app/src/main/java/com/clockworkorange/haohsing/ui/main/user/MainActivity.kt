package com.clockworkorange.haohsing.ui.main.user

import android.graphics.Color
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import com.clockworkorange.domain.usecase.user.UpLoadFcmTokenUseCase
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.databinding.ActivityMainBinding
import com.clockworkorange.haohsing.ui.GlobalErrorHandlerImpl
import com.clockworkorange.haohsing.ui.base.BaseActivity
import com.clockworkorange.haohsing.utils.doOnApplyWindowInsets
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var upLoadFcmTokenUseCase: UpLoadFcmTokenUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding.root.doOnApplyWindowInsets { v, insets, padding ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val bottomNavBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars())

            val paddingBottom = padding.bottom + bottomNavBars.bottom
            v.updatePadding(
                top = padding.top + systemBars.top,
                bottom = paddingBottom
            )
        }
        ViewCompat.getWindowInsetsController(binding.root)?.isAppearanceLightStatusBars = false
        GlobalErrorHandlerImpl.registerErrorHandler(this)
        uploadFcmToken()
    }

    fun setLightStatusBar() {
        binding.root.setBackgroundColor(Color.WHITE)
        ViewCompat.getWindowInsetsController(binding.root)?.isAppearanceLightStatusBars = true
    }

    fun setDarkStatusBar() {
        binding.root.setBackgroundColor(ContextCompat.getColor(this, R.color.colorSecondary))
        ViewCompat.getWindowInsetsController(binding.root)?.isAppearanceLightStatusBars = false
    }


    private fun uploadFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                lifecycleScope.launch {
                    upLoadFcmTokenUseCase.invoke(token)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        GlobalErrorHandlerImpl.registerErrorHandler(null)
    }
}