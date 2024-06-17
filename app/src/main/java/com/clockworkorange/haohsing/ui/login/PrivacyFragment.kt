package com.clockworkorange.haohsing.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.clockworkorange.haohsing.R


class PrivacyFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_privacy, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireView().findViewById<ImageButton>(R.id.bt_back).setOnClickListener {
            setFragmentResult(KEY_AGREE_PRIVACY, bundleOf(KEY_AGREE_PRIVACY to false))
            findNavController().navigateUp()
        }

        requireView().findViewById<Button>(R.id.bt_agree).setOnClickListener {
            setFragmentResult(KEY_AGREE_PRIVACY, bundleOf(KEY_AGREE_PRIVACY to true))
            findNavController().navigateUp()
        }
    }

    companion object {
        const val KEY_AGREE_PRIVACY = "AGREE_PRIVACY"
    }
}