package com.clockworkorange.domain.entity

data class UserInfo(
    val id: Int,
    val name: String,
    val email: String,
    val image: String,
    val phone: String,
    val city: String,
    val region: String,
    val role: Role,
    val vendorName: String?,
    val vendorId: Int?
){
    enum class Role{
        User, Owner, Manager, Engineer, Vendor, Admin, None;

        companion object{
            fun fromName(name: String): Role{
                return when(name){
                    "一般使用者" -> User
                    "產品擁有者" -> Owner
                    "進階管理者" -> Manager
                    "技術員" -> Engineer
                    "經銷商" -> Vendor
                    "系統管理者" -> Admin
                    else -> None
                }
            }
        }
    }
}
