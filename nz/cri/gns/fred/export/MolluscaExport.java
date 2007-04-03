package nz.cri.gns.fred.export;

import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nz.cri.gns.fred.abstractions.AgeRange;
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
	public List<Paleontology> getListsToExport(Sample sample) {
		List<Paleontology> listList = super.getListsToExport(sample);
		List<Paleontology> newList = new ArrayList<Paleontology>(listList.size());
		original: for (Paleontology list : listList) {
			if (list.getIdentifiers().size() == 0)
				continue original;
			for (Person identifier : list.getIdentifiers()) {
				if (excludedIndentifiers.contains(identifier.getName()))
					continue original;
			}
			for (PaleontologyListEntry entry : list.getListEntries()) {
				if (groupRequired(entry.getTaxonomicGroup())) {
					//Keep it!
					newList.add(list);
					continue original;
				}
			}
		}
		return newList;
	}

	@Override
	protected AgeRange getAgeByAllPaleontologies(Sample sample) {
		Set<Paleontology> lists = FREDUtil.getPaleontologies(sample);
		if (lists.size() == 1) {
			Paleontology list = lists.iterator().next();
			return list.getStage() == null ? null : new PaleontologyAge(list);
		}
		
		Set<Paleontology> relevantPals = getMostRecentLists(sample);
		for (Paleontology list : relevantPals) {
			boolean keep = false;
			for (PaleontologyListEntry entry : list.getListEntries()) {
				if (groups.contains(entry.getTaxonomicGroup()) || ageGroups.contains(entry.getTaxonomicGroup())) {
					keep = true;
					break;
				}
			}
			if (!keep)
				relevantPals.remove(list);
		}
		return new ListDerivedAge(relevantPals, ListDerivedAge.Type.MINIMUM);
	}
	
	

}
