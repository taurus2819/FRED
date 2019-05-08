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

import net.serenitybdd.cucumber.CucumberWithSerenity;
import net.thucydides.core.annotations.Steps;
import nz.cri.gns.fred.links.HyperLinks;
import nz.cri.gns.fred.navigation.FREDHomePage;
import nz.cri.gns.fred.navigation.NavigateToFredHomePage;
import static org.assertj.core.api.Assertions.assertThat;

import nz.cri.gns.fred.search.SearchFor;
import nz.cri.gns.fred.search.SearchForm;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 *
 * @author sitikond
 */
@RunWith(CucumberWithSerenity.class)
public class FREDNavigateToHomePage {
    FREDHomePage fredHomePage;
    List<String> keywords;
    
    @Steps
    NavigateToFredHomePage navigateTo;

    @Steps
    SearchFor searchFor;

    @Steps
    HyperLinks links;
    
    @Given("^Chris is on the FRED home page$")
    public void chris_is_on_the_fred_home_page() throws Throwable {
        navigateTo.theFREDHomePage();
    }

    @When("^he focuses on the FRED home page$")
    public void he_focuses_on_the_fred_home_page() throws Throwable {
        String currentURL = fredHomePage.getDriver().getCurrentUrl();
        assertThat(currentURL.equalsIgnoreCase(FREDHomePage.FRED_URL));
    }

    @Then("^the FRED page title should appear$")
    public void the_fred_page_title_should_appear() throws Throwable {
        String title = fredHomePage.getDriver().getTitle();
        boolean isFredTitle = title.equals(FREDHomePage.TITLE);
        assertThat(isFredTitle);
    }

//    @Then("check FRED is getting the data")
//    public void check_fred_is_getting_the_data() {
//        searchFor.group("National Paleontological Collection (NPC)");
//    }
    
}
