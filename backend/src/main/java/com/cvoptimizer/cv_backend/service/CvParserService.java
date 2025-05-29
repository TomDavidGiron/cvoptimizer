package com.cvoptimizer.cv_backend.service;

import com.cvoptimizer.cv_backend.entity.UserProfile;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CvParserService {

    public UserProfile parse(String rawText) {
        UserProfile user = new UserProfile();

        // Extract full name (very simple pattern)
        user.setFullName(extractLineByKeyword(rawText, "TOM GIRON")); // fallback

        // Email
        user.setEmail(extractPattern(rawText, "[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+"));

        // Phone number
        user.setPhone(extractPattern(rawText, "\\d{3}[- .]?\\d{3}[- .]?\\d{4}|\\d{10}|\\+972\\d{9}"));

        // Skills
        user.setSkills(extractSection(rawText, "TECHNICAL SKILLS", "PROJECTS"));

        // Experience
        user.setExperience(extractSection(rawText, "PROJECTS", "EDUCATION"));

        // Education
        user.setEducation(extractSection(rawText, "EDUCATION", "UDEMY|https|CERTIFICATE"));

        return user;
    }

    private String extractPattern(String text, String regex) {
        Matcher matcher = Pattern.compile(regex).matcher(text);
        return matcher.find() ? matcher.group() : "";
    }

    private String extractLineByKeyword(String text, String keyword) {
        for (String line : text.split("\n")) {
            if (line.toLowerCase().contains(keyword.toLowerCase())) {
                return line.trim();
            }
        }
        return "";
    }

    private String extractSection(String text, String startKeyword, String endKeyword) {
        int start = text.indexOf(startKeyword);
        int end = text.indexOf(endKeyword, start);
        if (start != -1 && end != -1 && end > start) {
            return text.substring(start, end).trim();
        }
        return "";
    }
}
