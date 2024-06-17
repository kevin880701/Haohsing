package com.clockworkorange.haohsing.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.databinding.FragmentMessageDialogBinding
import com.clockworkorange.haohsing.utils.ScreenUtils
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel

class MessageDialog : DialogFragment() {

    companion object {
        private const val KEY_ICON_TYPE = "ICON_TYPE"
        private const val KEY_TITLE = "TITLE"
        private const val KEY_MESSAGE = "MESSAGE"

        private const val KEY_BUTTON1_STYLE = "BUTTON1_STYLE"
        private const val KEY_BUTTON1_TITLE = "BUTTON1_TITLE"

        private const val KEY_BUTTON2_STYLE = "BUTTON2_STYLE"
        private const val KEY_BUTTON2_TITLE = "BUTTON2_TITLE"

        @JvmStatic
        fun newInstance(
            iconType: IconType,
            title: String,
            message: String? = null,
            button1Style: ButtonStyle,
            button1Title: String,
            button1Listener: (() -> Unit)? = null,
            button2Style: ButtonStyle? = null,
            button2Title: String? = null,
            button2Listener: (() -> Unit)? = null
        ) = MessageDialog().apply {
            arguments = Bundle().apply {
                putSerializable(KEY_ICON_TYPE, iconType)
                putString(KEY_TITLE, title)
                putString(KEY_MESSAGE, message)
                putSerializable(KEY_BUTTON1_STYLE, button1Style)
                putString(KEY_BUTTON1_TITLE, button1Title)
                putSerializable(KEY_BUTTON2_STYLE, button2Style)
                putString(KEY_BUTTON2_TITLE, button2Title)
            }
            this.button1Listener = button1Listener
            this.button2Listener = button2Listener
        }
    }

    enum class IconType { Info, Alert, Check, Wait }

    enum class ButtonStyle { Outline, Fill, FillRed }

    private val iconType: IconType by lazy { requireArguments().getSerializable(KEY_ICON_TYPE) as IconType }
    private val title: String by lazy { requireArguments().getString(KEY_TITLE)!! }
    private val message: String? by lazy { requireArguments().getString(KEY_MESSAGE) }

    private val button1Style: ButtonStyle by lazy {
        requireArguments().getSerializable(
            KEY_BUTTON1_STYLE
        ) as ButtonStyle
    }
    private val button1Title: String by lazy { requireArguments().getString(KEY_BUTTON1_TITLE)!! }
    private var button1Listener: (() -> Unit)? = null

    private val button2Style: ButtonStyle? by lazy {
        requireArguments().getSerializable(
            KEY_BUTTON2_STYLE
        ) as? ButtonStyle
    }
    private val button2Title: String? by lazy { requireArguments().getString(KEY_BUTTON2_TITLE) }
    private var button2Listener: (() -> Unit)? = null

    private var _binding: FragmentMessageDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessageDialogBinding.inflate(layoutInflater)
        initView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun initView() {
        val iconRes = when (iconType) {
            IconType.Info -> R.drawable.ic_info
            IconType.Alert -> R.drawable.ic_alet
            IconType.Check -> R.drawable.ic_check
            IconType.Wait -> R.drawable.ic_wait
        }
        binding.ivIcon.setImageResource(iconRes)

        binding.tvTitle.text = title

        message?.let {
            binding.tvMessage.text = it
            binding.tvMessage.isVisible = true
        } ?: kotlin.run {
            binding.tvMessage.isVisible = false
        }

        setupButton(binding.bt1, button1Style, button1Title, button1Listener)
        if (hadSetButton2()) {
            setupButton(binding.bt2, button2Style!!, button2Title!!, button2Listener)
        } else {
            binding.viewButtonSpace.isVisible = false
            binding.bt2.isVisible = false
        }

    }

    private fun hadSetButton2(): Boolean {
        return button2Style != null && button2Title != null
    }

    private fun setupButton(
        button: Button,
        style: ButtonStyle,
        title: String,
        listener: (() -> Unit)?
    ) {
        when (style) {
            ButtonStyle.Outline -> setOutlineStyle(button)
            ButtonStyle.Fill -> setFillStyle(button)
            ButtonStyle.FillRed -> setFillRedStyle(button)
        }

        button.text = title
        button.setOnClickListener {
            listener?.invoke()
            dismiss()
        }
    }

    private fun setOutlineStyle(button: Button) {
        button.apply {
            val shapeModel = ShapeAppearanceModel()
                .toBuilder()
                .setAllCorners(
                    CornerFamily.ROUNDED,
                    ScreenUtils.dp2px(button.context, 12).toFloat()
                )
                .build()

            val shapeDrawable = MaterialShapeDrawable(shapeModel).apply {
                fillColor = ContextCompat.getColorStateList(button.context, R.color.transparent)
                setStroke(
                    ScreenUtils.dp2px(button.context, 1).toFloat(),
                    ContextCompat.getColor(button.context, R.color.colorPrimary)
                )
            }
            setTextColor(ContextCompat.getColor(button.context, R.color.colorPrimary))

            ViewCompat.setBackground(button, shapeDrawable)
        }
    }

    private fun setFillStyle(button: Button) {
        button.apply {

            val shapeModel = ShapeAppearanceModel()
                .toBuilder()
                .setAllCorners(
                    CornerFamily.ROUNDED,
                    ScreenUtils.dp2px(button.context, 12).toFloat()
                )
                .build()

            val shapeDrawable = MaterialShapeDrawable(shapeModel).apply {
                fillColor = ContextCompat.getColorStateList(button.context, R.color.colorPrimary)
            }
            setTextColor(ContextCompat.getColor(button.context, R.color.white))

            ViewCompat.setBackground(button, shapeDrawable)
        }
    }

    private fun setFillRedStyle(button: Button) {
        button.apply {

            val shapeModel = ShapeAppearanceModel()
                .toBuilder()
                .setAllCorners(
                    CornerFamily.ROUNDED,
                    ScreenUtils.dp2px(button.context, 12).toFloat()
                )
                .build()

            val shapeDrawable = MaterialShapeDrawable(shapeModel).apply {
                fillColor = ContextCompat.getColorStateList(button.context, R.color.colorRed)
            }
            setTextColor(ContextCompat.getColor(button.context, R.color.white))

            ViewCompat.setBackground(button, shapeDrawable)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}