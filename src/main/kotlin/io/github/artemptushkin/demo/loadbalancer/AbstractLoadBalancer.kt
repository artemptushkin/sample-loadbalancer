package io.github.artemptushkin.demo.loadbalancer

import io.github.artemptushkin.demo.api.LoadBalancer
import io.github.artemptushkin.demo.api.Provider
import io.github.artemptushkin.demo.api.ProviderRegistry
import io.github.artemptushkin.demo.exception.ProviderRegistryException
import kotlinx.coroutines.*
import java.util.*

abstract class AbstractLoadBalancer(
    private val maximumNumberOfProviders: Int,
    private val healthCheckInterval: Long,
    private val aliveChecksResurrection: Int
) : LoadBalancer {
    private val providers: MutableMap<Provider, Int> = Collections.synchronizedMap(LinkedHashMap())
    private val heartBeater = HeartBeater(aliveChecksResurrection)

    init {
        CoroutineScope(Dispatchers.IO)
            .launch {
                while (isActive) {
                    heartBeater.beat(providers)
                    delay(healthCheckInterval)
                }
            }
    }

    protected fun aliveProviders(): MutableList<Provider> {
        return providers
            .filter { heartBeater.isAlive(it.value) }
            .map { it.key }
            .toMutableList()
    }

    abstract fun resolveProvider(): Provider

    override fun register(providerRegistry: ProviderRegistry): Provider {
        if (maximumNumberOfProviders == providers.size) {
            throw ProviderRegistryException("The maximum amount of registered providers - $maximumNumberOfProviders has been reached")
        }
        val provider = providerRegistry.register(SERVICE_ID_PATTERN.format(providers.size + 1))
        providers[provider] = aliveChecksResurrection
        return provider
    }

    override fun exclude(provider: Provider) {
        providers.remove(provider)
    }

    override fun exclude(serviceId: String) {
        providers.entries.removeIf { it.key.getServiceId() == serviceId }
    }

    override fun get(): Provider {
        if (aliveProviders().isEmpty()) {
            throw ProviderRegistryException("No providers have been registered")
        }
        return this.resolveProvider()
    }

    private companion object {
        const val SERVICE_ID_PATTERN = "provider-%s"
    }
}