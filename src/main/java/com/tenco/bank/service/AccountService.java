package com.tenco.bank.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tenco.bank.dto.SaveDTO;
import com.tenco.bank.dto.WithdrawDTO;
import com.tenco.bank.handler.exception.DataDeliveryException;
import com.tenco.bank.handler.exception.RedirectException;
import com.tenco.bank.repository.interfaces.AccountRepository;
import com.tenco.bank.repository.interfaces.HistoryRepository;
import com.tenco.bank.repository.model.Account;
import com.tenco.bank.repository.model.History;
import com.tenco.bank.utils.Define;

@Service
public class AccountService {

	private final AccountRepository accountRepository;
	private final HistoryRepository historyRepository;
	
	@Autowired // DI 처리 - 생략 가능
	public AccountService(AccountRepository accountRepository, HistoryRepository historyRepository) {
		this.accountRepository = accountRepository;
		this.historyRepository = historyRepository;
	}
	
	/**
	 * 계좌 생성 기능
	 */
	@Transactional // 트랜잭션 처리
	public void createAccount(SaveDTO dto, Integer principalId) {
		int result = 0;
		try {
			result = accountRepository.insert(dto.toAccount(principalId));
		} catch (DataAccessException e) {
			throw new DataDeliveryException(Define.INVALID_INPUT, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			throw new RedirectException(Define.UNKNOWN, HttpStatus.SERVICE_UNAVAILABLE);
		}
		
		if (result == 0) {
			throw new DataDeliveryException(Define.FAILED_PROCESSING, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	/**
	 * 계좌 목록 조회 기능
	 * @return
	 */
	public List<Account> readAccountListByUserId(Integer userId) {
		List<Account> accountListEntity = null;
		try {
			accountListEntity = accountRepository.findByUserId(userId);
		} catch (DataAccessException e) {
			throw new DataDeliveryException(Define.INVALID_INPUT, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			throw new RedirectException(Define.UNKNOWN, null);
		}
		
		return accountListEntity;
		
	}
	
	@Transactional
	public void updateAccountWithdraw(WithdrawDTO dto, Integer principalId) {
		// 1. 계좌 존재 여부 확인
		Account accountEntity = accountRepository.findByNumber(dto.getWAccountNumber());
		if (accountEntity == null) {
			throw new DataDeliveryException(Define.NOT_EXIST_ACCOUNT, HttpStatus.BAD_REQUEST);
		}
		
		// 2. 본인 계좌 여부 확인
		accountEntity.checkOwner(principalId);
		
		// 3. 계좌 비밀번호 확인
		accountEntity.checkPassword(dto.getWAccountPassword());
		
		// 4. 잔액 여부 확인
		accountEntity.checkBalance(dto.getAmount());
		
		// 5. 출금 기능\
		// accountEntity 객체의 잔액을 변경하고 업데이트 처리해야 한다.
		accountEntity.withdraw(dto.getAmount());
		// 업데이트 처리
		accountRepository.updateById(accountEntity);
		
		// 6. 거래 내역 등록
		History history = History.builder()
				.amount(dto.getAmount())
				.wBalance(accountEntity.getBalance())
				.dBalance(null)
				.wAccountId(accountEntity.getId())
				.dAccountId(null)
				.build();
		
		int rowResultCount = historyRepository.insert(history);
		if (rowResultCount != 1) {
			throw new DataDeliveryException(Define.FAILED_PROCESSING, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	
}
