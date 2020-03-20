package nz.cri.gns.fred.de;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import nz.cri.gns.auth.domain.exception.InsufficientPrivelegesException;
import nz.cri.gns.auth.domain.User;
import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.model.Adoption;
import nz.cri.gns.fred.model.FREDConstants;
import nz.cri.gns.fred.model.Record;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.util.FREDUtil;
import nz.cri.gns.fred.util.PersonUtil;
import nz.cri.gns.fred.util.StageUtil;
import nz.cri.gns.fred.website.ContentProvider;
import nz.cri.gns.intranet.Template;

public class AdoptionRecordDE extends RecordDE {

    public AdoptionRecordDE(User user, Sample sample, int folderID, DAOFactory factory, ContentProvider provider) throws StorageAccessException, InsufficientPrivelegesException {
        super(user, sample, folderID, FREDConstants.ADOPTION, factory, provider);
    }

    public AdoptionRecordDE(Record record, int folderId, User user, DAOFactory factory, ContentProvider provider) throws IllegalArgumentException, DataInputException, SQLException, IOException, InsufficientPrivelegesException, StorageAccessException {
        super(record, folderId, user, factory, provider);
    }

    @Override
    public void makeDataEntryHTML(PrintWriter out, DAOFactory factory) throws IOException, SQLException {
        super.makeDataEntryHTML(out, factory);

        Template template = provider.getContent("adoption.de.form");
        prepareTemplate(template, provider);

        Adoption adoption = record.getAdoption();

        template.addSub("AdoDate", FREDUtil.formatDateForDE(adoption.getAdoptionDate(), adoption.getDateRounding()));
        template.addSub("Adoptor", FREDUtil.getNames(adoption.getAdoptors(), "\n"));
        template.addSub("Comm", adoption.getComments());
        StageDEUtil.addStage2(template, adoption.getStage(), "Stage");
        template.loadAll(out);

        super.makeEndBitHTML(out);
    }

    @Override
    public void makePostFormHTML(PrintWriter out) throws IOException {
        Template template = provider.getContent("calendar.script");
        template.addSub("inputField", "AdoDate");
        template.addSub("button", "AdoDateCal");
        template.loadAll(out);
    }

    @Override
    public void makeExcelImportHTML(Writer out) throws IOException, SQLException {
    }

    @Override
    public void updateFromRequest(HttpServletRequest request, DAOFactory factory, boolean addIfNew) throws DataInputException {
        ArrayList<String[]> error = new ArrayList<>();

        super.updateFromRequest(request, factory, addIfNew);

        Adoption adoption = record.getAdoption();
        //Collection date
        try {
            String adoptionDate = request.getParameter("AdoDate");
            adoption.setAdoptionDate(FREDUtil.parseDateFromDE(adoptionDate));
            adoption.setDateRounding(FREDUtil.parseDateRoundingFromDE(adoptionDate));
        } catch (ParseException e) {
            error.add(new String[]{"Adoption Date", "Badly formatted date"});
        }

        //Adoptors
        try {
            adoption.setAdoptors(FREDUtil.getPersons(request.getParameter("Adoptor"), new PersonUtil(factory), "Adoptors", addIfNew));
        } catch (DataInputException e) {
            error.addAll(e.getError());
        }

        //Stage
        try {
            adoption.setStage(StageDEUtil.getStage(request, "Stage", adoption.getStage(), new StageUtil(factory), "Stage"));
        } catch (DataInputException e) {
            error.addAll(e.getError());
        }

        //Comments
        adoption.setComments(request.getParameter("Comm"));

        if (error.size() > 0) {
            throw new DataInputException(error);
        }
    }

    @Override
    public boolean usesCalendar() {
        return true;
    }

    @Override
    public String getHeading() {
        return "Edit adoption record";
    }

    @Override
    public int save(int dataOriginId) throws InsufficientPrivelegesException, StorageAccessException {
        int recordId = super.save(dataOriginId);
        if (record.getAdoption().getRecordId() == null) {
            recordUtil.saveOrUpdate(record.getAdoption());
        } else {
            recordUtil.saveOrUpdate(record.getAdoption());
        }
        return recordId;
    }

}
