package com.cvoptimizer.cv_backend.controller;

import com.cvoptimizer.cv_backend.dto.UserProfileDto;
import com.cvoptimizer.cv_backend.entity.UserProfile;
import com.cvoptimizer.cv_backend.service.PdfParserService;
import com.cvoptimizer.cv_backend.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private static final String UPLOAD_DIR = "uploads/";

    private final PdfParserService pdfParserService;
    private final UserProfileService userProfileService;

    @Autowired
    public UploadController(PdfParserService pdfParserService, UserProfileService userProfileService) {
        this.pdfParserService = pdfParserService;
        this.userProfileService = userProfileService;
    }

    @PostMapping
    public ResponseEntity<UserProfileDto> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            // Save the file to disk
            Files.createDirectories(Paths.get(UPLOAD_DIR));
            String filename = StringUtils.cleanPath(file.getOriginalFilename());
            Path path = Paths.get(UPLOAD_DIR + filename);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            // Parse the file
            File uploadedFile = path.toFile();
            String extractedText = pdfParserService.extractTextFromFile(uploadedFile);

            // Extract structured fields
            String fullName = pdfParserService.extractFullName(extractedText);
            String email = pdfParserService.extractEmail(extractedText);
            String phone = pdfParserService.extractPhone(extractedText);
            String skills = pdfParserService.extractSkills(extractedText);
            String experience = pdfParserService.extractExperience(extractedText);
            String education = pdfParserService.extractEducation(extractedText);

            // Create and save the UserProfile entity
            UserProfile profile = UserProfile.builder()
                    .fullName(fullName)
                    .email(email)
                    .phone(phone)
                    .skills(skills)
                    .experience(experience)
                    .education(education)
                    .build();

            UserProfile saved = userProfileService.saveUserProfile(profile);

            // Return as DTO
            UserProfileDto dto = new UserProfileDto(
                    saved.getId(),
                    saved.getFullName(),
                    saved.getEmail(),
                    saved.getPhone(),
                    saved.getSkills(),
                    saved.getExperience(),
                    saved.getEducation()
            );

            return ResponseEntity.ok(dto);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
