package io.github.artemptushkin.demo.loadbalancer

import io.github.artemptushkin.demo.api.Provider

class RandomOrderLoadBalancer(maximumNumberOfProviders: Int, healthCheckInterval: Long, aliveChecksResurrection: Int) :
    AbstractLoadBalancer(maximumNumberOfProviders, healthCheckInterval, aliveChecksResurrection) {
    private val randomRange = (0 until maximumNumberOfProviders)

    override fun resolveProvider(): Provider {
        val currentPosition = randomRange.random()
        return aliveProviders()[currentPosition]
    }
}