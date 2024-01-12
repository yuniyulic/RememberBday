package com.kakao.rememberBday.global.auth;

import java.util.HashMap;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface KakaoService {
	
	public String getKakaoLogin();
	
	HashMap<String, Object> getKakaoInfo(String code) throws Exception;
	
	public String getAccessTokenByUserId() throws Exception;
	
	ResponseEntity<String> getCalendars(String accessToken);
}