package io.github.artemptushkin.demo.provider

import io.github.artemptushkin.demo.api.Provider

class AlwaysAliveProvider(private val serviceId: String): Provider {
    override fun isAlive(): Boolean = true

    override fun getServiceId(): String = this.serviceId
}