/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nz.cri.gns.fred.servlet.util;

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
    public static void save(HttpSession session, List<Integer> featureIds, List<Integer> sampleIds, String queryString){
        
    }
    
    public static Optional<Snapshot> restore(HttpSession session){
        return Optional.empty();
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
