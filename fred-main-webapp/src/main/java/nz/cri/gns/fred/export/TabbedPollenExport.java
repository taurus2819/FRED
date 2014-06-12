package nz.cri.gns.fred.export;

import java.io.IOException;
import java.io.Writer;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;

import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.db.DBUtils;
import nz.cri.gns.fred.abstractions.AgeRange;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.hibernate.TaxonomicLookup;
import nz.cri.gns.fred.model.Age;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.Paleontology;
import nz.cri.gns.fred.model.PaleontologyListEntry;
import nz.cri.gns.fred.model.Person;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.model.SiteView;
import nz.cri.gns.fred.model.TaxonomicGroup;
import nz.cri.gns.fred.model.Taxon;
import nz.cri.gns.fred.util.SiteUtil;
import nz.cri.gns.util.map.Datum;
import nz.cri.gns.util.map.Datum.Coordinate;
import nz.cri.gns.util.map.Datum.LatLong;
import nz.cri.gns.util.map.DatumFactory;


public class TabbedPollenExport extends OldFormatFredExport {
    private boolean checkIdentifers = false;
    private SortedMap<String, Vector<String>>synonyms = null;
    
	public TabbedPollenExport(Writer writer, DAOFactory factory, SortedMap<String, Vector<String>>synonyms) {
		super(writer);
        Export.setFactory(factory);
        this.synonyms=synonyms;
        writeColumnHeaders();
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
    public void handleFeature(Feature feature) throws IOException, StorageAccessException {
		Set<Sample> samples = feature.getSamples();
		int num = samples.size();
		System.out.println("Feature: " + feature.getFrNumber());
		for (Sample sample : samples) {
			for (Paleontology list : getListsToExport(sample)) {
				handleList(feature, sample, getAgeRange(sample, list), list);
			}
		}
	}
    
	@Override
	public Collection<Paleontology> getListsToExport(Sample sample) throws StorageAccessException {
        if (sample.getDrillType() != null && sample.getDrillType().equals("Cutting")) {
            return new Vector<Paleontology>();
        }
        
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
        try {
            
            Integer count = null;
            HashMap recordedTaxa = new HashMap<String,String>();

            for (PaleontologyListEntry entry : list.getListEntries()) {
                recordedTaxa.put(entry.getTaxonomicName(), entry.getComments());
            }

            writeHeader(feature, sample, age, list);
            listTaxaPresence(recordedTaxa);        
            listTaxaDvcts(recordedTaxa);

            writer.write(EOL);
            writer.flush();
        
        } catch (Exception ex) {
            System.out.println(ex);
            writer.write(EOL);
        }
	}
    
    
    private void listTaxaPresence(HashMap<String,String>recordedTaxa){ 
        Taxon taxon = null;
        StringBuilder builder = new StringBuilder();
        
        for (String name:synonyms.keySet()) {            
            boolean isPresent = false;
            
            if (recordedTaxa.containsKey(name)) {                    
                isPresent=true;                  
            } else {
               for (String synonym: synonyms.get(name)) {
                   if (recordedTaxa.containsKey(synonym)) {                    
                       isPresent=true;
                       break;
                   }
               }
            }
            
            if (isPresent) {
                builder.append("P|");                    
            } else {
                builder.append("A|");
            }
        }
        
        try {
            writer.write(builder.toString());
        } catch (IOException ex) {
            Logger.getLogger(TabbedPollenExport.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void listTaxaDvcts(HashMap<String,String>recordedTaxa) { 
        Taxon taxon = null;
        String comments=null;
        StringBuilder builder = new StringBuilder("xxxxxxxxxxxxxxxxxxxxxxxxxxxxx|");//create spacer column for readability
        
        for (String name:synonyms.keySet()) {
            Integer total = new Integer(0);
//            int synstrike =0;
            Vector<String> syns = synonyms.get(name);
            
            for (String synonym: synonyms.get(name)) {
               if (recordedTaxa.containsKey(synonym)) { 
//                   synstrike+=1;
                   Integer count =parseDVct(synonym, recordedTaxa.get(synonym));
                   total+=count;
//                   if (synstrike > 1 && count > 0 && total > count) {
//                       String s = "stop";
//                   }
               }
            }
            
            
            if (total>0) {
                builder.append(total);
            } else {
                builder.append(".");
            }
            
            builder.append("|");
        }
        try {
            writer.write(builder.toString());
        } catch (IOException ex) {
             Logger.getLogger(TabbedPollenExport.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private Integer parseDVct(String synonym, String comments) {
        Integer val = new Integer(0);
        try {
            if (comments !=null) {
                int start=comments.indexOf("DVct");
                if (start >-1) {
//                    if ("DVct 3,. u;".equals(comments)){
//                        String s = "asfh";
//                    }
                    start+=4;
                    String tmp = null;
                    int finish = comments.indexOf(",;", start);
                    if (finish > -1) {
                        tmp = comments.substring(start, finish).trim();
                    } 
                    
                    if (tmp==null) {
                        finish = comments.indexOf(".;", start);
                        if (finish > -1) {
                            tmp = comments.substring(start,finish).trim();
                        }
                    }
                    
                    if (tmp==null) {
                        finish = comments.indexOf(",.", start);
                        if (finish > -1) {
                            tmp = comments.substring(start,finish).trim();
                        }
                    }

                    if (tmp==null) {
                        finish = comments.indexOf(";", start);
                        if (finish > -1) {
                            tmp = comments.substring(start,finish).trim();
                        }
                    }
                    
                    if (tmp==null) {
                        finish = comments.indexOf(",", start);
                        if (finish > -1) {
                            tmp = comments.substring(start,finish).trim();
                        }
                    }

                    if (tmp==null) {
                       tmp = comments.substring(start).trim(); 
                    }

                    val = new Integer(tmp);
                } 
            }
        } catch (Exception ex ) {
            System.out.println("barfing on comments for "+ synonym + " [" + comments + "]");
        }
        
        return val;
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
      
       
    protected void writeColumnHeaders() {
        try {
            writer.write("FRNUM | SAMPLE_ID | FEATURE_TYPE | DRILLTYPE | AGE_TYPE | BASE_AGE | TOP_AGE | IDENTIFIERS | GRAINSIZE(PRIM-SEC) | LAT (NZGD49) | LON(NZGD49) |");
            for (String name: synonyms.keySet()) {
                writer.write(name);
                writer.write("|");
            }
            
            writer.write("xxxxxxxxxxxxxxx|");
            
            for (String name: synonyms.keySet()) {
                writer.write(name);
                writer.write("|");
            }
            writer.write(EOL);
        } catch (IOException ex) {
            Logger.getLogger(TabbedPollenExport.class.getName()).log(Level.SEVERE, null, ex);
        }
	}
    
    protected void writeHeader(Feature feature, Sample sample, AgeRange age, Paleontology list) {
        try {
            String frSuffix = (feature.getSamples().size() > 1) ? ("(" + DBUtils.nvl(sample.getTopDepth()) + "-" + DBUtils.nvl(sample.getBottomDepth()) + ")") : "";
            writer.write(feature.getFrNumber().getFrNumber() + frSuffix + "|");
            
            writer.write(sample.getSampleId()+ "|");
            
            writer.write(feature.getFeatureType()+ "|") ;
            
            writer.write(sample.getDrillType()+ "|");
            
            writer.write(age.getAgeRangeType() + "|");
            
            writer.write(age.getLower().getBaseAge().toString());
            if (!age.isLowerCertain()) {
                writer.write("?");
            }
            writer.write("|");
            
            Age topAge = age.getUpper();
            if (topAge != null) {
                writer.write(topAge.getTopAge().toString());                
            } else {
                writer.write(age.getLower().getTopAge().toString());
            }
            
            if (!age.isUpperCertain()) {
                    writer.write("?");
                }
            writer.write("|");	
            
            if (list.getIdentifiers().size() > 0) {
                for (Iterator<Person> identifiers = list.getIdentifiers().iterator(); identifiers.hasNext(); ) {
                    writer.write(identifiers.next().getDisplayName());
                    if (identifiers.hasNext())
                        writer.write(";");
                }
                writer.write("|");
            } else {
                writer.write("|");
            }
            
            writer.write((sample.getPrimaryGrainSize()) ==null ? "n/a" : sample.getPrimaryGrainSize().getName());
            writer.write("-");
            writer.write((sample.getSecondaryGrainSize()) ==null ? "n/a" : sample.getSecondaryGrainSize().getName());
            writer.write("|");
            
            
            SiteView sv = null;
            if (feature.getSiteView() != null) {
                try {
                    sv = feature.getSiteView();               
                    LatLong ll = SiteUtil.getSiteLatLong(sv);
                    writer.write(ll.getLatAsDecDegree(5) + "| " + ll.getLongAsDecDegree(5) );
                } catch (SQLException ex) {
                    Logger.getLogger(PollenExport.class.getName()).log(Level.SEVERE, null, ex);
                    writer.write("oops|oops|");
                } catch (NamingException ex) {
                    Logger.getLogger(PollenExport.class.getName()).log(Level.SEVERE, null, ex);
                    writer.write("oops|oops|");
                }
                writer.write("|");
            }
        } catch (IOException ex) {
            Logger.getLogger(TabbedPollenExport.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
