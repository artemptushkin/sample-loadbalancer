package io.github.artemptushkin.demo.test.provider

import io.github.artemptushkin.demo.api.Provider
import io.github.artemptushkin.demo.api.ProviderRegistry

class AlwaysDeadProviderRegistry(private val capacity: Int): ProviderRegistry {
    override fun register(serviceId: String): Provider {
        return AlwaysDeadProvider(serviceId, capacity)
    }
}