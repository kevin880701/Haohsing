package com.clockworkorange.domain.data

import android.content.Context
import com.clockworkorange.domain.data.remote.ServiceWrapper
import com.clockworkorange.domain.data.remote.model.ApiFormOptionField
import com.clockworkorange.domain.data.remote.model.ChangeFilterParam
import com.clockworkorange.domain.data.remote.model.DoneWorkOrderParam
import com.clockworkorange.domain.entity.WorkOrderRequirement
import com.clockworkorange.domain.usecase.notification.Duration
import com.clockworkorange.domain.usecase.task.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

interface TaskRepository {
    suspend fun getMonthTaskSummaryList(year: Int, month: Int): List<TaskSummary>
    suspend fun getTaskDetail(taskId: Int): TaskDetail
    suspend fun getMaintainOptions(): MaintainOptions
    suspend fun getRepairOptions(): RepairOptions
    suspend fun doneMaintainTask(parameters: DoneMaintainTaskUseCaseParam): Boolean
    suspend fun doneRepairTask(parameters: DoneRepairTaskUseCaseParam): Boolean
    suspend fun updateWorkTime(taskId: Int): Boolean
    suspend fun doneInstallTask(parameters: DoneInstallTaskUseCaseParam): Boolean
    suspend fun getTaskHistory(parameters: Duration): List<TaskSummary>
    suspend fun getInstallRecordList(duration: Duration): List<InstallRecord>
}

