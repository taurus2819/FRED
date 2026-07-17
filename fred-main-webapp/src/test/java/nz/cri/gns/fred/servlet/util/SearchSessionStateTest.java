/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nz.cri.gns.fred.servlet.util;

/**
 *
 * @author sitikond
 */

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpSession;
import org.junit.Test;

public class SearchSessionStateTest {
    
    /**
     * The SearchSessionState.save saves the attributes (featureIds and sampleids) in the HttpSession (session object)
     * The buildSession is used to mock a HttpSession, and the SearchSessionState.restore(session)
     * is used to restore the session.pps
     * 
     * 
     * BELOW INFO FROM 
     * HttpSession is an interface, so you'll need to either write your own implementation of it, or mock it. 
     * I would recommend mocking it with Mockito, then stubbing getAttribute and setAttribute to delegate to 
     * a HashMap, or some other suitable structure. So in your test class, you'll have fields for
     * your mocked HttpSession, a real HashMap<String,Object>
     * 
     * below test is courtesy of a nice example  https://blog.frankel.ch/two-different-mocking-approaches/
     * 
     * 
     */

    @Test
    public void restoreReturnsSnapshotWithCopiedValues() {
        Map<String, Object> attributes = new HashMap<>();
        HttpSession session = buildSession(attributes);

        List<Integer> featureIds = new ArrayList<>(Arrays.asList(1, 2));
        List<Integer> sampleIds = new ArrayList<>(Arrays.asList(10, 20));

        SearchSessionState.save(session, featureIds, sampleIds, "query-string");

        // mutate original collections after saving to ensure the session keeps its own copies
        featureIds.add(3);
        sampleIds.add(30);

        //the seession should not be mutated with featureIds = 3 and sampleIds = 30
        Optional<SearchSessionState.Snapshot> snapshot = SearchSessionState.restore(session);

        assertTrue(snapshot.isPresent());
        assertThat(snapshot.get().getFeatureIds(), equalTo(Arrays.asList(1, 2)));
        assertThat(snapshot.get().getSampleIds(), equalTo(Arrays.asList(10, 20)));
        assertThat(snapshot.get().getQueryString(), equalTo("query-string"));
        
    }

    @Test
    public void restoreReturnsEmptyIfStateMissing() {
        HttpSession session = buildSession(new HashMap<>());

        assertFalse(SearchSessionState.restore(session).isPresent());
    }

    @Test
    public void restoreReturnsEmptyIfQueryMissing() {
        Map<String, Object> attributes = new HashMap<>();
        HttpSession session = buildSession(attributes);
        attributes.put("FRED.features", Arrays.asList(1, 2));

        assertFalse(SearchSessionState.restore(session).isPresent());
    }

    @Test
    public void restoreSupportsMissingSampleIds() {
        Map<String, Object> attributes = new HashMap<>();
        HttpSession session = buildSession(attributes);

        SearchSessionState.save(session, Arrays.asList(5, 6), null, "q");

        Optional<SearchSessionState.Snapshot> snapshot = SearchSessionState.restore(session);

        assertTrue(snapshot.isPresent());
        assertFalse(snapshot.get().hasSampleIds());
        assertThat(snapshot.get().getSampleIds(), nullValue());
    }

    private HttpSession buildSession(Map<String, Object> attributes) {
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute(anyString())).thenAnswer(invocation -> attributes.get(invocation.getArgument(0)));
        doAnswer(invocation -> {
            attributes.put(invocation.getArgument(0), invocation.getArgument(1));
            return null;
        }).when(session).setAttribute(anyString(), any());
        return session;
    }
}

