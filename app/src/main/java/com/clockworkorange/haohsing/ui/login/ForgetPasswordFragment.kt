package com.clockworkorange.haohsing.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.ui.base.BaseFragment

class ForgetPasswordFragment : BaseFragment() {

    private val args: ForgetPasswordFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_forget_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getView()?.findViewById<TextView>(R.id.tv_mail)?.text = args.mail
        getView()?.findViewById<Button>(R.id.bt_back)
            ?.setOnClickListener { findNavController().navigateUp() }
    }
}