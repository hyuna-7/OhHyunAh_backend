package org.wire.core.account.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.wire.core.account.domain.constant.TransferType;

public record TransferDto(
	Long id,
	Long receiverAccountId,
	TransferType transferType,
	BigDecimal balance,
	LocalDateTime createdAt
){
}
