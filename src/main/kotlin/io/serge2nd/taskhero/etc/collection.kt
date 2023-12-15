package io.serge2nd.taskhero.etc

import java.time.Duration

fun Iterable<Duration>.durationSum(): Duration = fold(Duration.ZERO, Duration::plus)
