package com.clockworkorange.domain.usecase.workorder

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.WorkOrderRepository
import com.clockworkorange.domain.di.IoDispatcher
import com.clockworkorange.domain.entity.WorkOrderRequirement
import kotlinx.coroutines.CoroutineDispatcher
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject

class GetWorkOrderUseCase @Inject constructor(
    private val workOrderRepository: WorkOrderRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<GetWorkOrderParam, List<WorkOrderInfo>>(dispatcher) {

    override suspend fun execute(parameters: GetWorkOrderParam): List<WorkOrderInfo> {
        return workOrderRepository.getWorkOrderList(parameters.deviceId, parameters.status)
    }
}
data class GetWorkOrderParam(
    val deviceId: Int,
    val status: WorkOrderStatus
)
enum class WorkOrderStatus(val id: Int){
    Pending(0),
    Schedule(1),
    Finished(2),
    Cancel(3);

    companion object{
        fun fromId(id: Int): WorkOrderStatus{
            return when(id){
                0 -> Pending
                1 -> Schedule
                2 -> Finished
                3 -> Cancel
                else -> throw IllegalArgumentException("無此id對應狀態")
            }
        }
    }
}


data class WorkOrderInfo(
    val id: Int,
    val status: WorkOrderStatus,
    val requirement: WorkOrderRequirement,
    val date: LocalDateTime
)