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
import nz.cri.gns.fred.model.*;
import nz.cri.gns.fred.dao.DAOFactory;

/*
 * A class used to prefilter and perform age checking for review, prior to 
 * a conventional Molluscan export
 */
public class MolluscanAgeValidator extends OldFormatFredExport {
         
	public MolluscanAgeValidator(Writer writer, DAOFactory factory) {
		super(writer);
                Export.setFactory(factory);
	}

	private static Set<String> groups;
	private static Set<String> ageGroups;
	private static double baseAge = 65;
	static {
		groups = new HashSet<String>(3);
		groups.add("BIVALVIA");
		groups.add("GASTROPODA");
		groups.add("SCAPHOPODA");
		
		ageGroups = new HashSet<String>(5);
		ageGroups.add("FORAMINIFERA");
		ageGroups.add("DINOPHYCEAE");
		ageGroups.add("RADIOLARIA");
		ageGroups.add("SPORITES");
		ageGroups.add("POLLENITES");
		
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
		for (Iterator<Paleontology> it = listSet.iterator(); it.hasNext(); ) {
			Paleontology list = it.next();
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
		
                //super.handleList(feature, sample, age, list);
                
                writeHeader(feature, sample, age, list);
		//writeLists(list); not required
	}

	@Override
	protected AgeRange getAgeByAllPaleontologies(Sample sample) throws StorageAccessException {
//Date date0 = new Date();
//try {
		List<Paleontology> lists =  Export.getFactory().getFredDAO().getPaleontologies(sample);
		if (lists.size() == 1) {
			Paleontology list = lists.iterator().next();
			return list.getStage() == null ? null : new PaleontologyAge(list);
		}
		Set<Paleontology> relevantPals = getMostRecentLists(sample);
		for (Iterator<Paleontology> it = relevantPals.iterator(); it.hasNext(); ) {
			boolean keep = false;
			for (PaleontologyListEntry entry : it.next().getListEntries()) {
				String group = entry.getTaxonomicGroup().getName().toUpperCase();
				if (groups.contains(group) || ageGroups.contains(group)) {
					keep = true;
					break;
				}
			}
			if (!keep)
				it.remove();
		}
		if (relevantPals.size() == 0)
			return null;
		else
			return new ListDerivedAge(relevantPals, ListDerivedAge.Type.MINIMUM);
//} finally {
//	System.out.println("Age by all pal: " + (new Date().getTime() - date0.getTime()));
//}
	}
	
	private void writeLists(Paleontology list) throws IOException {
		Set<TaxonomicGroup> groups = new HashSet<TaxonomicGroup>();
		for (PaleontologyListEntry entry : list.getListEntries()) {
			groups.add(entry.getTaxonomicGroup());
		}
		for (TaxonomicGroup group : groups) {
			if (!groupRequired(group))
				continue;
			writer.write("Group: " + group.getDisplayName() + EOL);
			for (PaleontologyListEntry entry : list.getListEntries()) {
				if (entry.getTaxonomicGroup().equals(group)) {
					writer.write(" " + entry.getTaxonomicName() + " * " + DBUtils.nvl(entry.getComments()) + EOL);
				}
			}
		}
	}


	protected void writeHeader(Feature feature, Sample sample, AgeRange age, Paleontology list) throws IOException {
		//File starts with a blank line and then the Item number
		writer.write(EOL);
		//Work out the fr number
		String frSuffix = (feature.getSamples().size() > 1) ? ("(" + DBUtils.nvl(sample.getTopDepth()) + "-" + DBUtils.nvl(sample.getBottomDepth()) + ")") : "";
		writer.write(feature.getFrNumber().getFrNumber() + frSuffix + "\t");
                writer.write(sample.getSampleId() + "\t");
		//Identifier
		if (list.getIdentifiers().size() > 0) {
			writer.write("Identifier:");
			for (Iterator<Person> identifiers = list.getIdentifiers().iterator(); identifiers.hasNext(); ) {
				writer.write(identifiers.next().getDisplayName());
				if (identifiers.hasNext())
					writer.write(",");
			}
			writer.write("\t");
		}
		
		//Stage
		if (age != null && (age.getLower() != null || age.getUpper() != null)) {
			writer.write("Stage:");
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
			
		}
        }
}
