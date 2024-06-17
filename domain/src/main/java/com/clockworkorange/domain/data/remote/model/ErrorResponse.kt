package com.clockworkorange.domain.data.remote.model

import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("msg")
    val msg: String,
    @SerializedName("errorMsg")
    val errorMsg: String
)
//{
//  "status": "fail",
//  "msg": "Token不存在!",
//  "errorMsg": "Token不存在!"
//}

//http code 500
//{"status":"fail","msg":"非預期錯誤!","errorMsg":"Current request is not a multipart request"}