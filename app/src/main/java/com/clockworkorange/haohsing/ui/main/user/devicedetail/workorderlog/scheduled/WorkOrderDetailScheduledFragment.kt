package com.clockworkorange.haohsing.ui.main.user.devicedetail.workorderlog.scheduled

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.clockworkorange.domain.usecase.task.toDisplayString
import com.clockworkorange.domain.usecase.workorder.ScheduledWorkOrderDetail
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.databinding.FragmentWorkOrderDetailScheduledBinding
import com.clockworkorange.haohsing.ui.base.BaseFragment
import com.clockworkorange.haohsing.ui.main.user.MainActivity
import com.clockworkorange.haohsing.ui.widget.setTextOnly
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class WorkOrderDetailScheduledFragment : BaseFragment() {
    private var _binding: FragmentWorkOrderDetailScheduledBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WorkOrderDetailScheduledViewModel by viewModels()

    private val args by navArgs<WorkOrderDetailScheduledFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkOrderDetailScheduledBinding.inflate(layoutInflater)
        initView()
        initListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
        viewModel.setId(args.orderId)
    }

    private fun initView() {
        (requireActivity() as? MainActivity)?.setLightStatusBar()
        binding.ilToolbar.tvTitle.text = "已安排"
        binding.ilOrderId.setTextOnly("單號")
        binding.ilOrderId.tvInfo.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorPrimary
            )
        )
        binding.ilAppointedTime.setTextOnly("到府時間")
        binding.ilRequirement.setTextOnly("需求")
        binding.ilErrorReason.tvTitle.text = "故障狀況"
        binding.ilNote.tvTitle.text = "備註說明"

    }

    private fun initListener() {
        binding.ilToolbar.btBack.setOnClickListener { findNavController().navigateUp() }
    }

    private fun bindViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { observeUiState() }
                launch { observeNavAction() }
                launch { viewModel.toastFlow.collect { showToast(it) } }
            }
        }
    }

    private suspend fun observeUiState() {
        viewModel.uiState.collect { uiState ->
            uiState.scheduledWorkOrderDetail?.let { fillDetail(it) }
        }
    }

    private fun fillDetail(detail: ScheduledWorkOrderDetail) {
        with(binding) {
            ilOrderId.tvInfo.text = detail.orderId.toString()
            ilEngineerInfo.tvPlace.text = detail.engineerInfo.place
            ilEngineerInfo.tvEngineerName.text = detail.engineerInfo.name
            ilEngineerInfo.tvPhone.text = detail.engineerInfo.phone
            ilAppointedTime.tvInfo.text =
                detail.appointedDatetime.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日\nHH:mm"))
            ilRequirement.tvInfo.text = detail.requirement.displayString
            ilNote.tvContent.text = detail.note
            ilErrorReason.tvContent.text = detail.errorReasons.toDisplayString()
        }
    }

    private suspend fun observeNavAction() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        (requireActivity() as? MainActivity)?.setDarkStatusBar()
        _binding = null
    }
}