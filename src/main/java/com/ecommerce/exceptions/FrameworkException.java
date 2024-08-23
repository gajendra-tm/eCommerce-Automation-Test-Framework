package com.ecommerce.exceptions;

import com.ecommerce.utils.LogUtils;
import io.qameta.allure.Attachment;

/**
 * Custom unchecked exception for framework-level errors.
 * <p>
 * Best practices included:
 * - Logging via LogUtils
 * - Allure reporting of exception details and log file
 * - Preserves cause stacktrace
 */
public class FrameworkException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a FrameworkException with a message.
     * Logs the error and attaches logs to Allure.
     * @param message the detail message
     */
    public FrameworkException(String message) {
        super(message);
        LogUtils.error("FrameworkException: " + message);
        attachStackTrace(this);
        LogUtils.attachLogFile();
    }

    /**
     * Constructs a FrameworkException with message and cause.
     * Logs the error and attaches stacktrace and logs to Allure.
     * @param message the detail message
     * @param cause the cause
     */
    public FrameworkException(String message, Throwable cause) {
        super(message, cause);
        LogUtils.error("FrameworkException: " + message, cause);
        attachStackTrace(cause != null ? cause : this);
        LogUtils.attachLogFile();
    }

    /**
     * Attach the exception stack trace to Allure report.
     * @param t the throwable whose stacktrace will be attached
     * @return the stacktrace string (for Allure attachment)
     */
    @Attachment(value = "Exception Stacktrace", type = "text/plain")
    private static String attachStackTrace(Throwable t) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement elem : t.getStackTrace()) {
            sb.append(elem.toString()).append(System.lineSeparator());
        }
        return sb.toString();
    }
} 
