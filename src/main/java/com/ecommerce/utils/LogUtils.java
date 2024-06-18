package com.ecommerce.utils;

import com.ecommerce.constants.AppConstants;
import com.ecommerce.constants.TimeConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.qameta.allure.Attachment;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * LogUtils provides thread-safe logging utilities:
 * - Console & file logging via Log4j2
 * - Allure attachments for log entries and full log file
 * - Hybrid configuration: defaults via constants, overrides via ConfigReader
 */
public final class LogUtils {

    private static final Logger logger = LogManager.getLogger(LogUtils.class);
    private static final String LOG_FILE_PATH = ConfigReader.containsKey(AppConstants.KEY_LOG_FILE_PATH)
            ? ConfigReader.get(AppConstants.KEY_LOG_FILE_PATH)
            : "logs/app.log";

    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter
            .ofPattern(ConfigReader.containsKey(AppConstants.KEY_LOG_TIMESTAMP_PATTERN)
                    ? ConfigReader.get(AppConstants.KEY_LOG_TIMESTAMP_PATTERN)
                    : TimeConstants.TIMESTAMP_PATTERN);

    private LogUtils() {
        // Prevent instantiation
    }

    public static synchronized void info(String message) {
        String entry = format("INFO", message);
        logger.info(message);
        attachLogEntry(entry);
    }

    public static synchronized void debug(String message) {
        String entry = format("DEBUG", message);
        logger.debug(message);
        attachLogEntry(entry);
    }

    public static synchronized void warn(String message) {
        String entry = format("WARN", message);
        logger.warn(message);
        attachLogEntry(entry);
    }

    public static synchronized void warn(String message, Throwable t) {
        String stack = getStackTrace(t);
        String entry = format("WARN", message + "\n" + stack);
        logger.warn(message, t);
        attachLogEntry(entry);
    }

    public static synchronized void error(String message) {
        String entry = format("ERROR", message);
        logger.error(message);
        attachLogEntry(entry);
    }

    public static synchronized void error(String message, Throwable t) {
        String stack = getStackTrace(t);
        String entry = format("ERROR", message + "\n" + stack);
        logger.error(message, t);
        attachLogEntry(entry);
    }

    private static String format(String level, String message) {
        String time = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String thread = Thread.currentThread().getName();
        return String.format("%s [%s] %s - %s", time, thread, level, message);
    }

    private static String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    /**
     * Public hook to attach a single log entry into the Allure report.
     */
    @Attachment(value = "Log Entry", type = "text/plain")
    public static String attachLogEntry(String entry) {
        return entry;
    }

    /**
     * Attach the entire log file (as bytes) into the Allure report.
     */
    @Attachment(value = "Complete Log File", type = "text/plain")
    public static byte[] attachLogFile() {
        try {
            Path path = Path.of(LOG_FILE_PATH);
            return Files.readAllBytes(path);
        } catch (IOException e) {
            logger.error("Unable to read log file for attachment", e);
            return new byte[0];
        }
    }
}
