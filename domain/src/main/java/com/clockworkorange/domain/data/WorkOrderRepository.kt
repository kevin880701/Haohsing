package com.clockworkorange.domain.data

import android.content.Context
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.clockworkorange.domain.data.remote.ServiceWrapper
import com.clockworkorange.domain.data.remote.model.ApiEditWorkOrderParam
import com.clockworkorange.domain.data.remote.model.ApiFormOptionField
import com.clockworkorange.domain.data.remote.model.ApiFormOptionParam
import com.clockworkorange.domain.data.remote.model.ChangeFilterParam
import com.clockworkorange.domain.usecase.customer.CustomerAgency
import com.clockworkorange.domain.usecase.device.DrainStatus
import com.clockworkorange.domain.usecase.device.FinishEngineerPairUseCaseParam
import com.clockworkorange.domain.usecase.task.EngineerInfo
import com.clockworkorange.domain.usecase.task.FormOption
import com.clockworkorange.domain.usecase.workorder.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.random.Random

interface WorkOrderRepository {
    suspend fun createWorkOrder(parameters: CreateWorkOrderParam): Boolean
    suspend fun editWorkOrder(parameters: EditWorkOrderParam): Boolean
    suspend fun cancelWorkOrder(workOrderId: Int, date: Date): Boolean
    suspend fun getWorkOrderList(deviceId: Int, status: WorkOrderStatus): List<WorkOrderInfo>
    suspend fun getPendingWorkOrderDetail(workOrderId: Int): PendingWorkOrderDetail
    suspend fun getScheduleWorkOrderDetail(workOrderId: Int): ScheduledWorkOrderDetail
    suspend fun getErrorReasonList(): List<FormOption>
    suspend fun getFinishedWorkOrderDetail(workOrderId: Int): FinishedWorkOrderDetail
    suspend fun getCustomerList(): List<CustomerAgency>
    suspend fun finishEngineerPair(parameters: FinishEngineerPairUseCaseParam): Boolean
}

