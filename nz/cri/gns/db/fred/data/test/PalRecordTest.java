package nz.cri.gns.db.fred.data.test;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.sql.SQLException;
import java.util.Iterator;

import junit.framework.TestCase;
import nz.cri.gns.auth.User;
import nz.cri.gns.db.fred.data.AccessDeniedException;
import nz.cri.gns.db.fred.data.PaleontologyRecord;
import nz.cri.gns.db.fred.data.Taxa;
import nz.cri.gns.db.fred.data.TaxaGroup;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.JspUtils;
import nz.cri.gns.test.TestingPageState;

public class PalRecordTest extends TestCase {

	TestingPageState state;
	DBConnection conn;
	User user;

	public PalRecordTest(String arg0)
		throws NotBoundException, IOException, SQLException {
		super(arg0);
		PaleontologyRecord.purge();
		this.state = new TestingPageState();
			DBConnection ipConn =
				JspUtils.createDatabaseConnection(
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
		PaleontologyRecord.purge();
		PaleontologyRecord sv1 = PaleontologyRecord.getData(781, this.user, this.state);
		PaleontologyRecord sv2 = PaleontologyRecord.getData(781, this.user, this.state);
		assertEquals(sv1.toString(), sv2.toString());
		assertEquals(1, PaleontologyRecord.getPoolSize());
		PaleontologyRecord sv3 = PaleontologyRecord.getData(223, this.user, this.state);
		assertNotSame(sv1.toString(), sv3.toString());
		assertEquals(2, PaleontologyRecord.getPoolSize());
		PaleontologyRecord sv4 = PaleontologyRecord.getData(781, this.user, this.state);
		assertEquals(2, PaleontologyRecord.getPoolSize());
	}
	
	public void testPalList() throws NotBoundException, SQLException, IOException, AccessDeniedException {
		PaleontologyRecord sv1 = PaleontologyRecord.getData(781, this.user, this.state);
		assertNotNull(sv1.get(PaleontologyRecord.TAXONOMIC_LIST));
		System.out.println(sv1.getAsVector(PaleontologyRecord.TAXONOMIC_LIST).size());
		for (Iterator i = sv1.getAsVector(PaleontologyRecord.TAXONOMIC_LIST).iterator(); i.hasNext(); ) {
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