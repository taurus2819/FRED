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
public class EnvironmentConfig {
    
    Properties configFile;
    
    public EnvironmentConfig() throws FileNotFoundException, IOException{
    	configFile = new Properties();    	
    	configFile.load(new FileInputStream("src/test/resources/serenity.conf"));
    }
    
    public String getProperty(String environment){
        String value = this.configFile.getProperty(environment);
        return value;
    }
    
}
