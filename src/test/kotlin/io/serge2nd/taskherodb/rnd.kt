package io.serge2nd.taskherodb

import io.serge2nd.taskhero.dto.HeroDto
import io.serge2nd.taskhero.dto.TeamDto
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneOffset.UTC
import kotlin.random.Random
import kotlin.text.Charsets.US_ASCII

fun Random.rndStr(n: Int = 8) = ByteArray(n) { nextInt(0x21, 0x7f).toByte() }.toString(US_ASCII)

fun rndStr(n: Int = 8) = Random.rndStr(n)

fun Random.rndAt(): OffsetDateTime = Instant.ofEpochMilli(nextLong()).atOffset(UTC)

fun rndAt() = Random.rndAt()

@Suppress("IMPLICIT_CAST_TO_ANY")
inline fun <reified V> Random.rnd() = when (V::class) {
    Boolean::class -> nextBoolean()
    Int::class -> nextInt()
    Long::class -> nextLong()
    Double::class -> nextDouble()
    String::class -> rndStr()
    OffsetDateTime::class -> rndAt()
    LocalDate::class -> rndAt().toLocalDate()
    Duration::class -> Duration.ofSeconds(nextLong())
    HeroDto::class -> HeroDto(rndStr())
    TeamDto::class -> TeamDto(rndStr())
    in Enum::class -> V::class.java.enumConstants.let { it[nextInt(it.size)] }
    else -> error("no random for ${V::class}")
} as V

inline fun <reified V> rnd() = Random.rnd<V>()
