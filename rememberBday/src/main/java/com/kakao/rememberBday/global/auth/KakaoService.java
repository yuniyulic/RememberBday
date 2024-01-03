package com.kakao.rememberBday.global.auth;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.kakao.rememberBday.domain.user.UserMapper;
import com.kakao.rememberBday.domain.user.UserRepository;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

@Service
public class KakaoService {
	
	@Autowired
	private UserMapper userMapper;

	@Value("${spring.security.oauth2.client.registration.kakao.client-id}")
	private String CLIENT_ID;

	@Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
	private String CLIENT_SECRET;
	
	@Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
	private String REDIRECT_URI;
	
	private final static String KAKAO_AUTH_URI = "https://kauth.kakao.com";
	private final static String KAKAO_API_URI = "https://kapi.kakao.com";
	
	public String getKakaoLogin() {
		return KAKAO_AUTH_URI + "/oauth/authorize"
							+ "?client_id=" + CLIENT_ID
			                + "&redirect_uri=" + REDIRECT_URI
			                + "&response_type=code";
	}
	
	public KakaoDTO getKakaoInfo(String code) throws Exception {
		if (code == null) throw new Exception("Failed get authorization code");
		
		String accessToken = "";
		String refreshToken = "";
		
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-type", "application/x-www-form-urlencoded");
			
			MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
			params.add("grant_type", "authorization_code");
			params.add("client_id", CLIENT_ID);
			params.add("client_secret", CLIENT_SECRET);
			params.add("code", code);
			params.add("redirect_uri", REDIRECT_URI);
			
			RestTemplate restTemplate = new RestTemplate();
			HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);
			
			ResponseEntity<String> response = restTemplate.exchange(
												KAKAO_AUTH_URI + "/oauth/token", 
												HttpMethod.POST,
												httpEntity, 
												String.class
			);
			
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObj = (JSONObject) jsonParser.parse(response.getBody());
			
			accessToken = (String) jsonObj.get("access_token");
			refreshToken = (String) jsonObj.get("refresh_token");
			
		} catch (Exception e) {
			throw new Exception("API call failed");
		}
		
		return getUserInfoWithToken(accessToken);
	}
	
	private KakaoDTO getUserInfoWithToken(String accessToken) throws Exception {
		// HttpHeader 생성
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + accessToken);
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		// HttpHeader 담기
		RestTemplate rt = new RestTemplate();
		HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(headers);
		ResponseEntity<String> response = rt.exchange(
											KAKAO_API_URI + "/v2/user/me", 
											HttpMethod.POST, 
											httpEntity,
											String.class
		);

		// Response 데이터 파싱
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObj = (JSONObject) jsonParser.parse(response.getBody());
		JSONObject account = (JSONObject) jsonObj.get("kakao_account");
		JSONObject profile = (JSONObject) account.get("profile");

		long id = (long) jsonObj.get("id");
		String email = String.valueOf(account.get("email"));
		String nickname = String.valueOf(profile.get("nickname"));
		
		//db에 유저정보 저장
		HashMap<String, Object> userInfo = new HashMap<>();
		userInfo.put("user_id", id);
		userInfo.put("user_name", nickname);
		userInfo.put("user_email", email);
		
		userMapper.save(userInfo);

		return KakaoDTO.builder()
						.user_id(id)
						.user_name(nickname)
						.user_email(email)
						.build();
	}
	
}