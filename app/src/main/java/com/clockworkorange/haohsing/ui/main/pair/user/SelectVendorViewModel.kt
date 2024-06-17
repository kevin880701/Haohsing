package com.clockworkorange.haohsing.ui.main.pair.user

import androidx.lifecycle.viewModelScope
import com.clockworkorange.domain.Result
import com.clockworkorange.domain.usecase.vendor.GetVendorListUseCase
import com.clockworkorange.domain.usecase.vendor.Vendor
import com.clockworkorange.haohsing.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectVendorViewModel @Inject constructor(
    private val getVendorListUseCase: GetVendorListUseCase
) : BaseViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val keyword: String = "",
        val filteredCustomers: List<Vendor> = emptyList()
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _vendors = MutableStateFlow<List<Vendor>>(emptyList())
    private val _keyword = MutableStateFlow<String>("")

    init {
        viewModelScope.launch {

            fetchVendorList()

            _vendors.combine(_keyword) { customers, keyword ->
                _uiState.update { it.copy(keyword = keyword) }
                if (keyword.isEmpty()) return@combine customers
                customers.filter { it.name.contains(keyword) }
            }.collect { filtered ->
                _uiState.update { it.copy(filteredCustomers = filtered) }
            }
        }
    }

    private suspend fun fetchVendorList() {
        _uiState.update { it.copy(isLoading = true) }

        when (val result = getVendorListUseCase.invoke(Unit)) {
            is Result.Success -> {
                _vendors.emit(result.data)
                _uiState.update { it.copy(isLoading = false) }
            }
            is Result.Error -> {
                _uiState.update { it.copy(isLoading = false) }
            }
            Result.Loading -> {}
        }
    }

    fun search(keyword: String) {
        _keyword.tryEmit(keyword)
    }

    fun clearSearch() {
        _keyword.tryEmit("")
    }

}