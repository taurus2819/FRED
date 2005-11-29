package nz.cri.gns.fred.de;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import nz.cri.gns.auth.InsufficientPrivelegesException;
import nz.cri.gns.auth.User;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.StorageAccessException;
import nz.cri.gns.fred.model.FREDConstants;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.util.FeatureUtil;
import nz.cri.gns.fred.website.ContentProvider;

public class OutcropLocalityDE extends LocalityDE {

	private SampleDE sampleDE;

	public OutcropLocalityDE(User user, int folderID, DAOFactory factory, ContentProvider content) throws SQLException, IOException, DataInputException, StorageAccessException, InsufficientPrivelegesException {
		super(user, folderID, FREDConstants.OUTCROP, factory, content);
		sampleDE = new SampleDE(user, feature, folderID, factory, content, true);
		sampleDE.setOutcropSample(true);
	}
	
	/**
	 * Instantiates from an existing feature
	 * @throws StorageAccessException 
	 */
	public OutcropLocalityDE(Feature feature, int folderId, User user, DAOFactory factory, ContentProvider content) throws IOException, SQLException, DataInputException, InsufficientPrivelegesException, StorageAccessException  {
		super(feature, folderId, user, factory, content);
		if (!feature.getFeatureType().equals(FREDConstants.OUTCROP))
			throw new DataInputException("Feature Type", "Invalid");
		
		sampleDE = new SampleDE((Sample)feature.getSamples().iterator().next(), folderId, user, factory, provider);
		sampleDE.setOutcropSample(true);
	}

	public void copyFrom(int featureId) throws InsufficientPrivelegesException, StorageAccessException {
		super.copyFrom(featureId);
		Feature feature = featureUtil.getFeature(featureId);
		Sample sample = (Sample)feature.getSamples().iterator().next();
		sampleDE.copyFrom(sample.getSampleId());
	}

/*	public void setField(int field, String value) throws DataInputException {
		if (field < 30) {
			super.setField(field, value);
		} else {
			try {
				sampleDE.setField(field, value);
			} catch (TaxonomicListException e) {}
		}
		savedFlag = false;
	}

	public String getField(int field) {
		if (field < 30) {
			return super.getField(field);
		} else {
			return sampleDE.getField(field); 
		}
	}

	public void setTempField(int field, String value) {
		if (field < 30) {
			super.setTempField(field, value);
		} else {
			sampleDE.setTempField(field, value);
		}
	}

	public String getTempField(int field) {
		if (field < 30) {
			return super.getTempField(field);
		} else {
			return sampleDE.getTempField(field);
		}
	}
	*/
	
	public void makeDataEntryHTML(PrintWriter out, DAOFactory factory) throws IOException, SQLException {
		super.makeDataEntryHTML(out, factory);
		sampleDE.makeDataEntryHTML(out, factory);
		super.makeEndBitHTML(out);
	}

	public void makeExcelImportHTML(Writer out) throws IOException, SQLException {
		super.makeExcelImportHTML(out);
		out.write("<td></td>");
		out.write("<td></td>");
		out.write("<td></td>");
		out.write("<td></td>");
		out.write("<td></td>");
		out.write("<td></td>");
		out.write("<td></td>");
		out.write("<td></td>");
		sampleDE.makeExcelImportHTML(out);
		out.write("</tr>\n");
	}
	
	public int save() throws SQLException, IOException, StorageAccessException, InsufficientPrivelegesException {
		super.save();
		sampleDE.save();
		return feature.getFeatureId();
	}
	
	public int submit() throws SQLException, IOException, DataInputException, InsufficientPrivelegesException, StorageAccessException {
		super.submit();
		sampleDE.submit();
		return feature.getFeatureId();	
	}

	public String getHeading() {
		return "Edit outcrop locality";
	}

    @Override
    public void makePostFormHTML(PrintWriter out) throws IOException {
        sampleDE.makePostFormHTML(out);
    }

    @Override
    public boolean usesCalendar() {
        return sampleDE.usesCalendar();
    }

    @Override
    public void updateFromRequest(HttpServletRequest request, DAOFactory factory) throws DataInputException {
        DataInputException e = null;
        //Update the feature fields, catching any exceptions
        try {
            super.updateFromRequest(request, factory);
        } catch (DataInputException _e) {
            e = _e;
        }
        //Update the sample fields, catching and updating any exceptions
        try {
            sampleDE.updateFromRequest(request, factory);
        } catch (DataInputException _e) {
            if (e == null)
                e = _e;
            else
                e.getError().addAll(_e.getError());
        }
        //Try updating FRNumber again (after creating a sample) - will change when FRNumber moves to feature
        if (FeatureUtil.isBacklogFeature(feature)) {
        	System.out.println("Trying to update FRNumber for feature " + feature.getFeatureId() + " from OutcropLocalityDE");
        	try {
        		super.updateFRNumber(feature, request.getParameter("FRNumber"));
			} catch (DataInputException _e) {
	            if (e == null)
	                e = _e;
	            else
	                e.getError().addAll(_e.getError());
			}        	
        }
        if (e != null)
            throw e;
    }

}
