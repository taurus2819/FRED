/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.cri.gns.fred.stepdefinitions;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.util.EnvironmentVariables;
import nz.cri.gns.fred.navigation.FREDHomePage;
import nz.cri.gns.fred.navigation.FREDQuickStartPage;
import nz.cri.gns.fred.navigation.FREDUserManualPage;
import nz.cri.gns.fred.navigation.NavigateToFredHomePage;
import nz.cri.gns.fred.search.BySelect;
import nz.cri.gns.fred.utils.FredTestConfig;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import static org.junit.Assert.assertTrue;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 *
 * @author sitikond
 */
public class FREDUserManualOpen {
    
    FREDHomePage fredHomePage;
    FREDQuickStartPage fredQuickStart;
    FREDUserManualPage fredUserManualPage;
    WebDriver driver;
    @Steps
    NavigateToFredHomePage navigateTo;
    URL pdfURL;
    BufferedInputStream bis;    
    EnvironmentVariables environmentVariables;
    
    static {
        WebDriverManager.chromedriver().setup();
    }
    
    @Given("^The user launches the FRED application$")
    public void The_user_launches_the_FRED_application() throws Throwable { 
        navigateTo.theFREDHomePage();
        driver = fredHomePage.getDriver();
        getWebDriver();        
    	driver.manage().window().maximize();
    	driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);  
        String url = EnvironmentSpecificConfiguration.from(environmentVariables)
                .getProperty("webdriver.base.url");
    	driver.get(url + "/fred");        
    }
    
    @When("^the user clicks on the 'FRED User Manual' menu$")
    public void the_user_clicks_on_the_fred_user_manual_menu() throws Throwable {
        driver.findElement(By.xpath("//*[@id='navlist']//*[contains(@href, 'manual.pdf')]")).click();
        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
    }
    
    @Then("the user manual pdf is opened to verify it is {string}")
    public void the_user_manual_pdf_is_opened_to_verify_it_is(String version) throws IOException {
        String getURL = driver.getCurrentUrl();    	
    	pdfURL = new URL(getURL); 
    	InputStream inputStream = pdfURL.openStream();
    	
    	bis = new BufferedInputStream(inputStream);
    	PDDocument document = PDDocument.load(bis);
    	String pdfContent = new PDFTextStripper().getText(document);
        driver.close();
    	assertTrue(pdfContent.contains(version));
    }

    private void getWebDriver() {
        if (driver.toString().equals("WebDriverFacade for chrome")){
            driver = new ChromeDriver();
        } else {
            driver = new FirefoxDriver();
        }
    }
    
}
