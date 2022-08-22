package nz.cri.gns.fred.de;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.SQLException;
import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;

import nz.cri.gns.auth.domain.exception.InsufficientPrivelegesException;
import nz.cri.gns.auth.domain.User;
import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.model.FREDConstants;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.util.FREDUtil;
import nz.cri.gns.fred.util.PersonUtil;
import nz.cri.gns.fred.website.ContentProvider;
import nz.cri.gns.intranet.Template;
import nz.cri.gns.xss.SanitizeHttpServletRequest;

public class DrillholeLocalitySiteApiDE extends LocalitySiteapiDE {

    public DrillholeLocalitySiteApiDE(User user, int folderID, DAOFactory factory, ContentProvider provider) throws SQLException, IOException, DataInputException, StorageAccessException, InsufficientPrivelegesException {
        this(user, folderID, factory, provider, FREDConstants.DRILLHOLE);
    }

    protected DrillholeLocalitySiteApiDE(User user, int folderID, DAOFactory factory, ContentProvider provider, String localityType) throws StorageAccessException, InsufficientPrivelegesException {
        super(user, folderID, localityType, factory, provider);
    }

    public DrillholeLocalitySiteApiDE(Feature feature, int folderID, User user, DAOFactory factory, ContentProvider provider) throws IOException, SQLException, DataInputException, InsufficientPrivelegesException, StorageAccessException {
        this(feature, folderID, user, factory, provider, FREDConstants.DRILLHOLE);
    }

    protected DrillholeLocalitySiteApiDE(Feature feature, int folderID, User user, DAOFactory factory, ContentProvider provider, String localityType) throws InsufficientPrivelegesException, StorageAccessException, DataInputException, IOException {
        super(feature, folderID, user, factory, provider);
        if (!feature.getFeatureType().equals(localityType)) {
            throw new DataInputException("Feature Type", "Invalid");
        }
    }

    @Override
    protected void getFromDatabase(Feature fromFeature) throws InsufficientPrivelegesException, IOException {
        super.getFromDatabase(feature);
        //set drillhole or vs fields fields
        feature.setPerson(fromFeature.getPerson());
        feature.setStartDate(fromFeature.getStartDate());
        feature.setStartDateRounding(fromFeature.getStartDateRounding());
        feature.setFinishDate(fromFeature.getFinishDate());
        feature.setFinishDateRounding(fromFeature.getFinishDateRounding());
        feature.setDrillholeLicenceName(fromFeature.getDrillholeLicenceName());
        feature.setDatumType(fromFeature.getDatumType());
        feature.setDatumElevation(fromFeature.getDatumElevation());
        feature.setStartDepth(fromFeature.getStartDepth());
        feature.setFinishDepth(fromFeature.getFinishDepth());
        feature.setDepthUnit(fromFeature.getDepthUnit());
    }

    @Override
    public void updateFromRequest(HttpServletRequest request, DAOFactory factory, boolean addIfNew) throws DataInputException {
        SanitizeHttpServletRequest sanitizeHttpRequest = new SanitizeHttpServletRequest();
        super.updateFromRequest(request, factory, addIfNew);

        String[] error = null;

        //Operator
        String operator = sanitizeHttpRequest.stripAllScripts(request.getParameter("Person")).trim();
        //Will always be a company
        if (operator.length() == 0) {
            feature.setPerson(null);
        } else if (feature.getPerson() == null || !feature.getPerson().getName().equals(operator)) {
            try {
                feature.setPerson(new PersonUtil(factory).findOrCreatePerson(operator));
            } catch (StorageAccessException e) {
                error = new String[]{"Operating Company", "Error storing the operator's name"};
            }
        }

        try {
            String startDate = sanitizeHttpRequest.stripAllScripts(request.getParameter("StartDate"));
            feature.setStartDate(FREDUtil.parseDateFromDE(startDate));
            feature.setStartDateRounding(FREDUtil.parseDateRoundingFromDE(startDate));
        } catch (ParseException e) {
            error = new String[]{"Start Date", "Badly formatted date"};
        }

        try {
            String finishDate = sanitizeHttpRequest.stripAllScripts(request.getParameter("FinishDate"));
            feature.setFinishDate(FREDUtil.parseDateFromDE(finishDate));
            feature.setFinishDateRounding(FREDUtil.parseDateRoundingFromDE(finishDate));
        } catch (ParseException e) {
            error = new String[]{"Start Date", "Badly formatted date"};
        }

        feature.setDrillholeLicenceName(sanitizeHttpRequest.stripAllScripts(request.getParameter("LicArea")));

        String datumType = sanitizeHttpRequest.stripAllScripts(request.getParameter("DatumType"));
        if (datumType.equals("-")) {
            feature.setDatumType(null);
        } else {
            feature.setDatumType(datumType);
        }

        String datumElevation = sanitizeHttpRequest.stripAllScripts(request.getParameter("DatumEl")).trim();
        if (datumElevation.length() == 0) {
            feature.setDatumElevation(null);
        } else {
            try {
                feature.setDatumElevation(new Double(datumElevation));
            } catch (NumberFormatException e) {
                error = new String[]{"Datum Elevation", "Invalid elevation entered"};
            }
        }

        Double startDepth = null;
        Double finishDepth = null;
        if (sanitizeHttpRequest.stripAllScripts(request.getParameter("StartDepth")).length() > 0) {
            try {
                startDepth = new Double(sanitizeHttpRequest.stripAllScripts(request.getParameter("StartDepth")).trim());
            } catch (NumberFormatException e) {
                error = new String[]{"Kickoff Depth", "Invalid depth entered"};
            }
        }

        if (sanitizeHttpRequest.stripAllScripts(request.getParameter("FinishDepth")).length() > 0) {
            try {
                finishDepth = new Double(sanitizeHttpRequest.stripAllScripts(request.getParameter("FinishDepth")).trim());
            } catch (NumberFormatException e) {
                error = new String[]{"Termination Depth", "Invalid depth entered"};
            }
            if (sanitizeHttpRequest.stripAllScripts(request.getParameter("StartDepth")).length() > 0) {
                if ("Bottom".equals(feature.getDatumType())) {
                    if (startDepth.doubleValue() < finishDepth.doubleValue()) {
                        error = new String[]{"Depths/Heights", "Top horizon < base horizon and datum = Bottom"};
                    }
                } else {
                    if (startDepth.doubleValue() > finishDepth.doubleValue()) {
                        error = new String[]{"Depths", "Top horizon/kickoff depth > base horizon/termination depth"};
                    }
                }
            }
        }

        feature.setStartDepth(startDepth);
        feature.setFinishDepth(finishDepth);
        String depthUnit = sanitizeHttpRequest.stripAllScripts(request.getParameter("DepthUnit"));
        if ("ft".equals(depthUnit)) {
            feature.setDepthUnit("ft");
        } else {
            feature.setDepthUnit("m");
        }

        if (error != null) {
            throw new DataInputException(error[0], error[1]);
        }
    }

