package com.ecommerce.utils;

import com.ecommerce.constants.AppConstants;
import com.ecommerce.constants.TimeConstants;
import com.ecommerce.drivers.DriverManager;
import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ScreenshotUtils provides utilities for capturing and storing screenshots:
 * - Takes WebDriver screenshots as bytes for Allure attachments
 * - Saves screenshots to disk with timestamped filenames
 * - Thread-safe static methods
 * - Integration with LogUtils and Allure reporting
 */
public final class ScreenshotUtils {

	private static final String SCREENSHOT_DIR = ConfigReader.containsKey(AppConstants.KEY_SCREENSHOT_PATH)
            ? ConfigReader.get(AppConstants.KEY_SCREENSHOT_PATH)
            : "screenshots";

    // timestamp formatter for filenames
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern(
            ConfigReader.containsKey(AppConstants.KEY_LOG_TIMESTAMP_PATTERN)
                    ? ConfigReader.get(AppConstants.KEY_LOG_TIMESTAMP_PATTERN)
                    : TimeConstants.TIMESTAMP_PATTERN
    );

    private ScreenshotUtils() {
        // Prevent instantiation
    }

    /**
     * Captures a screenshot as bytes and attaches it to Allure report.
     *
     * @param name descriptive name for the screenshot
     * @return byte[] screenshot bytes
     */
    @Attachment(value = "Screenshot - {name}", type = "image/png")
    public static byte[] captureScreenshotToAllure(String name) {
        try {
            byte[] screenshot = ((TakesScreenshot) DriverManager.getDriver())
                    .getScreenshotAs(OutputType.BYTES);
            LogUtils.info("Captured screenshot for: " + name);
            return screenshot;
        } catch (Exception e) {
            LogUtils.error("Failed to capture screenshot for: " + name, e);
            return new byte[0];
        }
    }

    /**
     * Captures a screenshot, saves it to disk, logs the path, and attaches to Allure.
     *
     * @param name descriptive name for the screenshot file
     * @return String full path of saved screenshot
     */
    public static String captureAndSaveScreenshot(String name) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String fileName = String.format("%s_%s.png", name.replaceAll("\\s+", "_"), timestamp);
        Path outputPath = Paths.get(SCREENSHOT_DIR, fileName);
        try {
            // Ensure directory exists
            Files.createDirectories(outputPath.getParent());

            // Capture screenshot bytes
            byte[] screenshot = ((TakesScreenshot) DriverManager.getDriver())
                    .getScreenshotAs(OutputType.BYTES);

            // Write to file
            Files.write(outputPath, screenshot);
            LogUtils.info("Saved screenshot to: " + outputPath.toString());

            // Attach to Allure
            captureScreenshotToAllure(name);

            return outputPath.toString();
        } catch (IOException e) {
            LogUtils.error("Failed to save screenshot to disk for: " + name, e);
            return null;
        }
    }
}
