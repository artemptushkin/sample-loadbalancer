package io.github.artemptushkin.demo.loadbalancer

import io.github.artemptushkin.demo.api.LoadBalancer
import io.github.artemptushkin.demo.provider.AlwaysAliveProviderRegistry
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.Test

class RandomLoadBalancerTest : AbstractLoadBalancerTest() {
    override fun createInstance(maximumNumberOfProviders: Int): LoadBalancer = RandomOrderLoadBalancer(maximumNumberOfProviders)

    @Test
    fun itReturnsProviders() {
        val loadBalancer = createInstance(3)

        loadBalancer.register(AlwaysAliveProviderRegistry())
        loadBalancer.register(AlwaysAliveProviderRegistry())
        loadBalancer.register(AlwaysAliveProviderRegistry())

        MatcherAssert.assertThat(loadBalancer.get(), notNullValue())
        MatcherAssert.assertThat(loadBalancer.get(), notNullValue())
        MatcherAssert.assertThat(loadBalancer.get(), notNullValue())
        MatcherAssert.assertThat(loadBalancer.get(), notNullValue())
    }
}