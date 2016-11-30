package nz.cri.gns.fred.servlet;

import java.io.IOException;
import java.net.URISyntaxException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import nz.cri.gns.fred.util.DataEntryTemplateUtil;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

/**
 *
 * @author duncanw
 */
public class DataEntryTemplateServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            new DataEntryTemplateUtil().writeDataUploadTemplate(request, response);
        } catch (InvalidFormatException|URISyntaxException ex) {
            throw new ServletException(ex);
        }
    }

    
}
