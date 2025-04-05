package org.wire.core.account.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.wire.core.account.domain.constant.TransferType;
import org.wire.core.common.constant.BankCode;

class AccountTest{

	@Test
	@DisplayName("출금 성공 시 잔액 차감 및 Transfer 생성 확인")
	void withdraw_shouldDecreaseBalanceAndCreateTransfer() {
		// given
		Account account = Account.register(1L, "1234567890123", BankCode.SHINHAN, 100000L);

		// when
		account.withdraw(30000L);

		// then
		assertThat(account.getBalance().value().longValue()).isEqualTo(70000L);
		assertThat(account.getTransfers()).hasSize(1);
		assertThat(account.getTransfers().get(0).getType()).isEqualTo(TransferType.WITHDRAW);
	}

	@Test
	@DisplayName("입금 성공 시 잔액 증가 및 Transfer 생성 확인")
	void deposit_shouldIncreaseBalanceAndCreateTransfer() {
		// given
		Account account = Account.register(1L, "1234567890123", BankCode.SHINHAN, 100000L);

		// when
		account.deposit(20000L);

		// then
		assertThat(account.getBalance().value().longValue()).isEqualTo(120000L);
		assertThat(account.getTransfers()).hasSize(1);
		assertThat(account.getTransfers().get(0).getType()).isEqualTo(TransferType.DEPOSIT);
	}

	@Test
	@DisplayName("이체 시 수수료 포함 잔액 차감 확인")
	void transfer_shouldApplyCommissionAndCreateTransfer() {
		// given
		Account sender = Account.register(1L, "1234567890123", BankCode.SHINHAN, 100000L);
		Account receiver = Account.register(2L, "9999999999999", BankCode.SHINHAN, 0L);

		// when
		sender.withdraw(receiver, 20000L, new BigDecimal("0.01")); // 수수료 200원
		receiver.deposit(sender, 20000L);

		// then
		assertThat(sender.getBalance().value().longValue()).isEqualTo(79800L); // 100000 - 20200
		assertThat(receiver.getBalance().value().longValue()).isEqualTo(20000L);

		assertThat(sender.getTransfers()).hasSize(1);
		assertThat(receiver.getTransfers()).hasSize(1);

		assertThat(sender.getTransfers().get(0).getType()).isEqualTo(TransferType.WITHDRAW);
		assertThat(receiver.getTransfers().get(0).getType()).isEqualTo(TransferType.DEPOSIT);
	}

	@Test
	@DisplayName("출금 시 잔액 부족 예외 발생")
	void withdraw_shouldFail_whenInsufficientBalance() {
		// given
		Account account = Account.register(1L, "1234567890123", BankCode.SHINHAN, 5000L);

		// when & then
		assertThatThrownBy(() -> account.withdraw(10000L))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("잔액이 부족합니다.");
	}

	@Test
	@DisplayName("입금 시 음수 금액 예외 발생")
	void deposit_shouldFail_whenNegative() {
		// given
		Account account = Account.register(1L, "1234567890123", BankCode.SHINHAN, 10000L);

		// when & then
		assertThatThrownBy(() -> account.deposit(0L))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("입금 금액은 0보다 커야 합니다.");
	}
}
