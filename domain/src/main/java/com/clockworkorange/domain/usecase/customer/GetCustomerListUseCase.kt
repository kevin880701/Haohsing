package com.clockworkorange.domain.usecase.customer

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.WorkOrderRepository
import com.clockworkorange.domain.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class GetCustomerListUseCase @Inject constructor(
    private val workOrderRepository: WorkOrderRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<Unit, List<CustomerAgency>>(dispatcher) {

    override suspend fun execute(parameters: Unit): List<CustomerAgency> {
        return workOrderRepository.getCustomerList()
    }
}

/**
 * 顧客單位
 *
 * e.g.
 *
 *  若為一般使用者 => 選擇一般家用
 *
 *  若非一般使用者 => 選擇xx公司
 */
data class CustomerAgency(
    val id: Int,
    val name: String
)