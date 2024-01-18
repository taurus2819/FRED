package nz.cri.gns.fred.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import nz.cri.gns.fred.de.DataEntryForm;
import nz.cri.gns.fred.de.DataInputException;
import nz.cri.gns.fred.de.TaxonomicListException;
import nz.cri.gns.fred.model.FREDConstants;
import nz.cri.gns.fred.model.PaleontologyListEntry;
import nz.cri.gns.jsp.ExtranetTemplate;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.hibernate.util.FredHibernate;
import nz.cri.gns.fred.website.WebsiteConstants;
import nz.cri.gns.auth.domain.exception.InsufficientPrivelegesException;
import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.FREDHibernateServlet;
import nz.cri.gns.fred.servlet.util.FredHelper;
import nz.cri.gns.fred.servlet.util.JspWriterImpl;
import nz.cri.gns.jsp.IconnedLink;

/**
 * Was dp.jsp. Submit data entry stuff.
 */
public class Dp_jsp extends FREDHibernateServlet {

    private static final Logger log = Logger.getLogger("nz.cri.gns.fred.servlet.Dp_jsp");

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JspWriterImpl out = new JspWriterImpl(response.getOutputStream());
        HttpSession session = request.getSession();
        FredHelper h = new FredHelper();
        response.setContentType("text/html;charset=utf-8");

        if (null == request.getParameter("SaveType")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter SaveType is missing");
            return;
        }

        ExtranetTemplate et = h.getExtranetTemplate(session);
        h.addButtons(et, new IconnedLink[]{
            new IconnedLink((String) session.getAttribute(WebsiteConstants.DATA_ENTRY_ERROR_REDIRECT), "images/back_arrow.gif", "Back to Data Entry"),
            new IconnedLink((String) session.getAttribute(WebsiteConstants.DATA_ENTRY_REDIRECT), "images/cancel.gif", "Quit")
        });

        DataEntryForm dataEntryForm = (DataEntryForm) session.getAttribute(WebsiteConstants.DATA_ENTRY_FORM);

