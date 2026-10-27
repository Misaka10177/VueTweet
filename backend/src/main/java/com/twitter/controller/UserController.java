package com.twitter.controller;

import com.twitter.dto.CurrentUserDto;
import com.twitter.dto.UserSummaryDto;
import com.twitter.security.AuthPrincipal;
import com.twitter.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public List<UserSummaryDto> getUsers() {
        return userService.listUsers();
    }

    @GetMapping("/currentUser")
    public CurrentUserDto getCurrentUser(@AuthenticationPrincipal AuthPrincipal principal) {
        return userService.currentUser(principal.getUserId());
    }

    @PostMapping("/checkUser")
    public Map<String, String> checkUser(@RequestBody Map<String, String> body) {
        Map<String, String> result = new HashMap<>();
        String userName = body.get("username");
        if (userName == null || userName.trim().isEmpty()) {
            result.put("status", "error");
            result.put("message", "用户名不能为空");
            return result;
        }
        if (userService.exists(userName.trim())) {
            result.put("status", "success");
        } else {
            result.put("status", "error");
            result.put("message", "用户 " + userName.trim() + " 不存在");
        }
        return result;
    }
}
