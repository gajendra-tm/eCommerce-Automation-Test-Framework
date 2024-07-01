package com.ecommerce.utils;

import io.qameta.allure.Step;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.Objects;

/**
 * Utility class for generating random values for testing.
 * Best Practices:
 * - Thread-safe
 * - SecureRandom usage
 * - Logging included
 */
public final class RandomUtils {

    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = UPPER.toLowerCase(Locale.ROOT);
    private static final String DIGITS = "0123456789";
    private static final String ALPHANUM = UPPER + LOWER + DIGITS;
    private static final SecureRandom RANDOM = new SecureRandom();

    private RandomUtils() {
        // Prevent instantiation
    }

    @Step("Generating random alphanumeric string of length {length}")
    public static String generateRandomAlphaNumeric(int length) {
        return generateRandomString(ALPHANUM, length);
    }

    @Step("Generating random numeric string of length {length}")
    public static String generateRandomNumeric(int length) {
        return generateRandomString(DIGITS, length);
    }

    @Step("Generating random alphabetic string of length {length}")
    public static String generateRandomAlphabetic(int length) {
        return generateRandomString(UPPER + LOWER, length);
    }

    private static String generateRandomString(String characters, int length) {
        if (length < 1) throw new IllegalArgumentException("Length must be at least 1");
        Objects.requireNonNull(characters);

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(RANDOM.nextInt(characters.length())));
        }
        String result = sb.toString();
        LogUtils.info("Generated random string: " + result);
        return result;
    }

    @Step("Generating random email with prefix: {prefix}")
    public static String generateRandomEmail(String prefix) {
        String email = prefix + generateRandomAlphaNumeric(6) + "@testmail.com";
        LogUtils.info("Generated random email: " + email);
        return email;
    }

    @Step("Generating random integer between {min} and {max}")
    public static int generateRandomIntInRange(int min, int max) {
        if (min >= max) throw new IllegalArgumentException("max must be greater than min");
        int value = RANDOM.nextInt((max - min) + 1) + min;
        LogUtils.info("Generated random int: " + value);
        return value;
    }

    @Step("Generating random boolean")
    public static boolean generateRandomBoolean() {
        boolean value = RANDOM.nextBoolean();
        LogUtils.info("Generated random boolean: " + value);
        return value;
    }
}