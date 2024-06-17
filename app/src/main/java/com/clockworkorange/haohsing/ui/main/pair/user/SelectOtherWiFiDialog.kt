package com.clockworkorange.haohsing.ui.main.pair.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.databinding.FragmentSelectOtherWiFiDialogBinding
import com.clockworkorange.haohsing.ui.widget.SpacesItemDecoration
import com.clockworkorange.haohsing.utils.GenericAdapterListener
import com.clockworkorange.haohsing.utils.ScreenUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SelectOtherWiFiDialog : BottomSheetDialogFragment(), GenericAdapterListener<SimpleWiFiInfo> {

    companion object {
        const val TAG = "SelectOtherWiFiDialog"
        private const val KEY_WIFI_LIST = "KEY_WIFI_LIST"
        fun newInstance(wifiList: List<SimpleWiFiInfo>, listener: Listener) =
            SelectOtherWiFiDialog().apply {
                this.listener = listener
                arguments = Bundle().apply {
                    putParcelableArray(KEY_WIFI_LIST, wifiList.toTypedArray())
                }
            }
    }

    interface Listener {
        fun onWifiSelected(wifi: SimpleWiFiInfo)
    }

    private var _binding: FragmentSelectOtherWiFiDialogBinding? = null
    private val binding get() = _binding!!

    private var listener: Listener? = null

    private val wifiList: Array<SimpleWiFiInfo> by lazy {
        arguments?.getParcelableArray(
            KEY_WIFI_LIST
        ) as Array<SimpleWiFiInfo>
    }

    private val adapter: SelectWiFiAdapter by lazy { SelectWiFiAdapter(this) }

    override fun getTheme(): Int {
        return R.style.CustomRoundBottomSheetDialogStyle
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectOtherWiFiDialogBinding.inflate(layoutInflater)
        initView()
        return binding.root
    }

    private fun initView() {
        binding.rvWifi.adapter = adapter
        binding.rvWifi.addItemDecoration(
            SpacesItemDecoration(
                bottom = ScreenUtils.dp2px(
                    requireContext(),
                    8
                )
            )
        )
        binding.rvWifi.itemAnimator = DefaultItemAnimator()
        adapter.data.addAll(wifiList)
    }

    override fun onItemClick(item: SimpleWiFiInfo) {
        listener?.onWifiSelected(item)
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}