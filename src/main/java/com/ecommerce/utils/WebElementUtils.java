package com.ecommerce.utils;

import com.ecommerce.constants.AppConstants;
import com.ecommerce.constants.TimeConstants;
import com.ecommerce.drivers.DriverManager;
import io.qameta.allure.Step;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Utility class that combines PageFactory for element initialization with reusable WebElement actions.
 * Best Practices:
 * - Thread safety via WebDriverManager
 * - Consistent explicit waits
 * - Optional Allure logging
 * - JavaScript execution for advanced interactions
 */
public class WebElementUtils {

	private static final int TIMEOUT = ConfigReader.containsKey(AppConstants.KEY_EXPLICIT_WAIT)
	        ? Integer.parseInt(ConfigReader.get(AppConstants.KEY_EXPLICIT_WAIT))
	        : (int) TimeConstants.DEFAULT_EXPLICIT_WAIT_SECONDS;


    private static WebDriver getDriver() {
        return DriverManager.getDriver();
    }

    // Sample WebElements - these should be overwritten in page object classes
    @FindBy(tagName = "button")
    private List<WebElement> buttons;

    @FindBy(tagName = "select")
    private List<WebElement> dropdowns;

    @FindBy(tagName = "input")
    private List<WebElement> inputFields;

    public WebElementUtils() {
        PageFactory.initElements(getDriver(), this);
    }

    @Step("Clicking on element")
    public static void click(WebElement element) {
        try {
            waitForClickability(element).click();
            LogUtils.info("Clicked on element: " + element);
        } catch (Exception e) {
            LogUtils.error("Click failed", e);
            ScreenshotUtils.captureScreenshotToAllure("click_failure");
            throw e;
        }
    }

    @Step("Sending keys to element")
    public void sendKeys(WebElement element, String text) {
        try {
            WebElement e = waitForVisibility(element);
            e.clear();
            e.sendKeys(text);
            LogUtils.info("Sent keys to element: " + element + " value: " + text);
        } catch (Exception e) {
            LogUtils.error("Send keys failed", e);
            ScreenshotUtils.captureScreenshotToAllure("sendkeys_failure");
            throw e;
        }
    }

    public String getText(WebElement element) {
        try {
            String text = waitForVisibility(element).getText();
            LogUtils.info("Element text: " + text);
            return text;
        } catch (Exception e) {
            LogUtils.error("Get text failed", e);
            ScreenshotUtils.captureScreenshotToAllure("gettext_failure");
            throw e;
        }
    }

    public boolean isElementVisible(WebElement element) {
        try {
            boolean status = element.isDisplayed();
            LogUtils.info("Element visibility: " + status);
            return status;
        } catch (Exception e) {
            LogUtils.warn("Element not visible");
            return false;
        }
    }

    public void clickWithJS(WebElement element) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) getDriver();
            js.executeScript("arguments[0].click();", element);
            LogUtils.info("JS click successful");
        } catch (Exception e) {
            LogUtils.error("JS click failed", e);
            ScreenshotUtils.captureScreenshotToAllure("js_click_failure");
            throw e;
        }
    }

    public void scrollIntoView(WebElement element) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) getDriver();
            js.executeScript("arguments[0].scrollIntoView(true);", element);
            LogUtils.info("Scrolled into view");
        } catch (Exception e) {
            LogUtils.error("Scroll failed", e);
        }
    }

    public void hover(WebElement element) {
        try {
            new Actions(getDriver()).moveToElement(element).perform();
            LogUtils.info("Hovered over element");
        } catch (Exception e) {
            LogUtils.error("Hover failed", e);
        }
    }

    public void selectDropdownByVisibleText(WebElement element, String text) {
        try {
            Select select = new Select(waitForVisibility(element));
            select.selectByVisibleText(text);
            LogUtils.info("Selected dropdown text: " + text);
        } catch (Exception e) {
            LogUtils.error("Dropdown selection failed", e);
            ScreenshotUtils.captureScreenshotToAllure("dropdown_text_failure");
            throw e;
        }
    }

    public void selectDropdownByValue(WebElement element, String value) {
        try {
            Select select = new Select(waitForVisibility(element));
            select.selectByValue(value);
            LogUtils.info("Selected dropdown value: " + value);
        } catch (Exception e) {
            LogUtils.error("Dropdown selection failed", e);
            ScreenshotUtils.captureScreenshotToAllure("dropdown_value_failure");
            throw e;
        }
    }

    private static WebElement waitForVisibility(WebElement element) {
        return new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(TIMEOUT))
                .until(ExpectedConditions.visibilityOf(element));
    }

    private static WebElement waitForClickability(WebElement element) {
        return new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(TIMEOUT))
                .until(ExpectedConditions.elementToBeClickable(element));
    }
}
