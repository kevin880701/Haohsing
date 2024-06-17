package com.clockworkorange.haohsing.ui.main.user.devicedetail.requireworkorder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.databinding.FragmentSelectWeekDayDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.*

class SelectWeekDayDialog : BottomSheetDialogFragment() {

    fun interface Listener {
        fun onWeekDaySelect(weekdays: List<Int>)
    }

    companion object {
        const val TAG = "SelectWeekDayDialog"
        private const val KEY_SELECTED_WEEKDAYS = "SELECTED_WEEKDAYS"
        fun newInstance(selectedWeekdays: IntArray = intArrayOf(), listener: Listener) =
            SelectWeekDayDialog().apply {
                arguments = Bundle().apply {
                    putIntArray(KEY_SELECTED_WEEKDAYS, selectedWeekdays)
                }
                this.listener = listener
            }
    }

    private var _binding: FragmentSelectWeekDayDialogBinding? = null
    private val binding get() = _binding!!

    private var listener: Listener? = null

    private val selectedWeekdays: IntArray by lazy {
        requireArguments().getIntArray(
            KEY_SELECTED_WEEKDAYS
        )!!
    }

    override fun getTheme(): Int {
        return R.style.CustomRoundBottomSheetDialogStyle
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectWeekDayDialogBinding.inflate(layoutInflater)
        initView()
        initListener()
        return binding.root
    }

    private fun initView() {
        selectedWeekdays.forEach { weekDay ->
            when (weekDay) {
                1 -> binding.tvSun.toggle()
                2 -> binding.tvMon.toggle()
                3 -> binding.tvTue.toggle()
                4 -> binding.tvWed.toggle()
                5 -> binding.tvThu.toggle()
                6 -> binding.tvFri.toggle()
                7 -> binding.tvSat.toggle()
            }
        }
    }

    private fun initListener() {
        binding.tvDone.setOnClickListener {
            listener?.onWeekDaySelect(getSelectedWeekday())
            dismiss()
        }

//        binding.tvSun.setOnClickListener { binding.tvSun.toggle() }
//        binding.tvMon.setOnClickListener { binding.tvMon.toggle() }
//        binding.tvTue.setOnClickListener { binding.tvTue.toggle() }
//        binding.tvWed.setOnClickListener { binding.tvWed.toggle() }
//        binding.tvThu.setOnClickListener { binding.tvThu.toggle() }
//        binding.tvFri.setOnClickListener { binding.tvFri.toggle() }
//        binding.tvSat.setOnClickListener { binding.tvSat.toggle() }
    }

    private fun getSelectedWeekday(): List<Int> {
        val selected = mutableListOf<Int>()

        if (binding.tvSun.isChecked) selected.add(Calendar.SUNDAY)
        if (binding.tvMon.isChecked) selected.add(Calendar.MONDAY)
        if (binding.tvTue.isChecked) selected.add(Calendar.TUESDAY)
        if (binding.tvWed.isChecked) selected.add(Calendar.WEDNESDAY)
        if (binding.tvThu.isChecked) selected.add(Calendar.THURSDAY)
        if (binding.tvFri.isChecked) selected.add(Calendar.FRIDAY)
        if (binding.tvSat.isChecked) selected.add(Calendar.SATURDAY)

        return selected
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}