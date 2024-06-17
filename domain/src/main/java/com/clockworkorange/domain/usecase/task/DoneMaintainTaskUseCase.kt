package com.clockworkorange.domain.usecase.task

import android.net.Uri
import android.os.Parcelable
import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.TaskRepository
import com.clockworkorange.domain.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.parcelize.Parcelize
import java.io.File
import java.time.LocalDateTime
import javax.inject.Inject

class DoneMaintainTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<DoneMaintainTaskUseCaseParam, Boolean>(dispatcher) {

    override suspend fun execute(parameters: DoneMaintainTaskUseCaseParam): Boolean {
        return taskRepository.doneMaintainTask(parameters)
    }
}

enum class DoneTaskType{
    Finish, NeedReSent, ReportError
}

data class DoneMaintainTaskUseCaseParam(
    val doneTaskType: DoneTaskType,
    val taskId: Int,
    val deviceId: Int,
    val tds: String,
    val checkTDS: String,
    val changeableFilter: List<FormOption>,
    val basicMaintain: List<FormOption>,
    val photoOldFilter: Uri,
    val photoNewFilter: Uri,
    val isWork: Boolean,
    val fee: String,
    val customerSignature: File,
    val reportErrorReason: String = "",
    val changedFilter: List<ChangedFilter> = emptyList()
)

@Parcelize
data class ChangedFilter(
    val type: FilterType,
    val name: String,
    val installDate: LocalDateTime,
    val lifeMonth: Int
) : Parcelable {

    enum class FilterType(val code: String){
        F1("F1"),
        F2("F2"),
        F3("F3"),
        F4("F4"),
        F5("F5");
    }
}

