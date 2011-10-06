package nz.cri.gns.fred.util;

import java.beans.IntrospectionException;
import java.net.URL;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.sf.hibernate.expression.Criterion;
import net.sf.hibernate.expression.Expression;
import nz.cri.gns.auth.InsufficientPrivelegesException;
import nz.cri.gns.auth.User;
import nz.cri.gns.auth.UserAccount;
import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.db.DBUtils;
import nz.cri.gns.fred.Match;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.FredDAO;
import nz.cri.gns.fred.de.DataInputException;
import nz.cri.gns.fred.model.FREDConstants;
import nz.cri.gns.fred.model.FrUserView;
import nz.cri.gns.fred.model.PaleontologyListEntry;
import nz.cri.gns.fred.model.Taxon;
import nz.cri.gns.fred.model.TaxonomicGroup;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class TaxonomicUtil extends ModelUtil {

    private FredDAO fredDAO;
    
	public TaxonomicUtil(DAOFactory dao) {
        super(dao);
        this.fredDAO = dao.getFredDAO();
	}
	
	public TaxonomicGroup getTaxonomicGroup(int groupId) throws StorageAccessException {
		return fredDAO.get(groupId, nz.cri.gns.fred.hibernate.TaxonomicGroup.class);
	}
	
	public List<TaxonomicGroup> getTaxonomicGroups() throws StorageAccessException {
		return fredDAO.getList("FROM TaxonomicGroup AS t", TaxonomicGroup.class);
	}
	
	public Taxon getTaxon(int taxonId) throws StorageAccessException {
		return fredDAO.get(taxonId, nz.cri.gns.fred.hibernate.TaxonomicLookup.class);
	}
	
	public List<TaxonomicGroup> getTaxonomicGroupsIsPanelistOf(UserAccount user) throws StorageAccessException {
		FrUserView frUser = fredDAO.get(new Integer(user.getId()), nz.cri.gns.fred.hibernate.FrUserView.class);
		List<TaxonomicGroup> groups = new Vector<TaxonomicGroup>();
		for (TaxonomicGroup group : frUser.getTaxonomicGroups())
			groups.add(group);
		Collections.sort(groups);
		return groups;
	}
	
	public boolean isUserPanelistOf(TaxonomicGroup group, UserAccount user) throws StorageAccessException {
		Integer userId = new Integer(user.getId());
		for (FrUserView frUser : group.getPanelists()) {
			if (frUser.getUserId().equals(userId))
				return true;
		}
		return false;
	}
	
	public void addPanelistToTaxonomicGroup(TaxonomicGroup group, FrUserView frUser) throws StorageAccessException {
		group.getPanelists().add(frUser);
		fredDAO.saveOrUpdate(group);
	}

	public void removePanelistFromTaxonomicGroup(TaxonomicGroup group, FrUserView frUser) throws StorageAccessException {
		group.getPanelists().remove(frUser);
		fredDAO.saveOrUpdate(group);
	}
	
	public Taxon approveTaxon(Taxon taxon, UserAccount user, String comments) throws StorageAccessException, InsufficientPrivelegesException {
		return approveRejectObsoleteTaxon(taxon, user, FREDConstants.APPROVED, comments);
	}

	public Taxon rejectTaxon(Taxon taxon, UserAccount user, String comments) throws StorageAccessException, InsufficientPrivelegesException {
		return approveRejectObsoleteTaxon(taxon, user, FREDConstants.REJECTED, comments);
	}
	
	public Taxon obsoleteTaxon(Taxon taxon, UserAccount user) throws StorageAccessException, InsufficientPrivelegesException {
		return approveRejectObsoleteTaxon(taxon, user, FREDConstants.OBSOLETE, taxon.getPanelistComments());
	}
	
	private Taxon approveRejectObsoleteTaxon(Taxon taxon, UserAccount user, String status, String comments) throws InsufficientPrivelegesException, StorageAccessException {
		if (!isUserPanelistOf(taxon.getTaxonomicGroup(), user))
			throw new InsufficientPrivelegesException();
		taxon.setStatus(status);
		taxon.setApprovedById(new Integer(user.getId()));
		taxon.setApprovedDate(new Date());
		taxon.setPanelistComments(comments);
		fredDAO.saveOrUpdate(taxon);
		return taxon;		
	}

	public void deleteTaxon(Taxon taxon, UserAccount user) throws StorageAccessException, InsufficientPrivelegesException {
		if (!isUserPanelistOf(taxon.getTaxonomicGroup(), user))
			throw new InsufficientPrivelegesException();
		if (!FREDUtil.isEmpty(taxon.getListEntries()))
			throw new IllegalStateException("Cannot delete as referenced in a Paleontology list");
		fredDAO.delete(taxon);
	}
	
	/**
	 * @deprecated use getTaxaCount
	 */
	@Deprecated
	public int getProvisionalCount(TaxonomicGroup group) throws StorageAccessException {
		return fredDAO.getTaxaCount(group, FREDConstants.PROVISIONAL);
	}
	
	public int getTaxaCount(TaxonomicGroup group, String status) throws StorageAccessException {
		return fredDAO.getTaxaCount(group, status);
	}

	public List<Taxon> getTaxa(TaxonomicGroup group, String status) throws StorageAccessException {
		List<Taxon> taxa = fredDAO.getTaxa(group, status);
		Collections.sort(taxa);
		return taxa;
	}
	
	public List<Taxon> getAppProvTaxa(String group) throws StorageAccessException {
		TaxonomicGroup taxaGroup = getTaxonomicGroup(group);
		List<Taxon> taxa = getTaxa(taxaGroup, FREDConstants.APPROVED);
		taxa.addAll(getTaxa(taxaGroup, FREDConstants.PROVISIONAL));
		Collections.sort(taxa);
		return taxa;
	}
	
	public TaxonomicGroup getTaxonomicGroup(String groupName) throws StorageAccessException {
		return fredDAO.findTaxonomicGroup(groupName);
	}

	/**
	 * Returns the taxonomic name stripped of any prefix and suffix
	 */
    public static String getCleanedName(String cleanName) throws DataInputException {
    	if (cleanName == null)
    		throw new DataInputException();
    	cleanName = cleanName.replaceAll("\"", "'");
    	cleanName = cleanName.replaceAll("<", "'");
    	cleanName = cleanName.replaceAll(">", "'");
    	cleanName = cleanName.replaceAll("  ", " ");
    	cleanName = cleanName.replaceAll("group", "gr.");
    	cleanName = cleanTaxaName(cleanName, "?");
    	cleanName = cleanTaxaNameOpen(cleanName, "subsp.");
    	cleanName = cleanTaxaNameOpen(cleanName, "subspp.");
    	cleanName = cleanTaxaNameOpen(cleanName, "sp.");
    	cleanName = cleanTaxaNameOpen(cleanName, "spp.");
    	cleanName = cleanTaxaNameOpen(cleanName, "subgen.");
    	cleanName = cleanTaxaNameOpen(cleanName, "gen.");
    	cleanName = cleanTaxaNameOpen(cleanName, "subfam.");
    	cleanName = cleanTaxaNameOpen(cleanName, "fam.");
    	cleanName = cleanTaxaName(cleanName, "indet.");
    	cleanName = cleanTaxaName(cleanName, "cf.");
    	cleanName = cleanTaxaName(cleanName, "aff.");
    	cleanName = cleanTaxaName(cleanName, "MS.");
    	cleanName = cleanTaxaName(cleanName, "s.s.");
    	cleanName = cleanTaxaName(cleanName, "s.s");
    	cleanName = cleanTaxaName(cleanName, "s.l.");
    	cleanName = cleanTaxaName(cleanName, "ex gr.");
    	cleanName = cleanTaxaName(cleanName, "gr.");
    	cleanName = cleanTaxaName(cleanName, "var.");
    	cleanName = cleanNoDot(cleanName, "et");
    	return cleanName;
    }
    
    private static String cleanTaxaName (String taxaName, String checkString) {
    	while (taxaName.indexOf(checkString) >= 0) {
    		taxaName = taxaName.substring(0, taxaName.indexOf(checkString)).trim() + " " + taxaName.substring(taxaName.indexOf(checkString) + checkString.length(), taxaName.length()).trim();
    		taxaName = taxaName.trim();
    	}
    	return taxaName;
    }
    
    private static String cleanTaxaNameOpen (String taxaName, String checkString) {
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
    
    private static String cleanAlphaChar (String taxaName, String checkString) {
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

    private static String cleanNoDot(String taxaName, String checkString) {
    	taxaName = cleanTaxaName(taxaName, " " + checkString + " ");
    	//check end of string
    	if (taxaName.lastIndexOf(" " + checkString) == taxaName.length() - checkString.length() - 1)
    		taxaName = taxaName.substring(0, taxaName.lastIndexOf(" et")).trim();
    	//check beginning of string
    	if (taxaName.indexOf(checkString + " ") == 0)
    		taxaName = taxaName.substring(checkString.length() + 1, taxaName.length()).trim();
    	return taxaName;
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
        } else if (!entry.getTaxonomicGroup().getName().equals(group)) {
                return false;
        }
        if (!equalsEmptyEquivNull(entry.getTaxonomicName(), name))
            return false;
        if (entry.getTaxon() == null && author != null && author.length() > 0)
        	return false;
        if (entry.getTaxon() != null && !equalsEmptyEquivNull(entry.getTaxon().getAuthor(), author))
            return false;
        entry.setSpecimenCount(specimenCount);
        entry.setSpecimenCoords(specimenCoords);
        entry.setComments(comments);
        return true;
    }

    public PaleontologyListEntry createPaleontologyListEntry() {
       return fredDAO.createNewPaleontologyListEntry();
    }
    
    public PaleontologyListEntry getPaleontologyListEntry(int palListId) throws StorageAccessException {
    	return fredDAO.get(palListId, nz.cri.gns.fred.hibernate.PalList.class);
    }

    public Taxon getTaxon(TaxonomicGroup taxonomicGroup, String name, String author) throws StorageAccessException {
		List<Criterion> criteria = new Vector<Criterion>();
		if (taxonomicGroup != null)
			criteria.add(Expression.eq("taxonomicGroup", taxonomicGroup));
		criteria.add(Expression.eq("taxonomicName", name));
		List<Taxon> taxa = fredDAO.getList(Taxon.class, criteria);
		if (taxa.size() > 0) {
			Taxon taxon = taxa.get(0);
        	if (author != null && (taxon.getAuthor() == null || taxon.getAuthor().length() == 0)) {
        		//If no author then fill in the gap.
        		taxon.setAuthor(author);
        	}
			return taxon;
		}
		return null;
    }

    public Taxon createTaxon() {
        return fredDAO.createNewTaxon();
    }
    
	public void submitProvisional(User user, PaleontologyListEntry entry) throws StorageAccessException {
		if (user == null || entry == null || entry.getTaxonomicGroup() == null || entry.getTaxonomicName() == null || entry.getTaxonomicName().length() == 0 || entry.getTaxon() == null)
			return;
		submitProvisional(user, entry.getTaxon());
	}

	public void submitProvisional(User user, Taxon taxon) throws StorageAccessException {
		taxon.setStatus(FREDConstants.PROVISIONAL);
		taxon.setSubmittedById(new Integer(user.getId()));
		taxon.setSubmittedDate(new Date());
		try {
			taxon = ensureCompatibleWithPersistenceLayer(taxon);
			fredDAO.saveOrUpdate(taxon);
		} catch (StorageAccessException e) {
			throw e;
		} catch (Exception e) {
			throw new StorageAccessException(e);
		}
	}
	
	/**
	 * Ensures that the taxon is compatible with the persistence layer
	 * @throws IntrospectionException 
	 */
	private Taxon ensureCompatibleWithPersistenceLayer(Taxon taxon) throws IntrospectionException {
		Taxon newTaxon = fredDAO.createNewTaxon();
		if (newTaxon.getClass().equals(taxon.getClass()))
			//Same class, assume they're compatible
			return taxon;
		
		//Copy from old to new
		return FREDUtil.beanCopy(taxon, newTaxon, Taxon.class, new FREDUtil.ExcludeByType(Set.class));
	}

	public PaleontologyListEntry ensureCompatibleWithPersistenceLayer(PaleontologyListEntry entry) throws IntrospectionException {
		PaleontologyListEntry newEntry = createPaleontologyListEntry();
		if (newEntry.getClass().equals(entry.getClass()))
			return entry;
		
		//Copy from the old to the new
		return FREDUtil.beanCopy(entry, newEntry, PaleontologyListEntry.class, new FREDUtil.ExcludeByType(Set.class));
	}

	public List<Taxon> getMatchingTaxa(String str, TaxonomicGroup group, Match matchType, int maxMatches) throws StorageAccessException {
		return fredDAO.getMatchingTaxa(str, group, matchType, maxMatches);
	}
	
	public List<TaxonomicGroup> getMatchingTaxonomicGroups(String str, Match matchType, int maxMatches) throws StorageAccessException {
		return fredDAO.getMatchingTaxonomicGroups(str, matchType, maxMatches);
	}
	
	public static String[] decodeTaxaComments(String commentsStr) {
		String[] decode = new String[3];
		if (commentsStr == null || commentsStr.length() == 0)
			return decode;
		if (commentsStr.indexOf("|") < 0) {
			try {
				//if numeric then value goes in SPEC_COUNT else value goes in COMMENTS
				Integer.parseInt(commentsStr);
				decode[0] = commentsStr;
				return decode;
			} catch (Exception e) {
				decode[2] = commentsStr;
				return decode;
			}
		}
		String[] bits = commentsStr.split("\\|");
		decode[0] = bits[0];
		decode[1] = bits[1];
		if (bits.length == 3)
			decode[2] = bits[2];
		return decode;
	}
	
	public static String encodeTaxaComments(PaleontologyListEntry palEntry) {
		Integer specCount = palEntry.getSpecimenCount();
		String specCoord = palEntry.getSpecimenCoords();
		String comments = palEntry.getComments();
		
		if (specCount == null && FREDUtil.isEmpty(specCoord) && FREDUtil.isEmpty(comments))
			return null;
		if (specCount == null) {
			if (FREDUtil.isEmpty(specCoord))
				try {
					Integer.parseInt(comments);
					return "||" + comments;
				} catch (Exception e) {
					return comments;
				}
			return "|" + specCoord + "|" + DBUtils.nvl(comments);
		}
		if (FREDUtil.isEmpty(specCoord) && FREDUtil.isEmpty(comments))
			return specCount.toString();
		if (FREDUtil.isEmpty(comments))
			return specCount.toString() + "|" + DBUtils.nvl(specCoord);
		return specCount.toString() + "|" + DBUtils.nvl(specCoord) + "|" + DBUtils.nvl(comments);
	}
	
	public static boolean isTaxonInNpc(Taxon taxon) {
		if (taxon == null)
			return false;
		InputSource xml;
		try {
			xml = new InputSource(new URL("http://data.gns.cri.nz/npc/catalogue/taxonCheck.jsp?taxonId=" + taxon.getTaxaId()).openStream());
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = db.parse(xml);
			Node existsNode = doc.getElementsByTagName("exists").item(0);
			return (existsNode != null);
		} catch (Exception e) {	}
		return false;
	}
        
        public static String javascriptSafe(String comments) {
            if (comments==null) {
                return null;
            }
            return comments.replace(";", "\\;");
        }
}