package com.clockworkorange.domain.usecase.workorder

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.WorkOrderRepository
import com.clockworkorange.domain.di.IoDispatcher
import com.clockworkorange.domain.entity.WorkOrderRequirement
import com.clockworkorange.domain.usecase.task.FormOption
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class GetPendingWorkOrderDetailUseCase @Inject constructor(
    private val workOrderRepository: WorkOrderRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<Int, PendingWorkOrderDetail>(dispatcher) {

    override suspend fun execute(parameters: Int): PendingWorkOrderDetail {
        return workOrderRepository.getPendingWorkOrderDetail(parameters)
    }
}

data class PendingWorkOrderDetail(
    val orderId: Int,
    val deviceId: Int,
    val requirement: WorkOrderRequirement,
    val deliveryDate: List<Int>,
    val deliveryTime: String,
    val errorReasons: List<FormOption>,
    val note: String?,
    val photoVideos: List<String> = emptyList()
)