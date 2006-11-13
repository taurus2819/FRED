package nz.cri.gns.fred.de;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;

import nz.cri.gns.auth.InsufficientPrivelegesException;
import nz.cri.gns.auth.User;
import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.db.ComboDescriptor;
import nz.cri.gns.db.DBUtils;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.model.FREDConstants;
import nz.cri.gns.fred.model.Lab;
import nz.cri.gns.fred.model.LabSection;
import nz.cri.gns.fred.model.PalListMeta;
import nz.cri.gns.fred.model.Paleontology;
import nz.cri.gns.fred.model.PaleontologyListEntry;
import nz.cri.gns.fred.model.Record;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.model.Taxon;
import nz.cri.gns.fred.model.TaxonomicGroup;
import nz.cri.gns.fred.model.UserView;
import nz.cri.gns.fred.util.FREDUtil;
import nz.cri.gns.fred.util.PersonUtil;
import nz.cri.gns.fred.util.RecordUtil;
import nz.cri.gns.fred.util.SampleUtil;
import nz.cri.gns.fred.util.TaxonomicUtil;
import nz.cri.gns.fred.website.ContentProvider;
import nz.cri.gns.intranet.Template;

public class PaleontologyRecordDE extends RecordDE {
	public class UnsavedTaxon implements Taxon {

		public Integer getTaxaId() {
			return null;
		}

		public void setTaxaId(Integer taxaId) {
			throw new IllegalStateException("Not savable");
		}

		private String taxonomicName;
		private String author;
		private String status;
		private Integer submittedById;
		private Date submittedDate;
		private Integer approvedById;
		private Date approvedDate;
		private String panelistComments;
		private String sendMessage;
	    private UserView submittedBy;
	    private UserView approvedBy;
		private TaxonomicGroup taxonomicGroup;
		private Set<PaleontologyListEntry> listEntries;
		
		public Integer getApprovedById() {
			return approvedById;
		}

		public void setApprovedById(Integer approvedById) {
			this.approvedById = approvedById;
		}

		public Date getApprovedDate() {
			return approvedDate;
		}

		public void setApprovedDate(Date approvedDate) {
			this.approvedDate = approvedDate;
		}

		public String getAuthor() {
			return author;
		}

		public void setAuthor(String author) {
			this.author = author;
		}

		public Set<PaleontologyListEntry> getListEntries() {
			return listEntries;
		}

		public void setListEntries(Set<PaleontologyListEntry> listEntries) {
			this.listEntries = listEntries;
		}

		public String getPanelistComments() {
			return this.panelistComments;
		}

		public void setPanelistComments(String panelistComments) {
			this.panelistComments = panelistComments;
		}
		
		public String getSendMessage() {
			return sendMessage;
		}

		public void setSendMessage(String sendMessage) {
			this.sendMessage = sendMessage;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public Integer getSubmittedById() {
			return submittedById;
		}

		public void setSubmittedById(Integer submittedById) {
			this.submittedById = submittedById;
		}

		public Date getSubmittedDate() {
			return submittedDate;
		}

		public void setSubmittedDate(Date submittedDate) {
			this.submittedDate = submittedDate;
		}

	    public void setSubmittedBy(UserView submittedBy) {
			this.submittedBy = submittedBy;
		}

		public UserView getSubmittedBy() {
			return submittedBy;
		}

		public void setApprovedBy(UserView approvedBy) {
			this.approvedBy = approvedBy;
		}

		public UserView getApprovedBy() {
			return approvedBy;
		}
		
		public TaxonomicGroup getTaxonomicGroup() {
			return taxonomicGroup;
		}

		public void setTaxonomicGroup(TaxonomicGroup taxonomicGroup) {
			this.taxonomicGroup = taxonomicGroup;
		}

		public String getTaxonomicName() {
			return taxonomicName;
		}

		public void setTaxonomicName(String taxonomicName) {
			this.taxonomicName = taxonomicName;
		}
		
		public int compareTo(nz.cri.gns.fred.model.Taxon arg0) {
			return taxonomicName.compareTo(arg0.getTaxonomicName());
		}

	}
	public class UnsavedListEntry implements PaleontologyListEntry {

