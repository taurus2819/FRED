package nz.cri.gns.fred.dataentry;

import java.io.IOException;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;

import nz.cri.gns.auth.InvalidCredentialsException;
import nz.cri.gns.auth.User;
import nz.cri.gns.fred.FREDUtils;
import nz.cri.gns.fred.data.Folder;
import nz.cri.gns.fred.data.PaleontologyRecord;
import nz.cri.gns.fred.data.Record;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.PageState;

public class PaleontologyRecordDE extends RecordDE {

	public PaleontologyRecordDE(User user, int sampleID, int folderID, PageState state)
		throws SQLException, IOException, DataInputException {
		super(user, sampleID, folderID, "PAL", state);
	}

	public PaleontologyRecordDE(User user, int folderID, PageState state)
		throws DataInputException, SQLException, IOException {
		super(user, folderID, "PAL", state);
	}

	public PaleontologyRecordDE(int recID, User user, PageState state)
		throws IllegalArgumentException, DataInputException, SQLException, IOException, InvalidCredentialsException {
		super(recID, user, state);
		record = (PaleontologyRecord) PaleontologyRecord.getData(recID, user, state);
		recordType = "PAL";
		folder =
			new Folder(
				record.getAsInt(Record.WORKING_FOLDER_ID),
				user,
				state);
	}

	public void makeDataEntryHTML(Writer out)
		throws IOException, SQLException {
		super.makeDataEntryHTML(out);
		super.makeEndBitHTML(out);
	}

	public int save()
		throws InvalidCredentialsException, SQLException, IOException {
		if (!savedFlag) {
			DBConnection conn = FREDUtils.getFREDConnection(state);
			ResultSet rs;
			conn.getConnection().setAutoCommit(false);
			try {
				super.save();
				conn.getConnection().commit();
				conn.getConnection().setAutoCommit(true);
				conn.releaseStatement();
				savedFlag = true;
				try {
					record =
						(PaleontologyRecord) PaleontologyRecord.getData(
							record.getRecordID(),
							user,
							state,
							true);
				} catch (Exception e) {
				}
			} catch (SQLException e) {
				conn.getConnection().rollback();
				conn.getConnection().setAutoCommit(true);
				conn.releaseStatement();
				savedFlag = false;
				throw new SQLException();
			} catch (IOException e) {
				conn.getConnection().rollback();
				conn.getConnection().setAutoCommit(true);
				conn.releaseStatement();
				savedFlag = false;
				throw new IOException();
			} catch (InvalidCredentialsException e) {
				conn.getConnection().rollback();
				conn.getConnection().setAutoCommit(true);
				conn.releaseStatement();
				savedFlag = false;
				throw new InvalidCredentialsException();
			}

		}
		return record.getRecordID();
	}

	protected void checkMandatoryFields() throws DataInputException {
	}

}
