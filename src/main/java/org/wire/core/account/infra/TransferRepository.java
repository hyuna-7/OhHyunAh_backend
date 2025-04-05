package org.wire.core.account.infra;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.wire.core.account.domain.Transfer;

public interface TransferRepository extends JpaRepository<Transfer, Long>, TransferRepositoryCustom{

	@Query("SELECT COALESCE(SUM(t.amount.value), 0) FROM Transfer t " +
		"WHERE t.ownerAccount.id = :accountId " +
		"AND t.type = 'WITHDRAW' " +
		"AND DATE(t.createdAt) = CURRENT_DATE")
	Long sumTodayTransferAmountByAccountId(@Param("accountId") Long accountId);
}
