package io.github.artemptushkin.demo.api

interface HeartBeater {

    fun beat(providers: MutableMap<Provider, Int>)
}
