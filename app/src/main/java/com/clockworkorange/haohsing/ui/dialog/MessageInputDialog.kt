package com.clockworkorange.haohsing.ui.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.isVisible
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.utils.hideKeyboard
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MessageInputDialog : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "ChangeNameDialog"
        private const val KEY_TITLE = "KEY_TITLE"
        private const val KEY_DEFAULT_TEXT = "KEY_DEFAULT_TEXT"
        private const val KEY_HINT = "KEY_HINT"
        private const val KEY_INPUT_TYPE = "KEY_INPUT_TYPE"
        private const val KEY_BUTTON_TITLE = "KEY_BUTTON_TITLE"
        private const val KEY_SHOW_CLEAR = "KEY_SHOW_CLEAR"

        fun newInstance(
            title: String,
            defaultText: String? = null,
            hint: String? = null,
            inputType: Int = EditorInfo.TYPE_CLASS_TEXT,
            buttonTitle: String? = null,
            showClearButton: Boolean = false,
            listener: Listener? = null
        ) = MessageInputDialog().apply {
            this.listener = listener
            arguments = Bundle().apply {
                putString(KEY_TITLE, title)
                putString(KEY_DEFAULT_TEXT, defaultText)
                putString(KEY_HINT, hint)
                putInt(KEY_INPUT_TYPE, inputType)
                putString(KEY_BUTTON_TITLE, buttonTitle)
                putBoolean(KEY_SHOW_CLEAR, showClearButton)
            }
        }

        fun createSearchDialog(context: Context, listener: Listener): MessageInputDialog {
            return newInstance(
                "搜尋",
                hint = "輸入",
                buttonTitle = context.getString(R.string.confirm),
                listener = listener
            )
        }
    }

    fun interface Listener {
        fun onMessageInput(text: String)
    }

    private val title by lazy { requireArguments().getString(KEY_TITLE)!! }
    private val defaultText: String? by lazy { requireArguments().getString(KEY_DEFAULT_TEXT) }
    private val hint: String? by lazy { requireArguments().getString(KEY_HINT) }
    private val inputType: Int by lazy { requireArguments().getInt(KEY_INPUT_TYPE) }
    private val buttonTitle: String? by lazy { requireArguments().getString(KEY_BUTTON_TITLE) }
    private val showClearButton: Boolean by lazy { requireArguments().getBoolean(KEY_SHOW_CLEAR) }
    private var listener: Listener? = null

    override fun getTheme(): Int {
        return R.style.CustomRoundBottomSheetDialogStyle
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_input_message_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireView().findViewById<TextView>(R.id.tv_name).text = title

        val etInput = requireView().findViewById<EditText>(R.id.et_input)
        val btSave = requireView().findViewById<Button>(R.id.bt_save)
        val btClear = requireView().findViewById<ImageButton>(R.id.bt_clear)
        val btClose = requireView().findViewById<ImageButton>(R.id.bt_close)

        hint?.let { etInput.hint = it }

        defaultText?.let { etInput.setText(it) }

        etInput.inputType = inputType

        buttonTitle?.let { btSave.text = it }

        btClear.isVisible = showClearButton

        btClear.setOnClickListener {
            etInput.setText("")
        }

        btSave.setOnClickListener {
            val input = etInput.editableText.toString()
            listener?.onMessageInput(input)
            etInput.hideKeyboard()
            dismiss()
        }

        btClose.setOnClickListener {
            etInput.hideKeyboard()
            dismiss()
        }
    }

}