package io.github.artemptushkin.demo.api

interface LoadBalancer {
    fun register(providerRegistry: ProviderRegistry): Provider
    fun get(): Provider
    fun exclude(provider: Provider)
    fun exclude(serviceId: String)
}