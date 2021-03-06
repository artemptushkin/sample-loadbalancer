package io.github.artemptushkin.demo.loadbalancer

import io.github.artemptushkin.demo.api.HeartBeater
import io.github.artemptushkin.demo.api.Provider

/**
 * This class can be inner io.github.artemptushkin.demo.loadbalancer.AbstractLoadBalancer but externalized on purpose of testing
 */
class ResurrectionHeartBeater(private val aliveChecksResurrection: Int): HeartBeater {

    override fun beat(providers: MutableMap<Provider, Int>) {
        providers.replaceAll { provider, currentCounter ->
            if (!provider.isAlive()) {
                0
            } else {
                if (currentCounter + 1 > aliveChecksResurrection) aliveChecksResurrection else currentCounter + 1
            }
        }
    }

    fun isAlive(currentHealthCounter: Int) = currentHealthCounter >= aliveChecksResurrection
}