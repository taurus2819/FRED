package nz.cri.gns.fred.export;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import nz.cri.gns.fred.model.Paleontology;
import nz.cri.gns.fred.model.PaleontologyListEntry;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.model.TaxonomicGroup;

public class MolluscaExport extends OldFormatFredExport {

	public MolluscaExport(Writer writer) {
		super(writer);
	}

	private static List<String> groups;
	
	static {
		groups = new ArrayList<String>(3);
		groups.add("BIVALVIA");
		groups.add("GASTROPODA");
		groups.add("SCAPHOPODA");
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
			for (PaleontologyListEntry entry : list.getListEntries()) {
				if (groupRequired(entry.getTaxonomicGroup())) {
					newList.add(list);
					continue original;
				}
			}
		}
		return newList;
	}
	
	

}
