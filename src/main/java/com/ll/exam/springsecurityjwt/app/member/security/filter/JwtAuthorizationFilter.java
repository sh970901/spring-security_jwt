package com.ll.exam.springsecurityjwt.app.member.security.filter;

import com.ll.exam.springsecurityjwt.app.member.entity.Member;
import com.ll.exam.springsecurityjwt.app.member.security.entity.MemberContext;
import com.ll.exam.springsecurityjwt.app.member.security.jwt.JwtProvider;
import com.ll.exam.springsecurityjwt.app.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final MemberService memberService;


    //인증 필터 추가
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.debug("JwtAuthorizationFilter 실행됨");
        String bearerToken = request.getHeader("Authorization");

        //토큰은 폐기가 불가능하니 처음에는 리프레쉬 토큰을 썼지만 유효기간을 짧게 두고 계속 보내는 방식이 클라이언트 쪽에서 할일이 많아질거같다고 느꼈습니다. 
        //모든 요청 보낼때마다 로그인이 풀렸는지 토큰이 만료되었는지 체크하는게 불편
        if (bearerToken != null) {
            String token = bearerToken.substring("Bearer ".length());
            // 1차 체크(정보가 변조되지 않았는지 체크)
            if (jwtProvider.verify(token)) {
                Map<String, Object> claims = jwtProvider.getClaims(token);
                // 캐시(레디스)를 통해서
                Member member = memberService.getByUsername__cached((String) claims.get("username"));
                // 2차 체크(화이트리스트에 포함되는지)
                // 블랙리스트는 문제가 되는 애들을 기록, 화이트리스트는 나머지를 다 이상한애로 보고 오직 유효한것만
                if ( memberService.verifyWithWhiteList(member, token) ) {
                    //로그인 처리
                    forceAuthentication(member);
                }
            }
        }
        //다음 필터 진행
        filterChain.doFilter(request, response);
    }

    private void forceAuthentication(Member member) {
        MemberContext memberContext = new MemberContext(member);

        UsernamePasswordAuthenticationToken authentication =
                UsernamePasswordAuthenticationToken.authenticated(
                        memberContext,
                        null,
                        member.getAuthorities()
                );

        //로그인 상태 처리
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }
}