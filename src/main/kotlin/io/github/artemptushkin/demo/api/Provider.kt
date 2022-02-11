package io.github.artemptushkin.demo.api

interface Provider {
    fun isAlive(): Boolean
    fun getServiceId(): String
}
