package org.wire.core.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

/**
 * ERROR CODE : 4xx Error + 넘버링
 */
@Getter
public enum ErrorCode{

	BAD_REQUEST("4001", HttpStatus.BAD_REQUEST, "Bad Request", "error.bad_request"),
	UNAUTHORIZED("4002", HttpStatus.UNAUTHORIZED, "Unauthorized", "error.unauthorized"),
	FORBIDDEN("4003", HttpStatus.FORBIDDEN, "Forbidden", "error.forbidden"),
	NOT_FOUND("4004", HttpStatus.NOT_FOUND, "Not Found", "error.not_found"),
	CONFLICT("4005", HttpStatus.CONFLICT, "Conflict", "error.conflict"),
	INVALID_PARAMETER(
		"4006", HttpStatus.BAD_REQUEST, "Invalid Parameter", "error.invalid_parameter"),
	EXCEED_DAILY_TRANSFER_LIMIT("4007", HttpStatus.BAD_REQUEST, "일일 이체 한도를 초과했습니다.",
		"error.exceed_daily_transfer_limit"),

	INTERNAL_SERVER_ERROR(
		"5001",
		HttpStatus.INTERNAL_SERVER_ERROR,
		"Internal Server Error",
		"error.internal_server_error"),
	UNKNOWN_SERVER_ERROR(
		"5002",
		HttpStatus.INTERNAL_SERVER_ERROR,
		"알 수 없는 에러가 발생했습니다.",
		"error.unknown_server_error");

	private final String code;
	private final HttpStatus status;
	private final String description;
	private final String messageCode;

	ErrorCode(
		final String code,
		final HttpStatus status,
		final String description,
		final String messageCode) {
		this.code = code;
		this.status = status;
		this.description = description;
		this.messageCode = messageCode;
	}
}
