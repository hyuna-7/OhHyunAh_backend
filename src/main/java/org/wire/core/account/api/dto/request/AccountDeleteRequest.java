package org.wire.core.account.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AccountDeleteRequest
	(@Schema(example = "20", description = "삭제 계좌 아이디") @Min(0) @NotNull Long accountId){
}
