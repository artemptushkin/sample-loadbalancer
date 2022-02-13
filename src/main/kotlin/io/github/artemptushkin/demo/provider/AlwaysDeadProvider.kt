package io.github.artemptushkin.demo.provider

import io.github.artemptushkin.demo.api.Provider

class AlwaysDeadProvider(private val serviceId: String): Provider {
    override fun isAlive(): Boolean = false

    override fun getServiceId(): String = this.serviceId
}