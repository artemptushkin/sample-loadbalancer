package io.github.artemptushkin.demo.loadbalancer

import io.github.artemptushkin.demo.api.LoadBalancer
import io.github.artemptushkin.demo.exception.ProviderLoadException
import io.github.artemptushkin.demo.exception.ProviderRegistryException
import io.github.artemptushkin.demo.test.provider.AlwaysAliveProviderRegistry
import io.github.artemptushkin.demo.test.provider.AlwaysDeadProviderRegistry
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.awaitility.Awaitility
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.sameInstance
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Duration
import kotlin.time.Duration.Companion.seconds

abstract class AbstractLoadBalancerTest {

    abstract fun createInstance(maximumNumberOfProviders: Int, healthCheckInterval: Long): LoadBalancer

    @Test
    fun itRegistersProvider() {
        assertThat(
            createInstance(2, 300).register(AlwaysAliveProviderRegistry(capacity = 3)), Matchers.notNullValue()
        )
    }

    @Test
    fun itFailsToRegisterMoreThanAllowedProviders() {
        val loadBalancer = createInstance(1, 300)
        loadBalancer.register(AlwaysAliveProviderRegistry(capacity = 3))

        assertThrows<ProviderRegistryException> { loadBalancer.register(AlwaysAliveProviderRegistry(capacity = 3)) }
    }

    @Test
    fun itFailsToGetWhenProviderHasBeenExcluded() {
        val loadBalancer = createInstance(1, 300)
        val provider = loadBalancer.register(AlwaysAliveProviderRegistry(capacity = 3))
        loadBalancer.exclude(provider)

        assertThrows<ProviderRegistryException> { loadBalancer.get() }
    }

    @Test
    fun itReturnsRegisteredProvider() {
        val loadBalancer = createInstance(1, 300)
        val provider = loadBalancer.register(AlwaysAliveProviderRegistry(capacity = 3))
        val found = loadBalancer.get()

        assertThat(found, sameInstance(provider))
    }

    @Test
    fun itRemovesDeadProviders() {
        val loadBalancer = createInstance(2, 100)
        loadBalancer.register(AlwaysDeadProviderRegistry(capacity = 3))
        loadBalancer.register(AlwaysDeadProviderRegistry(capacity = 3))

        Awaitility.await()
            .during(Duration.ofSeconds(5))
            .pollDelay(Duration.ofMillis(200))
            .untilAsserted {
                assertThrows<ProviderRegistryException> { loadBalancer.get() }
            }
    }

    @Test
    fun itThrowsExceptionWhenAllProvidersAreBusy() {
        val capacity = 2
        val loadBalancer = createInstance(2, 200)
        loadBalancer.register(AlwaysAliveProviderRegistry(capacity))

        runBlocking {
            for (i in 0 until capacity) {
                async { loadBalancer.get().doRequest() }
            }

            delay(0.5.seconds)

            assertThrows<ProviderLoadException> { loadBalancer.get() }
        }
    }
}