		private String comments;
		private Integer specimenCount;
		private String specimenCoords;
		private String taxonomicName;
		private TaxonomicGroup taxonomicGroup;
		private Paleontology paleontology;
		private Taxon taxon;
		private Set<PalListMeta> palListMetas;
		
		public Integer getPalListId() {
			return null;
		}

		public void setPalListId(Integer palListId) {
			throw new IllegalStateException("Not savable");
		}

		public String getComments() {
			return this.comments;
		}

		public void setComments(String comments) {
			this.comments = comments;
		}

		public Paleontology getPaleontology() {
			return paleontology;
		}

		public void setPaleontology(Paleontology paleontology) {
			this.paleontology = paleontology;
		}

		public String getSpecimenCoords() {
			return specimenCoords;
		}

		public void setSpecimenCoords(String specimenCoords) {
			this.specimenCoords = specimenCoords;
		}

		public Integer getSpecimenCount() {
			return specimenCount;
		}

		public void setSpecimenCount(Integer specimenCount) {
			this.specimenCount = specimenCount;
		}

		public Taxon getTaxon() {
			return taxon;
		}

		public void setTaxon(Taxon taxon) {
			this.taxon = taxon;
		}

		public TaxonomicGroup getTaxonomicGroup() {
			return taxonomicGroup;
		}

		public void setTaxonomicGroup(TaxonomicGroup taxonomicGroup) {
			this.taxonomicGroup = taxonomicGroup;
		}

		public String getTaxonomicName() {
			return taxonomicName;
		}

		public void setTaxonomicName(String taxonomicName) {
			this.taxonomicName = taxonomicName;
		}

		public Set<PalListMeta> getPalListMetas() {
			return palListMetas;
		}

		public void setPalListMetas(Set<PalListMeta> palListMetas) {
			this.palListMetas = palListMetas;
		}

		public String getUniqueIdentifier() {
			return String.valueOf(getPalListId());
		}

		public String getDisplayName() {
			return getTaxonomicName();
		}

		public int compareTo(PaleontologyListEntry arg0) {
			return getDisplayName().compareTo(arg0.getDisplayName());
		}
	}

	private Set<PaleontologyListEntry> badTaxaList;
	private boolean nonApprovedTaxaFlag = false;
    private TaxonomicUtil taxonomicUtil;

    public PaleontologyRecordDE(User user, Sample sample, int folderID, DAOFactory factory, ContentProvider provider) throws StorageAccessException, InsufficientPrivelegesException {
        super(user, sample, folderID, FREDConstants.PALEONTOLOGICAL, factory, provider);
        taxonomicUtil = new TaxonomicUtil(factory);
    }

    public PaleontologyRecordDE(Record record, int folderId, User user, DAOFactory factory, ContentProvider provider) throws IllegalArgumentException, DataInputException, SQLException, IOException, InsufficientPrivelegesException, StorageAccessException {
        super(record, folderId, user, factory, provider);
        taxonomicUtil = new TaxonomicUtil(factory);
    }

