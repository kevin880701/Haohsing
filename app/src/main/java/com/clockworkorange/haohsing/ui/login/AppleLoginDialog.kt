package com.clockworkorange.haohsing.ui.login

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.clockworkorange.haohsing.databinding.FragmentAppleLoginDialogBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*


@AndroidEntryPoint
class AppleLoginDialog : DialogFragment() {

    companion object {
        fun newInstance(listener: Listener) = AppleLoginDialog()
            .apply {
                this.listener = listener
            }
    }

    interface Listener {
        fun onAppleLoginSuccess(authCode: String)
        fun onAppleLoginFail()
    }

    private var listener: Listener? = null

    private var _binding: FragmentAppleLoginDialogBinding? = null
    private val binding get() = _binding!!

    private val CLIENT_ID = "com.clockworkorange.haohsing.service"
    private val REDIRECT_URI = "https://service.cloud-nest.com/LoginAPI/DoAppleLogin"
    private val SCOPE = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppleLoginDialogBinding.inflate(layoutInflater)
        initView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun initView() {
        with(binding.webview) {
            isHorizontalScrollBarEnabled = false
            isVerticalScrollBarEnabled = false
            webViewClient = AppleLoginWebView()
            webChromeClient = WebChromeClient()
            settings.javaScriptEnabled = true
            loadUrl(createURL())
        }
    }

    private fun createURL(): String {
        return Uri.parse("https://appleid.apple.com/auth/authorize")
            .buildUpon().apply {
                appendQueryParameter("response_type", "code")
                appendQueryParameter("v", "1.1.6")
                appendQueryParameter("client_id", CLIENT_ID)
                appendQueryParameter("redirect_uri", REDIRECT_URI)
                appendQueryParameter("scope", SCOPE)
                appendQueryParameter("state", UUID.randomUUID().toString())
                appendQueryParameter("response_mode", "query")
            }.build().toString()
    }

    inner class AppleLoginWebView : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            if (url != null && handleURL(Uri.parse(url))) {
                return true
            }
            return super.shouldOverrideUrlLoading(view, url)
        }

        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            if (handleURL(request?.url)) {
                return true
            }
            return super.shouldOverrideUrlLoading(view, request)
        }

        override fun shouldInterceptRequest(
            view: WebView?,
            request: WebResourceRequest?
        ): WebResourceResponse? {
            handleURL(request?.url)
            return super.shouldInterceptRequest(view, request)
        }

        private fun handleURL(url: Uri?): Boolean {
            Timber.d("handleURL: $url")
            if (url?.toString()?.startsWith(REDIRECT_URI) == true) {
                val authCode = url.getQueryParameter("code")
                requireActivity().runOnUiThread {
                    if (authCode != null && authCode.isNotEmpty()) {
                        listener?.onAppleLoginSuccess(authCode)
                    } else {
                        listener?.onAppleLoginFail()
                    }
                    dismiss()
                }
                return true
            }
            return false
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            binding.pbLoading.isVisible = true
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            val rect = Rect()
            requireActivity().window.decorView.getWindowVisibleDisplayFrame(rect)
            val lp = view?.layoutParams
            lp?.height = (rect.height() * 0.9f).toInt()
            view?.layoutParams = lp
            binding.pbLoading.isVisible = false
            super.onPageFinished(view, url)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}