package de.coachkompass.backend.domain.util;

import java.text.Normalizer;

public final class SlugUtil {
    private SlugUtil() {}

    public static String slugify(String input) {
        if (input == null) return "coach";
        String s = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        s = s.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        return s.isBlank() ? "coach" : s;
    }
}
