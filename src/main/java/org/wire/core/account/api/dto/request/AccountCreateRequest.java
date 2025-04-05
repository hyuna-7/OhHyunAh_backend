package org.wire.core.account.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AccountCreateRequest(
	@NotBlank @Schema(example = "001", description = "은행 코드 : 001 신한은행, 002 우리은행, 003 하나은행") String bankCode,
	@Schema(example = "1101234123412", description = "등록 계좌번호") @NotBlank String accountNumber,
	@Schema(example = "100000", description = "초기 계좌 금액") @Min(0) @NotNull Long balance){
}
