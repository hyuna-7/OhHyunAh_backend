package org.wire.core.account.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record WithdrawRequest(
	@Min(0)
	@NotNull
	@Schema(example = "1000", description = "이체 금액")
	Long amount
){
}
