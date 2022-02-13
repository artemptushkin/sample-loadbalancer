package io.github.artemptushkin.demo.loadbalancer

import io.github.artemptushkin.demo.api.LoadBalancer
import io.github.artemptushkin.demo.provider.AlwaysAliveProviderRegistry
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.sameInstance
import org.junit.jupiter.api.Test

class RoundRobinLoadBalancerTest : AbstractLoadBalancerTest() {
    override fun createInstance(maximumNumberOfProviders: Int, healthCheckInterval: Long): LoadBalancer =
        RoundRobinLoadBalancer(maximumNumberOfProviders, healthCheckInterval, 2)

    @Test
    fun itReturnsProvidersInSequentialOrder() {
        val loadBalancer = createInstance(3, 300)

        val provider1 = loadBalancer.register(AlwaysAliveProviderRegistry())
        val provider2 = loadBalancer.register(AlwaysAliveProviderRegistry())
        val provider3 = loadBalancer.register(AlwaysAliveProviderRegistry())

        assertThat(loadBalancer.get(), sameInstance(provider1))
        assertThat(loadBalancer.get(), sameInstance(provider2))
        assertThat(loadBalancer.get(), sameInstance(provider3))
        assertThat(loadBalancer.get(), sameInstance(provider1))
        assertThat(loadBalancer.get(), sameInstance(provider2))
        assertThat(loadBalancer.get(), sameInstance(provider3))
    }
}