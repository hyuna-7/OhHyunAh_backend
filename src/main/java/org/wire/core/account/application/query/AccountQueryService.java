package org.wire.core.account.application.query;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wire.core.account.api.dto.response.AccountTransferResponse;
import org.wire.core.account.application.dto.TransferDto;
import org.wire.core.account.infra.TransferRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountQueryService{
	private final TransferRepository transferRepository;

	@Transactional(readOnly = true)
	public AccountTransferResponse list(Long memberId, Long accountId, Long cursorId, int size) {

		List<TransferDto> lists = transferRepository.findByAccountId(memberId, accountId, cursorId, size);
		return new AccountTransferResponse(lists);
	}
}
