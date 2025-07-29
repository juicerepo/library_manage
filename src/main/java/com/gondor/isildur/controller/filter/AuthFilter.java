package com.gondor.isildur.controller.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gondor.isildur.util.JWTUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;

@WebFilter(urlPatterns = { "/*" })
public class AuthFilter implements Filter {

    // 定义不需要认证的路径，也就是白名单
    private static final List<String> EXCLUDED_PATHS = Arrays.asList(
            "/login",
            "/register",
            "/kaptcha",
            "admin",
            "book"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse rep = (HttpServletResponse) response;
        String requestURI = req.getRequestURI();

        // 检查是否在排除路径中
        boolean isExcluded = EXCLUDED_PATHS.stream().anyMatch(path ->
                requestURI.contains(path) || requestURI.equals(req.getContextPath() + "/")
        );

        if (isExcluded) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = req.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            rep.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header");
            return;
        }

        // 提取JWT令牌（去掉"Bearer "前缀）
        String token = authHeader.substring(7);

        try {
            Claims claims = JWTUtil.verifyToken(token);
            req.setAttribute("id", claims.get("id"));
            req.setAttribute("role", claims.get("role"));
            chain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            rep.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expired");
        } catch (SignatureException e) {
            rep.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token signature error");
        } catch (MalformedJwtException e) {
            rep.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Malformed token");
        } catch (Exception e) {
            rep.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
            e.printStackTrace();
        }
    }
}