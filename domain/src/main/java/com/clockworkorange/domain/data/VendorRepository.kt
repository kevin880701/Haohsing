package com.clockworkorange.domain.data

import com.clockworkorange.domain.data.remote.ServiceWrapper
import com.clockworkorange.domain.usecase.vendor.Vendor
import javax.inject.Inject

interface VendorRepository {
    suspend fun getVendorList(): List<Vendor>
}

class VendorRepositoryImpl @Inject constructor(
    private val serviceWrapper: ServiceWrapper
) : VendorRepository{

    override suspend fun getVendorList(): List<Vendor> {
        return serviceWrapper.getVendorList().map { it.toVendor() }
    }

}