class TaskRepositoryImpl(
    private val context: Context,
    private val serviceWrapper: ServiceWrapper
) : TaskRepository {
    override suspend fun getMonthTaskSummaryList(year: Int, month: Int): List<TaskSummary> {
        return serviceWrapper.getEngineerWorkOrderList(year, month).map { it.toTaskSummary() }
    }

    override suspend fun getTaskDetail(taskId: Int): TaskDetail {
        return serviceWrapper.getWorkOrderDetail(taskId).toTaskDetail()
    }

    override suspend fun getMaintainOptions(): MaintainOptions {
        val options = serviceWrapper.getWorkOrderOption(2)

        val filterOptions = options.filter { it.group2 == "更換濾芯" }
            .map { FormOption(it.code, it.group2, it.item) }

        val basicMaintainOptions = options.filter { it.group2 == "基礎保養" }
            .map { FormOption(it.code, it.group2, it.item) }
            .toMutableList()

        basicMaintainOptions.add(FormOption("BD1", "基礎保養", "清缸(一年一次)"))

        return MaintainOptions(filterOptions, basicMaintainOptions)
    }

    override suspend fun getRepairOptions(): RepairOptions {
        val options = serviceWrapper.getWorkOrderOption(3)

        val errorCodeOptions = options.filter { it.group2 == "故障代碼" }
            .filterNot { it.item == "其它" }
            .map { FormOption(it.code, it.group2, it.item) }

        val repairContentOptions = options.filter { it.group1 == "維修內容" }
            .filterNot { it.item == "非產品原因" }
            .map { FormOption(it.code, it.group2, it.item) }

        val changePartControl = options.filter { it.group1 == "更換零件" && it.group2 == "控制類" }
            .map { FormOption(it.code, it.group2, it.item) }

        val changePartHeat = options.filter { it.group1 == "更換零件" && it.group2 == "加熱類" }
            .map { FormOption(it.code, it.group2, it.item) }

        val changePartCool = options.filter { it.group1 == "更換零件" && it.group2 == "製冷類" }
            .map { FormOption(it.code, it.group2, it.item) }

        val changePartPipeline = options.filter { it.group1 == "更換零件" && it.group2 == "管路類" }
            .map { FormOption(it.code, it.group2, it.item) }

        val changePartRO = options.filter { it.group1 == "更換零件" && it.group2 == "RO機類" }
            .map { FormOption(it.code, it.group2, it.item) }

        return RepairOptions(
            errorCodeOptions,
            repairContentOptions,
            ChangePartOptions(
                changePartControl,
                changePartHeat,
                changePartCool,
                changePartPipeline,
                changePartRO
            )
        )
    }

    override suspend fun doneMaintainTask(parameters: DoneMaintainTaskUseCaseParam): Boolean {

        serviceWrapper.uploadWorkOrderEngineerImage(
            parameters.taskId,
            22,
            FileUtils.compressImageFromContentUri(context,  parameters.photoOldFilter)
        )

        serviceWrapper.uploadWorkOrderEngineerImage(
            parameters.taskId,
            21,
            FileUtils.compressImageFromContentUri(context, parameters.photoNewFilter)
        )

        serviceWrapper.uploadSignImage(parameters.taskId, parameters.customerSignature)

        val status: Int = when (parameters.doneTaskType) {
            DoneTaskType.Finish -> 2
            DoneTaskType.NeedReSent, DoneTaskType.ReportError -> 4
        }

        parameters.changedFilter.forEach { filter ->
            serviceWrapper.changeFilter(
                ChangeFilterParam(
                    parameters.deviceId,
                    filter.type.code,
                    filter.name,
                    filter.installDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    filter.lifeMonth
                )
            )
        }

        val doneValues = (parameters.changeableFilter + parameters.basicMaintain)
            .map { ApiFormOptionField(it.code) }
            .toMutableList()

        doneValues.add(ApiFormOptionField("BA1", value = parameters.tds))
        doneValues.add(ApiFormOptionField("BA2", value = parameters.checkTDS))


        val data = DoneWorkOrderParam(
            parameters.taskId,
            status,
            if (parameters.isWork) 1 else 0,
            parameters.fee,
            if (parameters.doneTaskType == DoneTaskType.NeedReSent) 1 else 0,
            parameters.reportErrorReason,
            doneValues
        )
        return serviceWrapper.doneWorkOrder(data)
    }

    override suspend fun doneRepairTask(parameters: DoneRepairTaskUseCaseParam): Boolean {

        serviceWrapper.uploadWorkOrderEngineerImage(
            parameters.taskId,
            31,
            FileUtils.compressImageFromContentUri(context, parameters.photoNewParts)
        )

        serviceWrapper.uploadWorkOrderEngineerImage(
            parameters.taskId,
            32,
            FileUtils.compressImageFromContentUri(context, parameters.photoOldParts)
        )

        serviceWrapper.uploadSignImage(parameters.taskId, parameters.customerSignature)

        val status: Int = when (parameters.doneTaskType) {
            DoneTaskType.Finish -> 2
            DoneTaskType.NeedReSent, DoneTaskType.ReportError -> 4
        }

        val doneValues = (parameters.errorCodeOptions + parameters.repairContentOptions + parameters.changePartOptions)
            .map { ApiFormOptionField(it.code) }
            .toMutableList()

        doneValues.add(ApiFormOptionField("CBA12", remark = parameters.errorCodeOtherMessage))
        doneValues.add(ApiFormOptionField("CCA5", remark = parameters.repairContentOtherMessage))

        val data = DoneWorkOrderParam(
            parameters.taskId,
            status,
            if (parameters.isWork) 1 else 0,
            parameters.fee,
            if (parameters.doneTaskType == DoneTaskType.NeedReSent) 1 else 0,
            parameters.reportErrorReason,
            doneValues
        )

        return serviceWrapper.doneWorkOrder(data)
    }

    override suspend fun updateWorkTime(taskId: Int): Boolean {
        return serviceWrapper.updateWorkTime(taskId)
    }

    override suspend fun doneInstallTask(parameters: DoneInstallTaskUseCaseParam): Boolean {

        serviceWrapper.uploadSignImage(parameters.taskId, parameters.customerSignature)

        val status: Int = when (parameters.doneTaskType) {
            DoneTaskType.Finish -> 2
            DoneTaskType.NeedReSent, DoneTaskType.ReportError -> 4
        }
        val data = DoneWorkOrderParam(
            parameters.taskId,
            status,
            1,
            parameters.fee,
            if (parameters.doneTaskType == DoneTaskType.ReportError) 1 else 0,
            parameters.reportErrorReason,
            emptyList()
        )

        return serviceWrapper.doneWorkOrder(data)
    }

    override suspend fun getTaskHistory(parameters: Duration): List<TaskSummary> {
        return serviceWrapper.getTaskHistory(parameters.num).map { it.toTaskSummary() }
    }

    override suspend fun getInstallRecordList(duration: Duration): List<InstallRecord> {
        return serviceWrapper.getInstallRecordList(duration.num).map { it.toInstallRecord() }
    }
}

class FakeTaskRepository(private val serviceWrapper: ServiceWrapper) : TaskRepository {

    private val fakeTaskSummary = mutableListOf<TaskSummary>()
    private val fakeTaskDetail = mutableListOf<TaskDetail>()

    init {
        fillFakeTaskSummary()
    }

