package com.kakao.rememberBday.domain.user;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
	//test
	String selectTest();
	
	public String save(HashMap<String, Object> userInfo);
	
	// 사용자 정보 조회
	Map<String, Object> selectUserById(long userId);
	
	// 사용자 정보 삽입
	void insertUser(Map<String, Object> userInfo);
	
	// 사용자 정보 업데이트
	void updateUser(Map<String, Object> userInfo);
}
