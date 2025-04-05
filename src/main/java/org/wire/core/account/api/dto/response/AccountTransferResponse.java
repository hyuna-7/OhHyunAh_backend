package org.wire.core.account.api.dto.response;

import java.util.List;

import org.wire.core.account.application.dto.TransferDto;

public record AccountTransferResponse(
	List<TransferDto> data
){
}
