package com.clockworkorange.domain.usecase.vendor

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.VendorRepository
import com.clockworkorange.domain.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class GetVendorListUseCase @Inject constructor(
    private val vendorRepository: VendorRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<Unit, List<Vendor>>(dispatcher) {

    override suspend fun execute(parameters: Unit): List<Vendor> {
        return vendorRepository.getVendorList()
    }
}

data class Vendor(
    val id: Int,
    val name: String
)