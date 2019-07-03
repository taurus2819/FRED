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
import cucumber.api.junit.Cucumber;
import java.util.List;
import junit.framework.AssertionFailedError;
import static org.assertj.core.api.Assertions.*;
//import net.serenitybdd.core.annotations.findby.By;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import net.thucydides.core.annotations.Steps;
import nz.cri.gns.fred.links.HyperLinks;
import nz.cri.gns.fred.navigation.FREDHomePage;
import nz.cri.gns.fred.navigation.FREDLoginPage;
import nz.cri.gns.fred.navigation.NavigateToFredHomePage;
import nz.cri.gns.fred.search.BySelect;
import nz.cri.gns.fred.search.SearchFor;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
/**
 *
 * @author sitikond
 */
@RunWith(CucumberWithSerenity.class)
public class NavigateToFredLoginPage {

    FREDHomePage fredHomePage;
    FREDLoginPage fredLoginPage;

    @Steps
    NavigateToFredHomePage navigateTo;
    
    @Steps
    HyperLinks clickLink;
    
    @Steps
    SearchFor searchFor;

    @Given("^user wants to login to FRED$")
    public void user_wants_to_login_to_fred() throws Throwable {
        navigateTo.theFREDHomePage();
    }

    @When("^the user clicks on the login menu$")
    public void the_user_clicks_on_the_login_menu() throws Throwable {
        WebElement loginMenuElement = 
                fredHomePage.getDriver().findElement(By.xpath("//*[@id='navlist']//*[contains(@href, 'login.jsp')]")); 
        loginMenuElement.click();
        
    }

    @Then("^the FRED lgoin page with the title FRED Login should appear$")
    public void the_fred_lgoin_page_with_the_title_fred_login_should_appear() throws Throwable {
        WebElement loginPageElement = fredLoginPage.getDriver().findElement(BySelect.get("xpath", "//*[@id=\"contentWrap\"]/div[1]/div"));
        String loginPageTitle = loginPageElement.getText().trim();     //fredLoginPage.getDriver().getTitle();
        boolean fredLoginPage = loginPageTitle.equals(FREDLoginPage.FRED_LOGIN_TITLE);
        assertTrue(loginPageTitle.equals(FREDLoginPage.FRED_LOGIN_TITLE));
    }
}
