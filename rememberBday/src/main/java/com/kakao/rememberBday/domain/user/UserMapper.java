package com.kakao.rememberBday.domain.user;

import java.util.HashMap;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
	//test
	String selectTest();
	
	public void save(HashMap<String, Object> userInfo);
}
