package nz.cri.gns.fred.hibernate.test;

import nz.cri.gns.fred.hibernate.FolderUserPK;
import junit.framework.TestCase;

public class PKTest extends TestCase {

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
