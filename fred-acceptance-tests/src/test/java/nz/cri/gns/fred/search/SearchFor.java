package nz.cri.gns.fred.search;

import net.serenitybdd.core.steps.UIInteractionSteps;
import net.thucydides.core.annotations.Step;
import org.openqa.selenium.By;

public class SearchFor extends UIInteractionSteps {

    @Step("Search for term {0}")
    public void term(String term) {
        $(SearchForm.SEARCH_FIELD).clear();
        $(SearchForm.SEARCH_FIELD).type(term);
        $(SearchForm.SEARCH_BUTTON).click();
    }
    
    @Step("Select group {0}")
    public void group(String term) {
        if (term.equalsIgnoreCase("National Paleontological Collection (NPC)")) {
            By linkNpc = SearchForm.LINK_NPC;
            $(linkNpc).click();
        } else if(term.equalsIgnoreCase("FRED User Manual")){
            By fredUserManualMenu = SearchForm.FRED_USER_MANUL;
            $(fredUserManualMenu).click();
        } else if(term.equalsIgnoreCase("Login")){
            By loginMenu = SearchForm.LOGIN;
            $(loginMenu).click();
        }
    }
}
