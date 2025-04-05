package org.wire.core.account.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record TransferRequest(@Schema(example = "1", description = "보낼 계좌의 아이디") Long toAccountId,
							  @Schema(example = "200", description = "보낼 금액") Long transferAmount){
}
