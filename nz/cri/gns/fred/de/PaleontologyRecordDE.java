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
import nz.cri.gns.db.ComboDescriptor;
import nz.cri.gns.db.DBUtils;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.StorageAccessException;
import nz.cri.gns.fred.model.FREDConstants;
import nz.cri.gns.fred.model.Lab;
import nz.cri.gns.fred.model.LabSection;
import nz.cri.gns.fred.model.Paleontology;
import nz.cri.gns.fred.model.PaleontologyListEntry;
import nz.cri.gns.fred.model.Record;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.model.Taxon;
import nz.cri.gns.fred.model.TaxonomicGroup;
import nz.cri.gns.fred.util.FREDUtil;
import nz.cri.gns.fred.util.PersonUtil;
import nz.cri.gns.fred.util.RecordUtil;
import nz.cri.gns.fred.util.SampleUtil;
import nz.cri.gns.fred.util.TaxonomicUtil;
import nz.cri.gns.fred.website.ContentProvider;
import nz.cri.gns.intranet.Template;

public class PaleontologyRecordDE extends RecordDE {

	private Vector badTaxaList;
	private boolean nonApprovedTaxaFlag = false;
    private TaxonomicUtil taxonomicUtil;

    public PaleontologyRecordDE(User user, Sample sample, int folderID, DAOFactory factory, ContentProvider provider) throws StorageAccessException, InsufficientPrivelegesException {
        super(user, sample, folderID, FREDConstants.ADOPTION, factory, provider);
        taxonomicUtil = new TaxonomicUtil(factory);
    }

    public PaleontologyRecordDE(Record record, int folderId, User user, DAOFactory factory, ContentProvider provider) throws IllegalArgumentException, DataInputException, SQLException, IOException, InsufficientPrivelegesException, StorageAccessException {
        super(record, folderId, user, factory, provider);
        taxonomicUtil = new TaxonomicUtil(factory);
    }

    public void updateFromRequest(HttpServletRequest request, DAOFactory factory) throws DataInputException {
       Vector<String[]> error = new Vector<String[]>();
        
       super.updateFromRequest(request, factory);
        
        Paleontology pal = record.getPaleontology();
        //Collection date
        try {
            String palDate = request.getParameter("PalDate");
            pal.setIdentificationDate(FREDUtil.parseDateFromDE(palDate));
            pal.setDateRounding(FREDUtil.parseDateRoundingFromDE(palDate));
        } catch (ParseException e) {
            error.add(new String[] {"Adoption Date", "Badly formatted date"});
        }
        
        //Adoptors
        try {
            pal.setIdentifiers(FREDUtil.getPersons(request.getParameter("Adoptor"), new PersonUtil(factory), "Adoptors"));
        } catch (DataInputException e) {
            error.addAll(e.getError());
        }
        
        //Stage
        try {
            pal.setStage(FREDUtil.getStage(request, "", pal.getStage(), new SampleUtil(factory), "Stage"));
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
            } catch (StorageAccessException e) {
                error.add(new String[] {"Lab Section", "Error accessing data storage"});
            }
        }
        
        pal.setLabNumber(request.getParameter("LabNum"));
        pal.setCollectionComments(request.getParameter("CollComm"));
        
