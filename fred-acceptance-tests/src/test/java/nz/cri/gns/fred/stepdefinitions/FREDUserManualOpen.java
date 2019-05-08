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
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import net.thucydides.core.annotations.Steps;
import nz.cri.gns.fred.navigation.FREDHomePage;
import nz.cri.gns.fred.navigation.FREDQuickStartPage;
import nz.cri.gns.fred.navigation.FREDUserManualPage;
import nz.cri.gns.fred.navigation.NavigateToFredHomePage;
import nz.cri.gns.fred.search.BySelect;
import nz.cri.gns.fred.utils.EnvironmentConfig;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import static org.junit.Assert.assertTrue;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

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
    
    
    static {
        WebDriverManager.chromedriver().setup();
    }
    
    @Given("^The user launches the FRED application$")
    public void The_user_launches_the_FRED_application() throws Throwable {
//        System.setProperty("webdriver.chrome.driver", "C:\\Prashanth\\myprojs\\Selenium\\chromedriver_win32\\chromedriver.exe");        
        driver = new ChromeDriver();
    	driver.manage().window().maximize();
    	driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);    	
        EnvironmentConfig cfg = new EnvironmentConfig();
        String fredURL = cfg.getProperty("webdriver.base.url");
        System.out.println("Configuration = " + fredURL);
    	driver.get(fredURL);
        
    }
    
    @When("^the user clicks on the 'FRED User Manual' menu$")
    public void the_user_clicks_on_the_fred_user_manual_menu() throws Throwable {
        driver.findElement(By.xpath("//*[@id='navlist']//*[contains(@href, 'manual.pdf')]")).click();
        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
    }
    
    @Then("^the user manual pdf is opened$")
    public void the_user_manual_pdf_is_opened() throws Throwable {
        String getURL = driver.getCurrentUrl();    	
    	pdfURL = new URL(getURL);
    	InputStream inputStream = pdfURL.openStream();    	
    	bis = new BufferedInputStream(inputStream);
    	PDDocument document = PDDocument.load(bis);
    	String pdfContent = new PDFTextStripper().getText(document);
    	assertTrue(pdfContent.contains("Version 1.0"));
    }
    
}
