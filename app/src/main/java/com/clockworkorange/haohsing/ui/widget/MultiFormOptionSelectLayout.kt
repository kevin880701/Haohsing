package com.clockworkorange.haohsing.ui.widget

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.ui.main.engineer.main.taskform.TaskFormViewModel
import com.clockworkorange.haohsing.utils.ScreenUtils
import com.google.android.flexbox.AlignContent
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import kotlinx.parcelize.Parcelize

class MultiFormOptionSelectLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FlexboxLayout(context, attrs, defStyleAttr) {

    private var isFirstSelect = true
    private val itemViews = mutableListOf<TextView>()

    fun interface Listener {
        fun onFormOptionSelect(option: TaskFormViewModel.UiFormOption)
    }

    private var listener: Listener? = null

    init {
        alignContent = AlignContent.STRETCH
        alignItems = AlignItems.STRETCH
        flexWrap = FlexWrap.WRAP
        setDividerDrawable(ContextCompat.getDrawable(context, R.drawable.shape_flexbox_divider))
        showDividerHorizontal = SHOW_DIVIDER_MIDDLE
        showDividerVertical = SHOW_DIVIDER_MIDDLE
    }

    fun setItems(list: List<TaskFormViewModel.UiFormOption>) {
        removeAllViews()
        itemViews.clear()
        list.forEach { item ->
            createTextView(item).let {
                if (isFirstSelect && list.none { it.isSelect }) {
                    it.setDefault()
                } else if (item.isSelect) {
                    it.setSelected()
                } else {
                    it.setUnSelect()
                }
                itemViews.add(it)
                addView(
                    it,
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                )
            }
        }
    }

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    private fun createTextView(item: TaskFormViewModel.UiFormOption): TextView {
        return TextView(context).apply {
            text = item.option.displayName
            tag = item
            setTextAppearance(R.style.TextAppearance_HAOHSING_Body1)
            val dp16 = ScreenUtils.dp2px(context, 16)
            val dp4 = ScreenUtils.dp2px(context, 4)
            setPadding(dp16, dp4, dp16, dp4)
            includeFontPadding = false
            setOnClickListener { onTextViewClick(this) }
        }
    }

    private fun onTextViewClick(textView: TextView) {
        val item = textView.tag as TaskFormViewModel.UiFormOption
        isFirstSelect = false
        listener?.onFormOptionSelect(item)
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

    override fun onSaveInstanceState(): Parcelable {
        return MultiFormOptionSelectLayoutStatus(super.onSaveInstanceState(), isFirstSelect)
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state !is MultiFormOptionSelectLayoutStatus) {
            super.onRestoreInstanceState(state)
            return
        }
        super.onRestoreInstanceState(state)
        this.isFirstSelect = state.isFirstSelect
    }

    @Parcelize
    data class MultiFormOptionSelectLayoutStatus(
        val state: Parcelable?,
        val isFirstSelect: Boolean
    ) : View.BaseSavedState(state)
}