package io.github.artemptushkin.demo.loadbalancer

import io.github.artemptushkin.demo.api.LoadBalancer
import io.github.artemptushkin.demo.exception.ProviderRegistryException
import io.github.artemptushkin.demo.provider.AlwaysAliveProviderRegistry
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.sameInstance
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

abstract class AbstractLoadBalancerTest {

    abstract fun createInstance(maximumNumberOfProviders: Int): LoadBalancer

    @Test
    fun itRegistersProvider() {
        assertThat(
            createInstance(2).register(AlwaysAliveProviderRegistry()), Matchers.notNullValue()
        )
    }

    @Test
    fun itFailsToRegisterMoreThanAllowedProviders() {
        val loadBalancer = createInstance(1)
        loadBalancer.register(AlwaysAliveProviderRegistry())

        assertThrows<ProviderRegistryException> { loadBalancer.register(AlwaysAliveProviderRegistry()) }
    }

    @Test
    fun itFailsToGetWhenProviderHasBeenExcluded() {
        val loadBalancer = createInstance(1)
        val provider = loadBalancer.register(AlwaysAliveProviderRegistry())
        loadBalancer.exclude(provider)

        assertThrows<ProviderRegistryException> { loadBalancer.get() }
    }

    @Test
    fun itReturnsRegisteredProvider() {
        val loadBalancer = createInstance(1)
        val provider = loadBalancer.register(AlwaysAliveProviderRegistry())
        val found = loadBalancer.get()

        assertThat(found, sameInstance(provider))
    }
}