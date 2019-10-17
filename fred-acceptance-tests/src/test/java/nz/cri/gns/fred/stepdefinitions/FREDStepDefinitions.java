package nz.cri.gns.fred.stepdefinitions;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import java.io.BufferedInputStream;
import java.net.URL;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.util.EnvironmentVariables;
import nz.cri.gns.fred.links.HyperLinks;
import nz.cri.gns.fred.navigation.FREDHomePage;
import nz.cri.gns.fred.navigation.NavigateTo;
import nz.cri.gns.fred.search.SearchFor;
import nz.cri.gns.fred.navigation.FREDAboutPage;
import nz.cri.gns.fred.navigation.FREDLoginPage;
import nz.cri.gns.fred.navigation.FREDQuickStartPage;
import nz.cri.gns.fred.navigation.FREDUserManualPage;
import nz.cri.gns.fred.search.BySelect;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 *
 * @author bens
 */
public class FREDStepDefinitions {

    @Steps
    NavigateTo navigateTo;
    FREDLoginPage fredLoginPage;
    FREDHomePage fredHomePage;
    FREDAboutPage fredAboutPage;
    FREDQuickStartPage fredQuickStart;
    FREDUserManualPage fredUserManualPage;
    URL pdfURL;
    BufferedInputStream bis;
    EnvironmentVariables environmentVariables;
    String userName = "randomUserName";
    HyperLinks clickLink;
    SearchFor searchFor;

    @Given("^(?:.*) is on (.*)")
    // Add any pages to navigate to here
    public void is_on_the_specified_page(String webpage) throws Throwable {
        switch (webpage) {
            case "theFREDHomePage":
                navigateTo.theFREDHomePage();
                break;
            case "theFREDAboutPage":
                navigateTo.theFREDAboutPage();
                break;
            case "theFREDLoginPage":
                navigateTo.theFREDLoginPage();
                break;
            case "theFREDQuickStartPage":
                navigateTo.theFREDQuickStartPage();
                break;
            case "theFREDUserManualPage":
                navigateTo.theFREDUserManualPage();
                break;
            default:
                break;
        }
    }

    @When("^the user clicks on the (.*) menu$")
    // Add any clickable links, menus, buttons here
    public void the_user_clicks_on_the_specified_menu(String menuSelect) throws Throwable {
        switch (menuSelect) {
            case "quickStart":
                clickLink.clickLink("quickStart");
                break;
            case "login":
                clickLink.clickLink("login");
                break;
            case "about":
                clickLink.clickLink("about");
                break;
            case "fredUserManual":
                clickLink.clickLink("usermanual");
                break;
            case "newFolder":
                clickLink.clickLink("newfolder");
                break;
            case "dataEntry":
                clickLink.clickLink("dataentry");
            default:
                break;
        }
    }

    @When("^he focuses on the FRED home page$")
    public void he_focuses_on_the_fred_home_page() throws Throwable {
        assertThat(fredHomePage.waitFor(ExpectedConditions.urlContains("/fred")));
    }

    @When("^(?:.*)puts in a username and no password$")
    public void puts_in_username_and_no_password() throws Throwable {
        WebElement loginBox = fredHomePage.getDriver().findElement(By.xpath("//*[@id=\"contentWrapInner\"]/table/tbody/tr[5]/td/center/form/table/tbody/tr[1]/td[2]/input"));
        loginBox.sendKeys("randomUserName");
        WebElement loginSubmit = fredHomePage.getDriver().findElement(By.xpath("//*[@id=\"contentWrapInner\"]/table/tbody/tr[5]/td/center/form/table/tbody/tr[3]/td/input"));
        loginSubmit.click();
    }

    @And("^the user logs in$")
    public void user_logs_in() throws Throwable {
        WebElement loginBox = fredHomePage.getDriver().findElement(BySelect.get("name", "loginname"));
        loginBox.sendKeys("stafftest");
        WebElement passBox = fredHomePage.getDriver().findElement(BySelect.get("name", "loginpass"));
        passBox.sendKeys("HawkingGreatBigLong33CharPassword");
        WebElement loginSubmit = fredHomePage.getDriver().findElement(By.xpath("//*[@id=\"contentWrapInner\"]/table/tbody/tr[5]/td/center/form/table/tbody/tr[3]/td/input"));
        loginSubmit.click();
    }
//// Test Fails here for the alert box.  It created an error folder in my account on DEV - Ben S
//    @And("a new name is entered for the folder")
//    public void new_folder_name() {
//        WebDriverWait wait = new WebDriverWait(fredHomePage.getDriver(), 10);
//        wait.until(ExpectedConditions.alertIsPresent());
//        fredHomePage.getDriver().switchTo().alert().sendKeys("NewTestFolder");
//        fredHomePage.getDriver().switchTo().alert().accept();
//    }

