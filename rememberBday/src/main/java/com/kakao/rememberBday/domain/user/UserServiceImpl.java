package com.kakao.rememberBday.domain.user;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserMapper userMapper;
	
	public String selectTest() {
		return userMapper.selectTest();
	}
	
	public String save(HashMap<String, Object> userInfo) {
		return userMapper.save(userInfo);
	}
}
