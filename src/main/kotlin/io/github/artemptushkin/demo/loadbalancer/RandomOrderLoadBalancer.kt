package io.github.artemptushkin.demo.loadbalancer

import io.github.artemptushkin.demo.api.Provider

class RandomOrderLoadBalancer(maximumNumberOfProviders: Int, healthCheckInterval: Long, aliveChecksResurrection: Int) :
    AbstractLoadBalancer(maximumNumberOfProviders, healthCheckInterval, aliveChecksResurrection) {

    override fun resolveProvider(aliveProviders: List<Provider>): Provider {
        return aliveProviders[aliveProviders.indices.random()]
    }
}