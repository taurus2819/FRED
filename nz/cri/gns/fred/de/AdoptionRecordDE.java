package nz.cri.gns.fred.de;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.SQLException;
import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;

import nz.cri.gns.auth.InsufficientPrivelegesException;
import nz.cri.gns.auth.User;
import nz.cri.gns.db.ComboDescriptor;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.StorageAccessException;
import nz.cri.gns.fred.model.Adoption;
import nz.cri.gns.fred.model.FREDConstants;
import nz.cri.gns.fred.model.Record;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.model.Stage;
import nz.cri.gns.fred.util.FREDUtil;
import nz.cri.gns.fred.util.PersonUtil;
import nz.cri.gns.fred.util.SampleUtil;
import nz.cri.gns.fred.website.ContentProvider;
import nz.cri.gns.intranet.Template;

public class AdoptionRecordDE extends RecordDE {

    public AdoptionRecordDE(User user, Sample sample, int folderID, DAOFactory factory, ContentProvider provider) throws StorageAccessException, InsufficientPrivelegesException {
        super(user, sample, folderID, FREDConstants.ADOPTION, factory, provider);
    }

	public AdoptionRecordDE(Record record, int folderId, User user, DAOFactory factory, ContentProvider provider) throws IllegalArgumentException, DataInputException, SQLException, IOException, InsufficientPrivelegesException, StorageAccessException {
		super(record, folderId, user, factory, provider);
	}

	public void makeDataEntryHTML(PrintWriter out, DAOFactory factory) throws IOException, SQLException {
		super.makeDataEntryHTML(out, factory);
        
        Template template = provider.getContent("adoption.de.form");
        
        Adoption adoption = record.getAdoption();
        
        template.addSub("AdoDate", FREDUtil.formatDateForDE(adoption.getAdoptionDate(), adoption.getDateRounding()));
        template.addSub("Adoptor", FREDUtil.getNames(adoption.getAdopters(), "\n"));
        template.addSub("Comm", adoption.getComments());
        Stage stage = adoption.getStage();
        if (stage != null && stage.getStageLowerMod() != null)
            template.addSub("isStartUnc", "Yes");
        if (stage != null && stage.getStageUpperMod() != null)
            template.addSub("isStopUnc", "Yes");
        
        template.loadUntil(out, "{@StageStart}");
        
        try {
            ComboDescriptor cd = new ComboDescriptor("Age_View", "Ag_ID", "Ag_Name");
    		cd.name = "StageStart";
    		cd.prompt = " -- Choose -- ";
    		cd.selected = (stage != null && stage.getStageLowerId() != null) ? adoption.getStage().getStageLowerId().toString() : null;
    		cd.orderBy = "Ag_Name";
    		FREDUtil.makeDropBox(out, cd);
    
    
            template.loadUntil(out, "{@StageStop}");
            cd.name = "StageStop";
            cd.selected = (stage != null && stage.getStageUpperId() != null) ? adoption.getStage().getStageUpperId().toString() : null;
            FREDUtil.makeDropBox(out, cd);
        } catch (Exception e) {
            //TODO somehting...
        }
        template.loadAll(out);
        
		super.makeEndBitHTML(out);
	}

    public void makePostFormHTML(PrintWriter out) throws IOException {
        Template template = provider.getContent("calendar.script");
        template.addSub("inputField", "AdoDate");
        template.loadAll(out);
    }

	public void makeExcelImportHTML(Writer out) throws IOException, SQLException {
	}

    public void updateFromRequest(HttpServletRequest request, DAOFactory factory) throws DataInputException {
        String[] error = null;
        
        super.updateFromRequest(request, factory);
        
        Adoption adoption = record.getAdoption();
        //Collection date
        try {
            String adoptionDate = request.getParameter("AdoDate");
            adoption.setAdoptionDate(FREDUtil.parseDateFromDE(adoptionDate));
            adoption.setDateRounding(FREDUtil.parseDateRoundingFromDE(adoptionDate));
        } catch (ParseException e) {
            error = new String[] {"Adoption Date", "Badly formatted date"};
        }
        
        //Adoptors
        try {
            adoption.setAdopters(FREDUtil.getPersons(request.getParameter("Adoptor"), new PersonUtil(factory)));
        } catch (DataInputException e) {
            error = new String[] {e.getField(), e.getMessage()};
        }
       
        //Stage
        try {
            adoption.setStage(FREDUtil.getStage(request, "", adoption.getStage(), new SampleUtil(factory)));
        } catch (DataInputException e) {
            error = new String[] {e.getField(), e.getMessage()};
        }

        //Comments
        adoption.setComments(request.getParameter("Comm"));
        
        if (error != null) {
            throw new DataInputException(error[0], error[1]);
        }
    }

    public boolean usesCalendar() {
        return true;
    }

	public String getHeading() {
		return "Edit adoption record";
	}
}
