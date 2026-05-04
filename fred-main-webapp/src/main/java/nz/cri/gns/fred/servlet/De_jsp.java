package nz.cri.gns.fred.servlet;

import nz.cri.gns.fred.servlet.util.FredHelper;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.*;
import nz.cri.gns.jsp.IconnedLink;
import nz.cri.gns.fred.de.DataEntryForm;
import nz.cri.gns.fred.de.DataEntryFormFactorySiteApi;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.hibernate.util.hibernate6.FredHibernate;
import nz.cri.gns.fred.model.FREDConstants;
import nz.cri.gns.fred.website.ContentProvider;
import nz.cri.gns.fred.website.WebsiteConstants;
import nz.cri.gns.jsp.ExtranetTemplate;
import java.io.File;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import nz.cri.gns.auth.domain.User;
import nz.cri.gns.auth.domain.exception.InsufficientPrivelegesException;
import nz.cri.gns.auth.security.IpGrantedAuthority;
import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.FREDHibernateServlet;
import nz.cri.gns.fred.FredGrantedAuthorities;
import nz.cri.gns.fred.de.DataEntryFormFactorySiteApi;
import nz.cri.gns.fred.de.DataInputException;
import nz.cri.gns.fred.servlet.util.JspWriterImpl;
import nz.cri.gns.xss.SanitizeHttpServletRequest;

/**
 * Was de.jsp
 */
