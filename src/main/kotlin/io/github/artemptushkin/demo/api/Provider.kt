package io.github.artemptushkin.demo.api

interface Provider {
    fun isAlive(): Boolean
    fun isBusy(): Boolean
    fun getServiceId(): String
    suspend fun doRequest()
}
