package com.pollux.digitalzoo.util

import android.util.Log
import kotlinx.coroutines.flow.*

inline fun <ResultType, RequestType> networkBoundResource(
    crossinline query: () -> Flow<ResultType>,
    crossinline fetch: suspend () -> RequestType,
    crossinline saveFetchResult: suspend (RequestType) -> Unit,
    crossinline shouldFetch: (ResultType) -> Boolean = { true },
    crossinline onFetchSuccess: () -> Unit = {  },
    crossinline onFetchFailed: (Throwable) -> Unit = {  }
) = flow<Resource<ResultType>> {
    val data = query().first()

    Log.d("query: ", "${data}")
    val flow = if (shouldFetch(data)) {
        emit(Resource.Loading(data))

        try {
            saveFetchResult(fetch())
            Log.d("saveFR: ", "Completed")
            onFetchSuccess()
            query().map {
                Resource.Success(it)
            }
        } catch (throwable: Throwable) {
            onFetchFailed(throwable)
            Log.d("onFF: ", "${throwable.message}")
            query().map { Resource.Error(throwable, it) }
        }
    } else {
        query().map { Resource.Success(it) }
    }

    emitAll(flow)
}