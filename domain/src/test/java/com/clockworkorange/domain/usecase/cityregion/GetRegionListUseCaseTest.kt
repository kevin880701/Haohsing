package com.clockworkorange.domain.usecase.cityregion

import com.clockworkorange.domain.MainCoroutineRule
import com.clockworkorange.domain.Result
import com.clockworkorange.domain.data
import com.clockworkorange.domain.data.CityRegionRepository
import kotlinx.coroutines.test.runTest
import org.junit.*

import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner.StrictStubs::class)
class GetRegionListUseCaseTest {

    @get:Rule
    var coroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var cityRegionRepository: CityRegionRepository

    @Test
    fun testLoadData() = runTest{

        val targetCity = "city1"
        val regionList = listOf("area1", "area2", "area3")

        Mockito.`when`(cityRegionRepository.getRegionList(targetCity))
            .thenReturn(regionList)

        val useCase = GetRegionListUseCase(
            cityRegionRepository,
            coroutineRule.testDispatcher
        )

        val result = useCase.invoke(targetCity)

        verify(cityRegionRepository).getRegionList(targetCity)
        Assert.assertTrue(result is Result.Success)
        Assert.assertArrayEquals(regionList.toTypedArray(), result.data!!.toTypedArray())

    }


}