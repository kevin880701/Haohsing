package com.clockworkorange.haohsing.ui.main.engineer

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.databinding.FragmentEngineerMainNavBinding
import com.clockworkorange.haohsing.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EngineerMainNavFragment : BaseFragment(), SelectEngineerTabReceiver.Listener {

    private var _binding: FragmentEngineerMainNavBinding? = null
    val binding get() = _binding!!

    enum class Tab {
        Home, Task, Install, Notification, Account
    }

    private val viewModel: EngineerMainNavViewModel by viewModels()

    private val receiver = SelectEngineerTabReceiver(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LocalBroadcastManager.getInstance(requireContext().applicationContext)
            .registerReceiver(receiver, SelectEngineerTabReceiver.createIntentFilter())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEngineerMainNavBinding.inflate(layoutInflater)
        initView()
        initListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
    }

    private fun initView() {
        binding.vpContainer.adapter = EngineerMainPageAdapter(requireActivity())
        binding.vpContainer.isUserInputEnabled = false
    }

    private fun initListener() {
        binding.flHome.setOnClickListener { viewModel.selectTab(Tab.Home) }

        binding.flTask.setOnClickListener { viewModel.selectTab(Tab.Task) }

        binding.flInstall.setOnClickListener { viewModel.selectTab(Tab.Install) }

        binding.flNotification.setOnClickListener { viewModel.selectTab(Tab.Notification) }

        binding.flAccount.setOnClickListener { viewModel.selectTab(Tab.Account) }
    }

    private fun selectTab(selectedTab: Tab) {
        when (selectedTab) {
            Tab.Home -> {
                binding.vpContainer.setCurrentItem(0, false)
                resetSelectStatus()
                val color = ContextCompat.getColor(requireContext(), R.color.colorSecondary)
                binding.ivMain.setImageResource(R.drawable.ic_home_selected)
                binding.tvMain.setTextColor(color)
            }
            Tab.Task -> {
                binding.vpContainer.setCurrentItem(1, false)
                resetSelectStatus()
                val color = ContextCompat.getColor(requireContext(), R.color.colorSecondary)
                binding.ivTask.setImageResource(R.drawable.ic_devicelist_selected)
                binding.tvTask.setTextColor(color)
            }
            Tab.Install -> {
                binding.vpContainer.setCurrentItem(2, false)
                resetSelectStatus()
                val color = ContextCompat.getColor(requireContext(), R.color.colorSecondary)
                binding.ivInstall.setImageResource(R.drawable.ic_install_list_select)
                binding.tvInstall.setTextColor(color)
            }
            Tab.Notification -> {
                binding.vpContainer.setCurrentItem(3, false)
                resetSelectStatus()
                val color = ContextCompat.getColor(requireContext(), R.color.colorSecondary)
                binding.ivNotification.setImageResource(R.drawable.ic_bell_selected)
                binding.tvNotification.setTextColor(color)
            }
            Tab.Account -> {
                binding.vpContainer.setCurrentItem(4, false)
                resetSelectStatus()
                val color = ContextCompat.getColor(requireContext(), R.color.colorSecondary)
                binding.ivAccount.setImageResource(R.drawable.ic_user_selected)
                binding.tvAccount.setTextColor(color)
            }
        }
    }

    private fun resetSelectStatus() {
        binding.ivMain.setImageResource(R.drawable.ic_home)
        binding.ivTask.setImageResource(R.drawable.ic_devicelist)
        binding.ivInstall.setImageResource(R.drawable.ic_install_list)
        binding.ivNotification.setImageResource(R.drawable.ic_bell)
        binding.ivAccount.setImageResource(R.drawable.ic_user)

        val color = Color.parseColor("#ACACAC")

        binding.tvMain.setTextColor(color)
        binding.tvTask.setTextColor(color)
        binding.tvInstall.setTextColor(color)
        binding.tvNotification.setTextColor(color)
        binding.tvAccount.setTextColor(color)
    }

    private fun bindViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    selectTab(uiState.tab)
                }
            }
        }
    }

    override fun onSelectTab(tab: Tab?, intent: Intent?) {
        lifecycleScope.launch {
            tab ?: return@launch
            viewModel.selectTab(tab)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(requireContext().applicationContext)
            .unregisterReceiver(receiver)
    }

}