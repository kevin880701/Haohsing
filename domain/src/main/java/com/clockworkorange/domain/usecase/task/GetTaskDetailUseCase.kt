package com.clockworkorange.domain.usecase.task

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.TaskRepository
import com.clockworkorange.domain.di.IoDispatcher
import com.clockworkorange.domain.entity.WorkOrderRequirement
import kotlinx.coroutines.CoroutineDispatcher
import java.time.LocalDateTime
import javax.inject.Inject

class GetTaskDetailUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<Int, TaskDetail>(dispatcher) {

    override suspend fun execute(parameters: Int): TaskDetail {
        return taskRepository.getTaskDetail(parameters)
    }
}


data class TaskDetail(
    val taskId: Int,
    val deviceId: Int,
    val status: TaskStatusCategory,
    val isErrorNeedResend: Boolean = false,
    val appointedDatetime: LocalDateTime,
    val deliveryTime: LocalDateTime?,
    val engineerInfo: EngineerInfo?,
    val customerInfo: CustomerInfo,
    val requirement: WorkOrderRequirement,
    val errorReason: List<FormOption>,
    val photoVideos: List<String>,
    val note: String?,
    val maintainRecord: MaintainRecord? = null,
    val repairRecord: RepairRecord? = null,
    val confirmRecord: ConfirmRecord? = null,
    val hasCustomerFeedback: Boolean = true,
    val vendorNote: String? = null,
)
data class EngineerInfo(
    val id: Int,
    val place: String,
    val name: String,
    val phone: String
){
    companion object{
        fun createDemo(): EngineerInfo{
            return EngineerInfo(
                123,
                "ＯＯ有限公司",
                "張小明",
                "04-42345678"
            )
        }
    }
}

data class CustomerInfo(
    val agency: String?,
    val name: String,
    val phone: String,
    val address: String
){
    companion object{
        fun createDemo(): CustomerInfo{
            return CustomerInfo(
                "ＯＯ有限公司",
                "陳小明",
                "0912345678",
                "台中市烏日區高鐵五路156號"
            )
        }
    }
}

data class MaintainRecord(
    val tds: String,
    val testTDS: String,
    val filterChanged: List<FormOption>,
    val basicMaintain: List<FormOption>
){
    companion object{
        fun createDemo() = MaintainRecord(
            "22",
            "23",
            listOf(
                FormOption("BB1", "更換濾芯", "第一道"),
                FormOption("BB2", "更換濾芯", "第二道"),
                FormOption("BB4", "更換濾芯", "RO膜")
            ),
            listOf(FormOption("BC2", "基礎保養", "電路檢查"))
        )
    }
}

data class RepairRecord(
    val errorCode: List<FormOption>,
    val repairParts: List<FormOption>,
    val changeParts: List<FormOption>
){
    companion object{
        fun createDemo() = RepairRecord(
            listOf(FormOption("CBA07", "故障代碼", "EP"), FormOption("CBA08", "故障代碼", "EA")),
            listOf(FormOption("CCA2", "維修內容", "故障維修"), FormOption("CCA3", "維修內容", "更換零件")),
            listOf(
                FormOption("CDA3", "控制類", "按鍵板"),
                FormOption("CDB1", "加熱類", "熱桶"),
                FormOption("CDB2", "加熱類", "溫桶"),
                FormOption("CDC6", "製冷類", "散熱風扇"),
                FormOption("CDD1", "管路類", "出水電磁閥"),
                FormOption("CDE04", "RO機類", "馬達")
            )
        )
    }
}

data class ConfirmRecord(
    val fee: Int,
    val customerSign: String,
    val engineerId: Int,
    val errorReason: String? = null,
    val quotationPDF: String? = null,
    val isRepairDone: Boolean = false
){
    companion object{
        fun createDemoError() = ConfirmRecord(
            1000,
            "http://XXX.jpg",
            232100,
            "異常原因XXOO"
        )

        fun createDemoNeedPrice() = ConfirmRecord(
            1000,
            "http://XXX.jpg",
            232100,
            isRepairDone = false
        )

        fun createDemoDone() = ConfirmRecord(
            1000,
            "http://XXX.jpg",
            232100,
            isRepairDone = true,
            quotationPDF = "http://xxx.pdf"
        )
    }
}
