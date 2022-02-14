package io.github.artemptushkin.demo.test.provider

import io.github.artemptushkin.demo.api.Provider
import kotlinx.coroutines.delay
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration.Companion.seconds

/**
 * It fails in 30% of chance. Designed on the demo purposes
 */
class RandomFailureProvider(private val serviceId: String, private val capacity: Int): Provider {
    private val currentRequestsHandled = AtomicInteger()
    override fun isAlive(): Boolean = Math.random() * 100 <= PROBABILITY

    override fun isBusy(): Boolean = currentRequestsHandled.get() >= capacity

    override fun getServiceId(): String = this.serviceId

    override suspend fun doRequest() {
        currentRequestsHandled.incrementAndGet()
        delay(1.0.seconds)
        currentRequestsHandled.decrementAndGet()
    }

    private companion object {
        const val PROBABILITY: Float = 30.0F
    }
}