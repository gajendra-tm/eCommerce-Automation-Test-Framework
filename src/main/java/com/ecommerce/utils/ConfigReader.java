package com.ecommerce.utils;

import io.qameta.allure.Step;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Utility class to read configuration properties with fallback mechanism.
 * Supports environment-based configuration: dev, qa, prod, with config.properties as base layer.
 */
public final class ConfigReader {

    private static final String BASE_CONFIG_PATH = "src/main/resources/config/";
    private static final String DEFAULT_ENV = "dev";
    private static final Properties baseProperties = new Properties();
    private static final Properties envProperties = new Properties();

    private ConfigReader() {
        // Prevent instantiation
    }

    static {
        loadBaseProperties();
        loadEnvProperties();
    }

    @Step("Loading base configuration from config.properties")
    private static void loadBaseProperties() {
        try (InputStream in = new FileInputStream(BASE_CONFIG_PATH + "config.properties")) {
            baseProperties.load(in);
            LogUtils.info("Loaded base configuration from config.properties");
        } catch (IOException e) {
            LogUtils.error("Failed to load base config: config.properties", e);
            throw new RuntimeException("Failed to load base config.properties", e);
        }
    }

    @Step("Loading environment-specific configuration for env={}")
    private static void loadEnvProperties() {
        String env = System.getProperty("env", DEFAULT_ENV).toLowerCase();
        String path = BASE_CONFIG_PATH + env + ".properties";
        try (InputStream in = new FileInputStream(path)) {
            envProperties.load(in);
            LogUtils.info("Loaded environment configuration from: " + path);
        } catch (IOException e) {
            LogUtils.error("Failed to load environment config: " + path, e);
            throw new RuntimeException("Failed to load environment config file: " + path, e);
        }
    }

    @Step("Getting config property: {key}")
    public static String get(String key) {
        String value = envProperties.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            value = baseProperties.getProperty(key);
        }
        if (value == null || value.trim().isEmpty()) {
            String msg = "Missing config key: " + key;
            LogUtils.error(msg, new IllegalArgumentException(msg));
            throw new RuntimeException(msg);
        }
        return value.trim();
    }

    @Step("Getting long config property: {key}")
    public static long getLong(String key) {
        try {
            return Long.parseLong(get(key));
        } catch (NumberFormatException e) {
            LogUtils.error("Invalid long for key: " + key, e);
            throw new RuntimeException("Invalid long value for key: " + key, e);
        }
    }

    @Step("Checking if config key exists: {key}")
    public static boolean containsKey(String key) {
        return envProperties.containsKey(key) || baseProperties.containsKey(key);
    }

    @Step("Getting all merged properties")
    public static Properties getAllProperties() {
        Properties all = new Properties();
        all.putAll(baseProperties);
        all.putAll(envProperties);
        return all;
    }

    @Step("getProperty alias for get() key={key}")
    public static String getProperty(String key) {
        return get(key);
    }
}
