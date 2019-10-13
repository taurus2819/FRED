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
 * @author bens
 */
@RunWith(CucumberWithSerenity.class)
public class FREDLoginXSS {

    FREDHomePage fredHomePage;
    FREDLoginPage fredLoginPage;
    String userName = "randomUserName";

    @Steps
    NavigateToFredHomePage navigateTo;
    
    @Steps
    HyperLinks clickLink;
    
    @Steps
    SearchFor searchFor;

    @Given("^(?:.*)is on the FRED login page$")
    public void is_on_the_FRED_login_page() throws Throwable {
        navigateTo.theFREDHomePage();
        WebElement loginMenuElement = 
                fredHomePage.getDriver().findElement(By.xpath("//*[@id='navlist']//*[contains(@href, 'login.jsp')]")); 
        loginMenuElement.click();
    }

    @When("^(?:.*)puts in a username and no password$")
    public void puts_in_username_and_no_password() throws Throwable {
        WebElement loginBox = 
                fredHomePage.getDriver().findElement(By.xpath("//*[@id=\"contentWrapInner\"]/table/tbody/tr[5]/td/center/form/table/tbody/tr[1]/td[2]/input")); 
        loginBox.sendKeys("randomUserName");
        WebElement loginSubmit = 
                fredHomePage.getDriver().findElement(By.xpath("//*[@id=\"contentWrapInner\"]/table/tbody/tr[5]/td/center/form/table/tbody/tr[3]/td/input")); 
        loginSubmit.click();
        
    }

    @Then("^(?:.*)the username value is not returned in HTML$")
    public void the_username_value_is_not_returned() throws Throwable {
        WebElement loginPageElement = fredLoginPage.getDriver().findElement(BySelect.get("xpath", "//*[@id=\"contentWrapInner\"]/table/tbody/tr[5]/td/center/form/table/tbody/tr[1]/td[2]/input"));
        String badUserName = loginPageElement.getText().trim();     //fredLoginPage.getDriver().getTitle();
        assertFalse(badUserName.equals(userName));
    }
}
