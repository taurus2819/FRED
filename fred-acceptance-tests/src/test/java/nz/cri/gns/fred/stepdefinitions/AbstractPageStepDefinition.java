/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.cri.gns.fred.stepdefinitions;

import cucumber.api.junit.Cucumber;
import nz.cri.gns.fred.navigation.FREDHomePage;
import nz.cri.gns.fred.navigation.NavigateToFredHomePage;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

/**
 *
 * @author sitikond
 */
@RunWith(Cucumber.class)
@Deprecated
public class AbstractPageStepDefinition {
    
    protected WebDriver driver;
    FREDHomePage fredHomePage;
    NavigateToFredHomePage navigateto;
    
    protected WebDriver getDriver(){
        System.out.println("getting chrome driver");
        if(driver == null){
            System.out.println("driver is null");
            navigateto.theFREDHomePage();
            driver = fredHomePage.getDriver();
            System.out.println("driver is NOT null" + driver.toString());
        }
        return driver;
    }
    
}
