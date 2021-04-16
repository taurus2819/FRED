package nz.cri.gns.fred.util;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.NamingException;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import nz.cri.gns.auth.domain.User;

import nz.cri.gns.dataaccess.StorageAccessException;
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

    private static final Logger log = Logger.getLogger("nz.cri.gns.fred.util.SiteUtil");

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
            case REG_MAINLAND_NZ:
                NorthingEasting nzmgCoord = (NorthingEasting) getSiteCoordinate(new NZMG(), feature.getSiteView());
                double easting = nzmgCoord.getEastWest();
                double northing = nzmgCoord.getNorthSouth();
                if (easting <= 2810000 && northing >= 6250000) {
                    return (isBacklog) ? MASTERFILE_NTH_NI_BACKLOG : MASTERFILE_NTH_NI;
                }
                if (northing >= 6160000 || (easting >= 2730000 && northing >= 6070000)) {
                    return (isBacklog) ? MASTERFILE_CEN_NI_BACKLOG : MASTERFILE_CEN_NI;
                }
                if (easting >= 2650000 || northing >= 6130000) {
                    return (isBacklog) ? MASTERFILE_STH_NI_BACKLOG : MASTERFILE_STH_NI;
                }
                if (northing >= 5920000) {
                    return (isBacklog) ? MASTERFILE_NELSON_BACKLOG : MASTERFILE_NELSON;
                }
                if (easting >= 2210000 && northing >= 5620000) {
                    return (isBacklog) ? MASTERFILE_CEN_SI_BACKLOG : MASTERFILE_CEN_SI;
                }
                if ((northing >= 5290000)) {
                    return (isBacklog) ? MASTERFILE_STH_SI_BACKLOG : MASTERFILE_STH_SI;
                }
                return (isBacklog) ? MASTERFILE_OFFSHORE_BACKLOG : MASTERFILE_OFFSHORE;
            case REG_CHATHAM_ISLANDS:
            case REG_CAMPBELL_ISLAND:
            case REG_AUCKLAND_ISLANDS:
            case REG_ANTIPODES_ISLANDS:
            case REG_THE_SNARES:
                return (isBacklog) ? MASTERFILE_NZ_ISLANDS_BACKLOG : MASTERFILE_NZ_ISLANDS;
            case REG_ROSS_SEA:
                return (isBacklog) ? MASTERFILE_ANTARCTICA_BACKLOG : MASTERFILE_ANTARCTICA;
            case REG_TOKELAU:
            case REG_FIJI:
            case REG_SAMOA:
            case REG_NIUE:
            case REG_COOK_ISLANDS:
            case REG_NORFOLK_ISLAND:
            case REG_TONGA:
            case REG_LORD_HOWE_ISLAND:
            case REG_KERMADEC_ISLANDS:
            case REG_BOUNTY_ISLANDS:
            case REG_MACQUARIE_ISLAND:
            case REG_VANUATU:
            case REG_PAPUA_NEW_GUINEA:
                return (isBacklog) ? MASTERFILE_PACIFIC_ISLANDS_BACKLOG : MASTERFILE_PACIFIC_ISLANDS;
            case REG_NEW_CALEDONIA:
                return (isBacklog) ? MASTERFILE_NEW_CALEDONIA_BACKLOG : MASTERFILE_NEW_CALEDONIA;
            case REG_OTHER:
                return (isBacklog) ? MASTERFILE_OFFSHORE_BACKLOG : MASTERFILE_OFFSHORE;
        }
        return (isBacklog) ? MASTERFILE_OFFSHORE_BACKLOG : MASTERFILE_OFFSHORE;
    }

    private static Datum.Coordinate getSiteCoordinate(Datum datum, SiteView siteView) throws SQLException, NamingException {
        Datum.LatLong ll = getSiteLatLong(siteView);
        if (ll == null) {
            return null;
        }
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
            tne = (TruncNorthingEasting) new NZMS260().convertFromNZGD49(ll);
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

    public static SiteRecord getSite(Feature feature) {
        SiteRecord sr = null;
        try {
            sr = nz.cri.gns.db.util.SiteUtil.querySite(feature.getSiteId());
        } catch (Exception ex) {
        }
        return sr;
    }

    public static SiteRecord getSite(Datum datum, Coordinate coord) {
        SiteRecord sr = null;
        int siteId = nz.cri.gns.db.util.SiteUtil.checkSiteExists(DatumFactory.getNzgd49(), datum.convertToNZGD49(coord), null);
        if (siteId != -1) {
            sr = nz.cri.gns.db.util.SiteUtil.querySite(siteId);
        }
        return sr;
    }

    public static SiteRecord getSite(String wellName) {
        return nz.cri.gns.db.util.SiteUtil.querySiteByWellName(wellName);
    }

    /**
     * Gets an appropriate record from the DB for the given site, inserting if
     * necessary
     *
     * @throws IOException
     * @throws SAXException
     * @throws FactoryConfigurationError
     * @throws ParserConfigurationException
     * @throws NamingException
     * @throws SQLException
     */
    public static SiteRecord getSite(SiteRecord site) throws ParserConfigurationException, FactoryConfigurationError, SAXException, IOException, SQLException, NamingException {
        return nz.cri.gns.db.util.SiteUtil.insertSite(site);
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
        if (feature.getOrigSystemId() == null) {
            return null;
        }
        Datum datum = DatumFactory.createDatum(feature.getOrigSystemId().intValue());
        return datum;
    }

    public static Coordinate getFREDCoordinate(Feature feature) {
        if (feature.getOrigCoord() == null || feature.getOrigSystemId() == null) {
            return null;
        }
        Datum datum = getFREDDatum(feature);
        Coordinate coord = datum.parseCoordinate(feature.getOrigCoord());
        return coord;
    }
    
    /**
     * Returns true if the string is not null, empty, or only has whitespace.
     * @param s the string
     * @return whether the String has data
     */
    public static boolean hasData(String s){
        return (null != s && !s.isEmpty() && !s.trim().isEmpty());
    }

    /**
     * Populate my fields that are relevant to the Site schema. After populating
     * them, you can invoke >>save(SiteRecord site) to submit the site to the
     * Site service.
     *
     * The SiteRecord is updated with these new values if they differ.
     *
     * @param error - an array. If it's not empty on return, something bad
     * happened.
     *
     * @returns A populated SiteRecord, either brand new or an existing one.
     */
    public static SiteRecord findOrMakeSiteInstance(
            List<String[]> error,
            String featureName,
            Integer origSystemId,
            String origCoords, // TODO: unused?
            String datumStr, // Please leave null. Use origSystemId.
            String east,
            String north,
            String locality,
            String country,
            Integer locationMethodId,
            Float accuracy,
            String mapSheet,
            User user
    ) {
        if (null == user) {
            throw new NullPointerException();
        }

        SiteRecord site; // return me.

        try {

            /* This is based on refactored existing code. Some behaviour has changed. -mikevdg */
            // try to re-use any existing site details.
            // take 1- try the well name
            site = SiteUtil.getSite(featureName);

            Datum datum;
            if (null == datumStr) {
                datum = DatumFactory.createDatum(origSystemId);
            } else {
                // This is a source of bugs. Don't use this.
                datum = DatumFactory.createDatum(datumStr);
            }

            if (null == site && !(hasData(east) || hasData(north))) {
                error.add(new String[]{"Coordinate", "Coordinate is required"});
                return null;
            }

            Datum.Coordinate coord = null;
            if (hasData(east) && hasData(north)) {
                try {
                    if (datum.isMapSheetSystem()) {
                        int precision = east.length();
                        if (north.length() != precision) {
                            error.add(new String[]{"Coordinate", "Truncated coordinates different lengths"});
                        } else if ((precision > 0 && precision < 3) || precision > 4) {
                            error.add(new String[]{"Coordinate", "Length of truncated coordinates must be 3 or 4"});
                        } else {
                            // WTF!? This is not a problem that needs reflection as a solution.
                            coord = (Datum.Coordinate) datum.preferredCoordinate().getConstructor(
                                    new Class[]{double.class, double.class, String.class, int.class}).newInstance(
                                            new Object[]{
                                                new Double(north),
                                                new Double(east),
                                                mapSheet,
                                                precision});
                        }
                    } else {
                        coord = (Datum.Coordinate) datum.preferredCoordinate().getConstructor(
                                new Class[]{double.class, double.class}).newInstance(
                                        new Object[]{
                                            new Double(north),
                                            new Double(east)});
                    }
                } catch (NumberFormatException e) {
                    error.add(new String[]{"Coordinate", "Non numeric coordinate entered"});
                } catch (IllegalArgumentException | InstantiationException | SecurityException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                    error.add(new String[]{"Coordinate", e.getMessage()});
                }
            }

            if (null == coord) {
                return site;
            }

            if (!datum.coordinateAcceptable(coord)) {
                error.add(new String[]{"Coordinate", "Coordinates not of correct type"});
            }

            if (null == site) {
                site = SiteUtil.getSite(datum, coord);
            }

            if (null == site) {
                // Make a new one.
                site = new SiteRecord();
            }

            if (null == origSystemId || null == origCoords) {
                try {
                    // TODO: This set the original coordinates from the datum, but we also have origCoords?
                    site.setOriginal(datum.getDatabaseId(), datum.getStringFor(coord));
                } catch (Exception e) {
                    log.log(Level.INFO, null, e);
                    error.add(new String[]{"Coordinate", "Datum is invalid. " + e.getMessage()});
                }
            } else {
                site.setOriginal(origSystemId, origCoords);
            }

            try {
                site.setLatLong(datum.convertToNZGD49(coord));
            } catch (Exception e) {
                log.log(Level.INFO, null, e);
                error.add(new String[]{"Coordinate", "Invalid coordinates specified. Ensure you enter the correct number of digits for the selected coordinate system"});
            }

            site.setDirections(locality);
            site.setCountry(country);
            site.setOwner(user.getId().intValue());

            if (null != locationMethodId) {
                site.setMethod(locationMethodId);
            }
            if (null != accuracy) {
                site.setAccuracy(accuracy);
            }
        } catch (Exception x) {
            error.add(new String[]{"Coordinate", x.getMessage()});
            site = null;
        }
        return site;
    }

    /**
     * Return the saved version of the SiteRecord. The original is not modified,
     * so only use the returned value.
     *
     * A quick note about the site service. It lives at
     * http://online.gns.cri.nz/online/json/site.jsp; it in turn invokes a
     * proprietary REST service by Arc to create this site. This service does
     * not return useful error messages, so your milage will vary.
     */
    public static SiteRecord save(SiteRecord site) throws nz.cri.gns.db.util.SiteUtil.SiteException {
        return nz.cri.gns.db.util.SiteUtil.findOrCreateSite(site);
    }

}
