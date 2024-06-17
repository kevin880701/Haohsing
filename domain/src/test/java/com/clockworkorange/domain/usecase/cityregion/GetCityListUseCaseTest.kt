package com.clockworkorange.domain.usecase.cityregion

import com.clockworkorange.domain.MainCoroutineRule
import com.clockworkorange.domain.Result
import com.clockworkorange.domain.data
import com.clockworkorange.domain.data.CityRegionRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner.StrictStubs::class)
class GetCityListUseCaseTest {

    @get:Rule
    var coroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var cityRegionRepository: CityRegionRepository

    @Test
    fun testLoadEmptyData() = runTest {

        Mockito.`when`(cityRegionRepository.getCityList())
            .thenReturn(emptyList())

        val useCase = GetCityListUseCase(
            cityRegionRepository,
            coroutineRule.testDispatcher
        )

        val result = useCase.invoke(Unit)

        verify(cityRegionRepository).getCityList()
        Assert.assertTrue(result is Result.Success)
        Assert.assertTrue(result.data!!.isEmpty())
    }

    @Test
    fun testLoadNormalData() = runTest {

        val cityList = listOf("台北市", "新北市", "彰化市")

        Mockito.`when`(cityRegionRepository.getCityList())
            .thenReturn(cityList)

        val useCase = GetCityListUseCase(
            cityRegionRepository,
            coroutineRule.testDispatcher
        )

        val result = useCase.invoke(Unit)

        verify(cityRegionRepository).getCityList()
        Assert.assertTrue(result is Result.Success)
        Assert.assertArrayEquals(cityList.toTypedArray(), result.data!!.toTypedArray())
    }

}