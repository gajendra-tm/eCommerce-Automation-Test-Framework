package com.ecommerce.listeners;

import com.ecommerce.utils.LogUtils;
import io.qameta.allure.Step;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {

    private int retryCount = 0;
    private static final int maxRetryCount = 2;

    @Override
    @Step("Retrying failed test. Attempt: {retryCount}")
    public boolean retry(ITestResult result) {
        if (retryCount < maxRetryCount) {
            retryCount++;
            LogUtils.warn("Retrying test: " + result.getName() + " | Attempt: " + retryCount);
            return true;
        }
        return false;
    }
}
