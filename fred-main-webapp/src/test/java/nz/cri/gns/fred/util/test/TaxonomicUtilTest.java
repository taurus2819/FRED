package nz.cri.gns.fred.util.test;

import static junit.framework.Assert.*;
import nz.cri.gns.fred.util.TaxonomicUtil;
import org.junit.Test;

public class TaxonomicUtilTest {

    @Test
    public void normaliseTaxonomicName() throws Exception {

        String[][] testCases = {
            {"no change to normal word",
                "word",
                "word"},
            {"straight replacements",
                "a 'b' 'c'",
                "a \"b\"  <c>"},
            {"group is deleted",
                "name",
                "group name"},
            {"spp.",
                "name",
                "name spp."},
            {"n. spp. indet.",
                "name",
                "name? n. spp. indet."},
            {"n.spp.indet no spaces",
                "name",
                "name n.spp.indet."},
            {"n. indet.",
                "name n.", // TODO is this expected?
                "name n. indet."},
            {"indet.",
                "name",
                "name indet."},
            {"name? n. spp. indet. aff. ex gr.",
                "name",
                "name? n. spp. indet. aff. ex gr."},
            {"two preserved words",
                "name name2",
                "name? n. spp. indet. name2 aff. ex gr. s.s."}};

        for (String[] testCase : testCases) {
            assertEquals(testCase[0], testCase[1], TaxonomicUtil.normaliseTaxonomicName(testCase[2]));
        }

    }

}
