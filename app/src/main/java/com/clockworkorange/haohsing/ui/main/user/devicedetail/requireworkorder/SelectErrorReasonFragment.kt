package com.clockworkorange.haohsing.ui.main.user.devicedetail.requireworkorder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.clockworkorange.domain.usecase.task.FormOption
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.databinding.FragmentSelectErrorReasonBinding
import com.clockworkorange.haohsing.databinding.LayoutErrorReasonGroupBinding
import com.clockworkorange.haohsing.utils.ScreenUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SelectErrorReasonFragment : BottomSheetDialogFragment() {

    companion object {
        private const val KEY_ERROR_REASON = "ERROR_REASON"
        fun newInstance(errorReasons: List<FormOption>, callback: (HashSet<FormOption>) -> Unit) =
            SelectErrorReasonFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArray(KEY_ERROR_REASON, errorReasons.toTypedArray())
                }
                this.callback = callback
            }
    }

    private var _binding: FragmentSelectErrorReasonBinding? = null
    private val binding get() = _binding!!

    private val errorReasons by lazy { requireArguments().getParcelableArray(KEY_ERROR_REASON) as Array<FormOption> }

    private var callback: ((HashSet<FormOption>) -> Unit)? = null

    private var selectedErrorReason = hashSetOf<FormOption>()

    private var groupClickLog = hashSetOf<String>()

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

        _binding = FragmentSelectErrorReasonBinding.inflate(layoutInflater)
        initView()
        initListener()
        return binding.root
    }

    private val groupViews = HashMap<String, MutableList<TextView>>()

    private fun initView() {
        errorReasons.groupBy { it.group }
            .forEach { (group, item) ->
                groupViews[group] = mutableListOf()

                val errorGroup =
                    LayoutErrorReasonGroupBinding.inflate(LayoutInflater.from(requireContext()))
                errorGroup.tvGroupName.text = group
                item.forEach {
                    val textView = createTextView(it)
                    errorGroup.flGroupItem.addView(
                        textView,
                        ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                    )
                    groupViews[group]?.add(textView)
                }
                val lp = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                lp.setMargins(0, 0, 0, ScreenUtils.dp2px(requireContext(), 32))
                binding.llErrorReasonGroup.addView(errorGroup.root, lp)
            }
    }

    private fun createTextView(errorReason: FormOption): TextView {
        return TextView(requireContext()).apply {
            text = errorReason.name
            tag = errorReason
            setTextAppearance(R.style.TextAppearance_HAOHSING_Body1)
            val dp16 = ScreenUtils.dp2px(requireContext(), 16)
            val dp4 = ScreenUtils.dp2px(requireContext(), 4)
            setPadding(dp16, dp4, dp16, dp4)
            includeFontPadding = false
            setDefault()
            setOnClickListener { onTextViewClick(this) }
        }
    }

    private fun onTextViewClick(textView: TextView) {
        val errorReason = textView.tag as FormOption
        val isGroupFirstClick = !groupClickLog.contains(errorReason.group)
        if (isGroupFirstClick) {
            groupViews[errorReason.group]?.forEach { it.setUnSelect() }
            textView.setSelected()
            groupClickLog.add(errorReason.group)
        }

        if (selectedErrorReason.contains(errorReason)) {
            selectedErrorReason.remove(errorReason)
            textView.setUnSelect()
        } else {
            selectedErrorReason.add(errorReason)
            textView.setSelected()
        }

    }

    private fun initListener() {
        binding.btFinish.setOnClickListener {
            callback?.invoke(selectedErrorReason)
            dismiss()
        }
    }


    private fun TextView.setDefault() {
        setBackgroundResource(R.drawable.shape_r4_f1f1f1)
        setTextColor(ContextCompat.getColor(context, R.color.colorSecondary))
    }

    private fun TextView.setUnSelect() {
        setBackgroundResource(R.drawable.shape_r4_f1f1f1)
        setTextColor(ContextCompat.getColor(context, R.color.colorLightGray))
    }

    private fun TextView.setSelected() {
        setBackgroundResource(R.drawable.shape_r4_bad6fb)
        setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        groupViews.clear()
        _binding = null
    }
}