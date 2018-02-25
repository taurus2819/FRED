package nz.cri.gns.fred.export;

import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.db.DBUtils;
import nz.cri.gns.fred.abstractions.AgeRange;
import nz.cri.gns.fred.dao.DAOFactory;
import static nz.cri.gns.fred.export.OldFormatFredExport.EOL;
import nz.cri.gns.fred.model.Age;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.Paleontology;
import nz.cri.gns.fred.model.PaleontologyListEntry;
import nz.cri.gns.fred.model.Person;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.model.SquirrelAgeView;
import nz.cri.gns.fred.model.TaxonomicGroup;

public class AutoConsensusAgeExport extends OldFormatFredExport {

	public AutoConsensusAgeExport(Writer writer, DAOFactory factory) {
		super(writer);
                Export.setFactory(factory);
                count = 1;
	}

	private static Set<String> groups;
	private static Set<String> ageGroups;
	private static Set<String> excludedIndentifiers;
	private static double baseAge = 66;
        private int count;
	static {
		groups = new HashSet<String>(3);
		groups.add("BIVALVIA");
		groups.add("GASTROPODA");
		groups.add("SCAPHOPODA");
		
	/**	ageGroups = new HashSet<String>(5);
		ageGroups.add("FORAMINIFERA");
		ageGroups.add("DINOPHYCEAE");
		ageGroups.add("RADIOLARIA");
		ageGroups.add("SPORITES");
		ageGroups.add("POLLENITES");
        **/
		excludedIndentifiers = new HashSet<String>(17);
		excludedIndentifiers.add("Anderson, S.G.");
		excludedIndentifiers.add("Bell, J.M.");
		excludedIndentifiers.add("Fraser, C.");
		excludedIndentifiers.add("Hector, J.");
		excludedIndentifiers.add("Henderson, J.");
		excludedIndentifiers.add("Hopkins, J.C.");
		excludedIndentifiers.add("Hutton, F.W.");
		excludedIndentifiers.add("Lillie, A.R.");
		excludedIndentifiers.add("Macpherson, E.O.");
		excludedIndentifiers.add("McKay, A.");
		excludedIndentifiers.add("Morgan, P.G.");
		excludedIndentifiers.add("Neef, G.");
		excludedIndentifiers.add("Ongley, M.");
		excludedIndentifiers.add("Orman, H.R.");
		excludedIndentifiers.add("Park, J.");
		excludedIndentifiers.add("Suter, H.");
		excludedIndentifiers.add("Tarvydas, R.K.");
	}	
	
	@Override
	protected boolean groupRequired(TaxonomicGroup group) {
		String name = group.getDisplayName();
		return groups.contains(name.toUpperCase());
	}

	@Override
	public Collection<Paleontology> getListsToExport(Sample sample) throws StorageAccessException {
//		Date date = new Date();
//		System.out.println("Start lists");
		List<Paleontology> listSet =  Export.getFactory().getFredDAO().getPaleontologies(sample);
//		System.out.println("Finish lists");
//		System.out.println("Pals: " + (new Date().getTime() - date.getTime()));
//		date = new Date();
		original: for (Iterator<Paleontology> it = listSet.iterator(); it.hasNext(); ) {
			Paleontology list = it.next();
			for (Person identifier : list.getIdentifiers()) {
				if (excludedIndentifiers.contains(identifier.getName())) {
					it.remove();
					continue original;
				}
			}
			boolean keep = false;
			for (PaleontologyListEntry entry : list.getListEntries()) {
				if (groupRequired(entry.getTaxonomicGroup())) {
					//Keep it!
					keep = true;
					break;
				}
			}
			if (!keep)
				it.remove();
		}
//		System.out.println("Get lists to export: " + (new Date().getTime() - date.getTime()));
		return listSet;
	}

	/**
	 * This is overridden to skip non-cenozoic mollusca - done here because this is 
	 * after the age is calculated.
	 */
	@Override
	public void handleList(Feature feature, Sample sample, AgeRange age, Paleontology list) throws IOException {
		//Reject anything that is unageable
		if (age == null)
			return;
		//Reject anything that ends before the Cenozoic begins
		if (
                    (age.getUpper() == null && age.getLower() == null) || 
                    (age.getUpper() == null && age.getLower() != null && age.getLower().getTopAge() > baseAge) || 
                    (age.getUpper() != null && age.getUpper().getTopAge() > baseAge))
                    return;
		handleList2(feature, sample, age, list);
	}        
       
