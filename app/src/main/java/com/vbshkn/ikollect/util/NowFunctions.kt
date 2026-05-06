package com.vbshkn.ikollect.util

import kotlin.time.Instant

fun now(): Long {
    return System.currentTimeMillis()
}

fun nowTimestamp(): String {
    return Instant.fromEpochMilliseconds(now()).toString()
}