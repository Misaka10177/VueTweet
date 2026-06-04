package com.twitter.controller;

import com.twitter.entity.User;
import com.twitter.repo.UserRepo;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.util.*;

@RestController
public class UserController {

    private final UserRepo userRepo;

    public UserController(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @GetMapping("/users")
    public List<Map<String, Object>> getUsers() {
        List<User> users = userRepo.findAll();
        List<Map<String, Object>> result = new ArrayList<>();
        for (User u : users) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", u.getId());
            map.put("name", u.getName());
            map.put("profilePhoto", u.getProfilePhoto());
            result.add(map);
        }
        return result;
    }

    @GetMapping("/currentUser")
    public Map<String, String> getCurrentUser(HttpServletRequest request) {
        Map<String, String> result = new HashMap<>();
        String userId = (String) request.getAttribute("userId");
        User user = userRepo.findById(userId).orElse(null);
        if (user != null) {
            result.put("userId", user.getId());
            result.put("username", user.getName());
        }
        return result;
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
        boolean exists = userRepo.existsById(userName.trim());
        if (exists) {
            result.put("status", "success");
        } else {
            result.put("status", "error");
            result.put("message", "用户 " + userName.trim() + " 不存在");
        }
        return result;
    }
}