class WorkOrderRepositoryImpl(
    private val context: Context,
    private val serviceWrapper: ServiceWrapper): WorkOrderRepository{

    override suspend fun createWorkOrder(parameters: CreateWorkOrderParam): Boolean {
        val deviceId = parameters.deviceId
        val addWorkOrderResult = serviceWrapper.addWorkOrder(deviceId)
        val workOrderId = addWorkOrderResult.mId
        val workOrderType = parameters.workOrderRequirement.id

        val errorReason = parameters.errorReasons.map { ApiFormOptionParam(it.code, "1", "") }

        val param = ApiEditWorkOrderParam(
            workOrderId,
            parameters.userName,
            parameters.userPhone,
            workOrderType,
            "",
            parameters.deliveryTime,
            parameters.note,
            errorReason
        )

        parameters.photoVideoFiles
            .forEach { contentUriString ->

                val uri = contentUriString.toUri()
                val doc = DocumentFile.fromSingleUri(context, uri) ?: return@forEach
                val fileName = doc.name ?: return@forEach
                val mimeType = doc.type

                if (mimeType?.contains("image") == true){
                    val imageFile = FileUtils.compressImageFromContentUri(context, uri)
                    serviceWrapper.uploadWorkOrderImage(workOrderId, imageFile)
                }else if (mimeType?.contains("video") == true){
                    if (mimeType.contains("mp4")){//只上傳mp4檔，其他忽略
                        val tmpFile = FileUtils.copyFileFromContentUri(context, uri, fileName)
                        serviceWrapper.uploadWorkOrderImage(workOrderId, tmpFile)
                    }
                }
            }

        return serviceWrapper.editWorkOrder(param)
    }

    override suspend fun editWorkOrder(parameters: EditWorkOrderParam): Boolean {
        val workOrderId = parameters.workOrderId
        val workOrderType = parameters.workOrderRequirement.id

        val errorReason = parameters.errorReasons.map { ApiFormOptionParam(it.code, "1", "") }

        val param = ApiEditWorkOrderParam(
            workOrderId,
            parameters.userName,
            parameters.userPhone,
            workOrderType,
            "",
            parameters.deliveryTime,
            parameters.note,
            errorReason
        )

        parameters.photoVideoFiles
            .filter { !it.startsWith("http") }
            .forEach { contentUriString ->

                val uri = contentUriString.toUri()
                val doc = DocumentFile.fromSingleUri(context, uri) ?: return@forEach
                val fileName = doc.name ?: return@forEach
                val mimeType = doc.type

                if (mimeType?.contains("image") == true){
                    val imageFile = FileUtils.compressImageFromContentUri(context, uri)
                    serviceWrapper.uploadWorkOrderImage(workOrderId, imageFile)
                }else if (mimeType?.contains("video") == true){
                    if (mimeType.contains("mp4")){//只上傳mp4檔，其他忽略
                        val tmpFile = FileUtils.copyFileFromContentUri(context, uri, fileName)
                        serviceWrapper.uploadWorkOrderImage(workOrderId, tmpFile)
                    }
                }
            }

        return serviceWrapper.editWorkOrder(param)
    }

    override suspend fun cancelWorkOrder(workOrderId: Int, date: Date): Boolean {
        return serviceWrapper.cancelWorkOrder(workOrderId)
    }

    override suspend fun getWorkOrderList(deviceId: Int, status: WorkOrderStatus): List<WorkOrderInfo> {
        return when(status){
            WorkOrderStatus.Pending ,WorkOrderStatus.Schedule ->
                serviceWrapper.getDeviceWorkOrderList(deviceId, status.id).mapNotNull { it.toWorkOrderInfo() }
            WorkOrderStatus.Finished ->
                serviceWrapper.getDeviceWorkOrderList(deviceId, status.id)
                    .filter { it.status == 2 }.mapNotNull { it.toWorkOrderInfo() }
            WorkOrderStatus.Cancel ->
                serviceWrapper.getDeviceWorkOrderList(deviceId, WorkOrderStatus.Finished.id)
                    .filter { it.status == 3 }.mapNotNull { it.toWorkOrderInfo() }//取消屬於已完成，其status為3
        }
    }

    override suspend fun getPendingWorkOrderDetail(workOrderId: Int): PendingWorkOrderDetail {
        return serviceWrapper.getWorkOrderDetail(workOrderId).toPendingWorkOrderDetail()
    }

    override suspend fun getScheduleWorkOrderDetail(workOrderId: Int): ScheduledWorkOrderDetail {
        return serviceWrapper.getWorkOrderDetail(workOrderId).toScheduledWorkOrderDetail()
    }

    override suspend fun getErrorReasonList(): List<FormOption> {
        return serviceWrapper.getWorkOrderOption(3)
            .filter { it.group1 == "客戶反應內容" }
            .map { FormOption(it.code, it.group2, it.item) }
    }

    override suspend fun getFinishedWorkOrderDetail(workOrderId: Int): FinishedWorkOrderDetail {
        return serviceWrapper.getWorkOrderDetail(workOrderId).toFinishedWorkOrderDetail()
    }

    override suspend fun getCustomerList(): List<CustomerAgency> {
        return serviceWrapper.getCustomerList().map { it.toCustomer() }
    }

    override suspend fun finishEngineerPair(parameters: FinishEngineerPairUseCaseParam): Boolean {
        //上傳表單 /v1/device/register/form
        //region upload form
        val formField = mutableListOf<ApiFormOptionField>()
        with(parameters.pairDeviceInspection){
            tds?.let { formField.add(ApiFormOptionField.create("AA1", it)) }
            psi?.let { formField.add(ApiFormOptionField.create("AA2", it)) }
            checkReliefValve.let { formField.add(ApiFormOptionField.create("AA3", it)) }
            voltage?.let { formField.add(ApiFormOptionField.create("AB1", it)) }
            checkGround.let { formField.add(ApiFormOptionField.create("AB2", it)) }
            drainStatus?.let {
                when(it){
                    DrainStatus.DrainSmooth -> formField.add(ApiFormOptionField.create("AC1", true))
                    DrainStatus.DrainPuddle -> formField.add(ApiFormOptionField.create("AC2", true))
                    DrainStatus.DrainOverflow -> formField.add(ApiFormOptionField.create("AC3", true))
                }
            }
            installROFilter.let { formField.add(ApiFormOptionField.create("AD1", it)) }
            checkROWork.let { formField.add(ApiFormOptionField.create("AD2", it)) }
            checkIntakeWaterWork.let { formField.add(ApiFormOptionField.create("AE1", it)) }
            checkOutWaterWork.let { formField.add(ApiFormOptionField.create("AE2", it)) }
            checkHeatWork.let { formField.add(ApiFormOptionField.create("AE3", it)) }
            checkCoolWork.let { formField.add(ApiFormOptionField.create("AE4", it)) }
            checkFunctionWork.let { formField.add(ApiFormOptionField.create("AE5", it)) }

            checkPowerPlug.let { formField.add(ApiFormOptionField.create("AF1", it)) }
            checkPowerSwitch.let { formField.add(ApiFormOptionField.create("AF2", it)) }
            checkWaterSwitch.let { formField.add(ApiFormOptionField.create("AF3", it)) }
            checkBasicFunction.let { formField.add(ApiFormOptionField.create("AF4", it)) }
            checkBasicMaintain.let { formField.add(ApiFormOptionField.create("AF5", it)) }
            checkConnectService.let { formField.add(ApiFormOptionField.create("AF6", it)) }
            checkGuide.let { formField.add(ApiFormOptionField.create("AF7", it)) }
        }

        serviceWrapper.uploadInstallForm(parameters.deviceId, formField)
        //endregion

        //安裝濾心 /v1/device/filter
        //region install filter
        if (parameters.pairDeviceInspection.changedFilter1 != null){
            val filter = parameters.pairDeviceInspection.changedFilter1
            serviceWrapper.changeFilter(
                ChangeFilterParam(
                    parameters.deviceId, filter.type.code, filter.name,
                    filter.installDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    filter.lifeMonth
                )
            )
        }

        if (parameters.pairDeviceInspection.changedFilter2 != null){
            val filter = parameters.pairDeviceInspection.changedFilter2
            serviceWrapper.changeFilter(
                ChangeFilterParam(
                    parameters.deviceId, filter.type.code, filter.name,
                    filter.installDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    filter.lifeMonth
                )
            )
        }

        if (parameters.pairDeviceInspection.changedFilter3 != null){
            val filter = parameters.pairDeviceInspection.changedFilter3
            serviceWrapper.changeFilter(
                ChangeFilterParam(
                    parameters.deviceId, filter.type.code, filter.name,
                    filter.installDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    filter.lifeMonth
                )
            )
        }


        if (parameters.pairDeviceInspection.changedFilter4 != null){
            val filter = parameters.pairDeviceInspection.changedFilter4
            serviceWrapper.changeFilter(
                ChangeFilterParam(
                    parameters.deviceId, filter.type.code, filter.name,
                    filter.installDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    filter.lifeMonth
                )
            )
        }

        if (parameters.pairDeviceInspection.changedFilter5 != null){
            val filter = parameters.pairDeviceInspection.changedFilter5
            serviceWrapper.changeFilter(
                ChangeFilterParam(
                    parameters.deviceId, filter.type.code, filter.name,
                    filter.installDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    filter.lifeMonth
                )
            )
        }
        //endregion

        //上傳照片 /v1/device/register/image  //照片種類(1:完工照 2:龍頭照 3:漏水斷路器 4:水壓表)
        //region upload install photo
        if (parameters.pairDeviceInspection.productImageUri != null){
            val uri = parameters.pairDeviceInspection.productImageUri
            val doc = DocumentFile.fromSingleUri(context, uri)
            if (doc?.type?.contains("image") == true) {
                val imageFile = FileUtils.compressImageFromContentUri(context, uri)
                serviceWrapper.uploadInstallPhoto(parameters.deviceId, 1, imageFile)
            }
        }

        if (parameters.pairDeviceInspection.faucetImageUri != null){
            val uri = parameters.pairDeviceInspection.faucetImageUri
            val doc = DocumentFile.fromSingleUri(context, uri)
            if (doc?.type?.contains("image") == true) {
                val imageFile = FileUtils.compressImageFromContentUri(context, uri)
                serviceWrapper.uploadInstallPhoto(parameters.deviceId, 2, imageFile)
            }
        }

        if (parameters.pairDeviceInspection.breakerImageUri != null){
            val uri = parameters.pairDeviceInspection.breakerImageUri
            val doc = DocumentFile.fromSingleUri(context, uri)
            if (doc?.type?.contains("image") == true) {
                val imageFile = FileUtils.compressImageFromContentUri(context, uri)
                serviceWrapper.uploadInstallPhoto(parameters.deviceId, 3, imageFile)
            }
        }

        if (parameters.pairDeviceInspection.waterPressureGaugeImageUri != null){
            val uri = parameters.pairDeviceInspection.waterPressureGaugeImageUri
            val doc = DocumentFile.fromSingleUri(context, uri)
            if (doc?.type?.contains("image") == true) {
                val imageFile = FileUtils.compressImageFromContentUri(context, uri)
                serviceWrapper.uploadInstallPhoto(parameters.deviceId, 4, imageFile)
            }
        }
        //endregion


        // when use qr code to add device
        if (parameters.mac != null) {
            serviceWrapper.pairDeviceUser(
                parameters.sn,
                parameters.mac,
                parameters.name,
                customerId = parameters.customerId,
                customerAddress = parameters.customerAddress
            )
        }

        return true
    }
}

