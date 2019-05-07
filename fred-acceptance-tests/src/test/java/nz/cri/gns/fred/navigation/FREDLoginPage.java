/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.cri.gns.fred.navigation;

import net.serenitybdd.core.pages.PageObject;
import net.thucydides.core.annotations.DefaultUrl;

/**
 *
 * @author sitikond
 */
@DefaultUrl("http://localhost:8090/fred/login.jsp?loginpage=%2Ffred%2F")
public class FREDLoginPage extends PageObject{
    public static final String FRED_LOGIN_TITLE = "FRED Login";
}
