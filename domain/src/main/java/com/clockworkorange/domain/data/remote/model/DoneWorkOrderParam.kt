package com.clockworkorange.domain.data.remote.model

import com.google.gson.annotations.SerializedName

data class DoneWorkOrderParam(
    @SerializedName("m_id")
    val mId: Int,
    @SerializedName("status")
    val status: Int,//狀態(2:完成 3:取消 4:異常/需重派)
    @SerializedName("work")
    val work: Int,//是否進行維修(0:否 1:是)
    @SerializedName("fee")
    var fee: String,
    @SerializedName("continuance")
    var isErrorNeedReSent: Int,
    @SerializedName("error_reason")
    var errorReason: String,
    @SerializedName("values")
    var values: List<ApiFormOptionField>
)

data class ApiFormOptionField(
    @SerializedName("code")
    val code: String,
    @SerializedName("value")
    val value: String = "1", //1 for 勾選/填寫
    @SerializedName("remark")
    val remark: String = ""
){
    companion object{
        fun create(code: String, value: String): ApiFormOptionField{
            return ApiFormOptionField(code, value)
        }

        fun create(code: String, check: Boolean): ApiFormOptionField{
            return ApiFormOptionField(code, value = if (check) "1" else "0")
        }
    }
}