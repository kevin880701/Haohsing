package com.clockworkorange.domain.usecase.device

import android.net.Uri
import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.WorkOrderRepository
import com.clockworkorange.domain.data.remote.model.ApiCustomer
import com.clockworkorange.domain.di.IoDispatcher
import com.clockworkorange.domain.usecase.task.ChangedFilter
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class FinishEngineerPairUseCase @Inject constructor(
    private val workOrderRepository: WorkOrderRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : UseCase<FinishEngineerPairUseCaseParam, Boolean>(dispatcher) {

    override suspend fun execute(parameters: FinishEngineerPairUseCaseParam): Boolean {
        return workOrderRepository.finishEngineerPair(parameters)
    }
}

data class FinishEngineerPairUseCaseParam(
    val deviceId: Int,
    val sn: String?,
    val mac: String?,
    val name: String,
    val pairDeviceInspection: DeviceInspection,
    val customerId: Int?,
    val customerAddress: String?
)

data class DeviceInspection(
    val tds: String? = null,
    val psi: String? = null,
    val checkReliefValve: Boolean = false,
    val voltage: String? = null,
    val checkGround: Boolean = false,
    val drainStatus: DrainStatus? = null,
    val installROFilter: Boolean = false,
    val checkROWork: Boolean = false,
    val changedFilter1: ChangedFilter? = null,
    val changedFilter2: ChangedFilter? = null,
    val changedFilter3: ChangedFilter? = null,
    val changedFilter4: ChangedFilter? = null,
    val changedFilter5: ChangedFilter? = null,
    val checkIntakeWaterWork: Boolean = false,
    val checkOutWaterWork: Boolean = false,
    val checkHeatWork: Boolean = false,
    val checkCoolWork: Boolean = false,
    val checkFunctionWork: Boolean = false,
    val checkPowerPlug: Boolean = false,
    val checkPowerSwitch: Boolean = false,
    val checkWaterSwitch: Boolean = false,
    val checkBasicFunction: Boolean = false,
    val checkBasicMaintain: Boolean = false,
    val checkConnectService: Boolean = false,
    val checkGuide: Boolean = false,
    val productImageUri: Uri? = null,
    val faucetImageUri: Uri? = null,
    val breakerImageUri: Uri? = null,
    val waterPressureGaugeImageUri: Uri? = null
)

enum class DrainStatus {
    DrainSmooth, DrainPuddle, DrainOverflow;
}