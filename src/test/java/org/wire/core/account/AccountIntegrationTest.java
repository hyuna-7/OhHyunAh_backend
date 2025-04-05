package org.wire.core.account;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.wire.core.account.api.dto.request.TransferRequest;
import org.wire.core.account.domain.Account;
import org.wire.core.account.domain.Transfer;
import org.wire.core.account.domain.constant.TransferType;
import org.wire.core.account.infra.AccountRepository;
import org.wire.core.common.constant.BankCode;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AccountIntegrationTest{

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("A가 B에게 30,000원 이체 - 수수료 1% 포함 → 잔액, 내역 검증")
	void transfer_withCommission_success() throws Exception {
		// given
		Account sender = accountRepository.save(Account.register(1L, "1111111111111", BankCode.SHINHAN, 100000L));
		Account receiver = accountRepository.save(Account.register(2L, "2222222222222", BankCode.SHINHAN, 10000L));

		TransferRequest transferRequest = new TransferRequest(sender.getId(), receiver.getId(), 30000L);

		// when
		mockMvc.perform(post("/v1/accounts/{accountId}/transfers", sender.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(transferRequest))
				.header("MEMBER_ID", 1L))
			.andExpect(status().isOk());

		// then
		Account updatedSender = accountRepository.findById(sender.getId()).orElseThrow();
		Account updatedReceiver = accountRepository.findById(receiver.getId()).orElseThrow();

		// 30,000 + 300(수수료 1%) = 30,300 차감됨
		assertThat(updatedSender.getBalance().value().longValue()).isEqualTo(100000L - 30300L);
		assertThat(updatedReceiver.getBalance().value().longValue()).isEqualTo(10000L + 30000L);

		// 거래 내역이 각각 하나씩 존재
		assertThat(updatedSender.getTransfers()).hasSize(1);
		assertThat(updatedReceiver.getTransfers()).hasSize(1);

		Transfer senderTransfer = updatedSender.getTransfers().get(0);
		assertThat(senderTransfer.getType()).isEqualTo(TransferType.WITHDRAW);
		assertThat(senderTransfer.getAmount().value().longValue()).isEqualTo(30000L);
		assertThat(senderTransfer.getCommission().value().longValue()).isEqualTo(300L);

		Transfer receiverTransfer = updatedReceiver.getTransfers().get(0);
		assertThat(receiverTransfer.getType()).isEqualTo(TransferType.DEPOSIT);
		assertThat(receiverTransfer.getAmount().value().longValue()).isEqualTo(30000L);
	}

	private final String TRANSFER_API = "/v1/accounts/{accountId}/transfers";

	@Test
	@DisplayName("이체 실패 - 일일 한도 초과")
	void transfer_fail_dailyLimitExceeded() throws Exception {
		// given
		Account sender = accountRepository.save(Account.register(1L, "1111111111111", BankCode.SHINHAN, 10_000_000L));
		Account receiver = accountRepository.save(Account.register(2L, "2222222222222", BankCode.SHINHAN, 0L));

		TransferRequest transferRequest = new TransferRequest(sender.getId(), receiver.getId(), 3_100_000L);

		// when & then
		mockMvc.perform(post(TRANSFER_API, sender.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(transferRequest))
				.header("MEMBER_ID", 1L))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("일일 이체 한도를 초과했습니다.")); // 에러 메시지는 네 서비스에 맞게
	}

	@Test
	@DisplayName("이체 실패 - 자기 계좌로 이체 시도")
	void transfer_fail_toSelfAccount() throws Exception {
		// given
		Account sender = accountRepository.save(Account.register(1L, "3333333333333", BankCode.SHINHAN, 500_000L));

		TransferRequest transferRequest = new TransferRequest(sender.getId(), sender.getId(), 10_000L);

		// when & then
		mockMvc.perform(post(TRANSFER_API, sender.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(transferRequest))
				.header("MEMBER_ID", 1L))
			.andExpect(status().isBadRequest());
	}
}
