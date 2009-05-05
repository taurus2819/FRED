package nz.cri.gns.fred.util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;

import javax.naming.NamingException;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.db.BasicDatabaseApp2;
import nz.cri.gns.db.DatabaseApp2;
import nz.cri.gns.db.site.SiteRecord;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.FredDAO;
import nz.cri.gns.fred.model.DatumMethod;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.RegistrationArea;
import nz.cri.gns.fred.model.SiteView;
import nz.cri.gns.util.map.Datum;
import nz.cri.gns.util.map.DatumFactory;
import nz.cri.gns.util.map.NZMG;
import nz.cri.gns.util.map.NZMS260;
import nz.cri.gns.util.map.NorthingEasting;
import nz.cri.gns.util.map.TruncNorthingEasting;
import nz.cri.gns.util.map.Datum.Coordinate;

import org.xml.sax.SAXException;

public class SiteUtil extends ModelUtil {

	private FredDAO fredDAO;

	public static final int REG_MAINLAND_NZ = 400;
	public static final int REG_CHATHAM_ISLANDS = 401;
	public static final int REG_ROSS_SEA = 402;
	public static final int REG_NEW_CALEDONIA = 403;
	public static final int REG_TOKELAU = 404;
	public static final int REG_FIJI = 405;
	public static final int REG_SAMOA = 406;
	public static final int REG_NIUE = 407;
	public static final int REG_COOK_ISLANDS = 408;
	public static final int REG_NORFOLK_ISLAND = 409;
	public static final int REG_TONGA = 410;
	public static final int REG_LORD_HOWE_ISLAND = 411;
	public static final int REG_KERMADEC_ISLANDS = 412;
	public static final int REG_BOUNTY_ISLANDS = 413;
	public static final int REG_THE_SNARES = 414;
	public static final int REG_CAMPBELL_ISLAND = 415;
	public static final int REG_AUCKLAND_ISLANDS = 416;
	public static final int REG_ANTIPODES_ISLANDS = 417;
	public static final int REG_MACQUARIE_ISLAND = 418;
	public static final int REG_OTHER = 419;
	public static final int REG_VANUATU = 420;
	public static final int REG_PAPUA_NEW_GUINEA = 421;
	
	public static final int MASTERFILE_NTH_NI = 1;
	public static final int MASTERFILE_CEN_NI = 2;
	public static final int MASTERFILE_STH_NI = 3;
	public static final int MASTERFILE_NELSON = 4;
	public static final int MASTERFILE_CEN_SI = 5;
	public static final int MASTERFILE_STH_SI = 6;
	public static final int MASTERFILE_NZ_ISLANDS = 7;
	public static final int MASTERFILE_ANTARCTICA = 8;
	public static final int MASTERFILE_PACIFIC_ISLANDS = 9;
	public static final int MASTERFILE_NEW_CALEDONIA = 10;
	public static final int MASTERFILE_OFFSHORE = 11;
	
	//This is a special backlog masterfile folder
	public static final int MASTERFILE_NTH_NI_BACKLOG = 14;
	public static final int MASTERFILE_CEN_NI_BACKLOG = 17;
	public static final int MASTERFILE_STH_NI_BACKLOG = 19;
	public static final int MASTERFILE_NELSON_BACKLOG = 12;
	public static final int MASTERFILE_CEN_SI_BACKLOG = 20;
	public static final int MASTERFILE_STH_SI_BACKLOG = 22;
	public static final int MASTERFILE_NZ_ISLANDS_BACKLOG = 23;
	public static final int MASTERFILE_ANTARCTICA_BACKLOG = 24;
	public static final int MASTERFILE_PACIFIC_ISLANDS_BACKLOG = 25;
	public static final int MASTERFILE_NEW_CALEDONIA_BACKLOG = 26;
	public static final int MASTERFILE_OFFSHORE_BACKLOG = 27;
	
	public SiteUtil(DAOFactory factory) {
		super(factory);
		this.fredDAO = factory.getFredDAO();
	}
	
	public SiteView getSiteView(int siteId) throws StorageAccessException {
		return fredDAO.get(siteId, nz.cri.gns.fred.hibernate.SiteView.class);
	}
	
