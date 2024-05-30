
package nz.cri.gns.fred.query;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.junit.Test;
import static org.junit.Assert.*;

public class FREDQueryTest {

    FREDQuery subject;

    Pattern siteApiPattern = Pattern.compile("([A-Z0-9_]+)=(?<ids>[0-9\\s]+)");
    Function<String, List<Integer>> sitesBySpatialFilter = (siteQuery) -> {
        Matcher m = siteApiPattern.matcher(siteQuery);
        if (m.matches()) {
            return Arrays.stream(m.group("ids").split("\\s+")).
                    map(id -> Integer.valueOf(id))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    };

    public FREDQueryTest() {
        subject = new FREDQuery(sitesBySpatialFilter, 2);
    }



    @Test
    public void rewriteSiteApiQueryAndLogic() throws Exception {
        FREDQuery.RewrittenQuery rewritten = subject.rewriteSiteApiQuery("select * where SITE_API.X = '1 2'", true);
        assertEquals("select * where 1=1", rewritten.query);
        assertEquals(Sets.newHashSet(1, 2), rewritten.allowedSites.get());

        rewritten = subject.rewriteSiteApiQuery("select * where SITE_API.X = '1 2' and SITE_API.Y = '2 4 5'", true);
        assertEquals("select * where 1=1 and 1=1", rewritten.query);
        assertEquals(Sets.newHashSet(2), rewritten.allowedSites.get());
    }

    @Test
    public void rewriteSiteApiQuery() throws Exception {
        FREDQuery.RewrittenQuery rewritten = subject.rewriteSiteApiQuery("select * where SITE_API.X = '1 2'", false);
        assertEquals("select * where (s.feature.siteId IN (1,2))", rewritten.query);
        assertTrue(rewritten.allowedSites.isEmpty());

        rewritten = subject.rewriteSiteApiQuery("select * where SITE_API.X = '1 2' or SITE_API.Y = '2 4 5'", false);
        assertEquals("select * where (s.feature.siteId IN (1,2)) or (s.feature.siteId IN (2,4) or s.feature.siteId IN (5))", rewritten.query);
        assertTrue(rewritten.allowedSites.isEmpty());
    }

    @Test
    public void andSiteListsTest() {
        assertEquals(Sets.newHashSet(3), subject.andSiteLists(
                Sets.newHashSet(3, 5, 9),
                Sets.newHashSet(1, 3, 4)
        ));

        assertEquals(Sets.newHashSet(3, 9), subject.andSiteLists(
                Sets.newHashSet(3, 5, 9),
                Sets.newHashSet(1, 3, 4, 9)
        ));

        // no intersection
        assertEquals(Sets.newHashSet(), subject.andSiteLists(
                Sets.newHashSet(5, 9),
                Sets.newHashSet(1, 3, 4)
        ));
    }

}
