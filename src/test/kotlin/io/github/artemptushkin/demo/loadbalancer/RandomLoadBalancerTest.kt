package io.github.artemptushkin.demo.loadbalancer

import io.github.artemptushkin.demo.api.LoadBalancer
import io.github.artemptushkin.demo.test.provider.AlwaysAliveProviderRegistry
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.Test

class RandomLoadBalancerTest : AbstractLoadBalancerTest() {
    override fun createInstance(maximumNumberOfProviders: Int, healthCheckInterval: Long): LoadBalancer =
        RandomOrderLoadBalancer(maximumNumberOfProviders, healthCheckInterval, 2)

    @Test
    fun itReturnsProviders() {
        val loadBalancer = createInstance(3, 300)

        loadBalancer.register(AlwaysAliveProviderRegistry(capacity = 3))
        loadBalancer.register(AlwaysAliveProviderRegistry(capacity = 3))
        loadBalancer.register(AlwaysAliveProviderRegistry(capacity = 3))

        MatcherAssert.assertThat(loadBalancer.get(), notNullValue())
        MatcherAssert.assertThat(loadBalancer.get(), notNullValue())
        MatcherAssert.assertThat(loadBalancer.get(), notNullValue())
        MatcherAssert.assertThat(loadBalancer.get(), notNullValue())
    }
}