package nz.cri.gns.db.fred.test;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.sql.SQLException;
import java.util.Iterator;

import junit.framework.TestCase;
import nz.cri.gns.auth.User;
import nz.cri.gns.db.fred.AccessDeniedException;
import nz.cri.gns.db.fred.FullPaleontologyRecord;
import nz.cri.gns.db.fred.Taxa;
import nz.cri.gns.db.fred.TaxaGroup;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.ExternalUtils;
import nz.cri.gns.test.TestingPageState;

public class FullPalRecordTest extends TestCase {

	TestingPageState state;
	DBConnection conn;
	User user;

	public FullPalRecordTest(String arg0)
		throws NotBoundException, IOException, SQLException {
		super(arg0);
		FullPaleontologyRecord.purge();
		this.state = new TestingPageState();
			DBConnection ipConn =
				ExternalUtils.createDatabaseConnection(
					state.getSession(),
					"nz.cri.gns.db.fred.test.ipConn",
					"ip",
					state.getContext());
		try {
			this.user = new User("pseudo_ben", "santor32", ipConn);
		} catch (Exception e) {
		}
	}


public void testPooling() throws NotBoundException, SQLException, IOException, AccessDeniedException {
	FullPaleontologyRecord.purge();
	FullPaleontologyRecord sv1 = FullPaleontologyRecord.getFullPaleontologyRecord(781, this.user, this.state);
	FullPaleontologyRecord sv2 = FullPaleontologyRecord.getFullPaleontologyRecord(781, this.user, this.state);
	assertEquals(sv1.toString(), sv2.toString());
	assertEquals(1, FullPaleontologyRecord.getPoolSize());
	FullPaleontologyRecord sv3 = FullPaleontologyRecord.getFullPaleontologyRecord(223, this.user, this.state);
	assertNotSame(sv1.toString(), sv3.toString());
	assertEquals(2, FullPaleontologyRecord.getPoolSize());
	FullPaleontologyRecord sv4 = FullPaleontologyRecord.getFullPaleontologyRecord(781, this.user, this.state);
	assertEquals(2, FullPaleontologyRecord.getPoolSize());
}

public void testPalList() throws NotBoundException, SQLException, IOException, AccessDeniedException {
	FullPaleontologyRecord sv1 = FullPaleontologyRecord.getFullPaleontologyRecord(781, this.user, this.state);
	assertNotNull(sv1.get(FullPaleontologyRecord.TAXONOMIC_LIST));
	System.out.println(sv1.getAsVector(FullPaleontologyRecord.TAXONOMIC_LIST).size());
	for (Iterator i = sv1.getAsVector(FullPaleontologyRecord.TAXONOMIC_LIST).iterator(); i.hasNext(); ) {
		TaxaGroup tg = (TaxaGroup)i.next();
		System.out.println(tg.getGroupName());
		if (tg.getTaxaList() != null) {
			for (Iterator i2 = tg.getTaxaList().iterator(); i2.hasNext(); ) {
				Taxa taxa = (Taxa)i2.next();
				System.out.println(tg.getGroupName() + ": " + taxa.getTaxonomicName());
			}
		}
	}
}

}