        if (null == dataEntryForm) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Sorry; your session went away by accident. Go back and try again.");
            return;
        }

        try {
            try {
                DAOFactory factory = FredHibernate.get().getDAOFactory();
                dataEntryForm.updateFromRequest(request, factory, false);

                if (request.getParameter("SaveType").equals("Submit")) {
                    dataEntryForm.submit(FREDConstants.DATA_ORIGIN_ONLINE);
                } else {
                    dataEntryForm.save(FREDConstants.DATA_ORIGIN_ONLINE);
                }

                String whereTo = (String) session.getAttribute(WebsiteConstants.DATA_ENTRY_REDIRECT);
                if (whereTo == null) {
                    response.sendRedirect("folder_list.jsp");
                } else {
                    response.sendRedirect(whereTo + "&q=" + Math.random());
                }

            } catch (TaxonomicListException e) {
                //Still save it
                // Can throw exceptions to be caught in the outer try{}catch{}
                dataEntryForm.save(FREDConstants.DATA_ORIGIN_ONLINE);

                session.setAttribute("taxa", request.getParameter("Taxa"));
                session.setAttribute(WebsiteConstants.BAD_TAXA_LIST, e.getTaxaList());
                session.setAttribute(WebsiteConstants.DATA_ENTRY_FORM, dataEntryForm);

                h.drawTop(out, et, request, response);

                out.write("<p>");

                h.include(request, out, "/content/detablestart.html");

                out.write("<table style=\"margin-left:20px; width:550px;\" border=\"0\">\n");
                out.write("\t\t\t<tr><td colspan=\"4\" class=\"deHeading\">Taxonomic Name Error</td></tr>\n");
                out.write("\t\t\t<tr><td colspan=\"4\">The following list contains taxonomic entries which do not match a value in the thesaurus.  This could be either because you have entered incorrect syntax or because the entry is not in the thesaurus.<br />Note submitted entries will be provisional until checked by database curators and you will not be able to submit this record until the entry has been approved.</td></tr>\n");
                out.write("\t\t\t<tr><td>&nbsp;</td></tr>\n");
                out.write("\t\t\t<tr><th>Group&nbsp;&nbsp;</th><th>Entered Name&nbsp;&nbsp;</th><th>Parsed Name&nbsp;&nbsp;</th><th>Author</th></tr>");

                for (PaleontologyListEntry t : e.getTaxaList()) {

                    out.write("<tr><td>");
                    out.print(t.getTaxonomicGroup().getName());
                    out.write("&nbsp;&nbsp;</td><td>");
                    out.print(t.getTaxonomicName());
                    out.write("&nbsp;&nbsp;</td><td>");
                    out.print(t.getTaxon().getTaxonomicName());
                    out.write("&nbsp;&nbsp;</td><td>");
                    out.print(t.getTaxon().getAuthor());
                    out.write("</td></tr>");

                }

                out.write("<tr><td colsapn=\"4\"><a href=\"submit_taxa.jsp\"><img src=\"images/submit.gif\" height=\"20\" width=\"20\" border=\"0\" alt=\"Submit Taxa\" title=\"Submit\"/></a>&nbsp;<a href=\"submit_taxa.jsp\">Submit Taxa.</a></td></tr>\n");
                out.write("\t\t\t<tr><td colspan=\"4\">Note: No reference to these taxa has been saved yet.  You must either choose to submit the above taxa or return to the data entry form, edit and re-save</td></tr>\n");
                out.write("\t\t\t</table>");

                h.include(request, out, "/content/detableend.html");

                out.write("</p>");

            }
        } catch (DataInputException e) {
            // DataInputExceptions contain a list of errors. They have no message.
            h.drawTop(out, et, request, response);
            h.include(request, out, "/content/detablestart.html");

            out.write("<table style=\"margin-left:20px; width:550px;\" border=\"0\">\n");
            out.write("\t\t\t<tr><td colspan=\"2\" class=\"deHeading\">Remaining Data Errors: ");
            out.write("</td></tr>");
            if (null != e.getError()) {
                for (String[] error : e.getError()) {

                    out.write("<tr><td class=\"heading\">Problem Field&nbsp;&nbsp;</td><td>");
                    out.print(error[0]);
                    out.write("</td></tr>\n");
                    out.write("\t\t\t\t<tr><td class=\"heading\">Error</td><td>");
                    out.print(error[1]);
                    out.write("</td></tr>");

                }
            } else if(request.getAttribute("errorMessage").toString().length()>0 ){
                out.write("<tr><td class=\"heading\">Data Input&nbsp;&nbsp;</td><td>");
                out.write("</td></tr>\n");
                out.write("\t\t\t\t<tr><td class=\"heading\">Error</td><td>");
                out.print(request.getAttribute("errorMessage").toString());
                out.write("</td></tr>");
            }
            else {
                out.write("<tr><td class=\"heading\">Problem Field&nbsp;&nbsp;</td><td>");
                out.write("</td></tr>\n");
                out.write("\t\t\t\t<tr><td class=\"heading\">Error</td><td>");
                out.print("Unfortunately, FRED didn't give any nice error messages to display here.");
                out.write("</td></tr>");
            }

            out.write("</table>");

            h.include(request, out, "/content/detableend.html");

            out.write("</p>");
            addOkButton(request, out);

        } catch (InsufficientPrivelegesException e) {
            h.drawTop(out, et, request, response);
            h.include(request, out, "/content/detablestart.html");

            out.write("<table style=\"margin-left:20px; width:550px;\" border=\"0\">\n");
            out.write("\t\t\t<tr><td class=\"deHeading\">Insufficient Privileges Error</td></tr>\n");
            out.write("\t\t\t<tr><td>You do not have sufficient rights to save this record</td></tr>");

            out.write("</table>");

            h.include(request, out, "/content/detableend.html");

            out.write("</p>");
            addOkButton(request, out);

        } catch (SQLException | StorageAccessException | IOException e) {
            log.log(Level.SEVERE, null, e);

            h.drawTop(out, et, request, response);
            h.include(request, out, "/content/detablestart.html");

            out.write("<table style=\"margin-left:20px; width:550px;\" border=\"0\">\n");
            out.write("\t\t\t<tr><td class=\"deHeading\">SQL Data Error</td></tr>\n");
            out.write("\t\t\t<tr><td>A Database error has occurred: ");
            out.print(e.getMessage());
            out.write("</td></tr>");

            out.write("</table>");

            h.include(request, out, "/content/detableend.html");

            out.write("</p>");
            addOkButton(request, out);

        } finally {
            out.flush();
        }

    }

    private void addOkButton( HttpServletRequest request, JspWriterImpl out) throws IOException {
        var session = request.getSession();
        var dest = (String) session.getAttribute(WebsiteConstants.DATA_ENTRY_REDIRECT);
        if (dest == null) {
            dest = "folder_list.jsp";
        } else {
            dest += "&q=" + Math.random();
        }
        out.write("<button onclick=\"window.location.href='");
        out.write(dest);
        out.write("';\">");
        out.write("Ok");
        out.write("</button>");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

}
