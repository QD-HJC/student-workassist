package com.seig.labzuoye_2.interceptor;

import com.seig.labzuoye_2.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        System.out.println("🔐 拦截请求: " + uri);

        // 放行登录和注册
        if (uri.contains("/api/user/login") || uri.contains("/api/user/register")) {
            return true;
        }

        // 放行OPTIONS
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String token = request.getHeader("token");
        System.out.println("🔐 token: " + (token != null ? token.substring(0, Math.min(20, token.length())) + "..." : "null"));

        if (token == null || token.isEmpty()) {
            response.setContentType("application/json;charset=utf-8");
            response.setStatus(401);
            response.getWriter().write("{\"code\":401,\"msg\":\"未登录\"}");
            return false;
        }

        try {
            Claims claims = jwtUtil.parseToken(token);
            request.setAttribute("userId", claims.get("userId"));
            request.setAttribute("role", claims.get("role"));
            System.out.println("🔐 解析成功");
            return true;
        } catch (Exception e) {
            System.out.println("🔐 token解析失败: " + e.getMessage());
            response.setContentType("application/json;charset=utf-8");
            response.setStatus(401);
            response.getWriter().write("{\"code\":401,\"msg\":\"token失效\"}");
            return false;
        }
    }
}