package com.ecommerce.drivers;

import com.ecommerce.utils.ConfigReader;
import com.ecommerce.utils.LogUtils;
import com.ecommerce.exceptions.FrameworkException;
import io.qameta.allure.Attachment;
import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

/**
 * Thread-safe WebDriver manager combining factory and lifecycle methods.
 * <p>
 * Best practices:
 * - ThreadLocal storage for parallel tests
 * - Browser type from config
 * - Optional remote execution via seleniumGrid.url
 * - Logging and Allure attachments on failures
 * - Clean teardown
 */
public final class DriverManager {
    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverManager() {
        // prevent instantiation
    }

    /**
     * Initialize WebDriver instance for current thread based on configuration.
     */
    @Step("Initializing WebDriver for thread")
    public static void initDriver() {
        String browser = ConfigReader.get("browser").toLowerCase();
        String gridUrl = ConfigReader.getProperty("seleniumGrid.url");
        try {
            WebDriver driver;
            switch (browser) {
                case "firefox":
                    FirefoxOptions fo = new FirefoxOptions();
                    if (gridUrl != null && !gridUrl.isEmpty()) {
                        driver = new RemoteWebDriver(new URL(gridUrl), fo);
                    } else {
                        driver = new FirefoxDriver(fo);
                    }
                    break;
                case "chrome":
                default:
                    ChromeOptions co = new ChromeOptions();
                    if (gridUrl != null && !gridUrl.isEmpty()) {
                        driver = new RemoteWebDriver(new URL(gridUrl), co);
                    } else {
                        driver = new ChromeDriver(co);
                    }
                    break;
            }
            // common setup
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(
                    ConfigReader.getLong("implicit.wait")));
            driver.manage().window().maximize();
            DRIVER.set(driver);
            LogUtils.info("WebDriver initialized: " + browser + (gridUrl != null ? " via Grid" : " locally"));
        } catch (MalformedURLException e) {
            LogUtils.error("Invalid Selenium Grid URL", e);
            throw new FrameworkException("Failed to initialize RemoteWebDriver", e);
        } catch (Exception e) {
            LogUtils.error("Error initializing WebDriver", e);
            throw new FrameworkException("WebDriver initialization failed", e);
        }
    }

    /**
     * Get the WebDriver for the current thread.
     * @return WebDriver instance
     */
    public static WebDriver getDriver() {
        WebDriver driver = DRIVER.get();
        if (driver == null) {
            throw new IllegalStateException("WebDriver has not been initialized. Call initDriver() first.");
        }
        return driver;
    }

    /**
     * Quit and remove the WebDriver for the current thread.
     */
    @Step("Quitting WebDriver for thread")
    public static void quitDriver() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            try {
                driver.quit();
                LogUtils.info("WebDriver quit successfully");
            } catch (Exception e) {
                LogUtils.error("Error quitting WebDriver", e);
            } finally {
                DRIVER.remove();
            }
        }
    }

    /**
     * Attach screenshot to Allure on test failure.
     */
    @Attachment(value = "Last Screenshot", type = "image/png")
    public static byte[] captureScreenshot() {
        try {
            return ((org.openqa.selenium.TakesScreenshot) getDriver()).getScreenshotAs(
                    org.openqa.selenium.OutputType.BYTES);
        } catch (Exception e) {
            LogUtils.error("Failed to capture screenshot", e);
            return new byte[0];
        }
    }
}