        //Taxa
        String taxa = request.getParameter("Taxa");
        Set<PaleontologyListEntry> badTaxaList = new HashSet<PaleontologyListEntry>();
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
			try {
                boolean found = false;
                String[] bits = taxaLine.split("\\*");
                Integer specCount = (bits[SPECIMEN_COUNT].length() == 0) ? null : new Integer(bits[SPECIMEN_COUNT]);
                for (Iterator<PaleontologyListEntry> it = removedTaxaList.iterator(); it.hasNext(); ) {
                    PaleontologyListEntry entry = it.next();
                    if (taxonomicUtil.isMatchingEntry(entry, bits[GROUP], bits[NAME], bits[AUTHOR], specCount, bits[SPECIMEN_COORD], bits[COMMENTS])) {
                        //It matches
                        it.remove();
                        found = true;
                    }
                }
                if (!found) {
                    PaleontologyListEntry entry = taxonomicUtil.createPaleontologyListEntry();
                    entry.setPaleontology(pal);
                    entry.setTaxonomicGroup(taxonomicUtil.getTaxonomicGroup(bits[GROUP]));
                    entry.setTaxonomicName(bits[NAME]);
                    
                    //clean TaxaName
                    String cleanName = TaxonomicUtil.getCleanedName(bits[NAME]);
                    Taxon taxon = taxonomicUtil.getTaxon(entry.getTaxonomicGroup(), cleanName, bits[AUTHOR]);
                    if (taxon == null) {
                        //It's a new taxon - create a record...but don't save it yet!
                        taxon = taxonomicUtil.createTaxon();
                        taxon.setAuthor(bits[AUTHOR]);
                        taxon.setStatus(FREDConstants.PROVISIONAL);
                        taxon.setTaxonomicGroup(entry.getTaxonomicGroup());
                        taxon.setTaxonomicName(cleanName);
                        taxon.setSubmittedById(new Integer(user.getId()));
                        taxon.setSubmittedDate(new Date());
                        //Also add the entry to the bad list
                        badTaxaList.add(entry);
                    }
                    entry.setTaxon(taxon);
                    entry.setSpecimenCount(new Integer(bits[SPECIMEN_COUNT]));
                    entry.setSpecimenCoords(bits[SPECIMEN_COORD]);
                    entry.setComments(bits[COMMENTS]);
                    taxaList.add(entry);
                }
			} catch (Exception e) {
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
			
			if (groups != null && groups.size() > 0) {
				for (TaxonomicGroup group : groups) {
					List<PaleontologyListEntry> list = recordUtil.getListEntries(pal, group);
					if (list == null || list.size() == 0) {
						out.println(group.getName() + "*****");
					} else {
						for (PaleontologyListEntry entry : list) {
							Taxon taxon = entry.getTaxon();
							out.println(group.getName() + "*" 
									+ taxon.getTaxonomicName() + "*" 
									+ DBUtils.nvl(taxon.getAuthor()) + "*" 
									+ DBUtils.nvl(entry.getSpecimenCount()) + "*" 
									+ DBUtils.nvl(entry.getSpecimenCoords()) + "*" 
									+ DBUtils.nvl(entry.getComments()));
						}
					}
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
        template.addSub("inputField", "PalDate");
        template.loadAll(out);
    }

	public void makeExcelImportHTML(Writer out) throws IOException, SQLException {
	}
	
    /*
	public int save()
		throws InsufficientPrivelegesException, SQLException, IOException {
		if (!savedFlag) {
			DBConnection conn = FREDUtils.getFREDConnection(state);
			conn.getConnection().setAutoCommit(false);
			try {
				super.save();
				//Delete existing PALEONTOLOGY record
				conn.executeUpdate("DELETE FROM paleontology WHERE record_id = ?", new int[] {Types.NUMERIC}, new Object[] {new Integer(record.getRecordID())});
				//Create new PALEONTOLOGY record
				String stageID = DataEntryUtils.getStageID(getField(IDT_AGE_START), getField(IDT_START_MOD), getField(IDT_AGE_STOP), getField(IDT_STOP_MOD), state);
				String query = "INSERT INTO paleontology (record_id, identification_date, date_rounding, stage_id, stage_comments, lab_section_id, lab_number, collection_comments) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
				conn.executeUpdate(query, new int[] {Types.NUMERIC, Types.DATE, Types.VARCHAR, Types.NUMERIC, Types.VARCHAR, Types.NUMERIC, Types.VARCHAR, Types.VARCHAR},
					 new Object[] {new Integer(record.getRecordID()), ((identDate != null) ? identDate.getDate() : null), ((identDate != null) ? identDate.getDateRounding() : null),
					 ((stageID != null) ? new Integer(stageID) : null), getField(STAGE_COMMENTS), ((getField(LAB_SECTION) != null) ? new Integer(getField(LAB_SECTION)) : null), getField(LAB_NUMBER), getField(COLLECTION_COMMENTS)});
				//Create IDENTIFIERS entries
				if (identifiers != null) {
					query = "INSERT INTO identifier (record_id, person_id) VALUES (?, ?)";
					int[] types = new int[] {Types.NUMERIC, Types.NUMERIC};
					Object[] values = new Object[2];
					values[0] = new Integer(record.getRecordID());
					for (Iterator i = identifiers.iterator(); i.hasNext();) {
						values[1] = (Integer) i.next();
						conn.executeUpdate(query, types, values);
					}
				}
				//Create PAL_LIST entry
				if (taxaList != null) {
					query = "INSERT INTO pal_list (record_id, group_id, taxa_id, taxonomic_name, specimen_count, specimen_coords, comments) VALUES (?, ?, ?, ?, ?, ?, ?)";
					int[] types = new int[] {Types.NUMERIC, Types.NUMERIC, Types.NUMERIC, Types.VARCHAR, Types.NUMERIC, Types.VARCHAR, Types.VARCHAR};
					Object[] values = new Object[7];
					values[0] = new Integer(record.getRecordID());
					for (Iterator i = taxaList.iterator(); i.hasNext();) {
						Taxa taxa = (Taxa) i.next();
						values[1] = taxa.getGroupID();
						values[2] = taxa.getTaxaID();
						values[3] = taxa.getTaxonomicName();
						values[4] = taxa.getSpecimenCount();
						values[5] = taxa.getSpecimenCoords();
						values[6] = taxa.getComments();	
						conn.executeUpdate(query, types, values);
					}
				}
				
				conn.getConnection().commit();
				conn.getConnection().setAutoCommit(true);
				conn.releaseStatement();
				savedFlag = true;
				record = (PaleontologyRecord) PaleontologyRecord.getData(record.getRecordID(), user, state, true);
				sample = new Sample(sample.getSampleID(), user, state, true);
			} catch (SQLException e) {
				conn.getConnection().rollback();
				conn.getConnection().setAutoCommit(true);
				conn.releaseStatement();
				savedFlag = false;
				throw e;
			} catch (IOException e) {
				conn.getConnection().rollback();
				conn.getConnection().setAutoCommit(true);
				conn.releaseStatement();
				savedFlag = false;
				throw e;
			} catch (InsufficientPrivelegesException e) {
				conn.getConnection().rollback();
				conn.getConnection().setAutoCommit(true);
				conn.releaseStatement();
				savedFlag = false;
				throw e;
			}

		}
		return record.getRecordID();
	}
	
	public int submit() throws SQLException, IOException, InsufficientPrivelegesException, DataInputException {
		int recordID = super.submit();
		record = PaleontologyRecord.getData(record.getRecordID(), user, state, true);
		return recordID;
	}
	*/
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

}
