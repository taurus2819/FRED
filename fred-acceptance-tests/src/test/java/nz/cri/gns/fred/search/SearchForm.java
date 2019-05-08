package nz.cri.gns.fred.search;

import org.openqa.selenium.By;

public class SearchForm {
    static By SEARCH_FIELD = By.cssSelector(".js-search-input");
    static By SEARCH_BUTTON = By.cssSelector(".js-search-button");
    static By LINK_NPC = By.linkText("National Paleontological Collection (NPC)");
    static By FRED_USER_MANUL = By.linkText("FRED User Manual");
    static By LOGIN = By.xpath("//*[@id='navlist']//*[contains(@href, 'login.jsp')]"); //By.cssSelector("a[href='login.jsp']");
}
