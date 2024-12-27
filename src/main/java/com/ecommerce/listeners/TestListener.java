package com.ecommerce.listeners;

import com.ecommerce.utils.LogUtils;
import com.ecommerce.utils.ScreenshotUtils;
import io.qameta.allure.Allure;
import org.testng.*;

import java.util.Arrays;

public class TestListener implements ITestListener, ISuiteListener {

    @Override
    public void onStart(ISuite suite) {
        LogUtils.info("Test Suite started: " + suite.getName());
    }

    @Override
    public void onFinish(ISuite suite) {
        LogUtils.info("Test Suite finished: " + suite.getName());
        Allure.addAttachment("Final Logs", "See complete logs in the attached report.");
        LogUtils.attachLogFile(); // Attach complete log file to Allure
    }

    @Override
    public void onTestStart(ITestResult result) {
        LogUtils.info("Test Started: " + getTestMethodName(result));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        LogUtils.info("Test Passed: " + getTestMethodName(result));
    }

    @Override
    public void onTestFailure(ITestResult result) {
        LogUtils.error("Test Failed: " + getTestMethodName(result), result.getThrowable());
        ScreenshotUtils.captureAndSaveScreenshot(getTestMethodName(result)); // capture and attach to allure
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        LogUtils.warn("Test Skipped: " + getTestMethodName(result));
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        LogUtils.warn("Test Failed but within success percentage: " + getTestMethodName(result));
    }

    @Override
    public void onStart(ITestContext context) {
        LogUtils.info("Test Context started: " + context.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        LogUtils.info("Test Context finished: " + context.getName());
    }

    private String getTestMethodName(ITestResult result) {
        return result.getMethod().getMethodName() + getParameters(result);
    }

    private String getParameters(ITestResult result) {
        Object[] parameters = result.getParameters();
        return parameters.length > 0 ? " with parameters " + Arrays.toString(parameters) : "";
    }
}
