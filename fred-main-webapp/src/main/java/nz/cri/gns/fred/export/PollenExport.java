package nz.cri.gns.fred.export;

import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.abstractions.AgeRange;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.hibernate.TaxonomicLookup;
import nz.cri.gns.fred.model.Age;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.Paleontology;
import nz.cri.gns.fred.model.PaleontologyListEntry;
import nz.cri.gns.fred.model.Person;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.model.TaxonomicGroup;
import nz.cri.gns.fred.model.Taxon;


public class PollenExport extends OldFormatFredExport {
    private HashMap<String, HashMap<String,Integer>> taxa = null;
    private boolean checkIdentifers = false;
    
	public PollenExport(Writer writer, DAOFactory factory) {
		super(writer);
        Export.setFactory(factory);
        taxa = new HashMap<String,HashMap<String,Integer>>();
	}

	private static Set<String> groups;
	private static Set<String> ageGroups;
	private static Set<String> excludedIdentifiers;
	private static double baseAge = 83.6;
	static {
		groups = new HashSet<String>(3);
		groups.add("SPORITES");
		groups.add("POLLENITES");
        groups.add("XANTHOPHYCEAE");
        groups.add("CHLOROPHYCEAE");
        groups.add("PRASINOPHYCEAE");
        groups.add("FUNGI");
        groups.add("ALGAE");
		
		ageGroups = new HashSet<String>(5);
		ageGroups.add("FORAMINIFERA");
		ageGroups.add("DINOPHYCEAE");
		ageGroups.add("RADIOLARIA");
		ageGroups.add("BIVALVIA");
		ageGroups.add("GASTROPODA");
		ageGroups.add("SCAPHOPODA");
        
		excludedIdentifiers = new HashSet<String>(17);
		excludedIdentifiers.add("Pocknall, D.T.");
		excludedIdentifiers.add("Mildenhall, D.C.");
		excludedIdentifiers.add("Raine, J.I.");
		excludedIdentifiers.add("McIntyre, D.J.");
		excludedIdentifiers.add("Couper, R.A.");
		excludedIdentifiers.add("Norriss, G.");
		excludedIdentifiers.add("Kennedy, E.M.");
		excludedIdentifiers.add("Harris, W.");
        excludedIdentifiers.add("Harris, W.F.");
	}
	
	
	@Override
	protected boolean groupRequired(TaxonomicGroup group) {
		String name = group.getDisplayName();
		return groups.contains(name.toUpperCase());
	}

