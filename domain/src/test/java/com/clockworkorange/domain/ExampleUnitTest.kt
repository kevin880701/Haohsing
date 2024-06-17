package com.clockworkorange.domain

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testSplit(){
       val data =  "1,3,5".split(",").map { it.toInt() }
        print(data)
    }

    enum class TestEnum{
        @SerializedName("0")
        Success,
        @SerializedName("1")
        Fail,
        @SerializedName("2")
        Unknown;

    }

    @Test
    fun testGsonSerializedEnum(){
        val data = hashMapOf<String, Any>()
        data["v1"] = TestEnum.Success
        data["v2"] = TestEnum.Fail
        data["v3"] = TestEnum.Unknown

        val jsonString = Gson().toJson(data)

        print(jsonString)
    }
}