public class De_jsp extends FREDHibernateServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SanitizeHttpServletRequest sanitizeHttpRequest = new SanitizeHttpServletRequest();
        JspWriterImpl out = new JspWriterImpl(response.getOutputStream());
        HttpSession session = request.getSession();
        FredHelper h = new FredHelper(); // Replaces subclassing FREDDEIPSysJspPage. 
        DAOFactory factory = FredHibernate.get().getDAOFactory();

        try {

            if (!h.checkAccess(request, response, new IpGrantedAuthority(FredGrantedAuthorities.FR_DATA_ENTRY))) {
                return;
            }

            response.setContentType("text/html;charset=utf-8");
            ExtranetTemplate et = h.getExtranetTemplate(session);

            DataEntryForm dataEntryForm = null;

            dataEntryForm = getDataEntryForm(h, request);

            if (dataEntryForm != null) {
                if (dataEntryForm.usesCalendar()) {
                    et.addScript("scripts/calendar-stripped.js");
                    et.addScript("scripts/calendar-en-stripped.js");
                    et.addScript("scripts/calendar-setup-stripped.js");
                    et.addStyleSheet("skins/aqua/theme.css");
                }
                et.addScript("scripts/dataentry.js");
                List list = dataEntryForm.getNavigation();
                IconnedLink[] links = (IconnedLink[]) list.toArray(new IconnedLink[list.size() + 1]);
                links[links.length - 1] = new IconnedLink((String) request.getSession().getAttribute(WebsiteConstants.DATA_ENTRY_REDIRECT), "images/cancel.gif", "Quit");
                h.addButtons(et, links);

                h.drawTop(out, et, request, response);

                if (request.getParameter("CopyID") != null) {
                    try {
                        dataEntryForm.copyFrom(Integer.parseInt(request.getParameter("CopyID")));
                    } catch (NumberFormatException e) {
                        throw new ServletException("Malformed parameter: CopyID");
                    }
                }

                String formType = sanitizeHttpRequest.stripAllScripts(request.getParameter("Type"));
                String featID = sanitizeHttpRequest.stripAllScripts(request.getParameter("FeatID"));
                String sampID = sanitizeHttpRequest.stripAllScripts(request.getParameter("SampID"));
                String recID = sanitizeHttpRequest.stripAllScripts(request.getParameter("RecID"));
                String foldID = sanitizeHttpRequest.stripAllScripts(request.getParameter("FoldID"));

                //save DataEntryForm in session
                session.setAttribute(WebsiteConstants.DATA_ENTRY_FORM, dataEntryForm);
                if (featID != null) {
                    session.setAttribute(WebsiteConstants.DATA_ENTRY_ERROR_REDIRECT, "de.jsp?Err=Yes&Type=" + formType + "&FeatID=" + featID + "&FoldID=" + foldID);
                } else if (sampID != null) {
                    session.setAttribute(WebsiteConstants.DATA_ENTRY_ERROR_REDIRECT, "de.jsp?Err=Yes&Type=" + formType + "&SampID=" + sampID + "&FoldID=" + foldID);
                } else if (recID != null) {
                    session.setAttribute(WebsiteConstants.DATA_ENTRY_ERROR_REDIRECT, "de.jsp?Err=Yes&Type=" + formType + "&RecID=" + recID + "&FoldID=" + foldID);
                } else {
                    session.setAttribute(WebsiteConstants.DATA_ENTRY_ERROR_REDIRECT, "de.jsp?Err=Yes&Type=" + formType + "&FoldID=" + foldID);
                }

                out.write("<form name=\"form1\" method=\"post\" action=\"dp.jsp\"> \n");
                out.write("\t\t\t<input type=\"hidden\" name=\"SaveType\" value=\"\" />\n");
                out.write("\t\t\t<p><div id=\"showInst\"><table border=\"0\" width=\"550\" style=\"border: none; width: 550px\"><tr><td style=\"text-align: left\"><a href=\"javascript:showHide('inst', 'showInst');\">Instructions...</a></td></tr></table></div><div id=\"inst\" style=\"visibilty: hidden; display: none\">");

                h.include(request, out, "/content/detablestart.html");

                out.write("<table border=\"0\" style=\"border: none; width: 550px\" width=\"550\"><tr><td style=\"text-align: left\">\n");
                out.write("\t\t\t<tr><td colspan=\"3\" class=\"deHeading\">Instructions</td></tr><tr><td style=\"text-align: left\">\n");
                out.write("\t\t\t<ul>\n");
                out.write("<li>Please fill out as much information as possible</li>\n");
                out.write("<li>Red fields must be completed before submitting this form</li>\n");
                out.write("<li>Some entry fields require specific formatting:\n");
                out.write("    <ul>\n");
                out.write("<li>For Date please use this format – 01/02/2011</li>\n");
                out.write("<li>Identifiers, Stage Limits, Group and Taxonomic name entries are all type-and-select</li>\n");
                out.write("<li>Laboratory information fields are drop-down select</li>\n");
                out.write("<li>All other fields are free-hand type</li>\n");
                out.write("    </ul>\n");
                out.write("<li>Taxonomic list:<ul>\n");
                out.write("<li>If you know the group enter it in the Group box otherwise leave it blank. For new entries (ie entries not in the database) please enter a Group and an Author if appropriate</li>\n");
                out.write("<li>Taxonomic name accepts the following abbreviations and qualifiers:\n");
                out.write("<ul>\n");
                out.write("<li>aff. = affinis/related to</li>\n");
                out.write("<li>cf. = confer/compared with</li>\n");
                out.write("<li>fam. = family</li>\n");
                out.write("<li>gen. = genus</li>\n");
                out.write("<li>gr. = group</li>\n");
                out.write("<li>MS. = manuscript name</li>\n");
                out.write("<li>n.gen. = new genus</li>\n");
                out.write("<li>n.sp. = new species (also n.spp.)</li>\n");
                out.write("<li>s.l. = sensu lato/broad sense</li>\n");
                out.write("<li>s.s. = sensu stricto/strict sense</li>\n");
                out.write("<li>sp. = species</li>\n");
                out.write("<li>spp. = multiple species</li>\n");
                out.write("<li>subgen. = subgenus</li>\n");
                out.write("<li>subsp. = subspecies</li>\n");
                out.write("<li>var. = variety</li>\n");
                out.write("</ul>\n");
                out.write("<li>Taxonomic names listed below in red have not yet been approved (or have been rejected) - place your mouse over these entries to see more details. Records can't be submitted until all entries are approved</li>\n");
                out.write("<li>Entries in the Comments box are interpreted in the following way:<ul>\n");
                out.write("<li>Numeric value -> Specimen Count</li>\n");
                out.write("<li>Non-numeric value -> Comments</li>\n");
                out.write("<li>x|y -> Specimen Count = x, Specimen Coordinates = y</li>\n");
                out.write("<li>x|y|z -> Specimen Count = x, Specimen Coordinates = y, Comments = z</li>\n");
                out.write("    </ul>\n");
                out.write("</li>\n");
                out.write("    </ul>\n");
                out.write("</li></ul>\n");
                out.write("\t\t\t</td></tr>\n");
                out.write("\t\t\t<tr><td style=\"text-align: right\"><a href=\"javascript:showHide('showInst', 'inst');\">Hide instructions...</a></td></tr></table>");

                h.include(request, out, "/content/detableend.html");

                out.write("</div>\n");
                out.write("\t\t\t\n");
                out.write("\t\t\t<p>");

                dataEntryForm.makeDataEntryHTML(new PrintWriter(out), factory);

                out.write("</form>");

                dataEntryForm.makePostFormHTML(new PrintWriter(out));

            }

            out.write("<script><!--\n");
            out.write("\tfunction submitForm(saveType) {\n");
            out.write("\t\tdocument.form1.SaveType.value = saveType;\n");
            out.write("\t\tif (!window.preSubmit || preSubmit()) {\n");
            out.write("\t\t\tdocument.form1.submit();\n");
            out.write("\t\t}\n");
            out.write("\t}\n");
            out.write("\t//--></script>");

            h.drawBottom(out, et);
            out.flush();
        } catch (StorageAccessException | InsufficientPrivelegesException | SQLException e) {
            throw new ServletException(e);
        }
    }

    public String getName(HttpServletRequest request) {
        return "FRED Data Entry";
    }

    protected DataEntryForm getDataEntryForm(FredHelper h, HttpServletRequest request) throws ServletException {
        DataEntryForm form = getDataEntryFormImpl(h, request);
        request.setAttribute(WebsiteConstants.DATA_ENTRY_FORM, form);
        request.getSession().setAttribute(WebsiteConstants.DATA_ENTRY_FORM, form);
        return form;
    }

    private DataEntryForm getDataEntryFormImpl(FredHelper h, HttpServletRequest request) throws ServletException {
        SanitizeHttpServletRequest sanitizeHttpRequest = new SanitizeHttpServletRequest();
        if (request.getAttribute(WebsiteConstants.DATA_ENTRY_FORM) != null) {
            return (DataEntryForm) request.getAttribute(WebsiteConstants.DATA_ENTRY_FORM);
        }
        HttpSession session = request.getSession();
        if (sanitizeHttpRequest.stripAllScripts(request.getParameter("Err")) != null || sanitizeHttpRequest.stripAllScripts(request.getParameter("CopyID")) != null) {
            return (DataEntryForm) session.getAttribute(WebsiteConstants.DATA_ENTRY_FORM);
        } else {
            DAOFactory factory = FredHibernate.get().getDAOFactory();
            User user = (User) h.getUser(session);
            String formType = sanitizeHttpRequest.stripAllScripts(request.getParameter("Type"));
            Integer foldId = h.paramAsInteger(request, "FoldID");
            if (null == foldId) {
                throw new ServletException("Parameter FoldID is missing or malformed");
            }

            try {
                // TODO: I've changed some of this behaviour - paramAsInteger will return null if the value is malformed. - mikevdg.

                ContentProvider provider = new ContentProvider(new File(request.getSession().getServletContext().getRealPath("/content")));
                if (formType.equals(FREDConstants.OUTCROP) || formType.equals(FREDConstants.DRILLHOLE) || formType.equals(FREDConstants.VERTICAL_SECTION)) {
                    Integer featID = h.paramAsInteger(request, "FeatID");
                    if (featID != null) { //editing
                        return DataEntryFormFactorySiteApi.getLocalityDataEntryForm(featID, foldId, user, factory, provider);
                    } else {
                        return DataEntryFormFactorySiteApi.getLocalityDataEntryForm(formType, user, foldId, factory, provider);
                    }
                } else if (formType.equals("Sample")) {
                    Integer sampID = h.paramAsInteger(request, "SampID");

                    if (sampID != null) { //editing
                        return DataEntryFormFactorySiteApi.getSampleDataEntryForm(sampID, foldId, user, factory, provider);
                    } else {
                        Integer featID = h.paramAsInteger(request, "FeatID");
                        return DataEntryFormFactorySiteApi.getSampleDataEntryForm(user, featID, foldId, factory, provider);
                    }
                } else {
                    Integer recID = h.paramAsInteger(request, "RecID");
                    if (recID != null) { //editing
                        return DataEntryFormFactorySiteApi.getRecordDataEntryForm(recID, foldId, user, factory, provider);
                    } else {
                        Integer sampID = h.paramAsInteger(request, "SampID");
                        return DataEntryFormFactorySiteApi.getRecordDataEntryForm(formType, user, sampID, foldId, factory, provider);
                    }
                }
            } catch (DataInputException | IOException | IllegalArgumentException | InsufficientPrivelegesException | SQLException | StorageAccessException e) {
                throw new ServletException(e);
            }
        }
    }

}
