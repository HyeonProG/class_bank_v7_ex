package com.tenco.bank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tenco.bank.dto.SaveDTO;
import com.tenco.bank.handler.exception.DataDeliveryException;
import com.tenco.bank.handler.exception.RedirectException;
import com.tenco.bank.repository.interfaces.AccountRepository;

@Service
public class AccountService {

	private final AccountRepository accountRepository;
	
	@Autowired // DI 처리 - 생략 가능
	public AccountService(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
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
			e.printStackTrace();
			throw new DataDeliveryException("잘 못된 요청입니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			throw new RedirectException("알 수 없는 오류", HttpStatus.SERVICE_UNAVAILABLE);
		}
		
		if (result == 0) {
			throw new DataDeliveryException("정상 처리 되지 않았습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
}
