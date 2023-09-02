package com.nexters.nextroom.data.network

import com.nexters.nextroom.domain.model.Result
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class ResultCallAdapterFactory : CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit,
    ): CallAdapter<*, *>? {
        val rawType = getRawType(returnType)
        require(returnType is ParameterizedType) {
            val name = parseTypeName(returnType)
            "Return 타입은 $name<Foo> 또는 $name<out Foo>로 정의되어야 합니다. "
        }
        return when (rawType) {
            Call::class.java -> resultAdapter(returnType)
            else -> null
        }
    }

    private fun resultAdapter(
        returnType: ParameterizedType,
    ): CallAdapter<Type, out Call<out Any>>? {
        val wrapperType = getParameterUpperBound(0, returnType)
        return when (getRawType(wrapperType)) {
            Result::class.java -> {
                val bodyType = extractReturnType(wrapperType, returnType)
                ResultCallAdapter(bodyType)
            }

            else -> null
        }
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun extractReturnType(
        wrapperType: Type,
        returnType: ParameterizedType,
    ): Type {
        require(wrapperType is ParameterizedType) {
            val name = parseTypeName(returnType)
            "Return 타입은 $name<ResponseBody>로 정의되어야 합니다."
        }
        return getParameterUpperBound(0, wrapperType)
    }

    private fun parseTypeName(type: Type): String {
        return type.toString()
            .split(".")
            .last()
    }
}