	public void handleList2(Feature feature, Sample sample, AgeRange age, Paleontology list) throws IOException {
		//Don't export unapproved features
		if (feature.getFrNumber() == null)
			return;
                
		writeHeader(feature, sample, age, list);
		
		writeLists(list);
	}

        
	public void writeLists(Paleontology list) throws IOException {
		Set<TaxonomicGroup> groups = new HashSet<TaxonomicGroup>();
		for (PaleontologyListEntry entry : list.getListEntries()) {
			groups.add(entry.getTaxonomicGroup());
		}
		for (TaxonomicGroup group : groups) {
			if (!groupRequired(group))
				continue;
			writer.write(" Group: " + group.getDisplayName().toUpperCase() + EOL);
			for (PaleontologyListEntry entry : list.getListEntries()) {
				if (entry.getTaxonomicGroup().equals(group)) {
					writer.write(" " + entry.getTaxonomicName() + " * " + DBUtils.nvl(entry.getComments()) + EOL);
				}
			}
		}
	} 
        
        @Override
	protected void writeHeader(Feature feature, Sample sample, AgeRange age, Paleontology list) throws IOException {            
		//File starts with a blank line and then the Item number
		writer.write(EOL);
		writer.write("Item " + count++ + EOL);		
		//Next line has a single space on it
		writer.write(" " + EOL);
		//Work out the fr number
		String frSuffix = (feature.getSamples().size() > 1) ? ("(" + DBUtils.nvl(sample.getTopDepth()) + "-" + DBUtils.nvl(sample.getBottomDepth()) + ")") : "";
		writer.write(" FOSSIL RECORD NUMBER -      " + feature.getFrNumber().getFrNumber() + frSuffix + EOL);
		//Identifier
		if (list.getIdentifiers().size() > 0) {
			writer.write("    Identifier:  ");
			for (Iterator<Person> identifiers = list.getIdentifiers().iterator(); identifiers.hasNext(); ) {
				writer.write(identifiers.next().getDisplayName());
				if (identifiers.hasNext())
					writer.write(",");
			}
			writer.write(EOL);
		}
		//Date
		if (list.getIdentificationDate() != null) {
			writer.write("    Date:        " + new SimpleDateFormat("dd/MM/yyyy").format(list.getIdentificationDate()) + EOL);
		}
                
                //Consensus age 
                for (SquirrelAgeView sqv :sample.getSquirrelAge()) {
                    if (sqv != null) {
                        writer.write("    ConsensusAge: " + sqv.getNarrowBaseAge() + "-" + sqv.getNarrowTopAge() + EOL);                        
                    }
                }
                
		//Stage
		if (age != null && (age.getLower() != null || age.getUpper() != null)) {
			writer.write("    Stage:       ");
			//Stages:
			boolean oneAge = age.getLower() == null || age.getUpper() == null || age.getLower().equals(age.getUpper());
			if (age.getLower() != null && age.getLower().equals(age.getUpper())) {
				oneAge = oneAge && !(age.isLowerCertain() ^ age.isUpperCertain());
			}
			DecimalFormat format = new DecimalFormat("0.0####");
			if (oneAge) {
				Age ageV = age.getLower() == null ? age.getUpper() : age.getLower();
				boolean ageU = age.getLower() == null ? age.isUpperCertain() : age.isLowerCertain();
				
				writer.write(ageV.getCode() + (ageU ? "" : "?") + "; ");
				writer.write(format.format(ageV.getBaseAge()) + "-" + format.format(ageV.getTopAge()));
			} else {
				writer.write(age.getLower().getCode() + (age.isLowerCertain() ? "" : "?")
						+ "-" + age.getUpper().getCode() + (age.isUpperCertain() ? "" : "?")
						+ "; " + format.format(age.getLower().getBaseAge()) + "-" + format.format(age.getUpper().getTopAge()));
			}
			writer.write(EOL);
			writer.write("    Comment on stage determination: [" + age.getAgeRangeType() + "]");
			if (list.getStageComments() != null) {
				writer.write("; " + list.getStageComments());
			}
			writer.write(EOL);
		}
		if (list.getLabNumber() != null) {
			writer.write("    Lab. number: " + list.getLabNumber() + EOL);
		}
		if (list.getCollectionComments() != null) {
			writer.write("    Comment on collection: " + list.getCollectionComments() + EOL);
		}
	}
	
        @Override
        public void handleFeature(Feature feature) throws IOException, StorageAccessException {
		Set<Sample> samples = feature.getSamples();		
		System.out.println("** Processing Feature: " + feature.getFeatureId() + " "  + feature.getFrNumber() + " ** ");
		for (Sample sample : samples) {                    
                    for (SquirrelAgeView sqv : sample.getSquirrelAge()) {
                        if (sqv.getNarrowBaseAge() <= 66.0 && sqv.getNarrowTopAge() >= 0.0) {                    
                            for (Paleontology list : getListsToExport(sample)) {
                                //System.out.println("SELECTED Sample " + feature.getFrNumber() + " " + sample.getSampleId());
                                 handleList(feature, sample, getAgeRange(sample, list), list);    
                            }
                        }
                    }
		}
        }

}
