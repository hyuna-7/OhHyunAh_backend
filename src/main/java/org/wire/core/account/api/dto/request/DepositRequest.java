package org.wire.core.account.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record DepositRequest(
	@Min(0)
	@NotNull
	@Schema(example = "200", description = "금액")
	Long amount
){
}
