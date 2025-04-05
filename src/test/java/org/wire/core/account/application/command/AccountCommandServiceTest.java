package org.wire.core.account.application.command;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.wire.core.account.api.dto.request.AccountCreateRequest;
import org.wire.core.account.api.dto.request.AccountDeleteRequest;
import org.wire.core.account.api.dto.response.AccountIdResponse;
import org.wire.core.account.domain.Account;
import org.wire.core.account.infra.AccountRepository;
import org.wire.core.account.infra.TransferRepository;
import org.wire.core.common.constant.BankCode;
import org.wire.core.common.exception.CustomException;

@ExtendWith(MockitoExtension.class)
class AccountCommandServiceTest{

	@InjectMocks
	private AccountCommandService accountCommandService;

	@Mock
	private AccountRepository accountRepository;

	@Mock
	private TransferRepository transferRepository;

	private static final Long MEMBER_ID = 1L;

	@Test
	@DisplayName("계좌 등록 성공")
	void register_success() {
		AccountCreateRequest request = new AccountCreateRequest("001", "1234567890123", 10000L);
		Account account = Account.register(MEMBER_ID, "1234567890123", BankCode.SHINHAN, 10000L);
		when(accountRepository.save(any(Account.class))).thenReturn(account);

		AccountIdResponse response = accountCommandService.register(MEMBER_ID, request);

		verify(accountRepository).save(any(Account.class));
		assert response != null;
	}

	@Test
	@DisplayName("계좌 삭제 실패 - 회원 ID 불일치")
	void delete_invalidMemberId() {
		AccountDeleteRequest request = new AccountDeleteRequest(MEMBER_ID, 1L);
		Account account = Account.register(2L, "1234567890123", BankCode.SHINHAN, 10000L);

		when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

		assertThatThrownBy(() -> accountCommandService.delete(request.memberId(), request))
			.isInstanceOf(CustomException.class);
	}

	@Test
	@DisplayName("출금 성공")
	void withdraw_success() {
		Long accountId = 1L;
		Account account = Account.register(MEMBER_ID, "1234567890123", BankCode.SHINHAN, 100000L);

		when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

		accountCommandService.withdraw(1L, accountId, 50000L);

		verify(accountRepository).findById(accountId);
	}

	@Test
	@DisplayName("입금 성공")
	void deposit_success() {
		Long accountId = 1L;
		Account account = Account.register(MEMBER_ID, "1234567890123", BankCode.SHINHAN, 100000L);

		when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

		accountCommandService.deposit(MEMBER_ID, accountId, 30000L);

		verify(accountRepository).findById(accountId);
	}

	@Test
	@DisplayName("이체 성공")
	void transfer_success() {
		Account sender = Account.register(MEMBER_ID, "1111111111111", BankCode.SHINHAN, 100000L);
		Account receiver = Account.register(2L, "2222222222222", BankCode.SHINHAN, 50000L);

		when(accountRepository.findById(1L)).thenReturn(Optional.of(sender));
		when(accountRepository.findById(2L)).thenReturn(Optional.of(receiver));
		when(transferRepository.sumTodayTransferAmountByAccountId(1L)).thenReturn(0L);

		accountCommandService.transfer(1L, 2L, 1L, 10000L);

		verify(accountRepository).findById(1L);
		verify(accountRepository).findById(2L);
	}
}
