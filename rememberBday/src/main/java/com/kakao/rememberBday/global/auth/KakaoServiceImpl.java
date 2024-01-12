package com.kakao.rememberBday.global.auth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
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
import org.springframework.web.util.UriComponentsBuilder;

import com.kakao.rememberBday.domain.user.UserMapper;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

@Service
public class KakaoServiceImpl implements KakaoService {
	
	@Autowired
	private UserMapper userMapper;
	
	@Autowired
    private SqlSession sqlSession;
	
	private long userId;
	
	private String accessToken = "";
	private String refreshToken = "";
	
	private final RestTemplate restTemplate;

	public KakaoServiceImpl(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

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
	
	public HashMap<String, Object> getKakaoInfo(String code) throws Exception {
		if (code == null) throw new Exception("Failed get authorization code");
		
//		String accessToken = "";
//		String refreshToken = "";
		
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
		
		System.out.println("accessToken : " + accessToken);
		return getUserInfoWithToken(accessToken);
	}
	
	private HashMap<String, Object> getUserInfoWithToken(String accessToken) throws Exception {
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
		
		// 사용자의 user_id 값
		userId = (long) userInfo.get("user_id");
		
		// DB에서 사용자 정보 조회
		Map<String, Object> existingUser = sqlSession.selectOne("selectUserById", userId);
		
		if (existingUser == null) {
			// 사용자 정보가 없으면 INSERT 실행
			sqlSession.insert("insertUser", userInfo);
			System.out.println("User inserted successfully.");
		} else {
			// 사용자 정보가 이미 있으면 UPDATE 실행
			sqlSession.update("updateUser", userInfo);
			System.out.println("User updated successfully.");
		}
		
		System.out.println("userInfo : " + userInfo);

		return userInfo;
	}
	
	@Override
	public String getAccessTokenByUserId() throws Exception {
		// userId를 활용하여 getKakaoInfo 메소드를 호출하여 accessToken을 얻음
//		HashMap<String, Object> kakaoInfo = getKakaoInfo(String.valueOf(userId));

		// 얻은 정보에서 accessToken 추출
//		String accessToken = (String) kakaoInfo.get("access_token");
		System.out.println("access_token : " + accessToken);

		return accessToken;
	}
	
	@Override
	public ResponseEntity<String> getCalendars(String accessToken) {
		// 헤더 설정
		HttpHeaders headers = createHeaders(accessToken);

		// 파라미터 설정
//		String url = "https://kapi.kakao.com/v2/api/calendar/calendars";
//		String filter = "USER";
		String url = "https://kapi.kakao.com/v2/api/calendar/events";

		// URI 생성
//		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url).queryParam("filter", filter);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
															.queryParam("calendar_id","primary")
															.queryParam("preset","THIS_MONTH");
//		.queryParam("from", "2024-01-01T00:00:00Z")
//		.queryParam("to", "2024-01-31T00:00:00Z")

		// 요청
		// restTemplate.exchange 메서드로 HTTP GET 요청을 보냄
		// builder.toUriString()로 생성된 URI 문자열을 사용
		// HttpHeaders는 이미 설정된 상태로 HttpEntity에 포함됨
		return restTemplate.exchange(builder.toUriString(), HttpMethod.GET, new HttpEntity<>(headers), String.class);
	}

	private HttpHeaders createHeaders(String accessToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + accessToken);
		return headers;
	}
}