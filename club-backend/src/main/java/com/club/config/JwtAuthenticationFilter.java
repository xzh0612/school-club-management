package com.club.config;

import com.club.entity.User;
import com.club.mapper.UserMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return "OPTIONS".equalsIgnoreCase(request.getMethod())
                || "/api/login".equals(path)
                || "/api/register".equals(path)
                || path.startsWith("/api/auth/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header == null || header.isBlank()) {
            writeUnauthorized(response, "未登录或token缺失");
            return;
        }

        String token = header.startsWith("Bearer ") ? header.substring(7) : header;
        try {
            Claims claims = jwtUtil.parseToken(token);
            Integer userId = Integer.valueOf(claims.getSubject());
            User user = userMapper.findById(userId);
            if (user == null || "inactive".equals(user.getStatus())) {
                writeUnauthorized(response, "账号不存在或已停用");
                return;
            }
            Integer tokenVersion = claims.get("tokenVersion", Integer.class);
            int expectedVersion = user.getTokenVersion() == null ? 0 : user.getTokenVersion();
            if ((tokenVersion == null ? 0 : tokenVersion) != expectedVersion) {
                writeUnauthorized(response, "token已失效，请重新登录");
                return;
            }

            request.setAttribute("userId", userId);
            request.setAttribute("username", user.getUsername());
            request.setAttribute("role", user.getRole());
            request.setAttribute("currentUser", user);

            String role = user.getRole() == null ? "" : user.getRole();
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    user.getUsername(),
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (JwtException | IllegalArgumentException e) {
            writeUnauthorized(response, "token无效或已过期");
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":401,\"msg\":\"" + message + "\",\"data\":null}");
    }
}
