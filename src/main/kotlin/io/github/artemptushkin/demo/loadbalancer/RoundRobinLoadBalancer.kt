package io.github.artemptushkin.demo.loadbalancer

import io.github.artemptushkin.demo.api.Provider
import java.util.concurrent.atomic.AtomicInteger

class RoundRobinLoadBalancer(maximumNumberOfProviders: Int, healthCheckInterval: Long, aliveChecksResurrection: Int) :
    AbstractLoadBalancer(maximumNumberOfProviders, healthCheckInterval, aliveChecksResurrection) {
    private val position = AtomicInteger()

    override fun resolveProvider(aliveProviders: List<Provider>): Provider {
        if (position.get() == aliveProviders.size) {
            position.set(0)
        }
        return aliveProviders[position.getAndIncrement()]
    }
}