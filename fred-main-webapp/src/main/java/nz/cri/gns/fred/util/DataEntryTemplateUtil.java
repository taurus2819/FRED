
package nz.cri.gns.fred.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import nz.cri.gns.core.Environment;
import nz.cri.gns.fred.FredStart;
import nz.cri.gns.xls.upload.XlsUploadUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

/**
 *
 * @author duncanw
 */
public class DataEntryTemplateUtil {
    public static final String TEMPLATE_FILE_NAME = "FRED.xlsm";
    
    public void writeDataUploadTemplate(HttpServletRequest request, HttpServletResponse response) throws IOException, InvalidFormatException, URISyntaxException {
        
        String baseUrl;
        String secureBaseUrl;
        XlsUploadUtils utils = new XlsUploadUtils();
        utils.setDefaultOpenSheetIndex(1);
        File versionedTemplate = new File(getVersionedTemplateFilename());
        String environment = utils.getEnvironmentName(request);
        if ("localhost".equals(environment) || !versionedTemplate.exists()) {
            baseUrl = utils.getBaseUrl(request);
            secureBaseUrl = baseUrl;
            utils.writeVersionedTemplate(
                    baseUrl, 
                    secureBaseUrl, 
                    TEMPLATE_FILE_NAME, 
                    environment, 
                    utils.getSystemProperties(FredStart.SYSTEM_PROPERTIES_FILE), 
                    response
            );            
        } else {
            utils.setXlsDownloadHeaders(TEMPLATE_FILE_NAME, environment, response);
            try (FileInputStream xlsInputStream = new FileInputStream(versionedTemplate)) {
                IOUtils.copy(xlsInputStream, response.getOutputStream());
            }
        }
    }
    
    private static String getDefaultBaseUrl() {
        return Environment.getDataUrl() + "/fred/";
    }
     
    public static void writeVersionedTemplate() throws IOException, InvalidFormatException, URISyntaxException {
                
        XlsUploadUtils utils = new XlsUploadUtils();
        utils.setDefaultOpenSheetIndex(1);
        utils.writeVersionedTemplate(
                getDefaultBaseUrl(), 
                TEMPLATE_FILE_NAME, 
                getVersionedTemplateFilename(), 
                utils.getSystemProperties(FredStart.SYSTEM_PROPERTIES_FILE)
        );         
    }
    
    public static String getDownloadUrl(HttpServletRequest request) {
        XlsUploadUtils utils = new XlsUploadUtils();
        return "data-entry-template/"+utils.getTemplateFilename(utils.getEnvironmentName(request), TEMPLATE_FILE_NAME);
    }
    
    public static String getVersionedTemplateFilename() {
        return System.getProperty("java.io.tmpdir") + File.separator + TEMPLATE_FILE_NAME;
    }    
}
