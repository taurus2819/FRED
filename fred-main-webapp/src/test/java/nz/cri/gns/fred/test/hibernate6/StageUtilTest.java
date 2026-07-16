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
import org.junit.Ignore;




public class StageUtilTest {        
    
    @Ignore
    public void testAge() throws StorageAccessException {
        
        DAOFactory factory = FredHibernate.get().getDAOFactory();
	    //StageUtil stageUtil = new StageUtil(factory);
        
        
        StageUtil util= new StageUtil(factory);
        assertNotNull(util);
        
        Age age = util.getAge(1);
        assertNotNull(age);
        assertEquals("Modern", age.getName());
        

    }

    @Ignore
    public void testAgeByName() throws StorageAccessException {
        DAOFactory factory = FredHibernate.get().getDAOFactory();
	    
        StageUtil util= new StageUtil(factory);
        assertNotNull(util);
        
        Age age = util.getAgeByName("Modern");
        assertNotNull(age);
        assertEquals(1, (int)age.getAgeId());
    }


    @Ignore
    public void testMatchingAges() throws StorageAccessException {
        DAOFactory factory = FredHibernate.get().getDAOFactory();
	    
        StageUtil util= new StageUtil(factory);
        assertNotNull(util);

        List<Age> ageList= util.getMatchingAges("Ma", 20);
        assertNotNull(ageList);
        assertEquals(9, ageList.size());
        
        
    }

    @Ignore
    public void testCreateAge() throws StorageAccessException {
        DAOFactory factory = FredHibernate.get().getDAOFactory();
        
        StageUtil util= new StageUtil(factory);
        assertNotNull(util);

        Age age = util.createAge();
        assertNotNull(age);
        age.setName("TestAge");
        age.setCode("TestCode");
        
        age.setPeriod("TestPeriod");
        age.setBaseAge(0.0);
        age.setTopAge(0.0);
        age.setComments("TestComments");
        age.setObsoleteFlag(0);
        age.setDuplicateFlag(0);
        
        util.saveOrUpdate(age);


    }

     
}
