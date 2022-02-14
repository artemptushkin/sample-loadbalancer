package io.github.artemptushkin.demo.test.provider

import io.github.artemptushkin.demo.api.Provider
import kotlinx.coroutines.delay
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration.Companion.seconds

class AlwaysAliveProvider(private val serviceId: String, private val capacity: Int): Provider {
    private val currentRequestsHandled = AtomicInteger()
    override fun isAlive(): Boolean = true

    override fun isBusy(): Boolean = currentRequestsHandled.get() >= capacity

    override fun getServiceId(): String = this.serviceId

    override suspend fun doRequest() {
        currentRequestsHandled.incrementAndGet()
        delay(1.0.seconds)
        currentRequestsHandled.decrementAndGet()
    }
}