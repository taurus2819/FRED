package nz.cri.gns.fred.test.hibernate6;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.List;
import org.junit.Test;
import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.util.StageUtil;
import nz.cri.gns.fred.model.Age;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.hibernate.util.hibernate6.FredHibernate;




public class StageUtilTest {        
    
    @Test
    public void testAge() throws StorageAccessException {
        
        DAOFactory factory = FredHibernate.get().getDAOFactory();
	    //StageUtil stageUtil = new StageUtil(factory);
        
        
        StageUtil util= new StageUtil(factory);
        assertNotNull(util);
        
        Age age = util.getAge(1);
        assertNotNull(age);
        assertEquals("Modern", age.getName());
        
        

    }

    
     
}
