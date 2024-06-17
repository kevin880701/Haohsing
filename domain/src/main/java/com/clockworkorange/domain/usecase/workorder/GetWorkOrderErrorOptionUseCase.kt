package com.clockworkorange.domain.usecase.workorder

import android.os.Parcelable
import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.WorkOrderRepository
import com.clockworkorange.domain.di.IoDispatcher
import com.clockworkorange.domain.usecase.task.FormOption
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

class GetWorkOrderErrorOptionUseCase @Inject constructor(
    private val workOrderRepository: WorkOrderRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<Unit, List<FormOption>>(dispatcher) {

    override suspend fun execute(parameters: Unit):List<FormOption> {
        return workOrderRepository.getErrorReasonList()
    }
}
