package nz.cri.gns.fred.hibernate.test;

import nz.cri.gns.fred.hibernate.SentToPK;
import junit.framework.TestCase;

public class SentToPKTest extends TestCase {

    public void testEquals() {
        SentToPK pk = new SentToPK();
        pk.setFossilGroupId(new Integer(225));
        pk.setSampleId(new Integer(1523));
        
        SentToPK pk1 = new SentToPK();
        pk1.setFossilGroupId(new Integer(225));
        pk1.setSampleId(new Integer(1523));
        
        assertEquals(pk, pk1);
    }
}
