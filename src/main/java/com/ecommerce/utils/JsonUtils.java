package com.ecommerce.utils;

import com.ecommerce.constants.AppConstants;
import com.ecommerce.exceptions.FrameworkException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Step;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * JsonUtils provides methods to read JSON test-data and convert between JSON and Java objects.
 * – JSON_FILE_PATH driven via config (AppConstants.KEY_JSON_PATH) with fallback.
 * – Defines getTestData(String) so DataProviders will compile.
 * – Logs via LogUtils and wraps errors in FrameworkException.
 * – Allure @Step annotations for report visibility.
 */
public final class JsonUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /** Path to JSON file (configurable via config.properties) */
    private static final String JSON_FILE_PATH =
            ConfigReader.containsKey(AppConstants.KEY_JSON_PATH)
            ? ConfigReader.get(AppConstants.KEY_JSON_PATH)
            : "src/main/resources/testdata/users.json";

    private JsonUtils() {
        // prevent instantiation
    }

    /**
     * Read the array under the given key from the JSON file.
     * Expects root object with an array field named `key`.
     *
     * @param key JSON field name whose value is an array
     * @return list of maps, each map = one object in that array
     */
    @Step("Reading JSON test data for key: {key}")
    public static List<Map<String, Object>> getTestData(String key) {
        LogUtils.info("Loading JSON from “" + JSON_FILE_PATH + "”, extracting array: " + key);
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(JSON_FILE_PATH));
            JsonNode root = MAPPER.readTree(bytes);
            JsonNode arrayNode = root.get(key);
            if (arrayNode == null || !arrayNode.isArray()) {
                String msg = "JSON key ‘" + key + "’ not found or not an array in " + JSON_FILE_PATH;
                LogUtils.error(msg);
                throw new FrameworkException(msg);
            }
            List<Map<String,Object>> list = MAPPER.convertValue(
                arrayNode,
                new TypeReference<List<Map<String,Object>>>() {}
            );
            LogUtils.info("Loaded " + list.size() + " records for key: " + key);
            return list;
        } catch (IOException e) {
            String msg = "Error reading JSON file: " + JSON_FILE_PATH;
            LogUtils.error(msg, e);
            throw new FrameworkException(msg, e);
        }
    }

    /** General object→JSON string */
    @Step("Serializing object to JSON")
    public static String toJsonString(Object obj) {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (IOException e) {
            String msg = "Serialization to JSON failed";
            LogUtils.error(msg, e);
            throw new FrameworkException(msg, e);
        }
    }

    /** General JSON string→object */
    @Step("Deserializing JSON to {clazz}")
    public static <T> T fromJsonString(String json, Class<T> clazz) {
        try {
            return MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            String msg = "Deserialization from JSON to " + clazz.getSimpleName() + " failed";
            LogUtils.error(msg, e);
            throw new FrameworkException(msg, e);
        }
    }
}
