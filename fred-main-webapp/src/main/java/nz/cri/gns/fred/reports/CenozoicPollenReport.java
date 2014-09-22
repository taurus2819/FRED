
package nz.cri.gns.fred.reports;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.rmi.NotBoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.db.DBUtils;
import nz.cri.gns.fred.Match;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.FredDAO;
import nz.cri.gns.fred.export.Export;
import nz.cri.gns.fred.export.PollenExport;
import nz.cri.gns.fred.hibernate.util.FredHibernate;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.FrNumber;
import nz.cri.gns.fred.model.Taxon;
import nz.cri.gns.fred.util.FeatureUtil;
import nz.cri.gns.util.NullOutputStream;
import org.xml.sax.SAXException;

/**
 * To use this, first receive the taxonomic synonyms file. A period separates each group of aliases. 
 * The first entry is the official or nomiated taxonomic name. Each subsequent entry are synonyms for the nominated taxon.
 * Run prepareSynonymList to assemble the internal fred ids for the taxonomic aliases
 * Run prepareSynonymSql to write a file containing sql insert statements for the taxa in step above
 * Truncate table fr.taxonomic_synonym and load sql statements from above.
 * Run report on prepared frNumber candidate list
 * Run postProcessCenozoicPollen() to aggregate ouput counts from synonyms with the nominated taxon outputs.
 *
 * @author richardt
 **/
public class CenozoicPollenReport {
    private FredDAO dao;
    private HashMap<String, HashMap<String,Integer>> taxa = null;
    private HashMap<String, HashMap<String,Integer>> aggregatedTaxa = null;
    PrintWriter writer;
    
    public CenozoicPollenReport() {
        dao = FredHibernate.get().getDAOFactory().getFredDAO();
    }
    
    
    public static void main(String[] args)     {
        CenozoicPollenReport report = new CenozoicPollenReport();
        //report.report();
        report.postProcessCenozoicPollen();
        //report.prepareSynonymList();
        //report.prepareSynonymSql();
                
    }
    
    private void report() {
        System.out.println("Setting up jndi");
        setupJNDI();
        System.out.println("Reading inputs");
        Iterable<String> candidates = parseInputFile("//tmp//cenozoic-pollen-frnums.txt");
        Vector<Feature> features = new Vector<Feature>(1024);
        
        FrNumber frnum = null;
        DAOFactory factory = FredHibernate.get().getDAOFactory();
        FeatureUtil util = new FeatureUtil(factory); 
        Feature feature = null;
        try {
            PrintWriter writer = new PrintWriter(new File("//tmp//taxa-age-dist-raw.txt"), "UTF-8");
            PollenExport export = new PollenExport(writer, factory);
            int count = 0;
            
            for (String num : candidates) {
                count++;
                try {
                    frnum = util.getFrNumber(num);
                    feature = util.getFeature(frnum);
                    export.handleFeature(feature);
                    if (count % 1000 == 0) {
                        System.out.println(count + "...");
                    }
                    
                } catch (Exception ex) {
                    System.out.println("skipping" + num);
                    System.out.println(ex);
                }
            }
            export.writeTaxaDistribution();            
            writer.flush();
            writer.close();            
            
        } catch (IOException ex) {
            System.out.println(ex);
        }
        System.out.println("Ferme");
    }
    
