package com.musterdekho.controllers;

import com.musterdekho.dtos.SkillRequest;
import com.musterdekho.dtos.UpdateProfileRequest;
import com.musterdekho.dtos.UserProfileResponse;
import com.musterdekho.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUser() {
        String username = getCurrentUsername();
        return ResponseEntity.ok(userService.getCurrentUser(username));
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateCurrentUser(@RequestBody UpdateProfileRequest request) {
        String username = getCurrentUsername();
        return ResponseEntity.ok(userService.updateCurrentUser(username, request));
    }

    @PostMapping("/me/skills")
    public ResponseEntity<UserProfileResponse> addSkill(@RequestBody SkillRequest request) {
        String username = getCurrentUsername();
        return ResponseEntity.ok(userService.addSkillToCurrentUser(username, request));
    }

    @DeleteMapping("/me/skills/{skillId}")
    public ResponseEntity<UserProfileResponse> removeSkill(@PathVariable Long skillId) {
        String username = getCurrentUsername();
        return ResponseEntity.ok(userService.removeSkillFromCurrentUser(username, skillId));
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }
}
