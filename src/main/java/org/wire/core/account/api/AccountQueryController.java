package org.wire.core.account.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.wire.core.account.api.dto.response.AccountTransferResponse;
import org.wire.core.account.application.query.AccountQueryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/accounts")
@Validated
@Tag(name = "Account Query", description = "Account Query API")
public class AccountQueryController{
	private final AccountQueryService accountQueryService;

	@Operation(
		summary = "이체 목록 조회",
		responses = {
			@ApiResponse(
				responseCode = "201",
				description = "이체 목록 조회 API",
				content = @Content(schema = @Schema(implementation = AccountTransferResponse.class)))
		})
	@GetMapping("/{accountId}/transfers")
	public ResponseEntity<AccountTransferResponse> clist(@RequestHeader(value = "MEMBER-ID") @Min(0) Long memberId,
		@PathVariable @Min(0) Long accountId,
		@RequestParam(required = false) Long cursorId, @RequestParam(defaultValue = "20") int size) {
		AccountTransferResponse response = accountQueryService.list(memberId, accountId, cursorId, size);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
