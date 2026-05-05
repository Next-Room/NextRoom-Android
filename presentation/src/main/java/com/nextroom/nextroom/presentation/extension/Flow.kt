package com.nextroom.nextroom.presentation.extension

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

@Suppress("UNCHECKED_CAST")
fun <T1, T2, T3, T4, T5, T6, R> combine(
    flow1: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    transform: suspend (T1, T2, T3, T4, T5, T6) -> R,
): Flow<R> = combine(
    combine(flow1, flow2, flow3, flow4, flow5) { t1, t2, t3, t4, t5 ->
        arrayOf<Any?>(t1, t2, t3, t4, t5)
    },
    flow6,
) { arr, t6 ->
    transform(arr[0] as T1, arr[1] as T2, arr[2] as T3, arr[3] as T4, arr[4] as T5, t6)
}