package io.github.artemptushkin.demo.loadbalancer

import io.github.artemptushkin.demo.api.LoadBalancer
import io.github.artemptushkin.demo.exception.ProviderRegistryException
import io.github.artemptushkin.demo.provider.AlwaysAliveProviderRegistry
import io.github.artemptushkin.demo.provider.RandomFailureProviderRegistry
import org.awaitility.Awaitility
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.sameInstance
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Duration

abstract class AbstractLoadBalancerTest {

    abstract fun createInstance(maximumNumberOfProviders: Int, healthCheckInterval: Long): LoadBalancer

    @Test
    fun itRegistersProvider() {
        assertThat(
            createInstance(2, 300).register(AlwaysAliveProviderRegistry()), Matchers.notNullValue()
        )
    }

    @Test
    fun itFailsToRegisterMoreThanAllowedProviders() {
        val loadBalancer = createInstance(1, 300)
        loadBalancer.register(AlwaysAliveProviderRegistry())

        assertThrows<ProviderRegistryException> { loadBalancer.register(AlwaysAliveProviderRegistry()) }
    }

    @Test
    fun itFailsToGetWhenProviderHasBeenExcluded() {
        val loadBalancer = createInstance(1, 300)
        val provider = loadBalancer.register(AlwaysAliveProviderRegistry())
        loadBalancer.exclude(provider)

        assertThrows<ProviderRegistryException> { loadBalancer.get() }
    }

    @Test
    fun itReturnsRegisteredProvider() {
        val loadBalancer = createInstance(1, 300)
        val provider = loadBalancer.register(AlwaysAliveProviderRegistry())
        val found = loadBalancer.get()

        assertThat(found, sameInstance(provider))
    }

    @Test
    fun itRemovesDeadProviders() {
        val loadBalancer = createInstance(2, 200)
        loadBalancer.register(RandomFailureProviderRegistry())
        loadBalancer.register(RandomFailureProviderRegistry())

        Awaitility.await()
            .during(Duration.ofSeconds(5))
            .pollDelay(Duration.ofMillis(300))
            .untilAsserted {
                assertThrows<ProviderRegistryException> { loadBalancer.get() }
            }
    }
}