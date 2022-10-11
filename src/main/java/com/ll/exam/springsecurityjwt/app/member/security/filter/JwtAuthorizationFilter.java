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

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.debug("JwtAuthorizationFilter 실행됨");
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null) {
            String token = bearerToken.substring("Bearer ".length());

            if (jwtProvider.verify(token)) {
                Map<String, Object> claims = jwtProvider.getClaims(token);
                String username = (String) claims.get("username");
                Member member = memberService.findByUsername(username).orElseThrow(
                        () -> new UsernameNotFoundException("'%s' Username not found.".formatted(username))
                );

                forceAuthentication(member);
            }
        }

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