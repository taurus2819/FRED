/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nz.cri.gns.fred.servlet.util;

import static java.lang.System.out;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpSession;

/**
 *
 * @author sitikond
 */
public class SearchSessionState {
    
    private static final String SAMPLE_IDS_KEY = "FRED.samples";
    private static final String FEATURE_IDS_KEY = "FRED.features";
    private static final String QUERY_STRING_KEY = "FRED.queryString";
       
    private SearchSessionState(){        
    }

    // Store search results in user's session
    /**
     * @param session - HTTP session to write to
     * @param featureIds - ids for matching features
     * @param sampleIds - ids for matching samples
     * @param queryString - human readable format of the executed query
     */
    public static void save(HttpSession session, List<Integer> featureIds, List<Integer> sampleIds, String queryString){
        List<Integer> featureCopy = new ArrayList<>(featureIds);
        List<Integer> sampleCopy = sampleIds == null ? null : new ArrayList<>(sampleIds);
        session.setAttribute(FEATURE_IDS_KEY, immutableCopy(featureCopy));
        session.setAttribute(SAMPLE_IDS_KEY, sampleIds == null ? null : immutableCopy(sampleCopy));
        session.setAttribute(QUERY_STRING_KEY, queryString);
        
    }
    
    //restor the saved resulots from the session
    /**
     * 
     * @param session - HTTP session to read from pps
     * @return 
     */
    public static Optional<Snapshot> restore(HttpSession session){
        List<Integer> featureIds = immutableCopy((List<Integer>) session.getAttribute(FEATURE_IDS_KEY));
        String queryString = (String) session.getAttribute(QUERY_STRING_KEY);
        
        if(featureIds == null || featureIds.isEmpty() || queryString == null){
            return Optional.empty();
        }
        
        // print session contents
//        Enumeration e = session.getAttributeNames();
//        while (e.hasMoreElements()) {
//            String name = (String)e.nextElement();
//            String value = session.getAttribute(name).toString();
//            out.println(name + " = " + value);
//        }
        
        List<Integer> sampleIds = immutableCopy((List<Integer>) session.getAttribute(SAMPLE_IDS_KEY));
        return Optional.of(new Snapshot(featureIds, sampleIds, queryString));
    }
    
    private static List<Integer> immutableCopy(List<Integer> sources) {
        if(sources == null){
            return null;
        }
        return Collections.unmodifiableList(sources);
    }
    
    public static final class Snapshot {
        private final List<Integer> featureIds;
        private final List<Integer> sampleIds;
        private final String queryString;

        private Snapshot(List<Integer> featureIds, List<Integer> sampleIds, String queryString) {
            this.featureIds = featureIds;
            this.sampleIds = sampleIds;
            this.queryString = queryString;
        }

        public List<Integer> getFeatureIds() {
            return featureIds;
        }

        public List<Integer> getSampleIds() {
            return sampleIds;
        }

        public boolean hasSampleIds() {
            return sampleIds != null && !sampleIds.isEmpty();
        }

        public String getQueryString() {
            return queryString;
        }    
}
    
    
}
