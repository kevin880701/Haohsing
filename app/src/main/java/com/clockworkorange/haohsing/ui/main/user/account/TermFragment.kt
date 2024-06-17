package com.clockworkorange.haohsing.ui.main.user.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.databinding.FragmentTermBinding
import com.clockworkorange.haohsing.ui.base.BaseFragment

class TermFragment : BaseFragment() {

    private var _binding: FragmentTermBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<TermFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTermBinding.inflate(layoutInflater)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
        initListener()
    }

    private fun initListener() = binding.run {
        ilToolbar.btBack.setOnClickListener { findNavController().navigateUp() }
    }

    private fun initView() = binding.run {
        ilToolbar.tvTitle.text = args.title
        webView.loadUrl(args.url)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}