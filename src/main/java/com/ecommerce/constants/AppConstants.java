package com.ecommerce.constants;

/**
 * Application-level constants used across the eCommerce automation framework.
 * <p>
 * Purpose:
 * - Centralize fixed values (e.g., configuration keys, file paths, group names)
 * - Avoid magic strings/numbers in code
 * - Facilitate maintainability and consistency
 */
public final class AppConstants {

    private AppConstants() {
        // Prevent instantiation
    }

    // Configuration property keys
    public static final String KEY_ENV = "env";
    public static final String KEY_BROWSER = "browser";
    public static final String KEY_BASE_URL = "baseUrl";
    public static final String KEY_IMPLICIT_WAIT = "implicit.wait";
    public static final String KEY_EXPLICIT_WAIT = "explicit.wait";
    public static final String KEY_POLLING_INTERVAL = "polling.interval";
    public static final String KEY_SCREENSHOT_PATH = "screenshot.path";
    public static final String KEY_LOG_FILE_PATH = "log.file.path";
    public static final String KEY_LOG_LEVEL = "log.level";
    public static final String KEY_LOG_TIMESTAMP_PATTERN = "log.timestamp.pattern";
    public static final String KEY_ALLURE_RESULTS_DIR = "allure.results.dir";
    public static final String KEY_RETRY_COUNT = "retry.count";

    // File and directory paths
    public static final String CONFIG_DIR = "src/main/resources/config/";
    public static final String DEV_CONFIG = CONFIG_DIR + "dev.properties";
    public static final String QA_CONFIG = CONFIG_DIR + "qa.properties";
    public static final String PROD_CONFIG = CONFIG_DIR + "prod.properties";

    // TestNG group names
    public static final String GROUP_SMOKE = "smoke";
    public static final String GROUP_REGRESSION = "regression";

    // Allure report labels
    public static final String LABEL_BROWSER = "Browser";
    
 // Database Configuration
    public static final String KEY_DB_URL = "db.url";
    public static final String KEY_DB_USER = "db.user";
    public static final String KEY_DB_PASSWORD = "db.password";
    public static final String KEY_DB_QUERY_PREFIX = "db.query.";

    // CSV File Path
    public static final String KEY_CSV_PATH = "csv.path";

    // JSON/Excel Test Data
    public static final String KEY_JSON_PATH = "json.path";
    public static final String KEY_EXCEL_PATH = "excel.path";
}
