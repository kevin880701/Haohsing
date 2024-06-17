package com.clockworkorange.domain.usecase.workorder

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.WorkOrderRepository
import com.clockworkorange.domain.di.IoDispatcher
import com.clockworkorange.domain.entity.WorkOrderRequirement
import com.clockworkorange.domain.usecase.task.FormOption
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class CreateWorkOrderUseCase @Inject constructor(
    private val workOrderRepository: WorkOrderRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<CreateWorkOrderParam, Boolean>(dispatcher){

    override suspend fun execute(parameters: CreateWorkOrderParam): Boolean {
        return workOrderRepository.createWorkOrder(parameters)
    }

}

data class CreateWorkOrderParam(
    val deviceId: Int,
    val userName: String,
    val userPhone: String,
    val workOrderRequirement: WorkOrderRequirement,
    val deliveryTime: String,
    val errorReasons: List<FormOption>,
    val photoVideoFiles: List<String>,
    val note: String
)