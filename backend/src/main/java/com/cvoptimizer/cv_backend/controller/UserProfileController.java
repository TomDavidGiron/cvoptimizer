package com.cvoptimizer.cv_backend.controller;

import com.cvoptimizer.cv_backend.dto.UserProfileDto;
import com.cvoptimizer.cv_backend.entity.UserProfile;
import com.cvoptimizer.cv_backend.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserProfileController {

    private final UserProfileService userProfileService;

    @Autowired
    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @PostMapping
    public UserProfileDto createUserProfile(@RequestBody UserProfile userProfile) {
        UserProfile saved = userProfileService.saveUserProfile(userProfile);
        return mapToDto(saved);
    }

    @GetMapping("/{id}")
    public UserProfileDto getUserProfile(@PathVariable Long id) {
        Optional<UserProfile> userProfile = userProfileService.getUserProfileById(id);
        return userProfile.map(this::mapToDto).orElse(null); // or throw exception if preferred
    }

    private UserProfileDto mapToDto(UserProfile userProfile) {
        return new UserProfileDto(
                userProfile.getId(),
                userProfile.getFullName(),
                userProfile.getEmail(),
                userProfile.getPhone(),
                userProfile.getSkills(),
                userProfile.getExperience(),
                userProfile.getEducation()
        );
    }
}


