/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.cri.gns.fred.navigation;

import net.thucydides.core.annotations.Step;

/**
 *
 * @author sitikond
 */
public class NavigateToFredUserManualPage {
    FREDUserManualPage fredUsmPage;
    
    @Step("Open the FRED user manual page")
    public void theFredUserManualPage(){
        fredUsmPage.open();
    }
}
