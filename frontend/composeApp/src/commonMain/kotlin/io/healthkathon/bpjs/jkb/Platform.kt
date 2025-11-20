package io.healthkathon.bpjs.jkb

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
