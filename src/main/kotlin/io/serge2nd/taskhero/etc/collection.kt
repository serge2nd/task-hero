package io.serge2nd.taskhero.etc

import java.time.Duration
import java.time.Duration.ofSeconds

fun Iterable<Duration>.durationSum() = fold(TimeAmount(), ::addTo).let(::duration)

private data class TimeAmount(var secs: Long = 0, var nanos: Int = 0)

private fun addTo(t: TimeAmount, d: Duration) = t.apply {
    secs += d.toSeconds() + (nanos + d.toNanosPart()) / SEC_NANOS
    nanos = (nanos + d.toNanosPart()) % SEC_NANOS
}

private fun duration(t: TimeAmount): Duration = ofSeconds(t.secs, t.nanos.toLong())

private const val SEC_NANOS = 1_000_000_000
