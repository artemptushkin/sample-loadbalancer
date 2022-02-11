package io.github.artemptushkin.demo.api

interface ProviderRegistry {
    fun register(serviceId: String): Provider
}