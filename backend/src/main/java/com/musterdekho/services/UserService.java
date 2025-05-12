package com.musterdekho.services;

import com.musterdekho.dtos.SkillRequest;
import com.musterdekho.dtos.UpdateProfileRequest;
import com.musterdekho.dtos.UserProfileResponse;
import com.musterdekho.models.Skill;
import com.musterdekho.models.User;
import com.musterdekho.repositories.SkillRepository;
import com.musterdekho.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SkillRepository skillRepository;

    public UserProfileResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapUserToProfile(user);
    }

    public UserProfileResponse getCurrentUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapUserToProfile(user);
    }

    @Transactional
    public UserProfileResponse updateCurrentUser(String username, UpdateProfileRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (request.getFullName() != null)
            user.setFullName(request.getFullName());
        if (request.getHeadline() != null)
            user.setHeadline(request.getHeadline());
        if (request.getAbout() != null)
            user.setAbout(request.getAbout());
        if (request.getProfileImage() != null)
            user.setProfileImage(request.getProfileImage());
        userRepository.save(user);
        return mapUserToProfile(user);
    }

    @Transactional
    public UserProfileResponse addSkillToCurrentUser(String username, SkillRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Skill skill = Skill.builder()
                .user(user)
                .skillName(request.getSkillName())
                .build();
        skillRepository.save(skill);
        return mapUserToProfile(user);
    }

    @Transactional
    public UserProfileResponse removeSkillFromCurrentUser(String username, Long skillId) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new RuntimeException("Skill not found"));
        if (!skill.getUser().getUsername().equals(username))
            throw new RuntimeException("You can only delete your own skill");
        skillRepository.delete(skill);
        return mapUserToProfile(skill.getUser());
    }

    private UserProfileResponse mapUserToProfile(User user) {
        List<String> skills = user.getId() != null ? skillRepository.findAll().stream()
                .filter(s -> s.getUser().getId().equals(user.getId()))
                .map(Skill::getSkillName).collect(Collectors.toList()) : List.of();
        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .headline(user.getHeadline())
                .about(user.getAbout())
                .profileImage(user.getProfileImage())
                .skills(skills)
                .build();
    }
}
