package com.clockworkorange.domain.usecase.workorder

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.WorkOrderRepository
import com.clockworkorange.domain.di.IoDispatcher
import com.clockworkorange.domain.entity.WorkOrderRequirement
import com.clockworkorange.domain.usecase.task.EngineerInfo
import com.clockworkorange.domain.usecase.task.FormOption
import kotlinx.coroutines.CoroutineDispatcher
import java.time.LocalDateTime
import javax.inject.Inject

class GetScheduledWorkOrderDetailUseCase @Inject constructor(
    private val workOrderRepository: WorkOrderRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<Int, ScheduledWorkOrderDetail>(dispatcher) {

    override suspend fun execute(parameters: Int): ScheduledWorkOrderDetail {
        return workOrderRepository.getScheduleWorkOrderDetail(parameters)
    }
}

data class ScheduledWorkOrderDetail(
    val orderId: Int,
    val deviceId: Int,
    val engineerInfo: EngineerInfo,
    val appointedDatetime: LocalDateTime,
    val requirement: WorkOrderRequirement,
    val errorReasons: List<FormOption>,
    val note: String?,
)