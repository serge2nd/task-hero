package io.serge2nd.taskhero.db

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import java.time.Duration

@Converter(autoApply = true)
class DurationToString : AttributeConverter<Duration, String> {

    override fun convertToDatabaseColumn(attr: Duration?) = attr?.toString()

    override fun convertToEntityAttribute(value: String?) = value?.let(Duration::parse)
}
