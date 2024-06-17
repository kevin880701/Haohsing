package com.clockworkorange.haohsing.ui.dialog

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.clockworkorange.haohsing.databinding.FragmentSignatureDialogBinding


class SignatureDialog : DialogFragment() {

    fun interface Listener {
        fun onSignatureDone(signature: Bitmap)
    }

    private var listener: Listener? = null

    private var _binding: FragmentSignatureDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignatureDialogBinding.inflate(layoutInflater)
        initView()
        initListener()
        return binding.root
    }

    private fun initView() = binding.run {
        tvFee.text = requireArguments().getString(KEY_DESCRIPTION)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun initListener() {
        binding.btClose.setOnClickListener { dismiss() }
        binding.btCancel.setOnClickListener { binding.signaturePad.clear() }
        binding.btConfirm.setOnClickListener {
            listener?.onSignatureDone(binding.signaturePad.getSignatureBitmap())
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val KEY_DESCRIPTION = "DESCRIPTION"

        @JvmStatic
        fun newInstance(description: String, listener: Listener) =
            SignatureDialog().apply {
                arguments = Bundle().apply {
                    putString(KEY_DESCRIPTION, description)
                }
                this.listener = listener
            }
    }
}