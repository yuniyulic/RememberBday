package com.kakao.rememberBday.domain.user;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
	
	String selectTest();
	
	String save(HashMap<String, Object> userInfo);
}
