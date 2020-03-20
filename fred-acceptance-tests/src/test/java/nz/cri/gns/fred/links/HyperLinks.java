package nz.cri.gns.fred.links;

import net.serenitybdd.core.steps.UIInteractionSteps;
import net.thucydides.core.annotations.Step;
import nz.cri.gns.fred.navigation.FREDHomePage;
public class HyperLinks extends UIInteractionSteps{

    FREDHomePage fredHomePage;

    @Step("Click on FRED Link")
    public void clickLink(String linkName) {
        switch(linkName){
            case "quickStart":
                $(FREDLinkForm.QuickStart).click();
                break;
            case "about":
                $(FREDLinkForm.About).click();
                break;
            case "login":
                $(FREDLinkForm.Login).click();
                break;
            case "usermanual":
                $(FREDLinkForm.UserManual).click();
                break;
            case "dataentry": 
                $(FREDLinkForm.DataEntry).click();
                break;
            case "newfolder": 
                $(FREDLinkForm.NewFolder).click();
                break;
            default:
                break;
        }
    }
}