    public void prepareSynonymList() {
        try {
            setupJNDI();
            
            FredDAO dao = FredHibernate.get().getDAOFactory().getFredDAO();
            DAOFactory factory = FredHibernate.get().getDAOFactory();

            PrintWriter writer = new PrintWriter(new FileWriter(new File("//tmp//taxa.txt")), true);
            PrintWriter quarantine = new PrintWriter(new FileWriter(new File("//tmp//taxaquarantine.txt")), true);
            BufferedReader br = new BufferedReader(new FileReader(new File("//tmp//pollensynonyms.txt")));  
                
            int taxa_id = -1;
            int group_id = 100;
            String line = null;
            List<Taxon> taxa = null;
            HashMap<Integer,HashSet<String>> groups = new HashMap<Integer,HashSet<String>>();
            
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.equals(".")) {
                    try {
                        if (groups.get(new Integer(group_id))==null){
                            groups.put(new Integer(group_id), new HashSet<String>());
                        }
                        if (groups.get(new Integer(group_id)).contains(line)) {
                            continue;
                        } else {
                            groups.get(new Integer(group_id)).add(line);
                        }
                        taxa = dao.getMatchingTaxa(line, null, Match.EXACT, 50);
                        if (taxa.size()==0) {
                            if (taxa_id==-1) {
                                quarantine.println("unable to match taxa: " + line); 
                            } else {
                                quarantine.println("unable to match synonym: " + line);
                            }
                        } else {
                            Taxon taxon = taxa.get(0);   
                            System.out.println("matching input: " + line + " with: " + taxon.getTaxonomicName() + " of (" + taxa.size() +") taxa");
                            if (taxa_id == -1) {
                                taxa_id=taxon.getTaxaId().intValue();
                                writer.print(taxa_id);
                                writer.print(':');
                            }
                            writer.print(taxon.getTaxaId());
                            writer.print(',');
                        }
                        // if new group allocate id
                    } catch (StorageAccessException ex) {
                        java.util.logging.Logger.getLogger(CenozoicPollenReport.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                    }
                    
                } else {
                        //terminate synomnym listing for this taxa
                        writer.println(',');
                        group_id++;   
                        taxa_id=-1;
                }
            }
            
            writer.flush();
            writer.close();
            quarantine.flush();
            quarantine.close();
       
        } catch (FileNotFoundException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
        } catch (IOException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
    
    public void prepareSynonymSql() {
        try {

            PrintWriter writer = new PrintWriter(new FileWriter(new File("//tmp//taxasql.txt")), true);
            BufferedReader br = new BufferedReader(new FileReader(new File("//tmp//taxa.txt")));                  
            writer.println("truncate table fr.taxonomic_synonym;");
            
            String line = null;
            while ((line = br.readLine()) != null) {
                if (line.equals(",")) {
                    continue;
                }
                //remove EOL markers
                line = line.replace(",,", "");
                String[] parts = line.split(":");
                String[] synonyms = parts[1].split(",");
                
                for (String synonym:synonyms) {
                    writer.println("insert into taxonomic_synonym (taxa_id, synonym_id) values (" +
                            parts[0] + "," + synonym + ");");
                }               
            }
            
            writer.flush();
            writer.close();
       
        } catch (FileNotFoundException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
        } catch (IOException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
    
    private Vector<String> loadSynonyms() {
        Vector<String> synonyms = new Vector<String>();
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
                synonyms.add(parts[1]);                          
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
        } catch (IOException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return synonyms;
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
     
      private void setupJNDI() {
        try {
            JNDI.setup();            
                  
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
            if (ex instanceof IllegalStateException) {
                if ("InitialContextFactoryBuilder already set".equals(ex.getMessage())) {
                    System.out.println("Using previous JNDI setup");
                    return;
                }
            }        
        }
        
        
        try {            
            InitialContext context = new InitialContext();
            final Connection conn = DBUtils.getJavaSqlConnection("gns", "fr");
            FredHibernate.get().configure(conn);
            context.bind("java:comp/env/jdbc/fr", new DataSource() {
                
                public int getLoginTimeout() throws SQLException {
                    return 0;
                }
                
                public void setLoginTimeout(int seconds) throws SQLException {
                }
                
                public void setLogWriter(PrintWriter out) throws SQLException {
                }
                
                public PrintWriter getLogWriter() throws SQLException {
                    return new PrintWriter(new NullOutputStream());
                }
                
                public Connection getConnection(String username, String password)
                        throws SQLException {
                    return null;
                }
                
                public Connection getConnection() throws SQLException {
                    return UnclosableConnection.create(conn);
                }
                
                @Override
                public boolean isWrapperFor(Class<?> iface) throws SQLException {
                    return conn.isWrapperFor(iface);
                }
                
                @Override
                public <T> T unwrap(Class<T> iface) throws SQLException {
                    return conn.unwrap(iface);
                }

                @Override
                public Logger getParentLogger() throws SQLFeatureNotSupportedException {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
            });
            
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
        } 
    }
      
      private void writeAggregatedTaxaDistribution(Vector<String> synonyms){ 
        Taxon taxon = null;
        writer.write("------------------------------");
        writer.write("---AGGREGATED OFFICIAL TAXA---");  
        writer.write("------------------------------");
        writer.write("\r\n");    
        for (String list:synonyms) {
            String[] ids = list.split(",");

            try {
                taxon = dao.getFirst("from TaxonomicLookup as taxon where taxon.taxaId = ?", 
                        nz.cri.gns.fred.hibernate.TaxonomicLookup.class, ids[0]);
                writeTaxonDistribution(taxon, aggregatedTaxa);

            } catch (StorageAccessException ex) {
                Logger.getLogger(PollenExport.class.getName()).log(Level.SEVERE, null, ex);
            }
        }  
    }
    
    public void aggregateTaxonDistribution(Taxon taxon, Taxon synonym) {
        String taxonName = taxon.getTaxonomicName();
        String synonymName = synonym.getTaxonomicName();
        if (taxonName==null || synonym==null) {
            return;
        }
        
        HashMap<String, Integer> taxonAges = null;
        HashMap<String, Integer> synonymAges = null;

        if (! aggregatedTaxa.containsKey(taxonName)) {
            taxonAges = taxa.get(taxonName);
            if (taxonAges == null) {
                taxonAges = new HashMap<String, Integer>();
            }
            aggregatedTaxa.put(taxonName, taxonAges);
        }
        if (taxon.getTaxaId()==synonym.getTaxaId()) {
            // dont double count
            return;
        }
        taxonAges = aggregatedTaxa.get(taxonName);
        synonymAges = taxa.get(synonymName);
        if (synonymAges == null) {
            return;
        }

        for (String key : synonymAges.keySet()) {
           int count = 0; 
           if (! taxonAges.containsKey(key)) {
               taxonAges.put(key, (Integer)synonymAges.get(key));
           } else {
               count = taxonAges.get(key)+synonymAges.get(key);
               taxonAges.put(key, count);   
           }
        }  
    }
    
     private void writeTaxonDistribution(Taxon taxon, HashMap<String, HashMap<String,Integer>> source) {
        String name = taxon.getTaxonomicName();
        if (name==null) {
            return;
        }
         
        writer.write(name);
        writer.write(":");
        HashMap<String, Integer> ages = source.get(name);       
        if (ages==null) {
            writer.write("\r\n");
            return;
        }
        Vector<String> keys = new Vector<String>(ages.keySet());
            
        Collections.sort(keys, 
                new Comparator<String>(){
                    public int compare(String left, String right) {
                        int leftOpenPos = left.indexOf("; ");
                        int leftClosePos = left.indexOf(" ", leftOpenPos+2);
                        int rightOpenPos = right.indexOf("; ");
                        int rightClosePos = right.indexOf(" ", rightOpenPos+2);
                        String leftStage= left.substring(leftOpenPos+2, leftClosePos).trim();
                        String rightStage=right.substring(rightOpenPos+2, rightClosePos).trim();
                        
                        try {
                            return new Double(leftStage.substring(0, leftStage.indexOf("-")-1))
                                    .compareTo(new Double(rightStage.substring(0, rightStage.indexOf("-")-1)));
                        } catch (Exception ex) {
                            return leftStage.compareTo(rightStage);
                        }
                    }
                });

        for (String key : keys) {
                writer.write(key);
                writer.write("(");
                writer.write(ages.get(key).toString()); // the count
                writer.write(") ");
                writer.write("|");
        }
        writer.write("\r\n");
    }

    public void postProcessCenozoicPollen() {
        try {
            setupJNDI();
            writer = new PrintWriter(new File("//tmp//taxa-age-dist.txt"), "UTF-8");
            readCountedTaxa("//tmp//taxa-age-dist-raw.txt");
            Vector<String> synonyms = loadSynonyms();
            writeTargetTaxaDistribution(synonyms);            
            writer.flush();
            writer.close();
            
            writer = new PrintWriter(new File("//tmp//taxa-age-dist-agg.txt"), "UTF-8");
            aggregateTaxaCounts(synonyms);
            writeAggregatedTaxaDistribution(synonyms);
            writer.flush();
            writer.close();
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CenozoicPollenReport.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(CenozoicPollenReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
     
    private void readCountedTaxa(String fileName) {
        taxa = new HashMap<String,HashMap<String,Integer>>();
        
        try {           
            BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));

            //skip header 
            /*br.readLine();
            br.readLine();
            br.readLine();*/
            
            String line;
            while ((line = br.readLine()) != null) {
                //dont process additional sections
                if (line.contains("---") || line.contains("===")) {
                    break;
                }
                //strip terminating pipe
                if (line.endsWith("|")) {
                    line=line.substring(0,line.length()-2);
                }
                String[] parts = line.split(":");
                // add taxon store
                if (! taxa.containsKey(parts[0])) {
                    taxa.put(parts[0], new HashMap<String, Integer>());
                }
                HashMap<String, Integer> taxonAges = taxa.get(parts[0]);
                
                String[] ageCounts = null;
                if (parts[1].indexOf("|")==-1) {
                    ageCounts = new String[]{parts[1]};
                } else {
                    ageCounts = parts[1].split("\\|");
                }
                
                for (String ageCount : ageCounts) {
                    int openpos = ageCount.lastIndexOf("(");
                    int closedpos = ageCount.lastIndexOf(")");
                    
                    String key = ageCount.substring(0, openpos).trim();
                    taxonAges.put(key, new Integer(ageCount.substring(openpos+1, closedpos)));
                }
            }           
        } catch (IOException ex) {
            Logger.getLogger(CenozoicPollenReport.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void aggregateTaxaCounts(Vector<String> synonyms) {        
        aggregatedTaxa = new HashMap<String,HashMap<String,Integer>>();
        Taxon synonym = null;
        Taxon taxon = null;
        for (String list:synonyms) {
            taxon = null;
            String[] ids = list.split(",");
            for (String id: ids) {
                try {
                    if (taxon == null) {
                        taxon = dao.getFirst("from TaxonomicLookup as taxon where taxon.taxaId = ?",
                            nz.cri.gns.fred.hibernate.TaxonomicLookup.class, id);
                        if (ids.length > 1) {
                            // process taxon if no synonyms
                            continue;
                        }
                    }
                    synonym = dao.getFirst("from TaxonomicLookup as taxon where taxon.taxaId = ?",
                            nz.cri.gns.fred.hibernate.TaxonomicLookup.class, id);

                    aggregateTaxonDistribution(taxon,synonym);
                    synonym = null;
                } catch (StorageAccessException ex) {
                    Logger.getLogger(PollenExport.class.getName()).log(Level.SEVERE, null, ex);
                    synonym=null;
                }
            }
        } 
    }
    
    private void writeTargetTaxaDistribution(Vector<String> synonyms){ 
        Taxon taxon = null;
        
        writer.write("-------------------");
        writer.write("---OFFICIAL TAXA---");  
        writer.write("-------------------");
        writer.write("\r\n");    
        for (String list:synonyms) {
            String[] ids = list.split(",");
            try {
                taxon = dao.getFirst("from TaxonomicLookup as taxon where taxon.taxaId = ?", 
                        nz.cri.gns.fred.hibernate.TaxonomicLookup.class, ids[0]);
                writeTaxonDistribution(taxon, taxa);

            } catch (StorageAccessException ex) {
                Logger.getLogger(PollenExport.class.getName()).log(Level.SEVERE, null, ex);
            }
        }  

        writer.write("----------------------------");
        writer.write("---OFFICAL & SYNONYM TAXA---");
        writer.write("----------------------------");
        writer.write("\r\n");
        for (String list:synonyms) {
            String[] ids = list.split(",");
            for (String id: ids) {
                try {
                    taxon = dao.getFirst("from TaxonomicLookup as taxon where taxon.taxaId = ?",
                            nz.cri.gns.fred.hibernate.TaxonomicLookup.class, id);
                    writeTaxonDistribution(taxon, taxa);

                } catch (StorageAccessException ex) {
                    Logger.getLogger(PollenExport.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
