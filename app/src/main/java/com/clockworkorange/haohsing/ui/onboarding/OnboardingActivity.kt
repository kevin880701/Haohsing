package com.clockworkorange.haohsing.ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.databinding.ActivityOnboardingBinding
import com.clockworkorange.haohsing.ui.login.LoginActivity
import com.clockworkorange.haohsing.utils.doOnApplyWindowInsets
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding

    private var currentPage: Int = 0

    private val viewModel: OnboardingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        initListener()
        bindViewModel()
    }

    private fun initView() {
        ViewCompat.getWindowInsetsController(binding.root)?.isAppearanceLightStatusBars = true
        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding.root.doOnApplyWindowInsets { v, insets, padding ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = padding.top + systemBars.top)
        }

        binding.vpOnboarding.adapter = OnBoardingPageAdapter(this)
        binding.vpOnboarding.isUserInputEnabled = false

    }

    private fun initListener() {
        binding.btNextLogin.setOnClickListener {
            if (currentPage == 2) {
                viewModel.registerLoginButtonClick()
            } else {
                setPage(++currentPage)
            }
        }
    }

    private fun bindViewModel() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.navToLogin.collect {
                    startActivity(Intent(this@OnboardingActivity, LoginActivity::class.java))
                    finish()
                }
            }
        }
    }

    private fun setPage(currentPage: Int) {
        binding.vpOnboarding.currentItem = currentPage
        binding.btNextLogin.text = if (currentPage < 2) "下一步" else "註冊/登入"
        setDot(currentPage)
    }

    private fun setDot(currentPage: Int) {
        binding.indicator1.setBackgroundResource(R.drawable.dot_unselect)
        binding.indicator2.setBackgroundResource(R.drawable.dot_unselect)
        binding.indicator3.setBackgroundResource(R.drawable.dot_unselect)
        when (currentPage) {
            0 -> binding.indicator1.setBackgroundResource(R.drawable.dot_select)
            1 -> binding.indicator2.setBackgroundResource(R.drawable.dot_select)
            2 -> binding.indicator3.setBackgroundResource(R.drawable.dot_select)
        }
    }
}