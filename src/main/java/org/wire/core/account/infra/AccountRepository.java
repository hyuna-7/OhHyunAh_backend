package org.wire.core.account.infra;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.lang.NonNull;
import org.wire.core.account.domain.Account;

import jakarta.persistence.LockModeType;

public interface AccountRepository extends JpaRepository<Account, Long>{
	@Lock(LockModeType.PESSIMISTIC_READ)
	@NonNull
	Optional<Account> findById(@NonNull Long id);

}
