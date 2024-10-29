package com.ecommerce.utils;

import com.ecommerce.constants.AppConstants;
import com.ecommerce.exceptions.FrameworkException;
import io.qameta.allure.Step;
import org.testng.annotations.DataProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;

/**
 * Centralized TestNG DataProviders that delegate to ExcelUtils, JsonUtils,
 * and also support database and CSV sources.
 */
public final class DataProviders {

    private DataProviders() {
        // prevent instantiation
    }

    // JDBC configuration from config.properties via AppConstants
    private static final String DB_URL         = ConfigReader.get(AppConstants.KEY_DB_URL);
    private static final String DB_USER        = ConfigReader.get(AppConstants.KEY_DB_USER);
    private static final String DB_PASSWORD    = ConfigReader.get(AppConstants.KEY_DB_PASSWORD);
    private static final String DB_QUERY_PREFIX = AppConstants.KEY_DB_QUERY_PREFIX;

    // CSV file path (configurable)
    private static final String CSV_PATH = ConfigReader.containsKey(AppConstants.KEY_CSV_PATH)
            ? ConfigReader.get(AppConstants.KEY_CSV_PATH)
            : "src/main/resources/testdata/data.csv";

    // ─────────────────────────────────────────────────────────────────────────────
    // Excel DataProvider
    // ─────────────────────────────────────────────────────────────────────────────
    @DataProvider(name = "excelDataProvider", parallel = true)
    @Step("Providing Excel data for test: {method}")
    public static Iterator<Object[]> excelDataProvider(Method method) {
        String sheetName = method.getName();
        LogUtils.info("Loading Excel data for sheet: " + sheetName);
        List<Map<String, String>> rows;
        try {
            rows = ExcelUtils.getTestData(sheetName);
        } catch (Exception e) {
            LogUtils.error("Failed to load Excel data for sheet: " + sheetName, e);
            throw new FrameworkException("Excel data load failure for sheet: " + sheetName, e);
        }
        return rows.stream()
                   .map(row -> new Object[]{ row })
                   .iterator();
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // JSON DataProvider
    // ─────────────────────────────────────────────────────────────────────────────
    @DataProvider(name = "jsonDataProvider", parallel = true)
    @Step("Providing JSON data for test: {method}")
    public static Iterator<Object[]> jsonDataProvider(Method method) {
        String dataKey = method.getName();
        LogUtils.info("Loading JSON data for key: " + dataKey);
        List<Map<String, Object>> records;
        try {
            records = JsonUtils.getTestData(dataKey);
        } catch (Exception e) {
            LogUtils.error("Failed to load JSON data for key: " + dataKey, e);
            throw new FrameworkException("JSON data load failure for key: " + dataKey, e);
        }
        return records.stream()
                      .map(record -> new Object[]{ record })
                      .iterator();
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Database DataProvider
    // ─────────────────────────────────────────────────────────────────────────────
    @DataProvider(name = "dbDataProvider", parallel = false)
    @Step("Providing database data for test: {method}")
    public static Object[][] dbDataProvider(Method method) {
        String queryKey = DB_QUERY_PREFIX + method.getName();
        String sql = ConfigReader.containsKey(queryKey)
                ? ConfigReader.get(queryKey)
                : failMissingQuery(method.getName());

        List<Object[]> rows = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            ResultSetMetaData md = rs.getMetaData();
            int cols = md.getColumnCount();
            while (rs.next()) {
                Object[] row = new Object[cols];
                for (int i = 1; i <= cols; i++) {
                    row[i - 1] = rs.getObject(i);
                }
                rows.add(row);
            }
            LogUtils.info("Fetched " + rows.size() + " rows from DB for query key: " + queryKey);
        } catch (SQLException e) {
            LogUtils.error("Database access error for query: " + sql, e);
            throw new FrameworkException("Error querying database for test: " + method.getName(), e);
        }
        return rows.toArray(new Object[0][]);
    }

    private static String failMissingQuery(String testName) {
        String msg = "No database query configured for test: " + testName;
        LogUtils.error(msg);
        throw new FrameworkException(msg);
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // CSV DataProvider
    // ─────────────────────────────────────────────────────────────────────────────
    @DataProvider(name = "csvDataProvider", parallel = true)
    @Step("Providing CSV data from file")
    public static Iterator<Object[]> csvDataProvider() {
        LogUtils.info("Loading CSV data from: " + CSV_PATH);
        List<Object[]> rows = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(CSV_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                rows.add(values);
            }
            LogUtils.info("Read " + rows.size() + " rows from CSV");
        } catch (IOException e) {
            LogUtils.error("Failed reading CSV data from: " + CSV_PATH, e);
            throw new FrameworkException("CSV data load failure", e);
        }
        return rows.iterator();
    }
}
