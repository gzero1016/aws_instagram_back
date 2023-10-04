package com.toyproject.instagram.security;

import com.toyproject.instagram.service.PrincipalDetailsService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Date;

// JWT 토큰을 관리해주는 로직
@Component // Ioc 등록
public class JwtTokenProvider {

    private final Key key;
    private final PrincipalDetailsService principalDetailsService;

    // Autowired는 IoC 컨테이너에서 객체를 자동 주입
    // Value는 application.yml에서 변수 데이터를 자동 주입

    // IoC 에서 생성될때 application.yml 에서 jwt.secret 의 값을 가져옴
    public JwtTokenProvider(@Value("${jwt.secret}") String secret, @Autowired PrincipalDetailsService principalDetailsService) {
        key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)); // 키등록
        this.principalDetailsService = principalDetailsService;
    }

    // JWT 토큰을 생성하는 로직
    // 로그인 인증을 Authentication 으로 해서 해당 객체를 갖고 JWT 를 만들어 주는것
    public String generateAccessToken(Authentication authentication) {
        String accessToken = null;

        // getPrincipal 을 하면 UserDetail 정보가 들어있음
        PrincipalUser principalUser = (PrincipalUser) authentication.getPrincipal();

        // 현재 날짜: new Date()
        // 현재 시간: newDate().getTime()
        // 현재 날짜의 현재 시간 부터 24시간 동안 엑세스 토큰의 유효 시간을 지정
        Date tokenExpiresDate = new Date(new Date().getTime() + (1000 * 60 * 60 * 24));

        // 일반적인 builder 패턴과는 다르게 JWT 에서는 compact 로 마무리 함
        accessToken = Jwts.builder()
                .setSubject("AccessToken") // 토큰의 이름
                .claim("username", principalUser.getUsername()) // 키값을 username, principalUser 에서 찾아낸 username 을 가져와 토큰에 넣어둠
                .setExpiration(tokenExpiresDate) // 만료 기간,시간
                .signWith(key, SignatureAlgorithm.HS256) // 키 값설정
                .compact();

        return accessToken;
    }

    // 토큰 유효성 검사 로직
    public Boolean validateToken(String token) {
        try {
            Jwts.parserBuilder() // Jwts.parserBuilder: 복호화하기위해
                    .setSigningKey(key) // 암호화했던 키
                    .build() // 암호화가 풀림
                    .parseClaimsJws(token); // 정보 변형이 있거나 기간이 만료가 되었거나 JWT 토큰을 사용할 수 있는지 없는지 판별 후 T/F 리턴해줌
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    // 토큰 Bearer 제거
    public String convertToken(String bearerToken) {
        String type = "Bearer "; // 없앨거
        // 널인지 확인, 공백인지 확인
        // hasText: 널 확인, 공백 확인을 동시헤 해주는 친구
        // startWith: type 으로 시작하는지 확인
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith(type)) {
            return bearerToken.substring(type.length()); // 만약 위 두 조건에 해당한다면 (Bearer )요정도 길이(7)부터 끝까지 짤라서 리턴해줌
        }
        return "";
    }

    public Authentication getAuthentication(String accessToken) {

        Authentication authentication = null;
        String username = (String) Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(accessToken)
                .getBody()
                .get("username")
                .toString();


        PrincipalUser principalUser = (PrincipalUser) principalDetailsService.loadUserByUsername(username);
//        System.out.println(principalUser);

//        PrincipalUser principalUser = new PrincipalUser();

        authentication = new UsernamePasswordAuthenticationToken(principalUser, null, principalUser.getAuthorities());
        return authentication;
    }
}
