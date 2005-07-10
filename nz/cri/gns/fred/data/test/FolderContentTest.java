package nz.cri.gns.fred.data.test;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import nz.cri.gns.db.DatabaseApp2;
import nz.cri.gns.db.DBUtils;
import nz.cri.gns.db.KeyValueObject;
import nz.cri.gns.fred.FREDUtils;
import nz.cri.gns.fred.data.FolderContent;
import nz.cri.gns.test.TestingPageState;
import junit.framework.TestCase;

/**
 *
 */
public class FolderContentTest extends TestCase implements Comparator {

	public void testFolderContent() throws NotBoundException, IOException, SQLException {
		TestingPageState state = new TestingPageState();
		
		DatabaseApp2 app = FREDUtils.getFREDConnection(state);
		
		//Get all the folder ids
		Vector folders = new Vector();
		ResultSet rs = app.executeQuery("SELECT folder_id from folder");
		while (rs.next()) {
			folders.add(rs.getString(1));
		}
		app.releaseStatement();
		
		long viewTime = 0;
		long classTime = 0;
		
		boolean viewFirst = true;
		for (Iterator it = folders.iterator(); it.hasNext(); ) {
			Vector viewFeatures = new Vector();
			Vector classFeatures = new Vector();
			int id = Integer.parseInt((String)it.next());
			System.out.println("Folder: " + id);
			if (viewFirst) {
				viewTime += getViewFeatures(id, viewFeatures, state);
				classTime += getClassFeatures(id, classFeatures, state);
			} else {
				classTime += getClassFeatures(id, classFeatures, state);
				viewTime += getViewFeatures(id, viewFeatures, state);
			}
			viewFirst = !viewFirst;
			
			//Reorder them so the key is being compared, not the value
			Collections.sort(viewFeatures, this);
			Collections.sort(classFeatures, this);
			try {
				assertEquals(viewFeatures.size(), classFeatures.size());
				for (int i=0; i<viewFeatures.size(); i++) {
					assertEquals(((KeyValueObject)viewFeatures.get(i)).key, ((KeyValueObject)classFeatures.get(i)).key);
				}
			} catch (Error e) {
				System.out.println("View: " + viewFeatures);
				System.out.println("Class: " + classFeatures);
				System.out.println("Class: " + classTime);
				System.out.println("View:  " + viewTime);
				throw e;
			}
		}
		System.out.println("Class: " + classTime);
		System.out.println("View:  " + viewTime);
	}

	/**
	 * @param viewFeatures
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	private long getClassFeatures(int id, Vector features, TestingPageState state) throws IOException, SQLException {
		Date start = new Date();
		FolderContent content = new FolderContent(id, state);
		long time = new Date().getTime() - start.getTime();
		features.addAll(content);
		return time;
	}

	/**
	 * @param viewFeatures
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	private long getViewFeatures(int id, Vector features, TestingPageState state) throws SQLException, IOException {
		Date start = new Date();
		String query = "SELECT DISTINCT feature_id, sample_name FROM folder_content_view WHERE folder_id = ? ORDER BY UPPER(sample_name)";
		DatabaseApp2 conn = FREDUtils.getFREDConnection(state);
		ResultSet rs = conn.executeQuery(query, DBUtils.SINGLE_NUMBER_TYPE, new Object[] {new Integer(id)});
		Vector feats = new Vector();
		while (rs.next()) {
			KeyValueObject feature = new KeyValueObject(rs.getString(1), rs.getString(2));
			if (!feats.contains(feature))
				feats.add(feature);
		}
		rs.close();
		conn.releaseStatement();
		long time = new Date().getTime() - start.getTime();
		features.addAll(feats);
		return time;
	}

	public int compare(Object arg0, Object arg1) {
		return new Integer(((KeyValueObject)arg0).key).compareTo(new Integer(((KeyValueObject)arg1).key));
	}
}
