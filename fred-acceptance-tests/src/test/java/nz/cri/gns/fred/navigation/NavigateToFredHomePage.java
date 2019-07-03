package nz.cri.gns.fred.navigation;
import net.thucydides.core.annotations.Step;

/**
 *
 * @author sitikond
 */
public class NavigateToFredHomePage {

    FREDHomePage fredHomePage;

    @Step("Open the FRED home page")
    public void theFREDHomePage() {
        fredHomePage.open();
    }
}