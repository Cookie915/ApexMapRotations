package com.example.apexmaprotations.models

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*

sealed interface Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>
    class Failure<T>(val exception: Throwable? = null) : Resource<T>
    object Loading : Resource<Nothing>
}

fun <T> Flow<T>.asResource(): Flow<Resource<T>> {
    return this
        .map<T, Resource<T>> {
            Resource.Success(it)
        }
        .onStart { emit(Resource.Loading) }
        .catch { emit(Resource.Failure(it)) }
}

fun <R> Flow<R>.toStateFlow(coroutineScope: CoroutineScope, initialValue: R) =
    stateIn(coroutineScope, SharingStarted.Lazily, initialValue)