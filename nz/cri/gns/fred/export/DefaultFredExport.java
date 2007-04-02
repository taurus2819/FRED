package nz.cri.gns.fred.export;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nz.cri.gns.fred.abstractions.AgeRange;
import nz.cri.gns.fred.model.Adoption;
import nz.cri.gns.fred.model.Paleontology;
import nz.cri.gns.fred.model.PaleontologyListEntry;
import nz.cri.gns.fred.model.Record;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.model.TaxonomicGroup;
import nz.cri.gns.fred.util.FREDUtil;

public abstract class DefaultFredExport implements FredRecordExport {

	/**
	 * Returns the most recent list for each group, in no particular order
	 */
	public List<Paleontology> getListsToExport(Sample sample) {
		return new ArrayList<Paleontology>(getMostRecentLists(sample));
	}

	public AgeRange getAgeRange(Sample sample, Paleontology list) {
		AgeRange age = getAgeByAdoption(sample);
		if (age != null)
			return age;
	
		age = getAgeByAllPaleontologies(sample);
		if (age != null)
			return age;
		
		age = getAgeByPaleontology(sample, list);
		if (age != null)
			return age;
		
		age = getAgeBySample(sample);
		if (age != null)
			return age;
		
		return null;		
	}
	
	/**
	 * Returns the age based on the 'known stage' if one is defined
	 * or the 'inferred stage' if one is defined, or null
	 * @param sample
	 * @return
	 */
	private AgeRange getAgeBySample(Sample sample) {
		if (sample.getKnownStage() != null)
			return new DefaultStageAgeRange(sample.getKnownStage());
		
		if (sample.getInferredStage() != null)
			return new DefaultStageAgeRange(sample.getInferredStage());
		
		return null;
	}

	protected AgeRange getAgeByPaleontology(Sample sample, Paleontology list) {
		return list.getStage() == null ? null : new PaleontologyAge(list);
	}

	protected AgeRange getAgeByAllPaleontologies(Sample sample) {
		Set<Paleontology> lists = FREDUtil.getPaleontologies(sample);
		if (lists.size() == 1) {
			Paleontology list = lists.iterator().next();
			return list.getStage() == null ? null : new PaleontologyAge(list);
		}
		
		Set<Paleontology> relevantPals = getMostRecentLists(sample);
		return new ListDerivedAge(relevantPals, ListDerivedAge.Type.MINIMUM);
	}

	private Set<Paleontology> getMostRecentLists(Sample sample) {
		//Collect the most recent list for each pal group
		Map<TaxonomicGroup, Date> chosenDate = new HashMap<TaxonomicGroup, Date>();
		Map<TaxonomicGroup, Paleontology> chosen = new HashMap<TaxonomicGroup, Paleontology>();
		
		for (Record record : sample.getRecords()) {
			Paleontology pal = record.getPaleontology();
			if (pal == null)
				continue;
			
			Date date = pal.getDate();
			if (date == null)
				continue;
			
			for (PaleontologyListEntry entry : pal.getListEntries()) {
				TaxonomicGroup group = entry.getTaxonomicGroup();
				Date alreadyDate = chosenDate.get(group);
				if (alreadyDate == null || date.after(alreadyDate))
					chosen.put(group, pal);
			}
		}
		
		Set<Paleontology> relevantPals = new HashSet<Paleontology>(chosen.values());
		return relevantPals;
	}

	/**
	 * If there is exactly one adoption record, then returns it, otherwise if there are 
	 * more than one adoptions, returns the most recent or if all adoption records are 
	 * undated then returns any.  If there are no adoptions, returns null.
	 */
	protected AgeRange getAgeByAdoption(Sample sample) {
		Set<Adoption> adoptions = FREDUtil.getAdoptions(sample);
		if (adoptions.size() == 1)
			return new AdoptionAge(adoptions.iterator().next());
		else if (adoptions.size() > 0) {
			//Find the most recent one
			Adoption chosen = null;
			Date chosenDate = new GregorianCalendar(1600, 0, 0).getTime();
			for (Adoption adoption : adoptions) {
				Date date = adoption.getDate();
				if (date != null && date.after(chosenDate)) {
					chosenDate = date;
					chosen = adoption;
				}
			}
			if (chosen != null) {
				return new AdoptionAge(chosen);
			} else 
				//All dateless
				return new AdoptionAge(adoptions.iterator().next());
		}
		return null;
	}

}
