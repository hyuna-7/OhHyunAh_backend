package org.wire.core.common.constant;

import java.util.Arrays;

import org.wire.core.common.exception.CustomException;
import org.wire.core.common.exception.ErrorCode;

import lombok.Getter;

@Getter
public enum BankCode{
	SHINHAN("001", "신한은행"),
	WOORI("002", "우리은행"),
	HANA("0003", "하나은행");

	private final String code;
	private final String name;

	BankCode(String code, String name) {
		this.code = code;
		this.name = name;
	}

	public static BankCode fromCode(String code) {
		return Arrays.stream(values())
			.filter(v -> v.getCode().equals(code))
			.findAny()
			.orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST, "지원하지 않는 은행 코드입니다."));
	}
}