package org.wire.core.account.infra;

import static org.wire.core.account.domain.QTransfer.transfer;

import java.util.List;

import org.wire.core.account.application.dto.TransferDto;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TransferRepositoryCustomImpl implements TransferRepositoryCustom{
	private final JPAQueryFactory queryFactory;

	@Override
	public List<TransferDto> findByAccountId(long memberId, long accountId, Long cursorId, int size) {
		return queryFactory.select(Projections.constructor(TransferDto.class,
				transfer.id,
				transfer.ownerAccount.id,
				transfer.type,
				transfer.balance.value,
				transfer.createdAt
			))
			.from(transfer)
			.where(transfer.ownerAccount.id.eq(accountId), descCursorId(cursorId))
			.orderBy(transfer.id.desc())
			.limit(size)
			.fetch();
	}

	private BooleanExpression descCursorId(Long cursorId) {
		return cursorId == null ? null : transfer.id.lt(cursorId);
	}

}
