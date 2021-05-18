package framework;

import com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.github.bonigarcia.wdm.managers.ChromiumDriverManager;
import runner.TestPlan;

import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * DriverFactory which will create respective driver Object
 *
 * @author Cognizant
 */
public class WebDriverFactory {
	/**
	 * Function to return the object for WebDriver {@link WebDriver} object
	 *
	 * @return Instance of the {@link WebDriver} object
	 */
	protected static final Logger LOG = LoggerFactory.getLogger(WebDriverFactory.class);

	public static WebDriver createWebDriverInstance(String strDevice) {
        WebDriver driver = null;
        Map<String, String> mobileEmulation = new HashMap<>();
        if (!strDevice.isEmpty() && !strDevice.equalsIgnoreCase("Web"))
        {
            mobileEmulation.put("deviceName", strDevice);
        }
        try {    
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setJavascriptEnabled(true);
            capabilities.setBrowserName(BrowserType.CHROME);
            capabilities.setPlatform(Platform.ANY);  
            ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.setExperimentalOption("mobileEmulation", mobileEmulation);
            capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
            System.out.println("Capabilities :"+capabilities.toString());
            try {
            	 /*For Testing with Local Grid*/ //driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), capabilities);
              	driver = new RemoteWebDriver(new URL(System.getProperty("hub")), capabilities);
            	 System.out.println("Hub URL :"+System.getProperty("hub"));
            	} catch (MalformedURLException e) {
            	    LOG.error(e.getMessage());
            	  }
                 driver.manage().timeouts().implicitlyWait(300, TimeUnit.SECONDS);
            LOG.info(" Driver Creation Completed");
        }catch(Exception ex){
		ex.printStackTrace();
	}
        return driver;
	}
}