data class FakeOrder(
    val deviceId: Int,
    val orderId: Int,
    val status: WorkOrderStatus,
    val param: CreateWorkOrderParam,
    val date: LocalDateTime
)

class FakeWorkOrderRepository(val serviceWrapper: ServiceWrapper) : WorkOrderRepository {

    private val fakeWorkOrder = hashMapOf<Int, FakeOrder>()

    override suspend fun createWorkOrder(parameters: CreateWorkOrderParam): Boolean {

        val fakeOrder = FakeOrder(
            parameters.deviceId,
            Random.nextInt(),
            WorkOrderStatus.Pending,
            parameters,
            LocalDateTime.now()
        )

        fakeWorkOrder[fakeOrder.orderId] = fakeOrder

        return true
    }

    override suspend fun editWorkOrder(parameters: EditWorkOrderParam): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun cancelWorkOrder(workOrderId: Int, date: Date): Boolean {
        val order = fakeWorkOrder[workOrderId] ?: return false
        fakeWorkOrder[workOrderId] = order.copy(status = WorkOrderStatus.Cancel, date = LocalDateTime.now())
        return true
    }

    override suspend fun getWorkOrderList(deviceId: Int, status: WorkOrderStatus): List<WorkOrderInfo> {
        return fakeWorkOrder.values
            .filter { it.deviceId == deviceId && it.status == status }
            .map {
                WorkOrderInfo(
                    it.orderId,
                    it.status,
                    it.param.workOrderRequirement,
                    it.date
                )
            }
    }

