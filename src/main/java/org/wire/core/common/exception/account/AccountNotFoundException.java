package org.wire.core.common.exception.account;

import org.wire.core.common.exception.CustomException;
import org.wire.core.common.exception.ErrorCode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AccountNotFoundException extends CustomException{
	public AccountNotFoundException(Long accountId, Long memberId) {
		super(ErrorCode.INVALID_PARAMETER);
		log.error("account not found. accountId: {}, memberId: {}", accountId, memberId);

	}
}
