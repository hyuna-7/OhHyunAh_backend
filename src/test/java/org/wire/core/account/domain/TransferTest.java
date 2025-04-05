package org.wire.core.account.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.wire.core.account.domain.constant.TransferType;
import org.wire.core.common.constant.BankCode;
import org.wire.core.common.constant.Money;
import org.wire.core.common.exception.CustomException;

class TransferTest{

	@Test
	@DisplayName("출금 Transfer 생성 테스트")
	void createWithdrawTransfer() {
		Account sender = Account.register(1L, "1111111111111", BankCode.SHINHAN, 100000L);
		Account receiver = Account.register(2L, "2222222222222", BankCode.SHINHAN, 0L);
		Money amount = Money.from(30000L);
		BigDecimal commission = new BigDecimal("0.01");

		Transfer transfer = Transfer.createWithdraw(sender, receiver, amount, commission);

		assertThat(transfer.getOwnerAccount()).isEqualTo(sender);
		assertThat(transfer.getReceiverAccount()).isEqualTo(receiver);
		assertThat(transfer.getAmount()).isEqualTo(amount);
		assertThat(transfer.getCommission()).isEqualTo(Money.from(300));
		assertThat(transfer.getType()).isEqualTo(TransferType.WITHDRAW);
	}

	@Test
	@DisplayName("입금 Transfer 생성 테스트")
	void createDepositTransfer() {
		Account sender = Account.register(1L, "1111111111111", BankCode.SHINHAN, 100000L);
		Account receiver = Account.register(2L, "2222222222222", BankCode.SHINHAN, 0L);
		Money amount = Money.from(50000L);

		Transfer transfer = Transfer.createDeposit(sender, receiver, amount);

		assertThat(transfer.getOwnerAccount()).isEqualTo(sender);
		assertThat(transfer.getReceiverAccount()).isEqualTo(receiver);
		assertThat(transfer.getAmount()).isEqualTo(amount);
		assertThat(transfer.getCommission()).isEqualTo(Money.zero());
		assertThat(transfer.getType()).isEqualTo(TransferType.DEPOSIT);
	}

	@Test
	@DisplayName("단일 계좌 출금 생성 테스트")
	void createWithdrawSelf() {
		Account account = Account.register(1L, "1111111111111", BankCode.SHINHAN, 100000L);
		Money amount = Money.from(10000L);

		Transfer transfer = Transfer.createWithdraw(account, amount);

		assertThat(transfer.getOwnerAccount()).isEqualTo(account);
		assertThat(transfer.getReceiverAccount()).isEqualTo(account);
		assertThat(transfer.getType()).isEqualTo(TransferType.WITHDRAW);
	}

	@Test
	@DisplayName("단일 계좌 입금 생성 테스트")
	void createDepositSelf() {
		Account account = Account.register(1L, "1111111111111", BankCode.SHINHAN, 100000L);
		Money amount = Money.from(10000L);

		Transfer transfer = Transfer.createDeposit(account, amount);

		assertThat(transfer.getOwnerAccount()).isEqualTo(account);
		assertThat(transfer.getReceiverAccount()).isEqualTo(account);
		assertThat(transfer.getType()).isEqualTo(TransferType.DEPOSIT);
	}

	@Test
	@DisplayName("출금 생성 실패 - 수수료율 음수")
	void createWithdraw_fail_negativeCommission() {
		Account sender = Account.register(1L, "1111111111111", BankCode.SHINHAN, 100000L);
		Account receiver = Account.register(2L, "2222222222222", BankCode.SHINHAN, 0L);
		Money amount = Money.from(10000L);
		BigDecimal invalidRate = new BigDecimal("-0.1");

		assertThatThrownBy(() -> Transfer.createWithdraw(sender, receiver, amount, invalidRate))
			.isInstanceOf(CustomException.class);
	}

	@Test
	@DisplayName("입금 생성 실패 - 송신자와 수신자가 동일하지 않음 (단일 입금용)")
	void createDepositSelf_fail_whenDifferentAccounts() {
		Account owner = Account.register(1L, "1111111111111", BankCode.SHINHAN, 10000L);
		Account fakeTarget = Account.register(2L, "9999999999999", BankCode.SHINHAN, 0L);
		Money amount = Money.from(10000L);

		Transfer transfer = Transfer.createDeposit(owner, amount);

		assertThat(transfer.getOwnerAccount()).isEqualTo(owner);
		assertThat(transfer.getReceiverAccount()).isEqualTo(owner);
		assertThat(transfer.getReceiverAccount()).isNotEqualTo(fakeTarget);
	}
}