    @Override
    public void makeDataEntryHTML(PrintWriter out, DAOFactory factory) throws IOException, SQLException, StorageAccessException {
        super.makeDataEntryHTML(out, factory);

        Template template = provider.getContent(getContentPrefix() + ".de.form");
        prepareTemplate(template, provider);
        loadTemplate(template, out);

        super.makeEndBitHTML(out);
    }

    protected void loadTemplate(Template template, PrintWriter out) throws IOException {
        if (feature.getPerson() != null) {
            template.addSub("Person", feature.getPerson().getDisplayName());
        }
        if (feature.getStartDate() != null) {
            template.addSub("StartDate", FREDUtil.formatDateForDE(feature.getStartDate(), feature.getStartDateRounding()));
        }
        if (feature.getFinishDate() != null) {
            template.addSub("FinishDate", FREDUtil.formatDateForDE(feature.getFinishDate(), feature.getFinishDateRounding()));
        }
        if (feature.getDrillholeLicenceName() != null) {
            template.addSub("LicArea", feature.getDrillholeLicenceName());
        }

        if (feature.getDatumType() != null) {
            template.addSub("is" + feature.getDatumType(), "yes");
            template.addSub("datumType", feature.getDatumType());
        }
        if (feature.getDatumElevation() != null) {
            template.addSub("datumElevation", feature.getDatumElevation().toString());
        }

        if (feature.getStartDepth() != null) {
            template.addSub("startDepth", feature.getStartDepth().toString());
        }
        if (feature.getFinishDepth() != null) {
            template.addSub("finishDepth", feature.getFinishDepth().toString());
        }

        if ("ft".equals(feature.getDepthUnit())) {
            template.addSub("depthft", "checked ");
        } else {
            template.addSub("depthm", "checked ");
        }


        template.loadAll(out);
    }

    @Override
    public void makeExcelImportHTML(Writer out) throws IOException, SQLException {
        super.makeExcelImportHTML(out);
        loadTemplate(provider.getContent(getContentPrefix() + ".de.excel"), new PrintWriter(out));
        //add whole lot of empty cells to wipe-out any samples
        for (int i = 0; i < 46; i++) {
            out.write("<td></td>\n");
        }
        out.write("</tr>\n");
    }

    protected String getContentPrefix() {
        return "drillhole";
    }

    @Override
    public boolean usesCalendar() {
        return true;
    }

    @Override
    public void makePostFormHTML(PrintWriter out) throws IOException {
        Template template = provider.getContent("calendar.script");
        template.addSub("inputField", "StartDate");
        template.addSub("button", "StartDateCal");
        template.loadAll(out);
        template = provider.getContent("calendar.script");
        template.addSub("inputField", "FinishDate");
        template.addSub("button", "FinishDateCal");
        template.loadAll(out);
    }

    public String getHeading() {
        return "Edit drillhole locality";
    }
}
