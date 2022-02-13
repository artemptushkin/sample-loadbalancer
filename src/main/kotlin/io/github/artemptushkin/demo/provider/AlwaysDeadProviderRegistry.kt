package io.github.artemptushkin.demo.provider

import io.github.artemptushkin.demo.api.Provider
import io.github.artemptushkin.demo.api.ProviderRegistry

class AlwaysDeadProviderRegistry: ProviderRegistry {
    override fun register(serviceId: String): Provider {
        return AlwaysDeadProvider(serviceId)
    }
}