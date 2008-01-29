package nz.cri.gns.fred.de;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import nz.cri.gns.auth.InsufficientPrivelegesException;
import nz.cri.gns.auth.User;
import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.db.DBUtils;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.model.FREDConstants;
import nz.cri.gns.fred.model.Lab;
import nz.cri.gns.fred.model.LabSection;
import nz.cri.gns.fred.model.Paleontology;
import nz.cri.gns.fred.model.PaleontologyListEntry;
import nz.cri.gns.fred.model.Record;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.model.Stage;
import nz.cri.gns.fred.model.Taxon;
import nz.cri.gns.fred.model.TaxonomicGroup;
import nz.cri.gns.fred.model.UnsavedListEntry;
import nz.cri.gns.fred.model.UnsavedTaxon;
import nz.cri.gns.fred.util.FREDUtil;
import nz.cri.gns.fred.util.PersonUtil;
import nz.cri.gns.fred.util.StageUtil;
import nz.cri.gns.fred.util.TaxonomicUtil;
import nz.cri.gns.fred.website.ContentProvider;
import nz.cri.gns.html.Attributes;
import nz.cri.gns.html.select.SelectBox;
import nz.cri.gns.intranet.Template;

public class PaleontologyRecordDE extends RecordDE {

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
            pal.setStage(StageDEUtil.getStage(request, "Stage", pal.getStage(), new StageUtil(factory), "Stage"));
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
        badTaxaList = new HashSet<PaleontologyListEntry>();
        String taxa = request.getParameter("Taxa");
        if (taxa != null) {
        	dealWithTaxa(taxa.split("\\n"), pal, error);
        } else {
        	String[] taxa2 = request.getParameterValues("Taxa2");
        	dealWithTaxa(taxa2, pal, error);
        }
		
        if (error.size() > 0) 
            throw new DataInputException(error);
        
