package pages;


import com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * PageFactory For ShopPage
 */
public class ItemsPage extends BasePage {

	@FindBy(xpath = "//span[text()='Products']")
    private WebElement ProductsTopBar;
    
    /**
     * The cart icon
     */
    @FindBy(className = "shopping_cart_link")
    private WebElement cartIcon;

    /**
     * Checkout
     */
    @FindBy(name = "checkout")
    private WebElement Checkout;

    public ItemsPage(WebDriver driver) {
        super(driver);
    }


    public void loginSuccessful() {
        Assert.assertTrue(ProductsTopBar.isDisplayed());
    }
    
    public void orderProduct(String ProductName) {
    	driver.findElement(By.xpath("//div[text()='"+ProductName+"']/following::button[1]")).click();
    	cartIcon.click();
    	Assert.assertTrue(driver.findElement(By.xpath("//div[@class='inventory_item_name'][text()='"+ProductName+"']")).isDisplayed());
    	Checkout.click();
    }



}
