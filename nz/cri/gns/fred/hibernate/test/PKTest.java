package nz.cri.gns.fred.hibernate.test;

import nz.cri.gns.fred.hibernate.FolderUserPK;
import nz.cri.gns.fred.hibernate.SentToPK;
import junit.framework.TestCase;

public class PKTest extends TestCase {

    public void testSentToPK() {
        SentToPK pk = new SentToPK();
        pk.setFossilGroupId(new Integer(225));
        pk.setSampleId(new Integer(1523));
        
        SentToPK pk1 = new SentToPK();
        pk1.setFossilGroupId(new Integer(225));
        pk1.setSampleId(new Integer(1523));
        
        assertEquals(pk, pk1);
    }

    public void testFolderUserPK() {
        FolderUserPK pk = new FolderUserPK();
        pk.setFolderId(new Integer(225));
        pk.setUserId(new Integer(1523));
        
        FolderUserPK pk1 = new FolderUserPK();
        pk1.setFolderId(new Integer(225));
        pk1.setUserId(new Integer(1523));
        
        assertEquals(pk, pk1);
    }
}
