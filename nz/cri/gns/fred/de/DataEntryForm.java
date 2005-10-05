package nz.cri.gns.fred.de;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import nz.cri.gns.auth.InsufficientPrivelegesException;
import nz.cri.gns.fred.IconnedLink;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.StorageAccessException;

public interface DataEntryForm {
	
	public void copyFrom(int id) throws StorageAccessException, InsufficientPrivelegesException;

	public List<IconnedLink> getNavigation();
	
	public void makeDataEntryHTML(PrintWriter out) throws IOException, SQLException;
	
	/**
	 * Writes any HTML that the form requires outside of the HTML form
	 */
	public void makePostFormHTML(PrintWriter out) throws IOException;
	
	public void makeExcelImportHTML(Writer out) throws IOException, SQLException;
	
	public void updateFromRequest(HttpServletRequest request, DAOFactory factory) throws DataInputException;
	
	public int save() throws SQLException, IOException, InsufficientPrivelegesException, StorageAccessException;
	
	public int submit() throws SQLException, IOException, InsufficientPrivelegesException, DataInputException, StorageAccessException, DataInputException, DataInputException;

	public void delete() throws IOException, SQLException, InsufficientPrivelegesException, StorageAccessException;
	
	public int getWorkingFolderID();

	/**
	 * Returns true if this form requires the javascript calendar functions
	 */
	public boolean usesCalendar();
	
	/**
	 * Provides a meaningful heading for the page on which this form is displayed
	 */
	public String getHeading();
}
