package com.cvoptimizer.cv_backend.service;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

@Service
public class PdfParserService {

    public String extractTextFromFile(File file) {
        try (FileInputStream input = new FileInputStream(file)) {
            BodyContentHandler handler = new BodyContentHandler(-1);
            Metadata metadata = new Metadata();
            ParseContext context = new ParseContext();
            PDFParser parser = new PDFParser();
            parser.parse(input, handler, metadata, context);
            return handler.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private Map<String, String> extractSections(String text, List<String> sectionHeaders) {
        Map<String, StringBuilder> sectionContentMap = new LinkedHashMap<>();
        Map<String, String> finalSections = new HashMap<>();
        Set<String> normalizedHeaders = new HashSet<>();
        for (String h : sectionHeaders) {
            String norm = h.trim().toUpperCase();
            normalizedHeaders.add(norm);
            sectionContentMap.put(norm, new StringBuilder());
        }

        String currentSection = null;

        for (String line : text.split("\n")) {
            String trimmedLine = line.trim();
            String upper = trimmedLine.toUpperCase();

            if (normalizedHeaders.contains(upper)) {
                currentSection = upper;
                continue;
            }

            if (currentSection != null && !trimmedLine.isEmpty()) {
                sectionContentMap.get(currentSection).append(trimmedLine).append("\n");
            }
        }

        for (String header : normalizedHeaders) {
            finalSections.put(header, sectionContentMap.get(header).toString().trim());
        }

        return finalSections;
    }

    public String extractFullName(String text) {
        String[] lines = text.split("\n");
        for (String line : lines) {
            if (line.toLowerCase().contains("tom giron")) {
                return line.trim();
            }
        }
        return "Unknown";
    }

    public String extractEmail(String text) {
        return text.lines()
                .filter(line -> line.contains("@"))
                .findFirst()
                .orElse("unknown@example.com")
                .trim();
    }

    public String extractPhone(String text) {
        return text.lines()
                .filter(line -> line.matches(".*\\d{2,}.*"))
                .filter(line -> line.contains("05") || line.contains("972"))
                .findFirst()
                .orElse("unknown")
                .trim();
    }

    public String extractSkills(String text) {
        Map<String, String> sections = extractSections(text, List.of("TECHNICAL SKILLS", "SKILLS"));
        return sections.getOrDefault("TECHNICAL SKILLS", sections.getOrDefault("SKILLS", ""));
    }

    public String extractExperience(String text) {
        Map<String, String> sections = extractSections(text, List.of("PROJECTS", "EXPERIENCE"));
        return sections.getOrDefault("PROJECTS", sections.getOrDefault("EXPERIENCE", ""));
    }

    public String extractEducation(String text) {
        Map<String, String> sections = extractSections(text, List.of("EDUCATION"));
        return sections.getOrDefault("EDUCATION", "");
    }
}
