package nz.cri.gns.fred.links;
import org.openqa.selenium.By;

/**
 *
 * @author bens
 */
// Does this have to be public if the constants are public?  Test project does not have Form class as public
public class FREDLinkForm {
    // By element name 
    public static By Login = By.xpath("//*[@id=\"contentWrapInner\"]/div[1]/a"); // Login button
    public static By QuickStart = By.xpath("//*[@id=\"navlist\"]/li[9]/a");     //Quick Start menu
    public static By About = By.xpath("//*[@id='navlist']//*[contains(@href, 'about.jsp')]");     //About menu
    public static By UserManual = By.xpath("//*[@id=\"navlist\"]/li[8]/a");     //User Manual link
    public static By DataEntry = By.xpath("//*[@id=\"navlist\"]/li[4]/a");     //Data Entry link
    public static By NewFolder = By.xpath("//*[@id=\"navlist\"]/li[8]/a");     //New Folder link
}
