package org.wire.core.account.application.command;

import java.math.BigDecimal;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wire.core.account.api.dto.request.AccountCreateRequest;
import org.wire.core.account.api.dto.request.AccountDeleteRequest;
import org.wire.core.account.api.dto.response.AccountIdResponse;
import org.wire.core.account.domain.Account;
import org.wire.core.account.infra.AccountRepository;
import org.wire.core.account.infra.TransferRepository;
import org.wire.core.common.constant.BankCode;
import org.wire.core.common.exception.CustomException;
import org.wire.core.common.exception.ErrorCode;
import org.wire.core.common.exception.account.AccountNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountCommandService{
	private final AccountRepository accountRepository;
	private final TransferRepository transferRepository;

	private static final BigDecimal DEFAULT_COMMISSION_RATE = new BigDecimal("0.01");
	private static final long DAILY_TRANSFER_LIMIT = 3_000_000L;

	public AccountIdResponse register(Long memberId, AccountCreateRequest request) {
		Account account = Account.register(memberId, request.accountNumber(),
			BankCode.fromCode(request.bankCode()), request.balance());

		Account savedAccount = accountRepository.save(account);
		return new AccountIdResponse(savedAccount.getId());
	}

	@Transactional
	public void delete(Long memberId, AccountDeleteRequest request) {
		Account account = accountRepository.findById(request.accountId())
			.orElseThrow(() -> new AccountNotFoundException(
				request.accountId(), memberId));
		if (!Objects.equals(account.getMemberId(), memberId)) {
			throw new CustomException(ErrorCode.INVALID_PARAMETER);
		}

		account.delete();
	}

	@Transactional
	public void withdraw(Long memberId, Long accountId, Long amount) {
		Account account = accountRepository.findById(accountId)
			.orElseThrow(() -> new AccountNotFoundException(accountId, memberId));
		account.withdraw(amount);
	}

	@Transactional
	public void deposit(Long memberId, Long accountId, Long amount) {
		Account account = accountRepository.findById(accountId)
			.orElseThrow(() -> new AccountNotFoundException(accountId, memberId));

		account.deposit(amount);
	}

	/**
	 * TODO
	 *  1.Event-driven 방식으로 개선
	 *  2.예외 처리 세분화
	 */
	@Transactional
	public void transfer(Long fromAccountId, Long toAccountId, Long memberId, Long transferAmount) {
		if (fromAccountId.equals(toAccountId)) {
			throw new CustomException(ErrorCode.INVALID_PARAMETER);
		}

		// Dead Lock 방지
		Account senderAccount = accountRepository.findById(Math.min(fromAccountId, toAccountId))
			.orElseThrow(() -> new AccountNotFoundException(fromAccountId, memberId));
		Account receiverAccount = accountRepository.findById(Math.max(fromAccountId, toAccountId))
			.orElseThrow(() -> new AccountNotFoundException(toAccountId, memberId));

		Long todayTotal = transferRepository.sumTodayTransferAmountByAccountId(fromAccountId);
		if (todayTotal + transferAmount > DAILY_TRANSFER_LIMIT) {
			throw new CustomException(ErrorCode.EXCEED_DAILY_TRANSFER_LIMIT);
		}

		senderAccount.withdraw(receiverAccount, transferAmount, DEFAULT_COMMISSION_RATE);
		receiverAccount.deposit(receiverAccount, transferAmount);
	}
}
