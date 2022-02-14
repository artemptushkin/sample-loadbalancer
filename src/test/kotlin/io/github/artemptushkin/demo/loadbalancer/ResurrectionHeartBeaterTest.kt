package io.github.artemptushkin.demo.loadbalancer

import io.github.artemptushkin.demo.api.Provider
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

class ResurrectionHeartBeaterTest {
    companion object {
        const val TIMES_TO_RESURRECT = 2
        const val ALIVE_LAST_TIME = 1
        const val DEAD_LAST_TIME = 0
    }

    @Test
    fun itSetsAsAliveWhenProviderWasAndIsAlive() {
        val provider = alwaysAliveProvider()

        val providers = mutableMapOf(provider to ALIVE_LAST_TIME)

        val heartBeater = ResurrectionHeartBeater(TIMES_TO_RESURRECT)
        for (i in 0 until TIMES_TO_RESURRECT) {
            heartBeater.beat(providers)
        }

        assertIsAlive(heartBeater, providers[provider]!!)
    }

    @Test
    fun itSetsAsDeadWhenProviderWasAliveAndIsNotNow() {
        val provider = alwaysDeadProvider()

        val providers = mutableMapOf(provider to ALIVE_LAST_TIME)

        val heartBeater = ResurrectionHeartBeater(TIMES_TO_RESURRECT)

        for (i in 0 until TIMES_TO_RESURRECT) {
            heartBeater.beat(providers)
        }

        assertIsDead(heartBeater, providers[provider]!!)
    }

    @Test
    fun itSetsAsAliveWhenProviderWasDeadAndIsAliveExpectedTimes() {
        val provider = mock<Provider> {
            on { isAlive() } doReturn true doReturn true
        }

        val providers = mutableMapOf(provider to DEAD_LAST_TIME)

        val heartBeater = ResurrectionHeartBeater(TIMES_TO_RESURRECT)

        for (i in 0 until TIMES_TO_RESURRECT) {
            heartBeater.beat(providers)
        }

        assertIsAlive(heartBeater, providers[provider]!!)
    }

    @Test
    fun itSetsAsDeadWhenProviderWasDeadAndWasAliveLessThenExpectedTimes() {
        val provider = mock<Provider> {
            on { isAlive() } doReturn true doReturn true
        }

        val providers = mutableMapOf(provider to DEAD_LAST_TIME)

        val heartBeater = ResurrectionHeartBeater(TIMES_TO_RESURRECT)
        for (i in 0 until TIMES_TO_RESURRECT - 1) {
            heartBeater.beat(providers)
        }

        assertIsDead(heartBeater, providers[provider]!!)
    }

    private fun assertIsAlive(heartBeater: ResurrectionHeartBeater, currentHealthCheckCounter: Int) {
        assertThat(heartBeater.isAlive(currentHealthCheckCounter), Matchers.`is`(true))
    }

    private fun assertIsDead(heartBeater: ResurrectionHeartBeater, currentHealthCheckCounter: Int) {
        assertThat(heartBeater.isAlive(currentHealthCheckCounter), Matchers.`is`(false))
    }

    private fun alwaysAliveProvider(): Provider {
        return mock {
            on { isAlive() } doReturn true
        }
    }

    private fun alwaysDeadProvider(): Provider {
        return mock {
            on { isAlive() } doReturn false
        }
    }
}