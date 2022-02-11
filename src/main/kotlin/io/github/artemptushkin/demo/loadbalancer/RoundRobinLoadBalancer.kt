package io.github.artemptushkin.demo.loadbalancer

import io.github.artemptushkin.demo.api.LoadBalancer
import io.github.artemptushkin.demo.api.Provider
import io.github.artemptushkin.demo.api.ProviderRegistry
import io.github.artemptushkin.demo.exception.ProviderRegistryException
import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

class RoundRobinLoadBalancer(private val maximumNumberOfProviders: Int, private val healthCheckInterval: Long) : LoadBalancer {
    private val providers: MutableList<Provider> = Collections.synchronizedList(mutableListOf())
    private val position = AtomicInteger()

    init {
        CoroutineScope(Dispatchers.IO)
            .launch {
                while (isActive) {
                    providers.removeIf { !it.isAlive() }
                    delay(healthCheckInterval)
                }
            }
    }

    override fun register(providerRegistry: ProviderRegistry): Provider {
        if (maximumNumberOfProviders == providers.size) {
            throw ProviderRegistryException("The maximum amount of registered providers - $maximumNumberOfProviders has been reached")
        }
        val provider = providerRegistry.register(SERVICE_ID_PATTERN.format(providers.size + 1))
        providers.add(provider)
        return provider
    }

    override fun exclude(provider: Provider) {
        providers.removeIf { it.getServiceId() == provider.getServiceId() }
    }

    override fun exclude(serviceId: String) {
        providers.removeIf { it.getServiceId() == serviceId }
    }

    override fun get(): Provider {
        if (providers.isEmpty()) {
            throw ProviderRegistryException("No providers have been registered")
        }
        if (position.get() == providers.size) {
            position.set(0)
        }
        val currentPosition = position.getAndIncrement()
        return providers[currentPosition]
    }

    private companion object {
        const val SERVICE_ID_PATTERN = "provider-%s"
    }
}