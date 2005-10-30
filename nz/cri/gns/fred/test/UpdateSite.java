
package nz.cri.gns.fred.test;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;
import nz.cri.gns.db.DBUtils;
import nz.cri.gns.db.QueryDescriptor;
import nz.cri.gns.db.site.SiteRecord;
import nz.cri.gns.fred.FREDUtils;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.intranet.SDEConnection;
import nz.cri.gns.jsp.JspUtils;
import nz.cri.gns.test.TestingPageState;
import nz.cri.gns.util.map.Datum;
import nz.cri.gns.util.map.DatumFactory;
import nz.cri.gns.util.map.TruncNorthingEasting;

import org.xml.sax.SAXException;

public class UpdateSite extends TestCase {

	public void testNZMS1NthIsl() throws NotBoundException, ParserConfigurationException, FactoryConfigurationError, SAXException, IOException {
		TestingPageState state = new TestingPageState();
		DBConnection scConn = JspUtils.createDatabaseConnection(state.getSession(), "sc", "sc", state.getContext());
		Datum datum = DatumFactory.createDatum("NZMS1 NthIsl");
		TruncNorthingEasting tne = new TruncNorthingEasting(448, 573, "N94", 3);
		SiteRecord sr = SiteRecord.insertSite(null, datum, tne, null, null, null, null, null, "NZ", "1619", 0, JspUtils.getInstance(state.getContext()));
		System.out.println("SiteID = " + sr.getId());
	}
	
	public void test2() throws NotBoundException, ParserConfigurationException, SAXException, IOException, SQLException {
		TestingPageState state = new TestingPageState();
		DBConnection conn = JspUtils.createDatabaseConnection(state.getSession(), "sc", "sc", state.getContext());
		String xml = "<site><insert><site-name><![CDATA[]]></site-name><coordinate><grid-ref><coord-sys>NZ Yard NthIsl</coord-sys><map-series>NZMS1 NthIsl</map-series><map-sheet>N94</map-sheet><northing>448</northing><easting>573</easting></grid-ref></coordinate><country-code>NZ</country-code><owner-id>1619</owner-id></insert></site>";
		SiteRecord sr = SiteRecord.createFromXML(xml, conn, SDEConnection.createSDEConnection(state, "sc"));
		System.out.println("SiteID = " + sr.getId());
	}
	
	public void _testUpdateSite() throws NotBoundException, SQLException, ParserConfigurationException, FactoryConfigurationError, SAXException, IOException {
		TestingPageState state = new TestingPageState();
		state.setInstance("gns");
		Datum datum;
		TruncNorthingEasting tne;
		Datum.LatLong ll;
		DBConnection scConn = JspUtils.createDatabaseConnection(state.getSession(), "sc", "sc", state.getContext());
		DBConnection frConn = FREDUtils.getFREDConnection(state);
		ResultSet rs = frConn.executeQuery("SELECT feature_id, sheet, easting, northing, comments FROM temp_yard_refs WHERE feature_id <> ?", new int[] {Types.NUMERIC}, new Object[] {new Integer(0)});
		PreparedStatement preStatement = frConn.preservePreparedStatement();
		while (rs.next()) {
			//try {
				
				String comments = rs.getString(5);
				if (rs.getString(2).charAt(0) == 'S') {
					datum = DatumFactory.createDatum("NZMS1 SthIsl");
				} else {
					datum = DatumFactory.createDatum("NZMS1 NthIsl");
				}
				tne = new TruncNorthingEasting(rs.getDouble(4), rs.getDouble(3), rs.getString(2), 3);
				if (datum.coordinateAcceptable(tne)) {
					ResultSet rs2 = frConn.executeQuery("SELECT site_id, audit_id FROM feature WHERE feature_id = ?", new int[] {Types.NUMERIC}, new Object[] {new Integer(rs.getInt(1))});
					rs2.next();
					
					Integer auditID = new Integer(rs2.getInt(2));
					if (rs2.getString(1) != null) {
						Integer siteID = new Integer(rs2.getInt(1));
						System.out.println("Moving FeatureID: " + rs.getString(1));
						ll = datum.convertToNZGD49(tne);
						QueryDescriptor qd = new QueryDescriptor("site");
						qd.addQueryColumn("latitude", Types.NUMERIC, new Double(ll.getNorthSouth()));
						qd.addQueryColumn("longitude", Types.NUMERIC, new Double(ll.getEastWest()));
						qd.addQueryColumn("orig_system_id", Types.NUMERIC, new Integer(datum.getDatabaseId()));
						qd.addQueryColumn("orig_coord", Types.VARCHAR, datum.getStringFor(tne));
						qd.addQueryColumn("shape", Types.NUMERIC, null);
						qd.addQueryColumn("flag", Types.NUMERIC, null);
						qd.addQueryColumn(QueryDescriptor.NOT_FOR_UPDATE, Types.NUMERIC, siteID);
						DBUtils.doUpdate(qd, "site_id = ?", scConn);
						frConn.executeUpdate("DELETE FROM temp_yard_refs WHERE feature_id = ?", new int[] {Types.NUMERIC}, new Object[] {new Integer(rs.getInt(1))});
					} else {
						System.out.println("Creating coordinate for FeatureID: " + rs.getString(1));
					/*	SiteRecord sr = SiteRecord.insertSite(null, datum, tne, null, null, null, null, null, "NZ", "1619", 0, JspUtils.getInstance(state.getContext()));
						QueryDescriptor qd = new QueryDescriptor("feature");
						qd.addQueryColumn("site_id", Types.NUMERIC, new Integer(sr.getId()));
						qd.addQueryColumn(QueryDescriptor.NOT_FOR_UPDATE, Types.NUMERIC, new Integer(rs.getInt(1)));
						DBUtils.doUpdate(qd, "feature_id = ?", frConn);
					*/}
					if (comments != null) {
						rs2 = frConn.executeQuery("SELECT curator_comments FROM audit_table WHERE audit_id = ?", new int[] {Types.NUMERIC}, new Object[] {auditID});
						rs2.next();
						String auditComm = rs2.getString(1);
						if (auditComm == null) {
							frConn.executeUpdate("UPDATE audit_table SET curator_comments = ? WHERE audit_id = ?", new int[] {Types.VARCHAR, Types.NUMERIC}, new Object[] {comments, auditID});
						} else if (auditComm.indexOf(comments) == -1) {
							frConn.executeUpdate("UPDATE audit_table SET curator_comments = ? WHERE audit_id = ?", new int[] {Types.VARCHAR, Types.NUMERIC}, new Object[] {rs2.getString(1) + "; " + comments, auditID});
						}
					}
					
				} else {
					System.out.println("Bad coordinate for FeatureID: " + rs.getString(1));
				}
			//} catch (Exception e) {
			//	System.out.println("Error: " + e.getMessage());
			//}
		}
		System.out.println("Finished");
	}

}