    @Override
	public AgeRange getAgeRange(Sample sample, Paleontology list) throws StorageAccessException {
		AgeRange age = getAgeByAdoption(sample);
		if (age != null)
            return age;
		age = getAgeByAllPaleontologiesExcludingSelf(sample);
        if (age != null)
            return age;	               
        age = getAgeByAllPaleontologies(sample);
		if (age != null)
			return age;        
		age = super.getAgeBySample(sample);
		if (age != null)
			return age;
		
		return null;		
	}
    
    
	@Override
	public Collection<Paleontology> getListsToExport(Sample sample) throws StorageAccessException {
//        if (sample.getDrillType() != null && sample.getDrillType().equals("Cutting")) {
//            return new Vector<Paleontology>();
//        }
        
		List<Paleontology> listSet =  Export.getFactory().getFredDAO().getPaleontologies(sample);

		original: for (Iterator<Paleontology> it = listSet.iterator(); it.hasNext(); ) {
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
		return listSet;

	}

	/**
	 * This is overridden to skip non-cenozoic pollen - done here because this is 
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
        // add taxa to running totals
        Integer count = null;
        for (PaleontologyListEntry entry : list.getListEntries()) {
            // does the taxon exist
            Taxon taxon = entry.getTaxon();
            if (taxon==null) {
                continue;
            }
            String name = taxon.getTaxonomicName();
            if (name == null) {
                //System.out.println("skipping unidentified taxon in pl" + entry.getPalListId());
                continue;
            }
            if (! taxa.containsKey(name)) {
                taxa.put(name, new HashMap<String, Integer>());
            }
              // and now the age 
            String key = getAgeKey(age);
            HashMap<String, Integer> taxonAges = taxa.get(name);  
            if (! taxonAges.containsKey(key)){
                taxonAges.put(key, 0);
            }	
            count = taxonAges.get(key)+1;                    
            taxonAges.put(key, count);
		}
	}
    
    private String getAgeKey(AgeRange age) {
        StringBuilder builder = new StringBuilder();
        
        boolean oneAge = age.getLower() == null || age.getUpper() == null || age.getLower().equals(age.getUpper());
        if (age.getLower() != null && age.getLower().equals(age.getUpper())) {
            oneAge = oneAge && !(age.isLowerCertain() ^ age.isUpperCertain());
        }
        DecimalFormat format = new DecimalFormat("0.0####");
        if (oneAge) {
            Age ageV = age.getLower() == null ? age.getUpper() : age.getLower();
            boolean ageU = age.getLower() == null ? age.isUpperCertain() : age.isLowerCertain();

            builder.append(ageV.getCode() + (ageU ? "" : "?") + "; ");
            builder.append(format.format(ageV.getBaseAge()) + "-" + format.format(ageV.getTopAge()));
        } else {
            builder.append(age.getLower().getCode() + (age.isLowerCertain() ? "" : "?")
                    + "-" + age.getUpper().getCode() + (age.isUpperCertain() ? "" : "?")
                    + "; " + format.format(age.getLower().getBaseAge()) + "-" + format.format(age.getUpper().getTopAge()));
        }
        builder.append(" ").append(age.getAgeRangeType());
        
        return builder.toString();
    }

	@Override
	protected AgeRange getAgeByAllPaleontologies(Sample sample) throws StorageAccessException {
		List<Paleontology> lists =  Export.getFactory().getFredDAO().getPaleontologies(sample);     
		if (lists.size() == 1) {
			Paleontology list = lists.iterator().next();
			return list.getStage() == null ? null : new PaleontologyAge(list, " incl Pollen");
		}
		Set<Paleontology> relevantPals = getMostRecentLists(sample);
		for (Iterator<Paleontology> it = relevantPals.iterator(); it.hasNext(); ) {
			boolean keep = false;
			for (PaleontologyListEntry entry : it.next().getListEntries()) {
				String group = entry.getTaxonomicGroup().getName();
				if (groups.contains(group.toUpperCase())) {
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
			return new ListDerivedAge(relevantPals, ListDerivedAge.Type.MINIMUM, " incl Pollen"); //pollen only
	}
	
	protected AgeRange getAgeByAllPaleontologiesExcludingSelf(Sample sample) throws StorageAccessException {

		List<Paleontology> lists =  Export.getFactory().getFredDAO().getPaleontologies(sample);
		if (lists.size() == 1) {
			Paleontology list = lists.iterator().next();
            for (Person identifier : list.getIdentifiers()) {
				if (excludedIdentifiers.contains(identifier.getName())) {
					return null;
				}
			} 
            for (PaleontologyListEntry entry : list.getListEntries()) {
				String group = entry.getTaxonomicGroup().getName();
				if (groups.contains(group.toUpperCase())) {
                    return null;
				}
			}
			return list.getStage() == null ? null : new PaleontologyAge(list," excl Pollen");
		}
        
		Set<Paleontology> relevantPals = getMostRecentLists(sample);
        if (relevantPals.size() == 0) {
			return null;
        }
        
        pals: for (Iterator<Paleontology> it = relevantPals.iterator(); it.hasNext(); ) {
			boolean keep = true;
            
            Paleontology paleontology = it.next();
            for (Person identifier : paleontology.getIdentifiers()) {
				if (excludedIdentifiers.contains(identifier.getName())) {
                    keep = false;
					break;
				}
			} 
            if (!keep) {
				it.remove();
                continue pals;
            }

            keep = false;
			for (PaleontologyListEntry entry : paleontology.getListEntries()) {
				String group = entry.getTaxonomicGroup().getName();
				if (! groups.contains(group.toUpperCase())) {
					keep = true;
					break;
				}
			}
			if (!keep)
				it.remove();
		}
		
        return new ListDerivedAge(relevantPals, ListDerivedAge.Type.MINIMUM, " excl Pollen");
	}
      
     
    public void writeTaxaDistribution() {
        //read synonyms
        TreeSet<String> sortedTaxa = new TreeSet<String>(taxa.keySet());
        
        try {
            for (String s : sortedTaxa) {
                writer.write(s);
                writer.write(":");
                HashMap<String, Integer> ages = taxa.get(s);
                for (String key : ages.keySet()) {
                    writer.write(key);
                    writer.write("(");
                    writer.write(ages.get(key).toString()); // the count
                    writer.write(") ");  
                    writer.write("|");
                }
                writer.write(EOL);
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
        
    }
}