    public void updateFromRequest(HttpServletRequest request, DAOFactory factory, boolean addIfNew) throws DataInputException {
       reinitialise(factory);
       Vector<String[]> error = new Vector<String[]>();
        
       super.updateFromRequest(request, factory, addIfNew);
        
        Paleontology pal = record.getPaleontology();
        //Collection date
        try {
            String palDate = request.getParameter("PalDate");
            pal.setIdentificationDate(FREDUtil.parseDateFromDE(palDate));
            pal.setDateRounding(FREDUtil.parseDateRoundingFromDE(palDate));
        } catch (ParseException e) {
            error.add(new String[] {"Adoption Date", "Badly formatted date"});
        }
        
        //Identifiers
        try {
            pal.setIdentifiers(FREDUtil.getPersons(request.getParameter("Identifier"), new PersonUtil(factory), "Identifiers", addIfNew));
        } catch (DataInputException e) {
            error.addAll(e.getError());
        }
        
        //Stage
        try {
            pal.setStage(StageDEUtil.getStage(request, "Stage", pal.getStage(), new SampleUtil(factory), "Stage"));
        } catch (DataInputException e) {
			error.addAll(e.getError());
        }
        
        //Stage Comments
        pal.setStageComments(request.getParameter("StComm"));
        
        String sectionId = request.getParameter("SectID");
        if (sectionId == null) {
            pal.setLabSection(null);
        } else {
            if (pal.getLabSection() == null || !pal.getLabSection().getLabSectionId().toString().equals(sectionId)) try {
                pal.setLabSection(recordUtil.getLabSection(Integer.parseInt(sectionId)));
            } catch (NumberFormatException e) {
            	pal.setLabSection(null);
            } catch (StorageAccessException e) {
            	e.printStackTrace();
                error.add(new String[] {"Lab Section", "Error accessing data storage"});
            }
        }
        
        pal.setLabNumber(request.getParameter("LabNum"));
        pal.setCollectionComments(request.getParameter("CollComm"));
        
        //Taxa
        String taxa = request.getParameter("Taxa");
        badTaxaList = new HashSet<PaleontologyListEntry>();
        Set<PaleontologyListEntry> taxaList = pal.getListEntries();
        if (taxaList == null) {
            taxaList = new HashSet<PaleontologyListEntry>();
            pal.setListEntries(taxaList);
        }
        //Copy all the old into the removed set ... for now 
        Set<PaleontologyListEntry> removedTaxaList = new HashSet<PaleontologyListEntry>(taxaList);

        //Mark it as ok ... for now
        nonApprovedTaxaFlag = false;
        for (String taxaLine : taxa.split("\\n")) {
		    taxaLine = taxaLine.trim();
            if (taxaLine.length() == 0)
            	continue;
			try {
                boolean found = false;
                String[] bits = taxaLine.split("\\*", -1);
                
                Integer specCount = (bits[SPECIMEN_COUNT].length() == 0) ? null : new Integer(bits[SPECIMEN_COUNT]);
                for (Iterator<PaleontologyListEntry> it = removedTaxaList.iterator(); it.hasNext(); ) {
                    PaleontologyListEntry entry = it.next();
                    if (taxonomicUtil.isMatchingEntry(entry, bits[GROUP], bits[NAME], bits[AUTHOR], specCount, bits[SPECIMEN_COORD], bits[COMMENTS])) {
                        //It matches
                        it.remove();
                        found = true;
                        if (entry.getTaxon() != null && !entry.getTaxon().getStatus().equals(FREDConstants.APPROVED))
                        	nonApprovedTaxaFlag = true;
                        break;
                    }
                }
                //Was not already in the list
                if (!found) {
                	//The group
                	TaxonomicGroup group = taxonomicUtil.getTaxonomicGroup(bits[GROUP]);
                	//clean TaxaName
                    String cleanName = TaxonomicUtil.getCleanedName(bits[NAME]);
                    //Prepare for having an entry
                    PaleontologyListEntry entry = null;
                    //Is the taxonomic name valid?
                    boolean blankTaxon = cleanName.length() == 0 && bits[NAME].length() > 0;
                    	
                    Taxon taxon = (blankTaxon) 
                    	? null 
                    	: taxonomicUtil.getTaxon(group, cleanName, bits[AUTHOR]);
                    
                    if (taxon == null && !blankTaxon) {
                        //It's a new taxon - create a record...but don't save it yet!
                    	entry = new UnsavedListEntry(); 
                    	taxon = new UnsavedTaxon();
                        taxon.setAuthor(bits[AUTHOR]);
                        taxon.setStatus(FREDConstants.PROVISIONAL);
                        taxon.setTaxonomicGroup(group);
                        taxon.setTaxonomicName(cleanName);
                        taxon.setSubmittedById(new Integer(user.getId()));
                        taxon.setSubmittedDate(new Date());
                        //Also add the entry to the bad list
                        badTaxaList.add(entry);
                    } else {
                    	entry = taxonomicUtil.createPaleontologyListEntry();
                    	taxaList.add(entry);
                    }
                    
                    entry.setPaleontology(pal);
                    entry.setTaxonomicGroup(taxonomicUtil.getTaxonomicGroup(bits[GROUP]));
                    entry.setTaxonomicName(bits[NAME]);
                    entry.setTaxon(taxon);
                    if (bits[SPECIMEN_COUNT].length() > 0)
                    	entry.setSpecimenCount(new Integer(bits[SPECIMEN_COUNT]));
                    entry.setSpecimenCoords(bits[SPECIMEN_COORD]);
                    entry.setComments(bits[COMMENTS]);
                    if (entry.getTaxon() != null && !entry.getTaxon().getStatus().equals(FREDConstants.APPROVED))
                    	nonApprovedTaxaFlag = true;
                }
			} catch (Exception e) {
				//e.printStackTrace();
				error.add(new String[] {"Taxanomic List", taxaLine + " not valid"});
			}
        }
        //Now remove any that remain in the 'removed' pile
        taxaList.removeAll(removedTaxaList);

        if (error.size() > 0) 
            throw new DataInputException(error);
        
		if (badTaxaList.size() > 0)
			throw new TaxonomicListException(badTaxaList);
		
	}
	
