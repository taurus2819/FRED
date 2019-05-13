/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.cri.gns.fred.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sitikond
 */
@Deprecated
public class FredTestConfig {
    
    Properties configFile;
    
    public FredTestConfig(String propertyFile) throws FileNotFoundException, IOException{
    	configFile = new Properties();    	
    	configFile.load(new FileInputStream(propertyFile));
    }
    
    public String getProperty(String environment){
        String value = this.configFile.getProperty(environment);
        return value;
    }
    
}
