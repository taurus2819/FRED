package nz.cri.gns.fred.data;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import nz.cri.gns.db.DatabaseApp2;
import nz.cri.gns.db.DBUtils;
import nz.cri.gns.db.KeyValueObject;
import nz.cri.gns.fred.FREDUtils;
import nz.cri.gns.jsp.PageState;

/**
 * Initial attempts to import the performance of this composite query (the view folder_content).
 * As yet, not used in the application.
 *
 */
public class FolderContent extends Vector implements Comparator {
	
	public FolderContent(int id, PageState state) throws IOException, SQLException {
		//Folder_content_view mimic
		DatabaseApp2 app = FREDUtils.getFREDConnection(state);
		Integer iId = new Integer(id);
		/*
		 * FROM folder_view fd, folder_content fc, feature_view fv
		 * WHERE fd.folder_id = fc.folder_id AND fc.feature_id = fv.feature_id AND fv.feature_status = 'approved'
		 */
		String query = "SELECT fv.feature_id, fv.sample_name FROM feature_view fv JOIN folder_content fc ON fv.feature_id = fc.feature_id WHERE fc.folder_id = ? AND fv.feature_status = 'approved'";
		ResultSet rs = app.executeQuery(query, DBUtils.SINGLE_NUMBER_TYPE, new Object[] {iId});
		while (rs.next()) {
			KeyValueObject feature = new KeyValueObject(rs.getString(1), rs.getString(2));
			if (!contains(feature))
				add(feature);
		}
		rs.close();
		app.releaseStatement();
		/*
		 * FROM folder_view fd, feature_view fv
		 * WHERE fd.folder_id = fv.feature_working_folder_id
		 */
		query = "SELECT feature_id, sample_name FROM feature_view WHERE feature_working_folder_id = ?";
		rs = app.executeQuery(query, DBUtils.SINGLE_NUMBER_TYPE, new Object[] {iId});
		while (rs.next()) {
			KeyValueObject feature = new KeyValueObject(rs.getString(1), rs.getString(2));
			if (!contains(feature))
				add(feature);
		}
		rs.close();
		app.releaseStatement();
		/*
		 * FROM folder_view fd, feature_view fv, sample s, audit_table a
		 * WHERE fd.folder_id = a.working_folder_id AND a.audit_id = s.audit_id AND s.sample_id = fv.sample_ID AND fv.feature_status = 'approved'
		 */
		query = "SELECT fv.feature_id, fv.sample_name FROM feature_view fv JOIN sample s ON fv.sample_id = s.sample_id JOIN audit_table a ON s.audit_id = a.audit_id WHERE a.working_folder_id = ? AND fv.feature_status = 'approved'";
		rs = app.executeQuery(query, DBUtils.SINGLE_NUMBER_TYPE, new Object[] {iId});
		while (rs.next()) {
			KeyValueObject feature = new KeyValueObject(rs.getString(1), rs.getString(2));
			if (!contains(feature))
				add(feature);
		}
		rs.close();
		app.releaseStatement();
		/*
		 * FROM folder_view fd, feature_view fv, record r, audit_table a
		 * WHERE fd.folder_id = a.working_folder_id AND a.audit_id = r.audit_id AND r.sample_id = fv.sample_id AND fv.feature_status = 'approved'		 
		 */
		query = "SELECT fv.feature_id, fv.sample_name FROM feature_view fv JOIN record s ON fv.sample_id = s.sample_id JOIN audit_table a ON s.audit_id = a.audit_id WHERE a.working_folder_id = ? AND fv.feature_status = 'approved'";
		rs = app.executeQuery(query, DBUtils.SINGLE_NUMBER_TYPE, new Object[] {iId});
		while (rs.next()) {
			KeyValueObject feature = new KeyValueObject(rs.getString(1), rs.getString(2));
			if (!contains(feature))
				add(feature);
		}
		rs.close();
		app.releaseStatement();
		/*
		 * FROM folder_view fd, feature_view fv
		 * WHERE fd.folder_id = fv.masterfile_id AND fv.feature_status = 'waiting'
		 */
		query = "SELECT fv.feature_id, fv.sample_name FROM feature_view fv WHERE fv.masterfile_id = ? AND fv.feature_status = 'waiting'";
		rs = app.executeQuery(query, DBUtils.SINGLE_NUMBER_TYPE, new Object[] {iId});
		while (rs.next()) {
			KeyValueObject feature = new KeyValueObject(rs.getString(1), rs.getString(2));
			if (!contains(feature))
				add(feature);
		}
		rs.close();
		app.releaseStatement();
		Collections.sort(this, this);
	}

	public int compare(Object arg0, Object arg1) {
		return ((KeyValueObject)arg0).value.toUpperCase().compareTo(((KeyValueObject)arg1).value.toUpperCase());
	}

}
