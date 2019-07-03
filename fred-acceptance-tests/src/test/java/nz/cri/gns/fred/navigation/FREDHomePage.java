package nz.cri.gns.fred.navigation;

import net.serenitybdd.core.pages.PageObject;
import net.thucydides.core.annotations.DefaultUrl;

@DefaultUrl("http://localhost:8090/fred/")
public class FREDHomePage extends PageObject {
    public static final String FRED_URL = "http://localhost:8090/fred/";
    public static final String TITLE = "FRED :: The Fossil Record Electronic Database";
}
