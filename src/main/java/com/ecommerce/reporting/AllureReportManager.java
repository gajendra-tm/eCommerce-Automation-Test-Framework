package com.ecommerce.reporting;

import com.ecommerce.utils.ConfigReader;
import com.ecommerce.utils.LogUtils;
import com.ecommerce.constants.AppConstants;
import com.ecommerce.drivers.DriverManager;
import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.Properties;

/**
 * AllureReportManager handles the configuration and attachments for Allure reporting.
 * <p>
 * Best practices included:
 * - Static initialization of environment properties
 * - Reusable attachment methods (screenshots, logs, page source, text)
 * - Thread-safe, final class with private constructor
 */
public final class AllureReportManager {

    static {
        writeEnvironmentProperties();
    }

    private AllureReportManager() {
        // Prevent instantiation
    }

    /**
     * Writes environment properties to allure-results/environment.properties for better report context.
     */
    private static void writeEnvironmentProperties() {
        Properties props = new Properties();
        try {
            props.setProperty("Environment", ConfigReader.getProperty("env"));
            props.setProperty("Browser", ConfigReader.getProperty("browser"));
            props.setProperty("Base URL", ConfigReader.getProperty("baseUrl"));
            props.setProperty("OS", System.getProperty("os.name") + " " + System.getProperty("os.version"));
            props.setProperty("Java Version", System.getProperty("java.version"));

            try (FileWriter writer = new FileWriter("allure-results/environment.properties")) {
                props.store(writer, "Allure Environment Properties");
            }
        } catch (IOException e) {
            System.err.println("Failed to write Allure environment properties: " + e.getMessage());
        }
    }
    
    
    @Attachment(value = "{0}", type = "text/plain")
    public static String step(String message) {
        return message;
    }

    @Attachment(value = "Screenshot", type = "image/png")
    public static byte[] attachScreenshot(byte[] screenshotBytes) {
        return screenshotBytes;
    }

    @Attachment(value = "Log File", type = "text/plain")
    public static byte[] attachLogFile() {
        String logPath = ConfigReader.containsKey(AppConstants.KEY_LOG_FILE_PATH)
                ? ConfigReader.get(AppConstants.KEY_LOG_FILE_PATH)
                : "logs/app.log";

        try {
            Path path = Path.of(logPath);
            return Files.readAllBytes(path);
        } catch (IOException e) {
            LogUtils.error("Failed to attach log file to Allure report", e);
            return new byte[0];
        }
        }
    
    
    

    /**
     * Attaches a screenshot to Allure report.
     *
     * @param methodName the test method name or custom label
     * @return byte array of the screenshot
     */
    @Attachment(value = "Screenshot - {methodName}", type = "image/png")
    public static byte[] attachScreenshot(String methodName) {
        try {
            return ((TakesScreenshot) DriverManager.getDriver()).getScreenshotAs(OutputType.BYTES);
        } catch (Exception e) {
            System.err.println("Failed to capture screenshot: " + e.getMessage());
            return new byte[0];
        }
    }

    /**
     * Attaches the current page source to Allure report.
     *
     * @return byte array of page source HTML
     */
    @Attachment(value = "Page Source", type = "text/html")
    public static byte[] attachPageSource() {
        try {
            return DriverManager.getDriver().getPageSource().getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.err.println("Failed to capture page source: " + e.getMessage());
            return new byte[0];
        }
    }

    /**
     * Attaches browser console logs to Allure report.
     *
     * @return concatenated console logs as a String
     */
    @Attachment(value = "Browser Console Logs", type = "text/plain")
    public static String attachConsoleLogs() {
        try {
            LogEntries logEntries = DriverManager.getDriver()
                    .manage()
                    .logs()
                    .get(LogType.BROWSER);
            StringBuilder sb = new StringBuilder();
            for (LogEntry entry : logEntries) {
                sb.append(new Date(entry.getTimestamp()))
                  .append(" ")
                  .append(entry.getLevel())
                  .append(" ")
                  .append(entry.getMessage())
                  .append(System.lineSeparator());
            }
            return sb.toString();
        } catch (Exception e) {
            System.err.println("Failed to capture browser console logs: " + e.getMessage());
            return "";
        }
    }

    /**
     * Attaches custom text messages to Allure report.
     *
     * @param name    attachment name
     * @param message message body
     * @return byte array of message
     */
    @Attachment(value = "{name}", type = "text/plain")
    public static byte[] attachText(String name, String message) {
        return message.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Adds a key-value parameter to Allure report.
     *
     * @param key   parameter name
     * @param value parameter value
     */
    public static void addParameter(String key, String value) {
        Allure.parameter(key, value);
    }
}
