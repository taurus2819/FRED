/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.cri.gns.fred.search;

import org.openqa.selenium.By;

/**
 *
 * @author sitikond
 */
public class BySelect {
    
    public static By get(String type, String selector){
        By by = null;
        if("id".equalsIgnoreCase(type)){
            by = By.id(selector);
        }else if("name".equalsIgnoreCase(type)){
            by = By.name(selector);
        }else if ("className".equalsIgnoreCase(type)) {
            by = By.className(selector);
        }else if ("css".equalsIgnoreCase(type)) {
            by = By.cssSelector(selector);
        }else if ("linkText".equalsIgnoreCase(type)) {
            by = By.linkText(selector);
        }else if ("xpath".equalsIgnoreCase(type)){
            by = By.xpath(selector);
        }
        return by;
    }
    
}
