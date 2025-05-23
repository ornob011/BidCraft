package com.dsi.hackathon.util;

public class TextUtils {

    public static String cleanUpText(String text) {
        return text.replaceAll("\\s+", " ")           // Replace all whitespace (spaces, tabs, newlines) with a single space
            .replaceAll("(?m)^\\s+", "")                   // Remove leading whitespace on lines
            .replaceAll("(?m)\\s+$", "")                   // Remove trailing whitespace on lines
            .replaceAll("\\n{2,}", "\n\n")                 // Collapse multiple blank lines
            .trim();
    }
}