	public static int getMasterfile(Feature feature) throws SQLException, NamingException {
		boolean isBacklog = FeatureUtil.isBacklogFeature(feature);
		switch (feature.getRegistrationArea().getRegAreaId().intValue()) {
			case REG_MAINLAND_NZ :
				NorthingEasting nzmgCoord = (NorthingEasting)getSiteCoordinate(new NZMG(), feature.getSiteView());
				double easting = nzmgCoord.getEastWest();
				double northing = nzmgCoord.getNorthSouth();
				if (easting <= 2810000 && northing >= 6250000)
					return (isBacklog) ? MASTERFILE_NTH_NI_BACKLOG : MASTERFILE_NTH_NI;
				if (northing >= 6160000 || (easting >= 2730000 && northing >= 6070000))
					return (isBacklog) ? MASTERFILE_CEN_NI_BACKLOG : MASTERFILE_CEN_NI;
				if (easting >= 2650000 || northing >= 6130000)
					return 	(isBacklog) ? MASTERFILE_STH_NI_BACKLOG : MASTERFILE_STH_NI;
				if (northing >= 5920000)
					return (isBacklog) ? MASTERFILE_NELSON_BACKLOG : MASTERFILE_NELSON;
				if (easting >= 2210000 && northing >= 5620000)
					return (isBacklog) ? MASTERFILE_CEN_SI_BACKLOG : MASTERFILE_CEN_SI;
				if ((northing >= 5290000))
					return (isBacklog) ? MASTERFILE_STH_SI_BACKLOG : MASTERFILE_STH_SI;
				return (isBacklog) ? MASTERFILE_OFFSHORE_BACKLOG : MASTERFILE_OFFSHORE;
			case REG_CHATHAM_ISLANDS :
			case REG_CAMPBELL_ISLAND :
			case REG_AUCKLAND_ISLANDS :
			case REG_ANTIPODES_ISLANDS :
			case REG_THE_SNARES :
				return (isBacklog) ? MASTERFILE_NZ_ISLANDS_BACKLOG : MASTERFILE_NZ_ISLANDS;
			case REG_ROSS_SEA :
				return (isBacklog) ? MASTERFILE_ANTARCTICA_BACKLOG : MASTERFILE_ANTARCTICA;
			case REG_TOKELAU :
			case REG_FIJI :
			case REG_SAMOA :
			case REG_NIUE :
			case REG_COOK_ISLANDS :
			case REG_NORFOLK_ISLAND :
			case REG_TONGA :
			case REG_LORD_HOWE_ISLAND :
			case REG_KERMADEC_ISLANDS :
			case REG_BOUNTY_ISLANDS :
			case REG_MACQUARIE_ISLAND :
			case REG_VANUATU :
			case REG_PAPUA_NEW_GUINEA :
				return (isBacklog) ? MASTERFILE_PACIFIC_ISLANDS_BACKLOG : MASTERFILE_PACIFIC_ISLANDS;
			case REG_NEW_CALEDONIA :
				return (isBacklog) ? MASTERFILE_NEW_CALEDONIA_BACKLOG : MASTERFILE_NEW_CALEDONIA;
			case REG_OTHER :
				return (isBacklog) ? MASTERFILE_OFFSHORE_BACKLOG : MASTERFILE_OFFSHORE;
		}
		return (isBacklog) ? MASTERFILE_OFFSHORE_BACKLOG : MASTERFILE_OFFSHORE;
	}
	
	private static Datum.Coordinate getSiteCoordinate(Datum datum, SiteView siteView) throws SQLException, NamingException {
		Datum.LatLong ll = getSiteLatLong(siteView);
		if (ll == null)
			return null;
		return datum.convertFromNZGD49(ll);
	}
	
	public static Datum.LatLong getSiteLatLong(SiteView siteView) throws SQLException, NamingException {
		return new Datum.LatLong(siteView.getLatitude(), siteView.getLongitude());
	}
	
	public static String getFrNumberMapSheet(Feature feature) throws SQLException, NamingException {
		RegistrationArea area = feature.getRegistrationArea();

		Datum.LatLong ll = getSiteLatLong(feature.getSiteView());
		
		//Try and make this into a NZMS260 coord
		TruncNorthingEasting tne = null;
		try {
			tne = (TruncNorthingEasting)new NZMS260().convertFromNZGD49(ll);
		} catch (Exception e) {
		}
		
		if (tne != null && NZMS260.isValidMapSheet(tne.getMapSheet())) {
			return tne.getMapSheet();
		} else if (!(area.getCode().equals("NZ") || area.getCode().equals("OT"))) {
			return area.getCode();
		} else {
			DecimalFormat format = new DecimalFormat("00");
			String mapSheet = ((ll.getNorthSouth() > 0) ? "N" : "S") + ((ll.getEastWest() > 0) ? "E" : "W") 
				+ format.format(Math.floor(Math.abs(ll.getNorthSouth())));
			format.applyPattern("000");
			return mapSheet + format.format(Math.floor(Math.abs(ll.getEastWest())));
		}
	}
	
	public static SiteRecord getSite(Feature feature) throws NamingException, SQLException {
		Connection conn = null;
		DatabaseApp2 app = null;
		SiteRecord sr = null;
		try {
			conn = FREDUtil.getConnection();
			app = new BasicDatabaseApp2(conn, "");
			sr = SiteRecord.querySite(app, feature.getSiteId().intValue());
		} finally {
			if (app != null) {
				app.close();
			} else if (conn != null) try {
				conn.close();
			} catch (SQLException e) {
			}
		}
		return sr;
	}
	
	/**
	 * Gets an appropriate record from the DB for the given site, 
	 * inserting if necessary
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws FactoryConfigurationError 
	 * @throws ParserConfigurationException 
	 * @throws NamingException 
	 * @throws SQLException 
	 */
	public static SiteRecord getSite(SiteRecord site) throws ParserConfigurationException, FactoryConfigurationError, SAXException, IOException, SQLException, NamingException {
		return site.insert(FREDUtil.getInstance());
	}
	
	public DatumMethod getSiteDatumMethod(int methodId) throws StorageAccessException {
		return fredDAO.get(methodId, nz.cri.gns.fred.hibernate.DatumMethod.class);
	}
	
	public List<DatumMethod> getSiteDatumMethods() throws StorageAccessException {
		return fredDAO.getList("FROM DatumMethod AS d WHERE d.nomAccuracyXY IS NOT NULL", DatumMethod.class);
	}
	
	public RegistrationArea getRegistrationArea(int id) throws StorageAccessException {
		return fredDAO.get(id, nz.cri.gns.fred.hibernate.RegistrationArea.class);
	}
	
	public static Datum getFREDDatum(Feature feature) {
		if (feature.getOrigSystemId() == null)
			return null;
		Datum datum = DatumFactory.createDatum(feature.getOrigSystemId().intValue());
		return datum;
	}
	
	public static Coordinate getFREDCoordinate(Feature feature) {
		if (feature.getOrigCoord() == null || feature.getOrigSystemId() == null)
			return null;
		Datum datum = getFREDDatum(feature);
		Coordinate coord = datum.parseCoordinate(feature.getOrigCoord());
		return coord;
	}
	
}
