package com.rapidobackup.console.common.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Utility class for generating URL-friendly slugs from text.
 *
 * A slug is a simplified version of a string that:
 * - Contains only lowercase letters, numbers, and hyphens
 * - Has no spaces or special characters
 * - Is suitable for use in URLs, identifiers, and file names
 *
 * Example: "Hello World! - My Company" → "hello-world-my-company"
 */
public final class SlugUtils {

    private static final Pattern SPECIAL_CHARS = Pattern.compile("[^a-zA-Z0-9\\s-]");
    private static final Pattern WHITESPACE_AND_UNDERSCORES = Pattern.compile("[\\s_]+");
    private static final Pattern MULTIPLE_HYPHENS = Pattern.compile("-+");
    private static final Pattern LEADING_TRAILING_HYPHENS = Pattern.compile("^-+|-+$");

    private SlugUtils() {
        // Utility class
    }

    /**
     * Generates a URL-friendly slug from the given text.
     *
     * @param text the input text to convert to a slug
     * @return a slug representation of the text, or empty string if input is null/empty
     */
    public static String generateSlug(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "";
        }

        // Convert to lowercase and normalize Unicode characters
        String normalized = Normalizer.normalize(text.toLowerCase(Locale.ROOT), Normalizer.Form.NFD);

        // Remove accents and non-ASCII characters
        String withoutAccents = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        // Replace special characters (except letters, numbers, spaces, hyphens) with hyphens
        String withoutSpecials = SPECIAL_CHARS.matcher(withoutAccents).replaceAll("-");

        // Replace whitespace and underscores with hyphens
        String hyphenated = WHITESPACE_AND_UNDERSCORES.matcher(withoutSpecials).replaceAll("-");

        // Replace multiple consecutive hyphens with single hyphen
        String singleHyphens = MULTIPLE_HYPHENS.matcher(hyphenated).replaceAll("-");

        // Remove leading and trailing hyphens
        String trimmed = LEADING_TRAILING_HYPHENS.matcher(singleHyphens).replaceAll("");

        return trimmed;
    }

    /**
     * Generates a unique slug by appending a counter if the base slug already exists.
     *
     * @param baseText the input text to convert to a slug
     * @param existingSlugChecker function to check if a slug already exists
     * @return a unique slug
     */
    public static String generateUniqueSlug(String baseText, java.util.function.Predicate<String> existingSlugChecker) {
        String baseSlug = generateSlug(baseText);

        if (baseSlug.isEmpty()) {
            baseSlug = "untitled";
        }

        String slug = baseSlug;
        int counter = 1;

        while (existingSlugChecker.test(slug)) {
            slug = baseSlug + "-" + counter;
            counter++;
        }

        return slug;
    }

    /**
     * Validates if a string is a valid slug format.
     *
     * @param slug the string to validate
     * @return true if the string is a valid slug, false otherwise
     */
    public static boolean isValidSlug(String slug) {
        if (slug == null || slug.trim().isEmpty()) {
            return false;
        }

        // Must contain only lowercase letters, numbers, and hyphens
        // Must not start or end with hyphen
        // Must not contain consecutive hyphens
        return slug.matches("^[a-z0-9]+(-[a-z0-9]+)*$");
    }

    /**
     * Sanitizes an existing slug to ensure it follows the correct format.
     *
     * @param slug the slug to sanitize
     * @return a sanitized slug
     */
    public static String sanitizeSlug(String slug) {
        return generateSlug(slug);
    }
}