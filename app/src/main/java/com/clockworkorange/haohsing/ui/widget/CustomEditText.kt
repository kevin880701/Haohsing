package com.clockworkorange.haohsing.ui.widget

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.databinding.LayoutCustomEdittextBinding
import kotlinx.parcelize.Parcelize

class CustomEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleRes) {

    enum class InputType {
        Text, Email, Password
    }

    enum class EndButtonType {
        ClearText, DisplayPassword
    }

    private var _binding: LayoutCustomEdittextBinding? = null
    private val binding get() = _binding!!

    private var isPasswordShowing = false

    val isErrorShowing: Boolean
        get() = _binding?.tvErrorNote?.isVisible ?: false

    init {
        _binding = LayoutCustomEdittextBinding.inflate(LayoutInflater.from(context))
        addView(binding.root)

        if (attrs != null) {
            val attributes = context.obtainStyledAttributes(attrs, R.styleable.CustomEditText)

            val hint = attributes.getString(R.styleable.CustomEditText_hint)
            binding.etInput.hint = hint

            val headImg = attributes.getDrawable(R.styleable.CustomEditText_headIcon)
            if (headImg != null) {
                binding.ivHead.isVisible = true
                binding.ivHead.setImageDrawable(headImg)
            } else {
                binding.ivHead.isVisible = false
            }

            when (attributes.getString(R.styleable.CustomEditText_inputType)) {
                "0" -> setInputType(InputType.Text)
                "1" -> setInputType(InputType.Email)
                "2" -> setInputType(InputType.Password)
            }

            when (attributes.getString(R.styleable.CustomEditText_endButtonType)) {
                "0" -> setEndButtonType(EndButtonType.ClearText)
                "1" -> setEndButtonType(EndButtonType.DisplayPassword)
                else -> binding.btEnd.isVisible = false
            }

            attributes.recycle()
        }

    }

    fun setInputType(type: InputType) {
        when (type) {
            InputType.Text -> {
                binding.etInput.inputType = android.text.InputType.TYPE_CLASS_TEXT
                binding.btEnd.isVisible = false
            }
            InputType.Email -> {
                binding.etInput.inputType = android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                binding.btEnd.isVisible = false
            }
            InputType.Password -> {
                binding.etInput.inputType =
                    android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.btEnd.isVisible = true
            }
        }
    }

    private fun setEndButtonType(type: EndButtonType) {
        binding.btEnd.isVisible = true
        when (type) {
            EndButtonType.ClearText -> {
                binding.btEnd.setImageResource(R.drawable.ic_x)
                binding.btEnd.setOnClickListener {
                    binding.etInput.setText("")
                }
            }
            EndButtonType.DisplayPassword -> {
                binding.btEnd.setImageResource(R.drawable.ic_eye_close)
                isPasswordShowing = false
                binding.btEnd.setOnClickListener {
                    isPasswordShowing = !isPasswordShowing
                    binding.btEnd.setImageResource(if (isPasswordShowing) R.drawable.ic_eye_open else R.drawable.ic_eye_close)
                    binding.etInput.inputType = if (isPasswordShowing) {
                        android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    } else {
                        android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                    }
                }
            }
        }
    }

    fun getInputText(): String {
        return binding.etInput.editableText.toString()
    }

    fun setError(note: String) {
        binding.tvErrorNote.isVisible = true
        binding.tvErrorNote.text = note
        binding.flEdittext.setBackgroundResource(R.drawable.bg_custom_edittext_error)
    }

    fun clearError() {
        binding.tvErrorNote.isInvisible = true
        binding.flEdittext.setBackgroundResource(R.drawable.bg_custom_edittext)
    }

    fun setChecked() {
        binding.btEnd.isVisible = true
        binding.btEnd.setImageResource(R.drawable.ic_check)
        binding.btEnd.setOnClickListener {
            //ignore
        }
    }

    fun clearCheck() {
        binding.btEnd.isVisible = false
    }

    fun setTextWatcher(watcher: (String) -> Unit) {
        binding.etInput.addTextChangedListener {
            it ?: return@addTextChangedListener
            watcher.invoke(it.toString())
        }
    }

    //https://stackoverflow.com/questions/57310783/is-there-an-elegant-way-to-save-and-restore-view-state-in-kotlin
    override fun onSaveInstanceState(): Parcelable {
        return CustomEditTextState(
            super.onSaveInstanceState(),
            binding.etInput.editableText.toString()
        )
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state !is CustomEditTextState) {
            super.onRestoreInstanceState(state)
            return
        }
        super.onRestoreInstanceState(state.superState)
        binding.etInput.setText(state.text)
    }

    @Parcelize
    internal class CustomEditTextState(val state: Parcelable?, val text: String) :
        View.BaseSavedState(state)
}