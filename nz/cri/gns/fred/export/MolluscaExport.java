package nz.cri.gns.fred.export;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import nz.cri.gns.fred.abstractions.AgeRange;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.Paleontology;
import nz.cri.gns.fred.model.PaleontologyListEntry;
import nz.cri.gns.fred.model.Person;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.model.TaxonomicGroup;
import nz.cri.gns.fred.util.FREDUtil;

public class MolluscaExport extends OldFormatFredExport {

	public MolluscaExport(Writer writer) {
		super(writer);
	}

	private static Set<String> groups;
	private static Set<String> ageGroups;
	private static Set<String> excludedIndentifiers;
	private static double baseAge = 66.5;
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
		return groups.contains(name);
	}

	@Override
	public Collection<Paleontology> getListsToExport(Sample sample) {
		Set<Paleontology> listSet =  FREDUtil.getPaleontologies(sample);
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
		return listSet;

//		List<Paleontology> listList = super.getListsToExport(sample);
//		List<Paleontology> newList = new ArrayList<Paleontology>(listList.size());
//		original: for (Paleontology list : listList) {
//			if (list.getIdentifiers().size() == 0)
//				continue original;
//			for (Person identifier : list.getIdentifiers()) {
//				if (excludedIndentifiers.contains(identifier.getName()))
//					continue original;
//			}
//			for (PaleontologyListEntry entry : list.getListEntries()) {
//				if (groupRequired(entry.getTaxonomicGroup())) {
//					//Keep it!
//					newList.add(list);
//					continue original;
//				}
//			}
//		}
//		return newList;
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
		System.out.println(age.getLower() + " -- " + age.getUpper());
		if ((age.getUpper() == null && age.getLower() == null) || (age.getUpper() == null && age.getLower() != null && age.getLower().getAgeStop() > baseAge) || (age.getUpper() != null && age.getUpper().getAgeStop() > baseAge))
			return;
		System.out.println("OK");
		super.handleList(feature, sample, age, list);
	}

	@Override
	protected AgeRange getAgeByAllPaleontologies(Sample sample) {
		Set<Paleontology> lists = FREDUtil.getPaleontologies(sample);
		if (lists.size() == 1) {
			Paleontology list = lists.iterator().next();
			return list.getStage() == null ? null : new PaleontologyAge(list);
		}
		
		Set<Paleontology> relevantPals = getMostRecentLists(sample);
		for (Iterator<Paleontology> it = relevantPals.iterator(); it.hasNext(); ) {
			boolean keep = false;
			for (PaleontologyListEntry entry : it.next().getListEntries()) {
				if (groups.contains(entry.getTaxonomicGroup()) || ageGroups.contains(entry.getTaxonomicGroup())) {
					keep = true;
					break;
				}
			}
			if (!keep)
				it.remove();
		}
		return new ListDerivedAge(relevantPals, ListDerivedAge.Type.MINIMUM);
	}
	
	

}
