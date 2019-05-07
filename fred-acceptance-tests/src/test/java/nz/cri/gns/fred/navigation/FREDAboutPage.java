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
@DefaultUrl("http://localhost:8090/fred/about.jsp")
public class FREDAboutPage extends PageObject{
    public static final String FRED_ABOUT_PAGE = "About the New Zealand Fossil Record File";
}
