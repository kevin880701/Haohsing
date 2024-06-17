package com.clockworkorange.haohsing.ui.launcher

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.clockworkorange.haohsing.ui.login.LoginActivity
import com.clockworkorange.haohsing.ui.main.engineer.EngineerMainActivity
import com.clockworkorange.haohsing.ui.main.user.MainActivity
import com.clockworkorange.haohsing.ui.onboarding.OnboardingActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LauncherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: LaunchViewModel by viewModels()

        lifecycleScope.launch {
            delay(200)
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.navAction.collect { action ->
                    when (action) {
                        is LaunchNavAction.NavToOnboarding -> {
                            startActivity(
                                Intent(
                                    this@LauncherActivity,
                                    OnboardingActivity::class.java
                                )
                            ).also { finish() }
                        }
                        is LaunchNavAction.NavToLogin -> {
                            startActivity(
                                Intent(
                                    this@LauncherActivity,
                                    LoginActivity::class.java
                                )
                            ).also { finish() }
                        }
                        is LaunchNavAction.NavToUserMain -> {
                            startActivity(
                                Intent(
                                    this@LauncherActivity,
                                    MainActivity::class.java
                                )
                            ).also { finish() }
                        }
                        is LaunchNavAction.NavToEngineerMain -> {
                            startActivity(
                                Intent(
                                    this@LauncherActivity,
                                    EngineerMainActivity::class.java
                                )
                            ).also { finish() }
                        }
                    }
                }
            }
        }

    }
}