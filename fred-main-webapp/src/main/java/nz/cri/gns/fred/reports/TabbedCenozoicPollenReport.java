package nz.cri.gns.fred.reports;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.FredDAO;
import nz.cri.gns.fred.export.TabbedPollenExport;
import nz.cri.gns.fred.hibernate.util.hibernate6.FredHibernate;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.FrNumber;
import nz.cri.gns.fred.model.Taxon;
import nz.cri.gns.fred.util.FeatureUtil;

/**
 *
 * @author richardt
 */
public class TabbedCenozoicPollenReport extends AbstractReport {
     private FredDAO dao;
    private HashMap<String, HashMap<String,Integer>> taxa = null;
    private HashMap<String, HashMap<String,Integer>> aggregatedTaxa = null;
    PrintWriter writer;
    
    public TabbedCenozoicPollenReport() {
        dao = FredHibernate.get().getDAOFactory().getFredDAO();
    }
    
    
    public static void main(String[] args) {
        try {
            TabbedCenozoicPollenReport report = new TabbedCenozoicPollenReport();
            report.report(args[0], args[1], args[2], args[3]);
        } catch (Exception e) {
            System.out.println("Usage: TabbedCenozoicPollenReport( <Oracle host> <Oracle SID> <DB username> <DB password> )");
            e.printStackTrace();            
        }
    }
    
    private void report(String host, String sid, String user, String password) {
        System.out.println("Setting up jndi");
        setupJNDI(host, sid, user, password);
        System.out.println("Reading inputs");
        Iterable<String> candidates = parseInputFile("//tmp//cenozoic-pollen-frnums.txt");
        Vector<Feature> features = new Vector<Feature>(1024);
        
        FrNumber frnum = null;
        DAOFactory factory = FredHibernate.get().getDAOFactory();
        FeatureUtil util = new FeatureUtil(factory); 
        Feature feature = null;
        try {
            PrintWriter writer = new PrintWriter(new File("//tmp//taxa-presence.txt"), "UTF-8");
            SortedMap<String, Vector<String>> synonyms = loadSynonyms();
            TabbedPollenExport export = new TabbedPollenExport(writer, factory, synonyms);
            int count = 0;
            
            for (String num : candidates) {
                count++;
                if (count>500) {
                    //break;
                }
                try {
                    frnum = util.getFrNumber(num);
                    feature = util.getFeature(frnum);
                    export.handleFeature(feature);
                    if (count % 2000 == 0) {
                        System.out.println(count + "...");
                    }
                    
                } catch (Exception ex) {
                    System.out.println("skipping" + num);
                    System.out.println(ex);
                }
            }
            writer.flush();
            writer.close();            
            
        } catch (IOException ex) {
            System.out.println(ex);
        }
        System.out.println("Ferme");
    }
   
        
    private Vector<String> loadDefinedTaxa() {
        Vector<String> taxa = new Vector<String>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File("//tmp//taxa.txt")));                  
            String line = null;
            while ((line = br.readLine()) != null) {
                if (line.equals(",")) {
                    continue;
                }

                //remove EOL markers
                line = line.replace(",,", "");
                String[] parts = line.split(":");
                String[] ids = parts[1].split(",");
                
                try {                 
                        Taxon taxon = dao.getFirst("from TaxonomicLookup as taxon where taxon.taxaId = ?",
                            nz.cri.gns.fred.hibernate.TaxonomicLookup.class, ids[0]);
                        if (taxon !=null) {
                            taxa.add(taxon.getTaxonomicName());
                        }

                } catch (StorageAccessException ex) {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                }                          
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
        } catch (IOException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return taxa;
    }
    
    
    private SortedMap<String, Vector<String>> loadSynonyms() {
        SortedMap<String, Vector<String>> taxa = new TreeMap<String,Vector<String>>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File("//tmp//taxa.txt")));                  
            String line = null;
            while ((line = br.readLine()) != null) {
                if (line.equals(",")) {
                    continue;
                }

                //remove EOL markers
                line = line.replace(",,", "");
                String[] parts = line.split(":");
                String[] ids = parts[1].split(",");
                
                Vector<String> synonyms = new Vector<String>();
                for (int i=1;i<ids.length;i++){                   
                    try {                 
                            Taxon taxon = dao.getFirst("from TaxonomicLookup as taxon where taxon.taxaId = ?",
                                nz.cri.gns.fred.hibernate.TaxonomicLookup.class, ids[i]);
                            if (taxon !=null) {
                                synonyms.add(taxon.getTaxonomicName());
                            }
                    } catch (StorageAccessException ex) {
                        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                    } 
                }
                
                try {                 
                    Taxon taxon = dao.getFirst("from TaxonomicLookup as taxon where taxon.taxaId = ?",
                        nz.cri.gns.fred.hibernate.TaxonomicLookup.class, ids[0]);
                    if (taxon !=null) {
                        taxa.put(taxon.getTaxonomicName(), synonyms);
                    }
                } catch (StorageAccessException ex) {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                } 
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
        } catch (IOException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return taxa;
    }
    
     private Iterable<String> parseInputFile(String file) {
         Vector<String> frnums = new Vector<String>();
         try {           
            BufferedReader br = new BufferedReader(new FileReader(new File(file)));

            //skip header 
            //br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                frnums.add(line);
            }            
        } catch (IOException ex) {
            Logger.getLogger(CenozoicPollenReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return frnums;
    }
}
