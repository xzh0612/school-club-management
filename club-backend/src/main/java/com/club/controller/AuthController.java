package com.club.controller;

import com.club.common.*;
import com.club.entity.*;
import com.club.service.*;
import com.club.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final SecurityContext securityContext;

    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO dto) {
        try {
            return Result.ok(userService.login(dto));
        } catch (RuntimeException e) {
            return Result.error(401, e.getMessage());
        }
    }

    @GetMapping("/user/info")
    public Result<UserVO> info(HttpServletRequest request) {
        return Result.ok(UserVO.from(securityContext.currentUser(request)));
    }

    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        userService.invalidateTokens(securityContext.currentUserId(request));
        return Result.ok();
    }

}
