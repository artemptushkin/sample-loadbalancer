package io.github.artemptushkin.demo.provider

import io.github.artemptushkin.demo.api.Provider

/**
 * It fails in 10% of chance. Designed on the demo purposes
 */
class RandomFailureProvider(private val serviceId: String): Provider {
    override fun isAlive(): Boolean = Math.random() * 100 <= PROBABILITY

    override fun getServiceId(): String = this.serviceId

    private companion object {
        const val PROBABILITY: Float = 10.0F
    }
}