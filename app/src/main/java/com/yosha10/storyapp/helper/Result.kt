package com.yosha10.storyapp.helper

sealed class Result<out R> private constructor() {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val error: Event<String>) : Result<Nothing>()
    object Loading : Result<Nothing>()
}