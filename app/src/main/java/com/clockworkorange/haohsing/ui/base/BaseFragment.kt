package com.clockworkorange.haohsing.ui.base

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import timber.log.Timber

open class BaseFragment : Fragment() {

    open var logEnable = false

    fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (logEnable) Timber.d("onAttach ${javaClass.simpleName}")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (logEnable) Timber.d("onCreate ${javaClass.simpleName}")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (logEnable) Timber.d("onViewCreated ${javaClass.simpleName}")
    }

    override fun onStart() {
        super.onStart()
        if (logEnable) Timber.d("onStart ${javaClass.simpleName}")
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (logEnable) Timber.d("onViewStateRestored ${javaClass.simpleName}")
    }

    override fun onResume() {
        super.onResume()
        if (logEnable) Timber.d("onResume ${javaClass.simpleName}")
    }

    override fun onPause() {
        super.onPause()
        if (logEnable) Timber.d("onPause ${javaClass.simpleName}")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (logEnable) Timber.d("onSaveInstanceState ${javaClass.simpleName}")
    }

    override fun onStop() {
        super.onStop()
        if (logEnable) Timber.d("onStop ${javaClass.simpleName}")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (logEnable) Timber.d("onDestroyView ${javaClass.simpleName}")
    }

    override fun onDestroy() {
        super.onDestroy()
        if (logEnable) Timber.d("onDestroy ${javaClass.simpleName}")
    }

    override fun onDetach() {
        super.onDetach()
        if (logEnable) Timber.d("onDetach ${javaClass.simpleName}")
    }
}