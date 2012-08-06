package nz.cri.gns.fred.export;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.abstractions.AgeRange;
import nz.cri.gns.fred.model.Adoption;
import nz.cri.gns.fred.model.Paleontology;
import nz.cri.gns.fred.model.PaleontologyListEntry;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.model.TaxonomicGroup;

public abstract class DefaultFredExport implements FredRecordExport {

	/**
	 * Returns the most recent list for each group, in no particular order
	 * @throws StorageAccessException 
	 */
    @Override
	public Collection<Paleontology> getListsToExport(Sample sample) throws StorageAccessException {
		return new ArrayList<Paleontology>(getMostRecentLists(sample));
	}

    @Override
	public AgeRange getAgeRange(Sample sample, Paleontology list) throws StorageAccessException {
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
//Date date0 = new Date();
//try {
		if (sample.getKnownStage() != null)
			return new DefaultStageAgeRange(sample.getKnownStage(), "FOF - Known Age");
		
		if (sample.getInferredStage() != null)
			return new DefaultStageAgeRange(sample.getInferredStage(), "FOF - Inferred Age");
		
		return null;
//} finally {
//	System.out.println("Age by sample: " + (new Date().getTime() - date0.getTime()));
//}
	}

	protected AgeRange getAgeByPaleontology(Sample sample, Paleontology list) {
//Date date0 = new Date();
//try {
		return list.getStage() == null ? null : new PaleontologyAge(list);
//} finally {
//	System.out.println("Age by pal: " + (new Date().getTime() - date0.getTime()));
//}
	}

	protected AgeRange getAgeByAllPaleontologies(Sample sample) throws StorageAccessException {
		List<Paleontology> lists =  Export.getFactory().getFredDAO().getPaleontologies(sample);
		if (lists.size() == 1) {
			Paleontology list = lists.iterator().next();
			return list.getStage() == null ? null : new PaleontologyAge(list);
		}
		
		Set<Paleontology> relevantPals = getMostRecentLists(sample);
		if (relevantPals.size() == 0)
			return null;
		
		return new ListDerivedAge(relevantPals, ListDerivedAge.Type.MINIMUM);
	}

	protected Set<Paleontology> getMostRecentLists(Sample sample) throws StorageAccessException {
		//Collect the most recent list for each pal group
		Map<TaxonomicGroup, Date> chosenDate = new HashMap<TaxonomicGroup, Date>();
		Map<TaxonomicGroup, Paleontology> chosen = new HashMap<TaxonomicGroup, Paleontology>();
		
		for (Paleontology pal : Export.getFactory().getFredDAO().getPaleontologies(sample)) {
			Date date = pal.getDate();
			
			for (PaleontologyListEntry entry : pal.getListEntries()) {
				TaxonomicGroup group = entry.getTaxonomicGroup();
				Date alreadyDate = chosenDate.get(group);
				if (alreadyDate == null || (date != null && date.after(alreadyDate)))
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
	 * @throws StorageAccessException 
	 */
	protected AgeRange getAgeByAdoption(Sample sample) throws StorageAccessException {
//		Date date0 = new Date();
//try {
		List<Adoption> adoptions = Export.getFactory().getFredDAO().getAdoptions(sample);
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
//} finally {
//	System.out.println("Age by adoption: " + (new Date().getTime() - date0.getTime()));
//}
	}

}
