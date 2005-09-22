package nz.cri.gns.fred.util;

import java.util.Collections;
import java.util.List;

import nz.cri.gns.auth.UserAccount;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.StorageAccessException;
import nz.cri.gns.fred.dao.TaxonomicDAO;
import nz.cri.gns.fred.dao.TaxonomicGroupDAO;
import nz.cri.gns.fred.dataentry.DataInputException;
import nz.cri.gns.fred.model.PaleontologyListEntry;
import nz.cri.gns.fred.model.Taxon;
import nz.cri.gns.fred.model.TaxonomicGroup;

/**
 * @author iainm
 */
public class TaxonomicUtil extends ModelUtil {

	private TaxonomicGroupDAO groupDAO;
    private TaxonomicDAO taxonomicDAO;
	
	public TaxonomicUtil(DAOFactory dao) {
        super(dao);
		this.groupDAO = dao.getTaxonomicGroupDAO();
        this.taxonomicDAO = dao.getTaxonomicDAO();
	}
	
	public List<TaxonomicGroup> getPanelsIsMemberOf(UserAccount user) throws StorageAccessException {
		List<TaxonomicGroup> panels = groupDAO.getPanelsIsMemberOf(Integer.parseInt(user.getId()));
		Collections.sort(panels);
		return panels;
	}
	
	public int getProvisionalCount(TaxonomicGroup group) throws StorageAccessException {
		return groupDAO.getProvisionalCount(group);
	}

	public TaxonomicGroup getTaxonomicGroup(String groupName) throws StorageAccessException {
		return groupDAO.findTaxonomicGroup(groupName);
	}

    public static String cleanAlphaChar (String taxaName, String checkString) {
        int len = taxaName.length();
        int pos = 0;
        boolean ok = true;
        while (ok) {
            pos = taxaName.indexOf(checkString, pos + 1);
            if (pos > 0 && pos + checkString.length() < len) {
                pos = pos + checkString.length();
                if (pos + 1 == len || pos + 2 == len) {
                    taxaName = taxaName.substring(0, pos);
                } else if (taxaName.indexOf(" ", pos + 1) <= pos + 2 && taxaName.indexOf(" ", pos + 1) > 0) {
                    taxaName = taxaName.substring(0, pos) + "  " + taxaName.substring(pos + 2, taxaName.length());
                }
            } else {
                ok = false;
            }
        }
        return taxaName;
    }

    public static String cleanTaxaNameOpen (String taxaName, String checkString) {
    	taxaName = cleanAlphaChar(taxaName, checkString);
    	taxaName = cleanTaxaName(taxaName, "n." + checkString + "indet.");
    	taxaName = cleanTaxaName(taxaName, "n. " + checkString + "indet.");
    	taxaName = cleanTaxaName(taxaName, "n." + checkString + " indet.");
    	taxaName = cleanTaxaName(taxaName, "n. " + checkString + " indet.");
    	taxaName = cleanTaxaName(taxaName, "n." + checkString);
    	taxaName = cleanTaxaName(taxaName, "n. " + checkString);
    	taxaName = cleanTaxaName(taxaName, checkString + "indet.");
    	taxaName = cleanTaxaName(taxaName, checkString + " indet.");
    	taxaName = cleanTaxaName(taxaName, checkString);
        return taxaName;
    }

    public static String cleanTaxaName (String taxaName, String checkString) {
    	while (taxaName.indexOf(checkString) >= 0) {
    		taxaName = taxaName.substring(0, taxaName.indexOf(checkString)).trim() + " " + taxaName.substring(taxaName.indexOf(checkString) + checkString.length(), taxaName.length()).trim();
    		taxaName = taxaName.trim();
    	}
    	return taxaName;
    }

    public static String getCleanedName(String cleanName) throws DataInputException {
    	if (cleanName == null)
    		throw new DataInputException();
    	cleanName = cleanName.replaceAll("\"", "'");
    	cleanName = cleanName.replaceAll("<", "'");
    	cleanName = cleanName.replaceAll(">", "'");
    	cleanName = cleanName.replaceAll("  ", " ");
    	cleanName = cleanName.replaceAll("group", "gr.");
    	cleanName = TaxonomicUtil.cleanTaxaName(cleanName, "?");
    	cleanName = TaxonomicUtil.cleanTaxaNameOpen(cleanName, "subsp.");
    	cleanName = TaxonomicUtil.cleanTaxaNameOpen(cleanName, "subspp.");
    	cleanName = TaxonomicUtil.cleanTaxaNameOpen(cleanName, "sp.");
    	cleanName = TaxonomicUtil.cleanTaxaNameOpen(cleanName, "spp.");
    	cleanName = TaxonomicUtil.cleanTaxaNameOpen(cleanName, "subgen.");
    	cleanName = TaxonomicUtil.cleanTaxaNameOpen(cleanName, "gen.");
    	cleanName = TaxonomicUtil.cleanTaxaNameOpen(cleanName, "subfam.");
    	cleanName = TaxonomicUtil.cleanTaxaNameOpen(cleanName, "fam.");
    	cleanName = TaxonomicUtil.cleanTaxaName(cleanName, "indet.");
    	cleanName = TaxonomicUtil.cleanTaxaName(cleanName, "cf.");
    	cleanName = TaxonomicUtil.cleanTaxaName(cleanName, "aff.");
    	cleanName = TaxonomicUtil.cleanTaxaName(cleanName, "MS.");
    	cleanName = TaxonomicUtil.cleanTaxaName(cleanName, "s.s.");
    	cleanName = TaxonomicUtil.cleanTaxaName(cleanName, "s.s");
    	cleanName = TaxonomicUtil.cleanTaxaName(cleanName, "s.l.");
    	cleanName = TaxonomicUtil.cleanTaxaName(cleanName, "ex gr.");
    	cleanName = TaxonomicUtil.cleanTaxaName(cleanName, "gr.");
    	cleanName = TaxonomicUtil.cleanTaxaName(cleanName, "var.");
    	return cleanName;
    }

    /**
     * Checks vital comparitive fields and if compatible, then updates others to match
     * and returns true, otherwise return false;
     * 
     * Vital fields are group and name and author
     * @param entry
     * @param group
     * @param name
     * @param author
     * @param specimenCount
     * @param specimenCoords
     * @param comments
     * @return
     */
    public boolean isMatchingEntry(PaleontologyListEntry entry, String group, String name, String author, Integer specimenCount, String specimenCoords, String comments) {
        if (entry.getTaxonomicGroup() == null) {
            if (group != null && group.length() > 0)
                return false;
            else if (!entry.getTaxonomicGroup().getName().equals(group))
                return false;
        }
        if (!equalsEmptyEquivNull(entry.getTaxonomicName(), name))
            return false;
        if (!equalsEmptyEquivNull(entry.getTaxon().getAuthor(), author))
            return false;
        
        entry.setSpecimenCount(specimenCount);
        entry.setSpecimenCoords(specimenCoords);
        entry.setComments(comments);
        
        return true;
    }

    public PaleontologyListEntry createPaleontologyListEntry() {
       return taxonomicDAO.createPaleontologyListEntry();
    }

    public Taxon getTaxon(TaxonomicGroup taxonomicGroup, String name, String author) throws StorageAccessException {
        return taxonomicDAO.getTaxon(taxonomicGroup, name, author);
    }

    public Taxon createTaxon() {
        return taxonomicDAO.createTaxon();
    }
}
