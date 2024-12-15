package com.ecommerce.listeners;

import com.ecommerce.utils.LogUtils;
import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * RetryTransformer sets the RetryAnalyzer dynamically for all tests.
 */
public class RetryTransformer implements IAnnotationTransformer {

    @Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        try {
            annotation.setRetryAnalyzer(RetryAnalyzer.class);
        } catch (Exception e) {
            LogUtils.error("Failed to set RetryAnalyzer for test method: " + testMethod.getName(), e);
        }
    }
}
