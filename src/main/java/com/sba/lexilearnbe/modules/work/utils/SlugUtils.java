package com.sba.lexilearnbe.modules.work.utils;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class SlugUtils {

    private SlugUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static String generateSlug(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("")
                .replaceAll("Đ", "D").replaceAll("đ", "d")
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
    }
}