package com.clockworkorange.domain.usecase.device

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.DeviceRepository
import com.clockworkorange.domain.di.IoDispatcher
import com.clockworkorange.domain.usecase.customer.CustomerAgency
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class SearchDeviceByMACUseCase @Inject constructor(
    private val deviceRepository: DeviceRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<String, SearchedDevice>(dispatcher) {

    override suspend fun execute(parameters: String): SearchedDevice {
        return deviceRepository.searchDeviceByMAC(parameters)
    }
}

data class SearchedDevice(
    val deviceId: Int,
    val sn: String?,
    val mac: String,
    val name: String?,
    val modelName: String,
    val userId: Int?,
    val modelImage: String,
    val installAddress: String?,
    val vendorName: String?,
    val customerAgency: CustomerAgency?,
    val isRegisterWarranty: Boolean
)