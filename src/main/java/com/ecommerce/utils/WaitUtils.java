package com.ecommerce.utils;

import com.ecommerce.constants.AppConstants;
import com.ecommerce.constants.TimeConstants;
import com.ecommerce.drivers.DriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * WaitUtils provides reusable wait methods with hybrid defaults from ConfigReader
 * and fallback support via AppConstants and TimeConstants.
 * 
 * Features:
 * - Explicit waits (WebDriverWait)
 * - Fluent waits (FluentWait) with custom polling
 * - JavaScript and AJAX readiness checks
 * - Hard wait for exceptional cases
 */
public final class WaitUtils {

    private WaitUtils() {
        // Prevent instantiation
    }

    private static WebDriver getDriver() {
        return DriverManager.getDriver();
    }

    /**
     * Determine explicit wait duration: config override or fallback.
     */
    private static Duration getExplicitWaitDuration() {
        return ConfigReader.containsKey(AppConstants.KEY_EXPLICIT_WAIT)
            ? Duration.ofSeconds(ConfigReader.getLong(AppConstants.KEY_EXPLICIT_WAIT))
            : Duration.ofSeconds(TimeConstants.DEFAULT_EXPLICIT_WAIT_SECONDS);
    }

    /**
     * Determine polling interval: config override or fallback.
     */
    private static Duration getPollingInterval() {
        return ConfigReader.containsKey(AppConstants.KEY_POLLING_INTERVAL)
            ? Duration.ofMillis(ConfigReader.getLong(AppConstants.KEY_POLLING_INTERVAL))
            : Duration.ofMillis(TimeConstants.DEFAULT_POLLING_INTERVAL_MS);
    }

    private static WebDriverWait newExplicitWait(Duration timeout) {
        return new WebDriverWait(getDriver(), timeout);
    }

    private static Wait<WebDriver> newFluentWait(Duration timeout, Duration polling) {
        return new FluentWait<>(getDriver())
            .withTimeout(timeout)
            .pollingEvery(polling)
            .ignoreAll(Arrays.asList(NoSuchElementException.class, StaleElementReferenceException.class));
    }

    // -------- Explicit Waits --------

    public static WebElement forVisibility(WebElement element) {
        return newExplicitWait(getExplicitWaitDuration())
            .until(ExpectedConditions.visibilityOf(element));
    }

    public static WebElement forVisibility(By locator) {
        return newExplicitWait(getExplicitWaitDuration())
            .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static WebElement forClickable(WebElement element) {
        return newExplicitWait(getExplicitWaitDuration())
            .until(ExpectedConditions.elementToBeClickable(element));
    }

    public static WebElement forClickable(By locator) {
        return newExplicitWait(getExplicitWaitDuration())
            .until(ExpectedConditions.elementToBeClickable(locator));
    }

    public static WebElement forPresence(By locator) {
        return newExplicitWait(getExplicitWaitDuration())
            .until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public static boolean forText(WebElement element, String text) {
        return newExplicitWait(getExplicitWaitDuration())
            .until(ExpectedConditions.textToBePresentInElement(element, text));
    }

    public static void forFrameAndSwitch(By locator) {
        newExplicitWait(getExplicitWaitDuration())
            .until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(locator));
    }

    // -------- Fluent Wait --------

    public static <V> V until(Function<WebDriver, V> condition) {
        return newFluentWait(getExplicitWaitDuration(), getPollingInterval())
            .until(condition);
    }

    public static <V> V until(Function<WebDriver, V> condition, Duration timeout, Duration polling) {
        return newFluentWait(timeout, polling).until(condition);
    }

    // -------- Page Load & AJAX --------

    public static void forPageLoad() {
        newExplicitWait(getExplicitWaitDuration())
            .until((ExpectedCondition<Boolean>) wd ->
                ((JavascriptExecutor) wd).executeScript("return document.readyState").equals("complete")
            );
    }

    public static void forJSandJQuery() {
        WebDriverWait wait = newExplicitWait(getExplicitWaitDuration());

        ExpectedCondition<Boolean> jQueryLoad = wd -> {
            try {
                return ((Long) ((JavascriptExecutor) wd).executeScript("return jQuery.active")) == 0;
            } catch (Exception e) {
                return true;
            }
        };

        ExpectedCondition<Boolean> jsLoad = wd ->
            ((JavascriptExecutor) wd).executeScript("return document.readyState").equals("complete");

        wait.until(jQueryLoad);
        wait.until(jsLoad);
    }

    // -------- Hard Wait (use sparingly) --------

    public static void hardWait(Duration duration) {
        try {
            TimeUnit.MILLISECONDS.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
