package com.clockworkorange.haohsing.ui.main.user.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.clockworkorange.domain.entity.ScheduleType
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.databinding.FragmentPowerScheduleBinding
import com.clockworkorange.haohsing.ui.base.BaseFragment
import com.clockworkorange.haohsing.ui.dialog.MessageDialog
import com.clockworkorange.haohsing.ui.dialog.MessageInputDialog
import com.clockworkorange.haohsing.ui.dialog.SelectItemDialog
import com.clockworkorange.haohsing.ui.main.user.MainActivity
import com.clockworkorange.haohsing.ui.main.user.devicedetail.requireworkorder.AddEdit
import com.clockworkorange.haohsing.ui.widget.setTextAndArrow
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PowerScheduleFragment : BaseFragment() {

    private var _binding: FragmentPowerScheduleBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: PowerScheduleViewModel

    private val args by navArgs<PowerScheduleFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPowerScheduleBinding.inflate(layoutInflater)
        initViewModel()
        initView()
        initListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
        viewModel.set(args.subjectId, args.addedit, args.scheduleId)
    }

    private fun initViewModel() {
        viewModel = when(args.scope) {
            ScheduleType.Device -> { ViewModelProvider(this).get(DevicePowerScheduleViewModel::class.java) }
            ScheduleType.Area -> { ViewModelProvider(this).get(AreaPowerScheduleViewModel::class.java) }
        }
    }

    private fun initView() {
        (requireActivity() as? MainActivity)?.setLightStatusBar()
        with(binding) {
            ilToolbar.tvTitle.text = when (args.addedit) {
                AddEdit.Add -> "新增開關機排程"
                AddEdit.Edit -> "編輯開關機排程"
            }
            ilName.setTextAndArrow("排程名稱")
            ilPowerOnTime.setTextAndArrow("開機時間")
            ilPowerOffTime.setTextAndArrow("睡眠時間")
            ilPreviewNotification.tvTitle.text = "通知內容預覽"
            ilPreviewNotification.tvContent.text = "設置好觸發條件設定，即可預覽通知。"
            btDelete.isVisible = args.addedit == AddEdit.Edit
        }
    }

    private fun initListener() {
        with(binding) {
            ilToolbar.btBack.setOnClickListener { findNavController().navigateUp() }
            ilName.root.setOnClickListener { showEditNameDialog() }
            ilPowerOnTime.root.setOnClickListener {
                showSelectTimeDialog {
                    viewModel.setPowerOnTime(
                        it
                    )
                }
            }
            ilPowerOffTime.root.setOnClickListener {
                showSelectTimeDialog {
                    viewModel.setPowerOffTime(
                        it
                    )
                }
            }

            btCancel.setOnClickListener { findNavController().navigateUp() }
            btDelete.setOnClickListener { showConfirmDeleteDialog() }
            btSave.setOnClickListener { viewModel.saveSchedule() }

            tvMon.setOnClickListener { viewModel.toggleWeekDay(1) }
            tvTue.setOnClickListener { viewModel.toggleWeekDay(2) }
            tvWed.setOnClickListener { viewModel.toggleWeekDay(3) }
            tvThu.setOnClickListener { viewModel.toggleWeekDay(4) }
            tvFri.setOnClickListener { viewModel.toggleWeekDay(5) }
            tvSat.setOnClickListener { viewModel.toggleWeekDay(6) }
            tvSun.setOnClickListener { viewModel.toggleWeekDay(7) }

        }
    }

    private fun showConfirmDeleteDialog() {
        MessageDialog.newInstance(
            MessageDialog.IconType.Alert,
            "確定刪除此排程",
            button1Title = getString(R.string.cancel),
            button1Style = MessageDialog.ButtonStyle.Outline,
            button2Title = getString(R.string.delete),
            button2Style = MessageDialog.ButtonStyle.FillRed,
            button2Listener = { viewModel.deleteSchedule() }
        ).show(childFragmentManager, "delete_schedule")
    }

    private fun showSelectTimeDialog(callback: (String) -> Unit) {
        val timeOption = buildList {
            for (i in 0..23) add("%02d:00".format(i))
        }.toTypedArray()

        SelectItemDialog.newInstance(
            "時間",
            timeOption,
            getString(R.string.finished)
        ) {
            callback.invoke(it)
        }.show(childFragmentManager, "select_time")
    }

    private fun showEditNameDialog() {
        MessageInputDialog.newInstance("名稱") { name ->
            viewModel.setName(name)
        }.show(childFragmentManager, "name")
    }

    private fun bindViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { observeUiState() }
                launch { viewModel.toastFlow.collect { showToast(it) } }
            }
        }
    }

    private suspend fun observeUiState() {
        viewModel.uiState.collect { uiState ->
            with(binding) {
                ilName.tvInfo.text = uiState.name
                ilPowerOnTime.tvInfo.text = uiState.powerOnTime ?: "--:--"
                ilPowerOffTime.tvInfo.text = uiState.powerOffTime ?: "--:--"
                ilLoading.root.isVisible = uiState.isLoading

                resetWeekDay()
                uiState.selectedWeekDay.forEach { weekDay ->
                    when (weekDay) {
                        1 -> binding.tvMon.isChecked = true
                        2 -> binding.tvTue.isChecked = true
                        3 -> binding.tvWed.isChecked = true
                        4 -> binding.tvThu.isChecked = true
                        5 -> binding.tvFri.isChecked = true
                        6 -> binding.tvSat.isChecked = true
                        7 -> binding.tvSun.isChecked = true
                    }
                }

                ilPreviewNotification.tvContent.text =
                    uiState.previewNotificationContent ?: "設置好觸發條件設定，即可預覽通知。"
                btSave.isEnabled = uiState.previewNotificationContent != null

                if (uiState.eventPop) {
                    findNavController().navigateUp()
                }
            }

        }
    }

    private fun resetWeekDay() {
        with(binding) {
            tvMon.isChecked = false
            tvTue.isChecked = false
            tvWed.isChecked = false
            tvThu.isChecked = false
            tvFri.isChecked = false
            tvSat.isChecked = false
            tvSun.isChecked = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (requireActivity() as? MainActivity)?.setDarkStatusBar()
        _binding = null
    }
}