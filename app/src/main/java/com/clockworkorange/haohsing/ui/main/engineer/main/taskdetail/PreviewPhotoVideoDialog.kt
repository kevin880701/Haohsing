package com.clockworkorange.haohsing.ui.main.engineer.main.taskdetail

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.clockworkorange.haohsing.databinding.FragmentPreviewPhotoVideoDialogBinding


class PreviewPhotoVideoDialog : DialogFragment() {

    companion object {
        private const val KEY_URL = "URL"
        fun newInstance(url: String) =
            PreviewPhotoVideoDialog().apply {
                arguments = Bundle().apply {
                    putString(KEY_URL, url)
                }
            }
    }

    private var _binding: FragmentPreviewPhotoVideoDialogBinding? = null
    private val binding get() = _binding!!

    private val url: String by lazy { requireArguments().getString(KEY_URL) ?: "" }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPreviewPhotoVideoDialogBinding.inflate(layoutInflater)
        initView()
        initListener()
        return binding.root
    }

    private fun initView() {
        val loadingDrawable = CircularProgressDrawable(binding.root.context).apply {
            strokeWidth = 5f
            centerRadius = 30f
            start()
        }

        Glide.with(this)
            .load(url)
            .placeholder(loadingDrawable)
            .into(binding.ivCover)

        if (url.endsWith("mp4")) {
            binding.viewVideo.isVisible = true
            binding.pbLoading.isVisible = true

            binding.viewVideo.setVideoURI(Uri.parse(url))
            binding.viewVideo.setOnPreparedListener {
                it.isLooping = true
                binding.pbLoading.isVisible = false

                val controller = MyMediaController(binding.viewVideo.context)
                controller.setAnchorView(binding.viewVideo)
                binding.viewVideo.setMediaController(controller)
                (controller.parent as? ViewGroup)?.removeView(controller)
                binding.flController.addView(controller)
                controller.show(0)

            }

            binding.viewVideo.start()
        }
    }

    private inner class MyMediaController(context: Context) : MediaController(context, false) {
        override fun hide() {
            this.show(0)
        }
    }

    private fun initListener() {
        binding.root.setOnClickListener { dismiss() }
        binding.btClose.setOnClickListener { dismiss() }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}