    private fun fillFakeTaskSummary() {
        val localDateTime = LocalDateTime.now()

        createTask(localDateTime.minusHours(1), TaskStatus.Scheduled).let { task ->
            fakeTaskSummary.add(task)
            val taskDetail = TaskDetail(
                task.taskId,
                1,
                task.statusCategory,
                task.isStatusErrorNeedResend,
                task.dateTime,
                LocalDateTime.now(),
                EngineerInfo.createDemo(),
                CustomerInfo.createDemo(),
                task.requirement,
                emptyList(),
                emptyList(),
                "飲水機顯示水質異常，一直沒有消失",
            )
            fakeTaskDetail.add(taskDetail)
        }

        createTask(localDateTime.plusHours(1), TaskStatus.Scheduled).let { task ->
            fakeTaskSummary.add(task)
            val taskDetail = TaskDetail(
                task.taskId,
                1,
                task.statusCategory,
                task.isStatusErrorNeedResend,
                task.dateTime,
                LocalDateTime.now(),
                EngineerInfo.createDemo(),
                CustomerInfo.createDemo(),
                task.requirement,
                emptyList(),
                emptyList(),
                "飲水機顯示水質異常，一直沒有消失",
            )
            fakeTaskDetail.add(taskDetail)
        }

        createTask(localDateTime.plusHours(2), TaskStatus.Scheduled).let { task ->
            fakeTaskSummary.add(task)
            val taskDetail = TaskDetail(
                task.taskId,
                1,
                task.statusCategory,
                task.isStatusErrorNeedResend,
                task.dateTime,
                LocalDateTime.now(),
                EngineerInfo.createDemo(),
                CustomerInfo.createDemo(),
                task.requirement,
                emptyList(),
                emptyList(),
                "飲水機顯示水質異常，一直沒有消失",
            )
            fakeTaskDetail.add(taskDetail)
        }

        createTask(localDateTime.plusHours(3), TaskStatus.Error, true).let { task ->
            fakeTaskSummary.add(task)
            val taskDetail = TaskDetail(
                task.taskId,
                1,
                task.statusCategory,
                task.isStatusErrorNeedResend,
                task.dateTime,
                LocalDateTime.now(),
                EngineerInfo.createDemo(),
                CustomerInfo.createDemo(),
                task.requirement,
                emptyList(),
                emptyList(),
                "飲水機顯示水質異常，一直沒有消失",
                MaintainRecord.createDemo(),
                RepairRecord.createDemo(),
                ConfirmRecord.createDemoError()
            )
            fakeTaskDetail.add(taskDetail)
        }

        createTask(localDateTime.plusDays(1), TaskStatus.Finished).let { task ->
            fakeTaskSummary.add(task)
            val taskDetail = TaskDetail(
                task.taskId,
                1,
                task.statusCategory,
                task.isStatusErrorNeedResend,
                task.dateTime,
                LocalDateTime.now(),
                EngineerInfo.createDemo(),
                CustomerInfo.createDemo(),
                task.requirement,
                emptyList(),
                emptyList(),
                "飲水機顯示水質異常，一直沒有消失",
                MaintainRecord.createDemo(),
                RepairRecord.createDemo(),
                ConfirmRecord.createDemoDone()
            )
            fakeTaskDetail.add(taskDetail)
        }

        createTask(localDateTime.plusDays(2), TaskStatus.Finished).let { task ->
            fakeTaskSummary.add(task)
            val taskDetail = TaskDetail(
                task.taskId,
                1,
                task.statusCategory,
                task.isStatusErrorNeedResend,
                task.dateTime,
                LocalDateTime.now(),
                EngineerInfo.createDemo(),
                CustomerInfo.createDemo(),
                task.requirement,
                emptyList(),
                emptyList(),
                "飲水機顯示水質異常，一直沒有消失",
                MaintainRecord.createDemo(),
                RepairRecord.createDemo(),
                ConfirmRecord.createDemoDone()
            )
            fakeTaskDetail.add(taskDetail)
        }

        createTask(localDateTime.plusDays(2), TaskStatus.Error, false).let { task ->
            fakeTaskSummary.add(task)
            val taskDetail = TaskDetail(
                task.taskId,
                1,
                task.statusCategory,
                task.isStatusErrorNeedResend,
                task.dateTime,
                LocalDateTime.now(),
                EngineerInfo.createDemo(),
                CustomerInfo.createDemo(),
                task.requirement,
                emptyList(),
                emptyList(),
                "飲水機顯示水質異常，一直沒有消失",
                MaintainRecord.createDemo(),
                RepairRecord.createDemo(),
                ConfirmRecord.createDemoError()
            )
            fakeTaskDetail.add(taskDetail)
        }


        createTask(localDateTime.plusDays(3), TaskStatus.Error, true).let { task ->
            fakeTaskSummary.add(task)
            val taskDetail = TaskDetail(
                task.taskId,
                1,
                task.statusCategory,
                task.isStatusErrorNeedResend,
                task.dateTime,
                LocalDateTime.now(),
                EngineerInfo.createDemo(),
                CustomerInfo.createDemo(),
                task.requirement,
                emptyList(),
                emptyList(),
                "飲水機顯示水質異常，一直沒有消失",
                MaintainRecord.createDemo(),
                RepairRecord.createDemo(),
                ConfirmRecord.createDemoNeedPrice()
            )
            fakeTaskDetail.add(taskDetail)
        }

        createTask(localDateTime.plusDays(3), TaskStatus.Finished).let { task ->
            fakeTaskSummary.add(task)
            val taskDetail = TaskDetail(
                task.taskId,
                1,
                task.statusCategory,
                task.isStatusErrorNeedResend,
                task.dateTime,
                LocalDateTime.now(),
                EngineerInfo.createDemo(),
                CustomerInfo.createDemo(),
                task.requirement,
                emptyList(),
                emptyList(),
                "飲水機顯示水質異常，一直沒有消失",
                MaintainRecord.createDemo(),
                RepairRecord.createDemo(),
                ConfirmRecord.createDemoDone()
            )
            fakeTaskDetail.add(taskDetail)
        }

        createTask(localDateTime.plusDays(3), TaskStatus.Finished).let { task ->
            fakeTaskSummary.add(task)
            val taskDetail = TaskDetail(
                task.taskId,
                1,
                task.statusCategory,
                task.isStatusErrorNeedResend,
                task.dateTime,
                LocalDateTime.now(),
                EngineerInfo.createDemo(),
                CustomerInfo.createDemo(),
                task.requirement,
                emptyList(),
                emptyList(),
                "飲水機顯示水質異常，一直沒有消失",
                MaintainRecord.createDemo(),
                RepairRecord.createDemo(),
                ConfirmRecord.createDemoDone()
            )
            fakeTaskDetail.add(taskDetail)
        }
    }

