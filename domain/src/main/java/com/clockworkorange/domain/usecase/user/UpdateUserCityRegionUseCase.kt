package com.clockworkorange.domain.usecase.user

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.UserRepository
import com.clockworkorange.domain.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class UpdateUserCityRegionUseCase @Inject constructor(
    private val userRepository: UserRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<UpdateUserCityRegionParam, Boolean>(dispatcher){

    override suspend fun execute(param: UpdateUserCityRegionParam): Boolean {
        return userRepository.updateCityRegion(param.city, param.region)
    }
}

data class UpdateUserCityRegionParam(
    val city: String,
    val region: String
)