    override suspend fun getPendingWorkOrderDetail(workOrderId: Int): PendingWorkOrderDetail {
        val order = fakeWorkOrder[workOrderId] ?: throw RuntimeException("not found")
        return PendingWorkOrderDetail(
            workOrderId,
            order.deviceId,
            order.param.workOrderRequirement,
            emptyList(),
            order.param.deliveryTime,
            order.param.errorReasons,
            order.param.note
        )
    }

    override suspend fun getScheduleWorkOrderDetail(workOrderId: Int): ScheduledWorkOrderDetail {
        val order = fakeWorkOrder[workOrderId] ?: throw RuntimeException("not found")
        return ScheduledWorkOrderDetail(
            workOrderId,
            order.deviceId,
            EngineerInfo(1, "---", "---", "---"),
            LocalDateTime.now(),
            order.param.workOrderRequirement,
            order.param.errorReasons,
            order.param.note
        )
    }

    override suspend fun getErrorReasonList(): List<FormOption> {
        return serviceWrapper.getWorkOrderOption(3)
            .filter { it.group1 == "客戶反應內容" }
            .map { FormOption(it.code, it.group2, it.item) }
    }

    override suspend fun getFinishedWorkOrderDetail(workOrderId: Int): FinishedWorkOrderDetail {
        TODO("Not yet implemented")
    }

    override suspend fun getCustomerList(): List<CustomerAgency> {
        TODO("Not yet implemented")
    }

    override suspend fun finishEngineerPair(parameters: FinishEngineerPairUseCaseParam): Boolean {
        TODO("Not yet implemented")
    }
}