    private static final int GROUP = 0;
    private static final int NAME = 1;
    private static final int AUTHOR = 2;
    private static final int SPECIMEN_COUNT = 3;
    private static final int SPECIMEN_COORD = 4;
    private static final int COMMENTS = 5;

    public void makeDataEntryHTML(PrintWriter out, DAOFactory factory) throws IOException, SQLException {
		try {
			super.makeDataEntryHTML(out, factory);
            Template template = provider.getContent("paleo.de.form");
            prepareTemplate(template, provider);
            Paleontology pal = record.getPaleontology();
            
            template.addSub("PalDate", FREDUtil.formatDateForDE(pal.getIdentificationDate(), pal.getDateRounding()));
            template.addSub("Identifier", FREDUtil.getNames(pal.getIdentifiers(), "\n"));
            StageDEUtil.addStageSubs(template, pal.getStage(), "Stage");
            template.addSub("StComm", pal.getStageComments());
     
            StageDEUtil.drawStageInputs(out, template, pal.getStage(), "stage", "Stage");
            template.loadUntil(out, "{@labArray}");

            List<Lab> labs = recordUtil.getAllLabs();

            for (Lab lab : labs) {
                String arrayName = "a" + lab.getLabId();
            	out.println(arrayName + " = new Array();");
                
            	int count = 0;
                for (LabSection section : new TreeSet<LabSection>(lab.getSections())) {
                	out.println(arrayName + "[" + (count++) + "] = new Option('" + section.getCode() + "'," + section.getLabSectionId() + ");"); 
                }
            }
				
            template.loadUntil(out, "{@lab}");
            
            //Create a select box for the labs
			ComboDescriptor cd = new ComboDescriptor(null, null, null);
			cd.name = "LabID";
			cd.prompt = "-- Choose --";
			cd.selected = (pal.getLabSection() == null) ? null : pal.getLabSection().getLab().getLabId().toString();
			cd.tagParams = "onChange='swapSection(this.form)'";
           
			HTMLUtil.createSelect(out, cd, labs, Lab.class, "getLabId", "getName");

			if (pal.getLabSection() != null)
				template.addSub("SectID", pal.getLabSection().getLabSectionId().toString()); 
			template.addSub("LabNum", pal.getLabNumber());
			template.addSub("CollComm", pal.getCollectionComments());
			if (!RecordUtil.isTaxaApproved(record))
				template.addSub("recordIdForUnapproved", record.getRecordId().toString());

			template.loadUntil(out, "{@Taxa}");
			
			List<TaxonomicGroup> groups = recordUtil.getTaxonomicGroups(pal);
			List<PaleontologyListEntry> badTaxa = (badTaxaList == null) ? new Vector<PaleontologyListEntry>() : new Vector<PaleontologyListEntry>(badTaxaList);
			if (groups != null && groups.size() > 0) {
				for (TaxonomicGroup group : groups) {
					List<PaleontologyListEntry> list = recordUtil.getListEntries(pal, group);
					if (list == null || list.size() == 0) {
						out.println(group.getName() + "*****");
					} else {
						for (PaleontologyListEntry entry : list) {
							Taxon taxon = entry.getTaxon();
							out.println(group.getName() + "*" 
									+ entry.getTaxonomicName() + "*" 
									+ DBUtils.nvl((taxon == null) ? "" : taxon.getAuthor()) + "*" 
									+ DBUtils.nvl(entry.getSpecimenCount()) + "*" 
									+ DBUtils.nvl(entry.getSpecimenCoords()) + "*" 
									+ DBUtils.nvl(entry.getComments()));
						}
					}
					//Also check for bad taxa of this group
					for (Iterator<PaleontologyListEntry> it = badTaxa.iterator(); it.hasNext(); ) {
						PaleontologyListEntry entry = it.next();
						if (entry.getTaxonomicGroup().equals(group) && entry.getTaxon() != null) {
							Taxon taxon = entry.getTaxon();
							out.println(group.getName() + "*" 
									+ entry.getTaxonomicName() + "*" 
									+ DBUtils.nvl((taxon == null) ? "" : taxon.getAuthor()) + "*" 
									+ DBUtils.nvl(entry.getSpecimenCount()) + "*" 
									+ DBUtils.nvl(entry.getSpecimenCoords()) + "*" 
									+ DBUtils.nvl(entry.getComments()));
							it.remove();
						}
					}
				}
			}
			//Finally check for any remaining bad taxa
			for (PaleontologyListEntry entry : badTaxa) {
				if (entry.getTaxon() != null) {
					Taxon taxon = entry.getTaxon();
					out.println(taxon.getTaxonomicGroup().getName() + "*" 
							+ entry.getTaxonomicName() + "*" 
							+ DBUtils.nvl((taxon == null) ? "" : taxon.getAuthor()) + "*" 
							+ DBUtils.nvl(entry.getSpecimenCount()) + "*" 
							+ DBUtils.nvl(entry.getSpecimenCoords()) + "*" 
							+ DBUtils.nvl(entry.getComments()));
				}
			}
			
			template.loadAll(out);
			super.makeEndBitHTML(out);
		} catch (StorageAccessException e) {
			e.printStackTrace();
			throw new IOException("Could not access storage: " + e.getMessage());
		} catch (NoSuchMethodException e) {
			//Shouldn't never happen
		} catch (IllegalAccessException e) {
			//Shouldn't never happen
		} catch (InvocationTargetException e) {
			//Shouldn't never happen
		} catch (NamingException e) {
			throw new IOException("Could not access necessary resource: " + e.getMessage());
		}
	}

