package com.hyomyang.oauth2login.oauth2.filter;

import com.hyomyang.oauth2login.oauth2.component.TokenExtractor;
import com.hyomyang.oauth2login.oauth2.security.UserPrincipal;
import com.hyomyang.oauth2login.oauth2.security.jwt.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@SuppressWarnings("unchecked")
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider tokenProvider;



    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = TokenExtractor.extractBearer(request.getHeader("Authorization"));

        if(token != null){
            try {
                Jws<Claims> jws = tokenProvider.parseToken(token);

                // refresh 토큰으로 접근 시도 차단(인증으로 인정 X)
                if(!tokenProvider.isRefresh(jws)){
                    // 여기서는 그냥 인증 세팅 안 하고 통과시키면,
                    // 보호된 엔드포인트에서 401로 떨어짐(표준 응답은 entryPoint가 처리)

                    Long userId = Long.valueOf(jws.getPayload().getSubject());
                    Object raw = jws.getPayload().get("roles");
                    List<String> roleUser = raw instanceof List<?> list ?
                            list.stream().map(String::valueOf).collect(Collectors.toList())
                            : List.of("ROLE_USER");

                    UserPrincipal userPrincipal = new UserPrincipal(userId, "", roleUser);

                    List<SimpleGrantedAuthority> authorities = (List<SimpleGrantedAuthority>) userPrincipal.getAuthorities();

                    var auth = new UsernamePasswordAuthenticationToken(userPrincipal, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }

            } catch (io.jsonwebtoken.ExpiredJwtException e) {
                SecurityContextHolder.clearContext();
                request.setAttribute("auth_error", "TOKEN_EXPIRED");

            } catch (io.jsonwebtoken.security.SignatureException e) {
                SecurityContextHolder.clearContext();
                request.setAttribute("auth_error", "TOKEN_INVALID_SIGNATURE");

            } catch (io.jsonwebtoken.MalformedJwtException e) {
                SecurityContextHolder.clearContext();
                request.setAttribute("auth_error", "TOKEN_MALFORMED");

            } catch (io.jsonwebtoken.UnsupportedJwtException e) {
                SecurityContextHolder.clearContext();
                request.setAttribute("auth_error", "TOKEN_UNSUPPORTED");

            } catch (IllegalArgumentException e) {
                SecurityContextHolder.clearContext();
                request.setAttribute("auth_error", "TOKEN_INVALID");

            } catch (Exception e) {
                SecurityContextHolder.clearContext();
                request.setAttribute("auth_error", "TOKEN_INVALID");
            }

        }
        filterChain.doFilter(request, response);
    }
}
