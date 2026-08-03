package com.seig.labzuoye_2.filter;

import com.seig.labzuoye_2.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();
        // 登录和注册放行
        if (uri.contains("/api/user/login") || uri.contains("/api/user/register")) {
            filterChain.doFilter(request, response);
            return;
        }

        // OPTIONS放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = request.getHeader("token");
        if (token != null && !token.isEmpty()) {
            try {
                Claims claims = jwtUtil.parseToken(token);
                String role = claims.get("role", String.class);
                Integer userId = claims.get("userId", Integer.class);

                System.out.println("🔐 JWT认证: userId=" + userId + ", role=" + role);

                // ✅ 将角色转换为Spring Security的GrantedAuthority
                String roleName = role;
                // 如果角色不包含"ROLE_"前缀，添加它
                if (!role.startsWith("ROLE_")) {
                    roleName = "ROLE_" + role;
                }

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userId,
                                null,
                                Collections.singletonList(new SimpleGrantedAuthority(roleName))
                        );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                System.out.println("❌ token解析失败: " + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}