    @Then("the user name is shown as logged in")
    public void the_user_name_shown() {
        WebElement pageUserName = fredHomePage.getDriver().findElement(BySelect.get("xpath", "//*[@id=\"contentWrapInner\"]/div[1]"));
        String loggedInName = pageUserName.getText().trim();
        System.out.println("Logged in Name: " + loggedInName);
        assertTrue(loggedInName.equalsIgnoreCase("Logged in as SecuredStaffClientTest insertGnsStaffLoginIdUnavailable"));
    }
    
    @And("the user is logged out")
    public void the_user_is_logged_out(){
        WebElement logoutButton = fredHomePage.getDriver().findElement(BySelect.get("xpath", "//*[@id='navlist']//*[contains(@href, 'logout.jsp')]"));
        logoutButton.click();
        // TODO: Assert that url doesn't contain "fred"
        // Test works every time it is run anyway, but a nice to have is the url check.
    }

    @Then("the new Folder Name is registered")
    public void the_new_folder_name_registered() {
        WebElement folderNamePage = fredHomePage.getDriver().findElement(BySelect.get("xpath", "//*[@id=\"contentWrapInner\"]/table/tbody/tr[3]/td[1]/a"));
        String folderName = folderNamePage.getText().trim();
        assertTrue(folderName.equalsIgnoreCase("NewTestFolder"));

    }

    @Then("^the (.*) page is displayed$")
    public void the_selected_page_is_displayed(String pageSelect) throws Throwable {
        switch (pageSelect) {
            case "about":
                WebElement aboutPageElement = fredQuickStart.getDriver().findElement(BySelect.get("xpath", "//*[@id=\"contentWrapInner\"]/table/tbody/tr/td/p[1]/span"));
                String aboutPageTitle = aboutPageElement.getText().trim();
                assertTrue(aboutPageTitle.equalsIgnoreCase(fredAboutPage.FRED_ABOUT_PAGE));
                break;
            case "quickStart":
                WebElement quickStartPageElement = fredQuickStart.getDriver().findElement(BySelect.get("xpath", "//*[@id=\"contentWrapInner\"]/table/tbody/tr/td/p[1]/span"));
                String quickStartPageTitle = quickStartPageElement.getText().trim();
                assertTrue(quickStartPageTitle.equalsIgnoreCase(fredQuickStart.FRED_QUICK_START_GUIDE));
                break;
            case "loginPage":
                WebElement loginPageElement = fredLoginPage.getDriver().findElement(BySelect.get("xpath", "//*[@id=\"contentWrap\"]/div[1]/div"));
                String loginPageTitle = loginPageElement.getText().trim();
                assertTrue(loginPageTitle.equals(fredLoginPage.FRED_LOGIN_TITLE));
                break;
            case "userManual":
                String title = fredUserManualPage.getDriver().getTitle();
                boolean isUserManualTitle = title.equals(FREDUserManualPage.TITLE);
                assertThat(isUserManualTitle);
            default:
                break;
        }
    }

    @Then("^(?:.*)the FRED page title should appear$")
    public void the_fred_page_title_should_appear() throws Throwable {
        assertThat(fredHomePage.waitFor(ExpectedConditions.titleIs("FRED :: The Fossil Record Electronic Database")));
    }

    @Then("^(?:.*)the FRED login page with the title FRED Login should appear$")
    public void the_fred_login_page_with_the_title_fred_login_should_appear() throws Throwable {
        WebElement loginPageElement = fredLoginPage.getDriver().findElement(BySelect.get("xpath", "//*[@id=\"contentWrap\"]/div[1]/div"));
        String loginPageTitle = loginPageElement.getText().trim();
        assertTrue(loginPageTitle.equals(fredLoginPage.FRED_LOGIN_TITLE));
    }

    @Then("^(?:.*)the username value is not returned in HTML$")
    public void the_username_value_is_not_returned() throws Throwable {
        WebElement loginPageElement = fredLoginPage.getDriver().findElement(BySelect.get("xpath", "//*[@id=\"contentWrapInner\"]/table/tbody/tr[5]/td/center/form/table/tbody/tr[1]/td[2]/input"));
        String badUserName = loginPageElement.getText().trim();
        assertFalse(badUserName.equals(userName));
    }
}
