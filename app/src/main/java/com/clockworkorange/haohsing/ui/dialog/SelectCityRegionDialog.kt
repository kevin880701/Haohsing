package com.clockworkorange.haohsing.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.clockworkorange.domain.Result
import com.clockworkorange.domain.usecase.cityregion.GetCityListUseCase
import com.clockworkorange.domain.usecase.cityregion.GetRegionListUseCase
import com.clockworkorange.haohsing.databinding.LayoutEmptyBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SelectCityRegionDialog : DialogFragment() {

    companion object {
        fun newInstance(listener: Listener) = SelectCityRegionDialog().apply {
            this.listener = listener
        }
    }

    interface Listener {
        fun selectResult(city: String, region: String)
    }

    @Inject
    lateinit var getCityListUseCase: GetCityListUseCase
    @Inject
    lateinit var getRegionListUseCase: GetRegionListUseCase

    private var listener: Listener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return LayoutEmptyBinding.inflate(layoutInflater).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            when (val result = getCityListUseCase.invoke(Unit)) {
                is Result.Success -> showSelectCityDialog(result.data)
                is Result.Error -> {
                    Toast.makeText(requireContext(), "無法取得縣市列表", Toast.LENGTH_SHORT).show()
                    dismiss()
                }
                else -> {}
            }
        }
    }

    private fun showSelectCityDialog(cityList: List<String>) {
        SelectItemDialog.newInstance(
            "縣市",
            cityList.toTypedArray(),
            "下一步",
        ) { city ->
            fetchRegionList(city)
        }.show(childFragmentManager, "select city")
    }

    private fun fetchRegionList(city: String) {
        lifecycleScope.launch {
            when (val result = getRegionListUseCase.invoke(city)) {
                is Result.Success -> {
                    showSelectRegionDialog(city, result.data)
                }
                is Result.Error -> {
                    Toast.makeText(requireContext(), "無法取得地區列表", Toast.LENGTH_SHORT).show()
                    dismiss()
                }
                else -> {}
            }
        }
    }

    private fun showSelectRegionDialog(city: String, regions: List<String>) {
        SelectItemDialog.newInstance(
            "地區",
            regions.toTypedArray(),
            "完成",
        ) { region ->
            listener?.selectResult(city, region)
            dismiss()
        }.show(childFragmentManager, "select region")
    }
}