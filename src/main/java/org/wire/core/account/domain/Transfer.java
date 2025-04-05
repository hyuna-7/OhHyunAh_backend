package org.wire.core.account.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.wire.core.account.domain.constant.TransferType;
import org.wire.core.common.constant.Money;
import org.wire.core.common.exception.CustomException;
import org.wire.core.common.exception.ErrorCode;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * TODO 입금/출금 내역을 송금 하는 사람과 받는 사람 각각 분리하여 저장 필요
 */

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(value = {AuditingEntityListener.class})
public class Transfer{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "account")
	private Account ownerAccount;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "receiver_account_id")
	private Account receiverAccount;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private TransferType type;

	@AttributeOverride(name = "value", column = @Column(name = "amount", nullable = false))
	@Embedded
	private Money amount;

	@AttributeOverride(name = "value", column = @Column(name = "commission", nullable = false))
	@Embedded
	private Money commission;

	@Embedded
	private Money balance;

	@CreatedDate
	@Column(updatable = false)
	private LocalDateTime createdAt;

	// 송금
	public static Transfer createWithdraw(Account senderAccount, Account receiverAccount, Money amount,
		BigDecimal commissionRate) {
		if (commissionRate.compareTo(BigDecimal.ZERO) < 0) {
			throw new CustomException(ErrorCode.INVALID_PARAMETER, "수수료율이 음수입니다.");
		}
		BigDecimal multiply = amount.value().multiply(commissionRate);
		Money commission = Money.from(multiply);

		return new Transfer(null, senderAccount, receiverAccount, TransferType.WITHDRAW, amount,
			commission, senderAccount.getBalance(), LocalDateTime.now());
	}

	// 출금
	public static Transfer createWithdraw(Account account, Money amount) {
		return new Transfer(null, account, account, TransferType.WITHDRAW, amount,
			Money.zero(), account.getBalance(), LocalDateTime.now());
	}

	// 입금
	public static Transfer createDeposit(Account account, Money amount) {
		return new Transfer(null, account, account, TransferType.DEPOSIT, amount, Money.zero(), account.getBalance(),
			LocalDateTime.now());
	}

	// 입금
	public static Transfer createDeposit(Account senderAccount, Account receiverAccount, Money amount) {
		return new Transfer(null, senderAccount, receiverAccount, TransferType.DEPOSIT, amount, Money.zero(),
			senderAccount.getBalance(), LocalDateTime.now());
	}
}
