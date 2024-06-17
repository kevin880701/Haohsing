package com.clockworkorange.domain.usecase.task

import android.os.Parcelable
import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.TaskRepository
import com.clockworkorange.domain.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

class GetMaintainOptionsUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<Unit, MaintainOptions>(dispatcher) {

    override suspend fun execute(parameters: Unit): MaintainOptions {
        return taskRepository.getMaintainOptions()
    }
}

data class MaintainOptions(
    val changeFilters: List<FormOption>,
    val basicMaintain: List<FormOption>
)

@Parcelize
data class FormOption(
    val code: String,
    val group: String,
    val name: String,
    val remark: String? = ""
) : Parcelable{

    val displayName: String
        get() = if (code in listOf<String>("CBA12", "CCA5")) ("其他:${remark?:""}") else name

}

fun List<FormOption>.toDisplayString(showGroupName: Boolean = true, showDot: Boolean = true): String{
    val groupOption = this.groupBy { it.group }
    return buildString {
        groupOption.forEach { (option, items) ->
            if (showDot) append("•")
            if (showGroupName){
                append(option)
                append("-")
            }
            items.forEachIndexed { index, formOption ->
                append(formOption.name)
                if (index != items.size-1){
                    append("、")
                }
            }
            append("\n")
        }
    }.dropLast(1)
}