    override suspend fun getMonthTaskSummaryList(year: Int, month: Int): List<TaskSummary> {
        return fakeTaskSummary
    }

    override suspend fun getTaskDetail(taskId: Int): TaskDetail {
        return fakeTaskDetail.first { it.taskId == taskId }
    }

    private fun createTask(
        time: LocalDateTime,
        status: TaskStatus = TaskStatus.getRandom(),
        needResent: Boolean = Random.nextBoolean()
    ): TaskSummary {

        return TaskSummary(
            FakeData.createId(),
            time,
            "陳先生${Random.nextInt(0, 1000)}",
            "台中市烏日區高鐵五路156號",
            WorkOrderRequirement.getRandom(),
            TaskStatusCategory.fromStatus(status),
            needResent,
            status
        )
    }

    override suspend fun getMaintainOptions(): MaintainOptions {
        val options = serviceWrapper.getWorkOrderOption(2)

        val filterOptions = options.filter { it.group2 == "更換濾芯" }
            .map { FormOption(it.code, it.group2, it.item) }

        val basicMaintainOptions = options.filter { it.group2 == "基礎保養" }
            .map { FormOption(it.code, it.group2, it.item) }

        return MaintainOptions(filterOptions, basicMaintainOptions)
    }

    override suspend fun getRepairOptions(): RepairOptions {
        val options = serviceWrapper.getWorkOrderOption(3)

        val errorCodeOptions = options.filter { it.group2 == "故障代碼" }
            .filterNot { it.item == "其它" }
            .map { FormOption(it.code, it.group2, it.item) }

        val repairContentOptions = options.filter { it.group1 == "維修內容" }
            .filterNot { it.item == "非產品原因" }
            .map { FormOption(it.code, it.group2, it.item) }

        val changePartControl = options.filter { it.group1 == "更換零件" && it.group2 == "控制類" }
            .map { FormOption(it.code, it.group2, it.item) }

        val changePartHeat = options.filter { it.group1 == "更換零件" && it.group2 == "加熱類" }
            .map { FormOption(it.code, it.group2, it.item) }

        val changePartCool = options.filter { it.group1 == "更換零件" && it.group2 == "製冷類" }
            .map { FormOption(it.code, it.group2, it.item) }

        val changePartPipeline = options.filter { it.group1 == "更換零件" && it.group2 == "管路類" }
            .map { FormOption(it.code, it.group2, it.item) }

        val changePartRO = options.filter { it.group1 == "更換零件" && it.group2 == "RO機類" }
            .map { FormOption(it.code, it.group2, it.item) }

        return RepairOptions(
            errorCodeOptions,
            repairContentOptions,
            ChangePartOptions(
                changePartControl,
                changePartHeat,
                changePartCool,
                changePartPipeline,
                changePartRO
            )
        )
    }

    override suspend fun doneMaintainTask(parameters: DoneMaintainTaskUseCaseParam): Boolean {
        return false
    }

    override suspend fun doneRepairTask(parameters: DoneRepairTaskUseCaseParam): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun updateWorkTime(taskId: Int): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun doneInstallTask(parameters: DoneInstallTaskUseCaseParam): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getTaskHistory(parameters: Duration): List<TaskSummary> {
        TODO("Not yet implemented")
    }

    override suspend fun getInstallRecordList(duration: Duration): List<InstallRecord> {
        TODO("Not yet implemented")
    }
}