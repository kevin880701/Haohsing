package com.clockworkorange.haohsing.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.databinding.FragmentSelectItemDialogBinding
import com.clockworkorange.haohsing.databinding.ItemSelectTextBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SelectItemDialog : BottomSheetDialogFragment() {

    companion object {
        private const val KEY_TITLE = "TITLE"
        private const val KEY_ITEM = "ITEM"
        private const val KEY_BUTTON_TITLE = "BUTTON_TITLE"

        @JvmStatic
        fun newInstance(
            title: String,
            items: Array<String>,
            buttonTitle: String,
            callback: (String) -> Unit
        ) =
            SelectItemDialog().apply {
                arguments = Bundle().apply {
                    putString(KEY_TITLE, title)
                    putStringArray(KEY_ITEM, items)
                    putString(KEY_BUTTON_TITLE, buttonTitle)
                }
                this.callback = callback
            }
    }

    private var _binding: FragmentSelectItemDialogBinding? = null
    private val binding get() = _binding!!

    private val title: String by lazy { requireArguments().getString(KEY_TITLE) ?: "" }
    private val items: Array<String> by lazy {
        requireArguments().getStringArray(KEY_ITEM) ?: emptyArray()
    }
    private val buttonTitle: String by lazy { requireArguments().getString(KEY_BUTTON_TITLE) ?: "" }
    private var callback: ((String) -> Unit)? = null

    private val adapter by lazy { SelectItemAdapter() }

    override fun getTheme(): Int {
        return R.style.CustomRoundBottomSheetDialogStyle
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectItemDialogBinding.inflate(layoutInflater)
        initView()
        initListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun initView() {
        binding.tvTitle.text = title
        binding.btFinish.text = buttonTitle
        binding.rvItems.adapter = adapter
        adapter.setItems(items)
    }

    private fun initListener() {
        binding.btFinish.setOnClickListener {
            callback?.invoke(adapter.getSelectItem())
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

class SelectItemAdapter : RecyclerView.Adapter<SelectItemAdapter.ViewHolder>() {

    private val items = mutableListOf<String>()
    private var selectItem: String = ""

    fun setItems(items: Array<String>) {
        this.items.addAll(items)
        selectItem = items[0]
        notifyDataSetChanged()
    }

    fun getSelectItem(): String {
        return selectItem
    }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSelectTextBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class ViewHolder(private val binding: ItemSelectTextBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.flRoot.setOnClickListener {
                selectItem = items[bindingAdapterPosition]
                notifyDataSetChanged()
            }
        }

        fun bind(item: String) {
            binding.tvItem.text = item
            if (item == selectItem) {
                binding.flRoot.setBackgroundResource(R.drawable.shape_r6_80bad6fb)
                binding.tvItem.setTextColor(
                    ContextCompat.getColor(
                        binding.tvItem.context,
                        R.color.black
                    )
                )
            } else {
                binding.flRoot.setBackgroundColor(Color.TRANSPARENT)
                binding.tvItem.setTextColor(
                    ContextCompat.getColor(
                        binding.tvItem.context,
                        R.color.color99666666
                    )
                )
            }
        }
    }

}