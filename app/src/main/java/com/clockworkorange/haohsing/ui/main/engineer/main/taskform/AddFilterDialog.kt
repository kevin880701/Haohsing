package com.clockworkorange.haohsing.ui.main.engineer.main.taskform

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import com.clockworkorange.domain.usecase.task.ChangedFilter
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.databinding.FragmentAddFilterDialogBinding
import com.clockworkorange.haohsing.ui.dialog.SelectItemDialog
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class AddFilterDialog : BottomSheetDialogFragment() {

    companion object {
        private const val KEY_FilterType = "FilterType"
        fun newInstance(filterType: ChangedFilter.FilterType, listener: Listener) =
            AddFilterDialog().apply {
                arguments = Bundle().apply {
                    putSerializable(KEY_FilterType, filterType)
                }
                this.listener = listener
            }
    }

    fun interface Listener {
        fun onAddFilter(filter: ChangedFilter)
    }

    private var _binding: FragmentAddFilterDialogBinding? = null
    private val binding get() = _binding!!

    private val filterType: ChangedFilter.FilterType by lazy {
        requireArguments().getSerializable(
            KEY_FilterType
        ) as ChangedFilter.FilterType
    }

    private var lifeMonth: Int = 3

    private var listener: Listener? = null

    override fun getTheme(): Int {
        return R.style.CustomRoundBottomSheetDialogStyle
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.setOnShowListener {
            val bottomSheetDialog = dialog as BottomSheetDialog
            val view =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
            val bottomSheetBehavior = BottomSheetBehavior.from(view)

            val lp = view.layoutParams
            lp.height = WindowManager.LayoutParams.MATCH_PARENT
            view.layoutParams = lp

            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheetBehavior.peekHeight = view.height
            bottomSheetBehavior.skipCollapsed = true
        }
        _binding = FragmentAddFilterDialogBinding.inflate(layoutInflater)
        initView()
        initListener()
        return binding.root
    }

    private fun initView() {
        val defaultName = when (filterType) {
            ChangedFilter.FilterType.F1 -> "濾芯-1"
            ChangedFilter.FilterType.F2 -> "濾芯-2"
            ChangedFilter.FilterType.F3 -> "濾芯-3"
            ChangedFilter.FilterType.F4 -> "濾芯-Ro"
            ChangedFilter.FilterType.F5 -> "濾芯-5"
        }
        binding.tvFilterName.text = defaultName
        binding.tvInstallDate.text =
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日"))
    }

    private fun initListener() {

        binding.flFilterLife.setOnClickListener {
            val lifeOption = arrayOf(
                "1個月",
                "2個月",
                "3個月",
                "4個月",
                "5個月",
                "6個月",
                "7個月",
                "8個月",
                "9個月",
                "10個月",
                "11個月",
                "12個月",
            )

            SelectItemDialog.newInstance(
                "剩餘壽命",
                lifeOption,
                getString(R.string.confirm)
            ) {
                val index = lifeOption.indexOf(it)
                lifeMonth = index + 1
                binding.tvFilterLife.text = "%d個月".format(lifeMonth)
            }.show(childFragmentManager, "select_life")
        }

        binding.btSave.setOnClickListener {
            val filter = ChangedFilter(
                filterType,
                binding.tvFilterName.text.toString(),
                LocalDateTime.now(),
                lifeMonth
            )
            listener?.onAddFilter(filter)
            dismiss()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}