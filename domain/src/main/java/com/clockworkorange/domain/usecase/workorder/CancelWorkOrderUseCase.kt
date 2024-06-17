package com.clockworkorange.domain.usecase.workorder

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.WorkOrderRepository
import com.clockworkorange.domain.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import java.util.*
import javax.inject.Inject

class CancelWorkOrderUseCase @Inject constructor(
    private val workOrderRepository: WorkOrderRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<Int, Boolean>(dispatcher) {
    override suspend fun execute(parameters: Int): Boolean {
        return workOrderRepository.cancelWorkOrder(parameters, Date())
    }
}