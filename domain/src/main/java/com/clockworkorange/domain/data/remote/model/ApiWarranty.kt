package com.clockworkorange.domain.data.remote.model
import com.google.gson.annotations.SerializedName


data class ApiWarranty(
    @SerializedName("warranty_name")
    val warrantyName: String?,
    @SerializedName("warranty_email")
    val warrantyEmail: String?,
    @SerializedName("warranty_tel")
    val warrantyTel: String?,
    @SerializedName("inv_date")
    val invDate: String?
) {
    fun isWarrantyExist() = warrantyName != null && warrantyEmail !=null && warrantyTel != null && invDate != null
}