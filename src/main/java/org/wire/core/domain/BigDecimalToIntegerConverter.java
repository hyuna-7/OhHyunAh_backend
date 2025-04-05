package org.wire.core.domain;

import java.math.BigDecimal;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class BigDecimalToIntegerConverter implements AttributeConverter<BigDecimal, Integer>{

	@Override
	public Integer convertToDatabaseColumn(BigDecimal attribute) {
		return attribute != null ? attribute.intValueExact() : null;
	}

	@Override
	public BigDecimal convertToEntityAttribute(Integer dbData) {
		return dbData != null ? BigDecimal.valueOf(dbData) : null;
	}
}
