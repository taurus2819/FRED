/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.cri.gns.fred.stepdefinitions;

import cucumber.api.PendingException;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.And;
import cucumber.api.junit.Cucumber;
import java.util.concurrent.TimeUnit;
import net.thucydides.core.annotations.Steps;
import nz.cri.gns.fred.navigation.FREDAboutPage;
import nz.cri.gns.fred.navigation.FREDHomePage;
import nz.cri.gns.fred.navigation.FREDQuickStartPage;
import nz.cri.gns.fred.navigation.FREDUserManualPage;
import nz.cri.gns.fred.navigation.NavigateToFredHomePage;
import nz.cri.gns.fred.navigation.NavigateToFredUserManualPage;
import nz.cri.gns.fred.search.BySelect;
import nz.cri.gns.fred.search.SearchFor;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 *
 * @author sitikond
 */
@RunWith(Cucumber.class)
public class DocumentationAvailableAndNavigation {
    
    FREDHomePage fredHomePage;
    FREDAboutPage fredAboutPage;
    FREDQuickStartPage fredQuickStart;
    FREDUserManualPage fredUserManualPage;
    WebDriver driver;
    
    @Steps
    NavigateToFredHomePage navigateTo;
    
    @Steps
    NavigateToFredUserManualPage navigateToFredUsmPage;
    
    @Steps
    SearchFor searchFor;
    
    @Given("^the user is on the FRED homepage$")
    public void the_user_is_on_the_fred_homepage() throws Throwable {
        navigateTo.theFREDHomePage();
        driver = fredHomePage.getDriver();
        String title = fredHomePage.getDriver().getTitle();
        boolean isFredTitle = title.equals(FREDHomePage.TITLE);
        assertTrue(isFredTitle);
    }

    @When("^the user clicks on the 'About' menu$")
    public void the_user_clicks_on_the_about_menu() throws Throwable {
        WebElement aboutMenuElement =  
                driver.findElement(BySelect.get("xpath", "//*[@id='navlist']//*[contains(@href, 'about.jsp')]"));     //About menu
                //fredHomePage.getDriver().findElement(BySelect.get("xpath", "//*[@id=\"navlist\"]/li[10]/a"));     //About menu
        if(aboutMenuElement.getText().trim().equalsIgnoreCase("About")){
            aboutMenuElement.click();
        }else{
            assertTrue(false);
        }
    }    

    @Then("^the 'About' page is displayed$")
    public void the_about_page_is_displayed() throws Throwable {  //*[@id="contentWrapInner"]/table/tbody/tr/td/p[1]/span
        WebElement aboutPageElement = 
                driver.findElement(BySelect.get("xpath", "//*[@id=\"contentWrapInner\"]/table/tbody/tr/td/p[1]/span"));
                //fredAboutPage.getDriver().findElement(BySelect.get("xpath", "//*[@id=\"contentWrapInner\"]/table/tbody/tr/td/p[1]/span"));
        String aboutPageTitle = aboutPageElement.getText().trim();     //fredLoginPage.getDriver().getTitle();
        assertTrue(aboutPageTitle.equalsIgnoreCase(fredAboutPage.FRED_ABOUT_PAGE));
    }
    
    @When("^the user clicks on the 'Quick Start' menu$")
    public void the_user_clicks_on_the_quick_start_menu() throws Throwable {
        WebElement quickStartMenuElement =  
                fredAboutPage.getDriver().findElement(BySelect.get("xpath", "//*[@id=\"navlist\"]/li[9]/a"));     //Quick Start menu
        if(quickStartMenuElement.getText().trim().equalsIgnoreCase("Quick Start")){
            quickStartMenuElement.click();
        }else{
            assertTrue(false);
        }            
    }

    @Then("^the 'Quick Start' page is displayed$")
    public void the_quick_start_page_is_displayed() throws Throwable {
        WebElement quickStartPageElement = 
                fredQuickStart.getDriver().findElement(BySelect.get("xpath", "//*[@id=\"contentWrapInner\"]/table/tbody/tr/td/p[1]/span"));
        String quickStartPageTitle = quickStartPageElement.getText().trim();
        assertTrue(quickStartPageTitle.equalsIgnoreCase(fredQuickStart.FRED_QUICK_START_GUIDE));
    }
}