		if (badTaxaList.size() > 0)
			throw new TaxonomicListException(badTaxaList);
	}
	
    private void dealWithTaxa(String[] taxa, Paleontology pal, Vector<String[]> error) {
        Set<PaleontologyListEntry> taxaList = pal.getListEntries();
        if (taxaList == null) {
            taxaList = new HashSet<PaleontologyListEntry>();
            pal.setListEntries(taxaList);
        }
        //Copy all the old into the removed set ... for now 
        Set<PaleontologyListEntry> removedTaxaList = new HashSet<PaleontologyListEntry>(taxaList);

        //Mark it as ok ... for now
        nonApprovedTaxaFlag = false;

        if (taxa != null && taxa.length > 0) {
	        for (int i = 0; i < taxa.length; i++) {
	        	String taxaLine = taxa[i].trim();
	            if (taxaLine.length() == 0)
	            	continue;
				try {
	                boolean found = false;
	                String[] bits = taxaLine.split("\\*", -1);
	                
	                String groupStr = null;
	                String nameStr = null;
	                String authorStr = null;
	                String specCountStr = null;
	                String specCoordStr = null;
	                String commentsStr = null;
	                if (bits.length == 6) {
	                	groupStr = bits[GROUP];
	                	nameStr = bits[NAME];
	                	authorStr = bits[AUTHOR];
	                	specCountStr = bits[SPECIMEN_COUNT];
	                	specCoordStr = bits[SPECIMEN_COORD];
	                	commentsStr = bits[COMMENTS];
	                } else {
	                	groupStr = bits[GROUP];
	                	nameStr = bits[NAME];
	                	authorStr = bits[AUTHOR];
	                	String[] commentsBits = TaxonomicUtil.decodeTaxaComments(bits[3]);
	                	specCountStr = commentsBits[0];
	                	specCoordStr = commentsBits[1];
	                	commentsStr = commentsBits[2];
	                }
	                
	            	Integer specCount = (specCountStr == null || specCountStr.length() == 0) ? null : new Integer(specCountStr);
	                for (Iterator<PaleontologyListEntry> it = removedTaxaList.iterator(); it.hasNext(); ) {
	                    PaleontologyListEntry entry = it.next();
	                    if (taxonomicUtil.isMatchingEntry(entry, groupStr, nameStr, authorStr, specCount, specCoordStr, commentsStr)) {
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
	                	TaxonomicGroup group = taxonomicUtil.getTaxonomicGroup(groupStr);
	                	//clean TaxaName
	                    String cleanName = TaxonomicUtil.getCleanedName(nameStr);
	                    //Prepare for having an entry
	                    PaleontologyListEntry entry = null;
	                    //Is the taxonomic name valid?
	                    boolean blankTaxon = cleanName.length() == 0 && nameStr.length() > 0;
	                    	
	                    Taxon taxon = (blankTaxon) 
	                    	? null 
	                    	: taxonomicUtil.getTaxon(group, cleanName, authorStr);
	                    
	                    if (taxon == null && !blankTaxon) {
	                        //It's a new taxon - create a record...but don't save it yet!
	                    	entry = new UnsavedListEntry(); 
	                    	taxon = new UnsavedTaxon();
	                        taxon.setAuthor(authorStr);
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
	                    entry.setTaxonomicGroup(taxonomicUtil.getTaxonomicGroup(groupStr));
	                    entry.setTaxonomicName(nameStr);
	                    entry.setTaxon(taxon);
	                    if (specCountStr != null && specCountStr.length() > 0)
	                    	entry.setSpecimenCount(new Integer(specCountStr));
	                    entry.setSpecimenCoords(specCoordStr);
	                    entry.setComments(commentsStr);
	                    if (entry.getTaxon() != null && !entry.getTaxon().getStatus().equals(FREDConstants.APPROVED))
	                    	nonApprovedTaxaFlag = true;
	                }
				} catch (Exception e) {
					e.printStackTrace();
					error.add(new String[] {"Taxanomic List", taxaLine + " not valid"});
				}
	        }
        }
        
        //Now remove any that remain in the 'removed' pile
        taxaList.removeAll(removedTaxaList);
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
     
            StageDEUtil.drawStageInputs(out, template, pal.getStage(), "stage", "Stage", new StageUtil(factory));
            template.loadUntil(out, "{@labArray}");

            List<Lab> labs = recordUtil.getLabs();

            for (Lab lab : labs) {
                String arrayName = "a" + lab.getLabId();
            	out.println(arrayName + " = new Array();");
                
            	int count = 0;
                for (LabSection section : new TreeSet<LabSection>(lab.getSections())) {
                	out.println(arrayName + "[" + (count++) + "] = new Option('" + section.getCode() + "'," + section.getLabSectionId() + ");"); 
                }
            }
				
            template.loadUntil(out, "{@lab}");
			SelectBox<Lab> selectBox = new SelectBox<Lab>(labs);
			Attributes attributes = Attributes.createNameOnlyAttributes("LabID");
			attributes.setAttribute("onChange", "swapSection(this.form)");
			selectBox.writeBox(attributes, "-- Choose --", null, (pal.getLabSection() == null) ? null : pal.getLabSection().getLab(), out);

			if (pal.getLabSection() != null)
				template.addSub("SectID", pal.getLabSection().getLabSectionId().toString()); 
			template.addSub("LabNum", pal.getLabNumber());
			template.addSub("CollComm", pal.getCollectionComments());

			template.loadUntil(out, "{@Taxa}");
			
			List<TaxonomicGroup> groups = recordUtil.getTaxonomicGroups(pal);
			List<PaleontologyListEntry> badTaxa = (badTaxaList == null) ? new Vector<PaleontologyListEntry>() : new Vector<PaleontologyListEntry>(badTaxaList);
			if (groups != null && groups.size() > 0) {
				for (TaxonomicGroup group : groups) {
					List<PaleontologyListEntry> list = recordUtil.getListEntries(pal, group);
					if (list == null || list.size() == 0) {
						out.println("addTaxa('" + group.getName() + ": ', '', '');");
					} else {
						for (PaleontologyListEntry entry : list) {
							Taxon taxon = entry.getTaxon();
							out.println("addTaxa(\"" + group.getName() + "\", \""
									+ entry.getTaxonomicName() + "\", \""
									+ DBUtils.nvl((taxon == null) ? "" : taxon.getAuthor()) + "\", \""
									+ DBUtils.nvl(TaxonomicUtil.encodeTaxaComments(entry)) + "\");");
						}
					}
					//Also check for bad taxa of this group
					for (Iterator<PaleontologyListEntry> it = badTaxa.iterator(); it.hasNext(); ) {
						PaleontologyListEntry entry = it.next();
						if (entry.getTaxonomicGroup().equals(group) && entry.getTaxon() != null) {
							Taxon taxon = entry.getTaxon();
							out.println("addTaxa(\"" + group.getName() + "\", \""
									+ entry.getTaxonomicName() + "\", \""
									+ DBUtils.nvl((taxon == null) ? "" : taxon.getAuthor()) + "\", \""
									+ DBUtils.nvl(TaxonomicUtil.encodeTaxaComments(entry)) + "\");");
							it.remove();
						}
					}
				}
			}
			//Finally check for any remaining bad taxa
			for (PaleontologyListEntry entry : badTaxa) {
				if (entry.getTaxon() != null) {
					Taxon taxon = entry.getTaxon();
					out.println("addTaxa(\"" + taxon.getTaxonomicGroup().getName() + "\", \""
							+ entry.getTaxonomicName() + "\", \""
							+ DBUtils.nvl((taxon == null) ? "" : taxon.getAuthor()) + "\", \""
							+ DBUtils.nvl(TaxonomicUtil.encodeTaxaComments(entry)) + "\");");
				}
			}
			
			template.loadAll(out);
			super.makeEndBitHTML(out);
		} catch (StorageAccessException e) {
			e.printStackTrace();
			throw new IOException("Could not access storage: " + e.getMessage());
		}
	}

	public void makePostFormHTML(PrintWriter out) throws IOException {
        Template template = provider.getContent("calendar.script");
		template.addSub("button", "PalDateCal");
        template.addSub("inputField", "PalDate");
        template.loadAll(out);
    }

	public void makeExcelImportHTML(Writer out) throws IOException, SQLException {
		super.makeExcelImportHTML(out);
		Paleontology pal = record.getPaleontology();
		out.write("<td>#" + FREDUtil.formatDateForDE(pal.getIdentificationDate(), pal.getDateRounding()) + "#</td>\n");
		out.write("<td>" + FREDUtil.getNames(pal.getIdentifiers(), "#") + "</td>");
		Stage stage = pal.getStage();
		if (stage != null) {
			if (stage.getLowerAgeView() != null)
				out.write("<td>" + stage.getLowerAgeView().getAgeId().toString() + "</td>"
						+ "<td>" + DBUtils.nvl(stage.getStageLowerMod()) + "</td>");	
			else
				out.write("<td></td><td></td>");
			if (stage.getUpperAgeView() != null)
				out.write("<td>" + stage.getUpperAgeView().getAgeId().toString() + "</td>"
						+ "<td>" + DBUtils.nvl(stage.getStageUpperMod()) + "</td>");
			else
				out.write("<td></td><td></td>");
		} else {
			out.write("<td></td><td></td><td></td><td></td>");
		}
		out.write("<td>" + DBUtils.nvl(pal.getStageComments()) + "</td>");
		out.write("<td>" + ((pal.getLabSection() != null) ? pal.getLabSection().getLabSectionId() : "") + "</td>");
		out.write("<td>" + DBUtils.nvl(pal.getLabNumber()) + "</td>");
		out.write("<td>" + DBUtils.nvl(pal.getCollectionComments()) + "</td>");
		out.write("</tr>");

		List<TaxonomicGroup> groups = recordUtil.getTaxonomicGroups(pal);
		if (groups != null && groups.size() > 0) {
			for (TaxonomicGroup group : groups) {
				List<PaleontologyListEntry> list;
				try {
					list = recordUtil.getListEntries(pal, group);
					if (list == null || list.size() == 0) {
						out.write("<tr><td>" + group.getName() + "</td></tr>");
					} else {
						for (PaleontologyListEntry entry : list) {
							Taxon taxon = entry.getTaxon();
							out.write("<tr><td>" + group.getName() + "</td>" 
									+ "<td>" + DBUtils.nvl(entry.getTaxonomicName()) + "</td>" 
									+ "<td>" + DBUtils.nvl((taxon == null) ? "" : taxon.getAuthor()) + "</td>" 
									+ "<td>" + ((TaxonomicUtil.encodeTaxaComments(entry) != null) ? TaxonomicUtil.encodeTaxaComments(entry) : "*") + "</td></tr>");
						}
					}
				} catch (StorageAccessException e) {
					e.printStackTrace();
				}
			}
		}
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
