package org.wire.core.common.exception;

import org.springframework.lang.NonNull;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{
	private final ErrorCode errorCode;
	private final String description;
	private final String messageCode;
	private final Object[] args;

	public CustomException(@NonNull ErrorCode errorCode, @NonNull String description) {
		super(errorCode.getDescription() + ", " + description);
		this.errorCode = errorCode;
		this.description = description;
		this.messageCode = errorCode.getMessageCode();
		this.args = null;
	}

	public CustomException(
		@NonNull ErrorCode errorCode, @NonNull String description, Object... args) {
		super(errorCode.getDescription() + ", " + description);
		this.errorCode = errorCode;
		this.description = description;
		this.messageCode = errorCode.getMessageCode();
		this.args = args;
	}

	public CustomException(
		@NonNull ErrorCode errorCode, @NonNull String description, @NonNull Throwable cause) {
		super(errorCode.getDescription() + ", " + description, cause);
		this.errorCode = errorCode;
		this.description = description;
		this.messageCode = errorCode.getMessageCode();
		this.args = null;
	}

	public CustomException(
		@NonNull ErrorCode errorCode,
		@NonNull String description,
		@NonNull String messageCode,
		Object... args) {
		super(errorCode.getDescription() + ", " + description);
		this.errorCode = errorCode;
		this.description = description;
		this.messageCode = messageCode;
		this.args = args;
	}

	public CustomException(
		@NonNull ErrorCode errorCode, @NonNull String description, @NonNull String messageCode) {
		super(errorCode.getDescription() + ", " + description);
		this.errorCode = errorCode;
		this.description = description;
		this.messageCode = messageCode;
		this.args = null;
	}

	public CustomException(@NonNull ErrorCode errorCode) {
		super(errorCode.getDescription());
		this.errorCode = errorCode;
		this.description = errorCode.getDescription();
		this.messageCode = errorCode.getMessageCode();
		this.args = null;
	}

	public CustomException(@NonNull ErrorCode errorCode, @NonNull Throwable cause) {
		super(errorCode.getDescription(), cause);
		this.errorCode = errorCode;
		this.description = errorCode.getDescription();
		this.messageCode = errorCode.getMessageCode();
		this.args = null;
	}
}
