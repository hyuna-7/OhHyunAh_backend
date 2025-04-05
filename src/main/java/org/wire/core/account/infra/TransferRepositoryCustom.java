package org.wire.core.account.infra;

import java.util.List;

import org.wire.core.account.application.dto.TransferDto;

public interface TransferRepositoryCustom{

	List<TransferDto> findByAccountId(long memberId, long accountId, Long cursorId, int size);
}
