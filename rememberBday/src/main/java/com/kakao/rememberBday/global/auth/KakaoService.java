package com.kakao.rememberBday.global.auth;

import java.util.HashMap;

import org.springframework.stereotype.Service;

@Service
public interface KakaoService {
	
	public String getKakaoLogin();
	
	HashMap<String, Object> getKakaoInfo(String code) throws Exception;
	
}