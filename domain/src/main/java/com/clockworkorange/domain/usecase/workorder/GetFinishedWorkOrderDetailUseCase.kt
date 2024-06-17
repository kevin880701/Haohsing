package com.clockworkorange.domain.usecase.workorder

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.WorkOrderRepository
import com.clockworkorange.domain.di.IoDispatcher
import com.clockworkorange.domain.entity.WorkOrderRequirement
import com.clockworkorange.domain.usecase.task.*
import kotlinx.coroutines.CoroutineDispatcher
import java.time.LocalDateTime
import javax.inject.Inject

class GetFinishedWorkOrderDetailUseCase @Inject constructor(
    private val workOrderRepository: WorkOrderRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<Int, FinishedWorkOrderDetail>(dispatcher){

    override suspend fun execute(parameters: Int): FinishedWorkOrderDetail {
        return workOrderRepository.getFinishedWorkOrderDetail(parameters)
    }
}

data class FinishedWorkOrderDetail(
    val orderId: Int,
    val status: TaskStatusCategory,
    val deliveryTime: LocalDateTime?,
    val engineerInfo: EngineerInfo,
    val customerInfo: CustomerInfo,
    val requirement: WorkOrderRequirement,
    val errorReason: List<FormOption>,
    val note: String?,
    val maintainRecord: MaintainRecord? = null,
    val repairRecord: RepairRecord? = null,
    val confirmRecord: ConfirmRecord? = null
)