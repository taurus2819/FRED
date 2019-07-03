package nz.cri.gns.fred.links;

import org.openqa.selenium.By;

public class HyperLinks {
//    public static By LINK_NPC = By.cssSelector(//*[@id="contentWrapInner"]/table/tbody/tr[5]/td/p[2]/a);
    public static By LINK_NPC = By.linkText("National Paleontological Collection (NPC)");
    public static By LOGIN = By.cssSelector("a[href='login.jsp?loginpage=%2Ffred%2Findex.jsp']");
}
