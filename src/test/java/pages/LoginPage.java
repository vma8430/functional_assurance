package pages;

import com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter;

import org.junit.Assert;
import org.openqa.selenium.*;

import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.Date;

/**
 * PageFactory For ShopPage
 */
public class LoginPage extends BasePage {
    protected static final Logger LOG = LoggerFactory.getLogger(LoginPage.class);
    /**
     * All WebElements are identified by @FindBy annotation
     */

    @FindBy(className =  "login_logo")
	private WebElement HomePageIcon;

	/**
	 * The login button
	 */
	@FindBy(id = "login-button")
	private WebElement loginButton;

	/**
	 * The user name input
	 */
	@FindBy(id = "user-name")
	private WebElement usernameField;

	/**
	 * The password input
	 */
	@FindBy(id = "password")
	private WebElement passwordField;

	/**
	 * Error Message
	 */
	@FindBy(xpath = "//h3[text()='Epic sadface: Sorry, this user has been locked out.']")
	private WebElement errorMessage;

	/**
	 * Menu Icon
	 */
	@FindBy(id = "react-burger-menu-btn")
	private WebElement menuIcon;

	/**
	 * Logout
	 */
	@FindBy(xpath = "//a[text()=\"Logout\"]")
	private WebElement Logout;

	public LoginPage(WebDriver driver) {
		super(driver);
	}

	public void login(String username, String password) {

		Assert.assertTrue(HomePageIcon.isDisplayed());
		usernameField.sendKeys(username);
		passwordField.sendKeys(password);
		loginButton.click();

	}

	public void loginFailed() {

		Assert.assertTrue(errorMessage.isDisplayed());

	}

	public void logout() {

		menuIcon.click();
		Logout.click();

	}

	public void logoutSuccessful() {
		Assert.assertTrue(HomePageIcon.isDisplayed());
	}

}