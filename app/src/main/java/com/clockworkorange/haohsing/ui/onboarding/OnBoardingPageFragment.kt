package com.clockworkorange.haohsing.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.databinding.FragmentOnBoardingPageBinding


class OnBoardingPageFragment : Fragment() {

    companion object {
        private const val KEY_PAGE = "KEY_PAGE"

        @JvmStatic
        fun newInstance(page: Int) = OnBoardingPageFragment().apply {
            arguments = Bundle().apply {
                putInt(KEY_PAGE, page)
            }
        }
    }

    private val page: Int by lazy { requireArguments().getInt(KEY_PAGE) }

    private var _binding: FragmentOnBoardingPageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnBoardingPageBinding.inflate(layoutInflater)
        initView()
        return binding.root
    }

    private fun initView() {
        when (page) {
            0 -> binding.ivContent.setImageResource(R.drawable.img_onboarding_1)
            1 -> binding.ivContent.setImageResource(R.drawable.img_onboarding_2)
            2 -> binding.ivContent.setImageResource(R.drawable.img_onboarding_3)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}