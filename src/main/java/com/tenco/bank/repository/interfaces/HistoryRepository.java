package com.tenco.bank.repository.interfaces;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.tenco.bank.repository.model.History;

// HistoryRepository 인터페이스와 history.xml 파일을 매칭 시킨다.
@Mapper
public interface HistoryRepository {

	public int insert(History history);
	public int updateById(History history);
	public int deleteById(Integer id);
	
	// 거래내역 조회
	public History findById(Integer id);
	public List<History> findAll();
	
}