	public void makePostFormHTML(PrintWriter out) throws IOException {
        Template template = provider.getContent("calendar.script");
		template.addSub("button", "PalDateCal");
        template.addSub("inputField", "PalDate");
        template.loadAll(out);
    }

	public void makeExcelImportHTML(Writer out) throws IOException, SQLException {
	}
	
	protected void checkMandatoryFields() throws DataInputException {
		if (badTaxaList.size() > 0 || nonApprovedTaxaFlag)
			throw new DataInputException("Mandatory Fields", "Not all taxonomic entries are approved");
	}

    public boolean usesCalendar() {
        return true;
    }

	public String getHeading() {
		return "Edit paleontological record";
	}
	
	public int save(int dataOriginId) throws InsufficientPrivelegesException, StorageAccessException {
		int recordId = super.save(dataOriginId);
		/*if (record.getPaleontology().getRecordId() == null)
			recordUtil.save(record.getPaleontology());
		else
			recordUtil.update(record.getPaleontology());*/
		return recordId;
	}
	
	public void save(PaleontologyListEntry entry) throws StorageAccessException {
        Paleontology pal = record.getPaleontology();
        Set<PaleontologyListEntry> taxaList = pal.getListEntries();
        if (taxaList == null) {
            taxaList = new HashSet<PaleontologyListEntry>();
            pal.setListEntries(taxaList);
        }
        entry.setPaleontology(pal);
        try {
        	taxaList.add(taxonomicUtil.ensureCompatibleWithPersistenceLayer(entry));
        } catch (Exception e) {	
        	throw new StorageAccessException(e);
        }

	}
	
	public void reinitialise(DAOFactory factory) {
		super.reinitialise(factory);
	}
}
