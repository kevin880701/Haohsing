package com.clockworkorange.haohsing.ui.main.pair.engineer

import androidx.lifecycle.viewModelScope
import com.clockworkorange.domain.Result
import com.clockworkorange.domain.usecase.customer.CustomerAgency
import com.clockworkorange.domain.usecase.customer.GetCustomerListUseCase
import com.clockworkorange.haohsing.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectCustomerViewModel @Inject constructor(
    private val getCustomerListUseCase: GetCustomerListUseCase
) : BaseViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val keyword: String = "",
        val filteredCustomerAgencies: List<CustomerAgency> = emptyList()
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _customers = MutableStateFlow<List<CustomerAgency>>(emptyList())
    private val _keyword = MutableStateFlow<String>("")

    init {
        viewModelScope.launch {

            fetchCustomerList()

            _customers.combine(_keyword) { customers, keyword ->
                _uiState.update { it.copy(keyword = keyword) }
                if (keyword.isEmpty()) return@combine customers
                customers.filter { it.name.contains(keyword) }
            }.collect { filtered ->
                _uiState.update { it.copy(filteredCustomerAgencies = filtered) }
            }
        }
    }

    private suspend fun fetchCustomerList() {
        _uiState.update { it.copy(isLoading = true) }

        when (val result = getCustomerListUseCase.invoke(Unit)) {
            is Result.Success -> {
                _customers.emit(result.data)
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