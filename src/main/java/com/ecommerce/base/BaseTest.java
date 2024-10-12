package com.ecommerce.base;

import com.ecommerce.drivers.DriverManager;
import com.ecommerce.exceptions.FrameworkException;
import com.ecommerce.reporting.AllureReportManager;
import com.ecommerce.utils.ConfigReader;
import com.ecommerce.utils.LogUtils;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;

/**
 * BaseTest combines setup/teardown logic for all tests.
 * Supports cross-browser via @Parameters, parallel execution, logging, Allure integration.
 */
public abstract class BaseTest {
    protected WebDriver driver;

    /**
     * Initialize WebDriver before any tests in this class.
     * @param browser from TestNG XML parameter
     */
    @Parameters({"browser"})
    @BeforeClass(alwaysRun = true)
    public void setUpClass(@Optional("chrome") String browser) {
        try {
            LogUtils.info("[BaseTest] Starting setup for browser: " + browser);
            // optionally override browser in config
            System.setProperty("browser", browser);
            DriverManager.initDriver();
            driver = DriverManager.getDriver();
            AllureReportManager.addParameter("Browser", browser);
        } catch (Exception e) {
            LogUtils.error("BaseTest setup failed", e);
            throw new FrameworkException("Test setup failed", e);
        }
    }

    /**
     * Navigate to base URL before each test method.
     */
    @BeforeMethod(alwaysRun = true)
    public void setUpTest() {
        String baseUrl = ConfigReader.get("baseUrl");
        LogUtils.info("Navigating to base URL: " + baseUrl);
        driver.get(baseUrl);
    }

    /**
     * Tear down WebDriver after all tests in this class.
     */
    @AfterClass(alwaysRun = true)
    public void tearDownClass() {
        try {
            DriverManager.quitDriver();
            LogUtils.info("[BaseTest] Teardown complete");
        } catch (Exception e) {
            LogUtils.error("BaseTest teardown failed", e);
        }
    }

    /**
     * On test failure, capture screenshot and attach logs.
     */
    @AfterMethod(alwaysRun = true)
    public void tearDownMethod(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            LogUtils.error("Test failed: " + result.getName());
            DriverManager.captureScreenshot();
            AllureReportManager.attachScreenshot(result.getName());
            LogUtils.attachLogFile();
        }
    }
}
