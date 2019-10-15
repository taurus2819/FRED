package nz.cri.gns.fred.navigation;
import net.thucydides.core.annotations.Step;

/**
 *
 * @author bens
 */
public class NavigateTo {

    FREDAboutPage fredAboutPage;
    FREDHomePage fredHomePage;
    FREDLoginPage fredLoginPage;
    FREDQuickStartPage fredQuickStart;
    FREDUserManualPage fredUserManual;

    @Step("Open the FRED About page")
    public void theFREDAboutPage() {
        fredAboutPage.open();
    }
    @Step("Open the FRED home page")
    public void theFREDHomePage() {
        fredHomePage.open();
    }
    
    @Step("Open the FRED Login page")
    public void theFREDLoginPage() {
        fredLoginPage.open();
    }
    @Step("Open the FRED QuickStart page")
    public void theFREDQuickStartPage() {
        fredQuickStart.open();
    }
    @Step("Open the FRED User Manual page")
    public void theFREDUserManualPage() {
        fredUserManual.open();
    }
}

