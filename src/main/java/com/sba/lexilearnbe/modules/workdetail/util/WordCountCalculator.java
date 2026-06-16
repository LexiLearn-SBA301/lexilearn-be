package com.sba.lexilearnbe.modules.workdetail.util;

public final class WordCountCalculator {

    private WordCountCalculator() {
    }

    public static int count(String content) {
        if (content == null || content.isBlank()) {
            return 0;
        }

        return content.trim().split("\\s+").length;
    }
}
