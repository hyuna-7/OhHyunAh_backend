package org.wire.core.common.constant;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.wire.core.domain.BigDecimalToIntegerConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(of = "value")
@Embeddable
public class Money implements Comparable<Money>{

	@Convert(converter = BigDecimalToIntegerConverter.class)
	@Column(name = "balance", precision = 10, columnDefinition = "int")
	private final BigDecimal value;

	@Deprecated
	protected Money() {
		this.value = null;
	}

	private Money(BigDecimal value) {
		this.value = value;
	}

	public static Money from(long value) {
		return new Money(BigDecimal.valueOf(value).setScale(0, RoundingMode.UP));
	}

	public static Money from(BigDecimal value) {
		return new Money(value.setScale(0, RoundingMode.UP));
	}

	public static Money zero() {
		return Money.from(0L);
	}

	public Money negate() {
		return Money.from(value.negate());
	}

	public boolean lessThan(Money other) {
		int result = this.compareTo(other);
		return result < 0;
	}

	public boolean greaterThan(Money other) {
		int result = this.compareTo(other);
		return result > 0;
	}

	public boolean lessThanOrEqual(Money other) {
		int result = this.compareTo(other);
		return result <= 0;
	}

	public BigDecimal value() {
		return value;
	}

	@Override
	public String toString() {
		return "Money{" + "value=" + value + '}';
	}

	@Override
	public int compareTo(@NotNull Money other) {
		assert this.value != null;
		return this.value.compareTo(other.value);
	}

	public Money minus(Money other) {
		assert this.value != null;
		return Money.from(this.value.subtract(other.value));
	}

	public Money add(Money other) {
		assert this.value != null;
		return Money.from(this.value.add(other.value));
	}

	public Money minus(Money other, BigDecimal rate) {
		Money commission = calculateCommission(other, rate);
		BigDecimal totalAmount = other.value.add(commission.value);
		return this.minus(Money.from(totalAmount));
	}

	public static Money calculateCommission(Money other, BigDecimal rate) {
		return Money.from(other.value.multiply(rate));
	}
}
