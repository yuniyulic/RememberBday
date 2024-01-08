package com.kakao.rememberBday.global.auth;

import org.springframework.stereotype.Service;

@Service
public interface KakaoService {
	
	public String getKakaoLogin();
	
	KakaoDTO getKakaoInfo(String code) throws Exception;
	
}