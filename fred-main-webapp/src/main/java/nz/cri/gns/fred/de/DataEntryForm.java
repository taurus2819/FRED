package nz.cri.gns.fred.de;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import nz.cri.gns.auth.domain.exception.InsufficientPrivelegesException;
import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.jsp.IconnedLink;

public interface DataEntryForm {
	
	public void copyFrom(int id) throws StorageAccessException, InsufficientPrivelegesException;

	public List<IconnedLink> getNavigation();
	
	public void makeDataEntryHTML(PrintWriter out, DAOFactory factory) throws IOException, SQLException, StorageAccessException;
	
	/**
	 * Writes any HTML that the form requires outside of the HTML form
	 */
	public void makePostFormHTML(PrintWriter out) throws IOException;
	
	public void makeExcelImportHTML(Writer out) throws IOException, SQLException;

	public void updateFromRequest(HttpServletRequest request, DAOFactory factory, boolean addIfNew) throws DataInputException, nz.cri.gns.dataaccess.StorageAccessException;

	public int save(int dataOriginId) throws SQLException, IOException, InsufficientPrivelegesException, StorageAccessException;

	public int submit(int dataOriginId) throws SQLException, IOException, InsufficientPrivelegesException, DataInputException, StorageAccessException;
	
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
