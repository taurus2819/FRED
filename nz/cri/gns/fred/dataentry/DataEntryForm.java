
package nz.cri.gns.fred.dataentry;

import java.io.IOException;
import java.io.Writer;
import java.sql.SQLException;

import nz.cri.gns.auth.InvalidCredentialsException;


public interface DataEntryForm {

	public int getFieldCount();

	public void setField(int field, String value) throws DataInputException;

	public String getField(int field);

	public void makeNavPanelHTML(Writer out) throws IOException;
	
	public void makeDataEntryHTML(Writer out) throws IOException, SQLException;
	
	public int save() throws SQLException, IOException, InvalidCredentialsException;
	
	public int submit() throws SQLException, IOException, InvalidCredentialsException, DataInputException;
	
	public void delete() throws IOException, SQLException, InvalidCredentialsException;

}
