package org.wire.core.account.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wire.core.account.api.dto.request.AccountCreateRequest;
import org.wire.core.account.api.dto.request.AccountDeleteRequest;
import org.wire.core.account.api.dto.request.DepositRequest;
import org.wire.core.account.api.dto.request.TransferRequest;
import org.wire.core.account.api.dto.request.WithdrawRequest;
import org.wire.core.account.api.dto.response.AccountIdResponse;
import org.wire.core.account.application.command.AccountCommandService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/accounts")
@Tag(name = "Account Command", description = "Account Command API")
@Validated
public class AccountCommandController{
	private final AccountCommandService accountCommandService;

	@Operation(
		summary = "계좌 등록",
		responses = {
			@ApiResponse(
				responseCode = "201",
				description = "계좌 생성 완료",
				content = @Content(schema = @Schema(implementation = AccountIdResponse.class)))
		})
	@PostMapping
	public ResponseEntity<AccountIdResponse> register(
		@RequestHeader("MEMBER_ID") Long memberId,
		@RequestBody @Valid AccountCreateRequest request) {
		AccountIdResponse response = accountCommandService.register(memberId, request);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@Operation(
		summary = "계좌 삭제",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "계좌 삭제 완료"
			)}
	)
	@DeleteMapping
	public ResponseEntity<Void> delete(
		@RequestHeader("MEMBER_ID") Long memberId,
		@RequestBody @Valid AccountDeleteRequest request) {
		accountCommandService.delete(memberId, request);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(
		summary = "이체",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "이체 완료"
			)})
	@PostMapping("/{accountId}/transfers")
	public ResponseEntity<Void> transfer(
		@RequestBody @Valid TransferRequest request,
		@PathVariable @Min(0) Long accountId,
		@RequestHeader("MEMBER_ID") Long memberId) {
		accountCommandService.transfer(accountId, request.toAccountId(),
			memberId, request.transferAmount());

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(
		summary = "입금",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "입금 완료"
			)})
	@PostMapping("/{accountId}/deposit")
	public ResponseEntity<Void> deposit(@RequestBody @Valid DepositRequest request,
		@PathVariable @Min(0) Long accountId,
		@RequestHeader("MEMBER_ID") Long memberId) {
		accountCommandService.deposit(memberId, accountId, request.amount());

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(
		summary = "출금",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "출금 완료"
			)})
	@PostMapping("/{accountId}/withdraw")
	public ResponseEntity<Void> withdraw(@RequestBody @Valid WithdrawRequest request,
		@PathVariable @Min(0) Long accountId,
		@RequestHeader("MEMBER_ID") Long memberId) {
		accountCommandService.withdraw(memberId, accountId, request.amount());

		return new ResponseEntity<>(HttpStatus.OK);
	}

}
