package com.ecommerce.constants;

import java.time.Duration;

/**
 * Time-related constants for use throughout the automation framework.
 * <p>
 * Purpose:
 * - Centralize duration values to avoid magic numbers in code
 * - Provide default values that can be overridden via configuration
 * - Ensure consistency in waits, timeouts, and sleep intervals
 */
public final class TimeConstants {

    private TimeConstants() {
        // Prevent instantiation
    }

    // Default durations for Selenium waits (in seconds or milliseconds)
    public static final long DEFAULT_IMPLICIT_WAIT_SECONDS = 10;
    public static final long DEFAULT_EXPLICIT_WAIT_SECONDS = 20;
    public static final long DEFAULT_FLUENT_TIMEOUT_SECONDS = 30;
    public static final long DEFAULT_POLLING_INTERVAL_MS = 500;

    // Typed Duration constants for direct usage
    public static final Duration IMPLICIT_WAIT = Duration.ofSeconds(DEFAULT_IMPLICIT_WAIT_SECONDS);
    public static final Duration EXPLICIT_WAIT = Duration.ofSeconds(DEFAULT_EXPLICIT_WAIT_SECONDS);
    public static final Duration FLUENT_WAIT_TIMEOUT = Duration.ofSeconds(DEFAULT_FLUENT_TIMEOUT_SECONDS);
    public static final Duration FLUENT_WAIT_POLLING = Duration.ofMillis(DEFAULT_POLLING_INTERVAL_MS);

    // Common hard-wait durations (use sparingly)
    public static final Duration SHORT_SLEEP = Duration.ofMillis(500);
    public static final Duration MEDIUM_SLEEP = Duration.ofSeconds(2);
    public static final Duration LONG_SLEEP = Duration.ofSeconds(5);

    // Date/time formatting patterns
    public static final String TIMESTAMP_PATTERN = "yyyyMMdd_HHmmss_SSS";
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
}
