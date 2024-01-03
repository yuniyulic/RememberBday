package com.kakao.rememberBday.domain.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
	
	@Autowired
	private UserMapper userMapper;
	
	public String selectTest() {
		return userMapper.selectTest();
	}
}
