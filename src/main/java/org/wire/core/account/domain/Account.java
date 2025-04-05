package org.wire.core.account.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.wire.core.common.constant.BankCode;
import org.wire.core.common.constant.Money;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@EntityListeners(value = {AuditingEntityListener.class})
public class Account{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long memberId;

	@Column(nullable = false, unique = true, length = 20)
	private String accountNumber;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 3)
	private BankCode bankCode;

	@Embedded
	private Money balance;

	@OneToMany(mappedBy = "ownerAccount", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Transfer> transfers = new ArrayList<>();

	@CreatedDate
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	@Column(name = "updated_at")
	LocalDateTime updatedAt;

	@Column(name = "deleted_at")
	LocalDateTime deletedAt;

	public static Account register(Long memberId, String accountNumber, BankCode bankCode, Long balance) {
		return new Account(null, memberId, accountNumber, bankCode, Money.from(balance), new ArrayList<>(),
			LocalDateTime.now(),
			LocalDateTime.now(), null);
	}

	public void deposit(Long amount) {
		Money money = Money.from(amount);
		validateDeposit(money);
		this.balance = this.balance.add(money);
		transfers.add(Transfer.createDeposit(this, money));
	}

	public void deposit(Account sender, Long amount) {
		Money money = Money.from(amount);
		validateDeposit(money);
		this.balance = this.balance.add(money);
		transfers.add(Transfer.createDeposit(sender, this, money));
	}

	public void withdraw(Long amount) {
		Money money = Money.from(amount);
		validateWithdraw(money);
		this.balance = this.balance.minus(money);
		transfers.add(Transfer.createWithdraw(this, money));
	}

	public void withdraw(Account receiver, Long amount, BigDecimal commissionRate) {
		Money money = Money.from(amount);
		validateWithdraw(money);

		this.balance = this.balance.minus(money, commissionRate);
		transfers.add(Transfer.createWithdraw(this, receiver, money, commissionRate));
	}

	public void delete() {
		this.deletedAt = LocalDateTime.now();
	}

	private void validateWithdraw(Money amount) {
		if (amount.lessThanOrEqual(Money.zero())) {
			throw new IllegalArgumentException("출금 금액은 0보다 커야 합니다.");
		}
		if (this.balance.lessThan(amount)) {
			throw new IllegalArgumentException("잔액이 부족합니다.");
		}
	}

	private void validateDeposit(Money amount) {
		if (amount.lessThanOrEqual(Money.zero())) {
			throw new IllegalArgumentException("입금 금액은 0보다 커야 합니다.");
		}
	}

}
