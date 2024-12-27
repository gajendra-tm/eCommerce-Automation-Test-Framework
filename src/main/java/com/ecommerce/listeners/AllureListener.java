package com.ecommerce.listeners;

import com.ecommerce.reporting.AllureReportManager;
import com.ecommerce.utils.LogUtils;
import com.ecommerce.utils.ScreenshotUtils;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import io.qameta.allure.Attachment;

public class AllureListener implements ITestListener {

    @Override
    public void onStart(ITestContext context) {
        LogUtils.info("Test Suite started: " + context.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        LogUtils.info("Test Suite finished: " + context.getName());
        AllureReportManager.attachLogFile();
    }

    @Override
    public void onTestStart(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        LogUtils.info("Test started: " + methodName);
        AllureReportManager.step("Starting test: " + methodName);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        LogUtils.info("Test passed: " + methodName);
        AllureReportManager.step("Test passed: " + methodName);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        Throwable cause = result.getThrowable();

        LogUtils.error("Test failed: " + methodName, cause);
        AllureReportManager.step("Test failed: " + methodName);
        ScreenshotUtils.captureAndSaveScreenshot(methodName);
        attachStackTrace(cause);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        LogUtils.warn("Test skipped: " + methodName);
        AllureReportManager.step("Test skipped: " + methodName);
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        LogUtils.warn("Test failed but within success percentage: " + methodName);
    }

    @Attachment(value = "Stack Trace", type = "text/plain")
    private String attachStackTrace(Throwable throwable) {
        return throwable != null ? throwable.toString() : "No stack trace available";
    }
}
