/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.cri.gns.fred.test.site.util;

import nz.cri.gns.fred.site.util.OrigCoordInfoUtil;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sitikond
 */
public class OrigCoordInfoUtilTest {
    
    public OrigCoordInfoUtilTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getJson method, of class OrigCoordInfoUtil.
     */
//    @Test
//    public void testGetJson() throws Exception {
//        System.out.println("getJson");
//        int system_id = 0;
//        String origCoord = "";
//        OrigCoordInfoUtil.OrigCoord expResult = null;
//        OrigCoordInfoUtil.OrigCoord result = OrigCoordInfoUtil.getJson(system_id, origCoord);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
    
    /**
     * Test of getJson method, of class OrigCoordInfoUtil.
     */
    @Test
    public void testGetJson1() throws Exception {
        System.out.println("getJson1");
        int system_id = 16;
        String origCoord = "C40|8612|5642";
        String expResult = "OrigCoord{epsg=27200, format=gridref, gridref=C40/86125642, latitude=null, longitude=null, easting=null,  northing=null}";
        OrigCoordInfoUtil.OrigCoord result = OrigCoordInfoUtil.getJson(system_id, origCoord);
        System.out.println(result.toString());
        assertEquals(expResult, result.toString());
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }
    
    /**
     * Test of getJson method, of class OrigCoordInfoUtil.
     */
    @Test
    public void testGetJson2() throws Exception {
        System.out.println("getJson2");
        int system_id = 0;
        String origCoord = "";
        String expResult = "OrigCoord{epsg=4167, format=DD, gridref=null, latitude=-39.08102037777, longitude=174.52428614166, easting=null,  northing=null}";
        OrigCoordInfoUtil.OrigCoord result = OrigCoordInfoUtil.getJson(28, "-39.08102037777|174.52428614166");
        System.out.println(result.toString());
        assertEquals(expResult, result.toString());
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }
    
    /**
     * Test of getJson method, of class OrigCoordInfoUtil.
     */
    @Test
    public void testGetJson3() throws Exception {
        System.out.println("getJson3");
        int system_id = 0;
        String origCoord = "";
        String expResult = "OrigCoord{epsg=27200, format=EN, gridref=null, latitude=null, longitude=null, easting=2696700.0,  northing=5953800.0}";
        OrigCoordInfoUtil.OrigCoord result = OrigCoordInfoUtil.getJson(38, "2696700|5953800");
        System.out.println(result.toString());
        assertEquals(expResult, result.toString());
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }
    
    /**
     * Tesing the OrigCoordInfoUtil.parseCoordinate
     */
    @Test
    public void parsesDecimalDegreees(){
        //L - latitude, G - longitude
        assertEquals("-39.08102037777", OrigCoordInfoUtil.parseCoordinate("-39.08102037777", 'L'));
        assertEquals("174.52428614166", OrigCoordInfoUtil.parseCoordinate("174.52428614166", 'G'));
    }
    
    @Test
    public void parsingDegreeDecimalMinutes(){
        assertEquals("-39.081020377767", OrigCoordInfoUtil.parseCoordinate("39 4.861222666 S", 'L'));
        assertEquals("174.524286141667", OrigCoordInfoUtil.parseCoordinate("174 31.4571685' E", 'G'));
    }
    
    @Test
    public void parseDegreeMinutesNDecimalSecs(){
        assertEquals("-39.081020377778", OrigCoordInfoUtil.parseCoordinate("39 4 51.67336 S", 'L'));
        assertEquals("174.524286141667", OrigCoordInfoUtil.parseCoordinate("174 31 27.43011 E", 'G'));
    }
    
    @Test
    public void safeguardCoordinateFormatsWhenBuildingOrigCoord() throws Exception{
        OrigCoordInfoUtil.OrigCoord result = OrigCoordInfoUtil.getJson(28, "39 4 51.67336 S|174 31.4571685 E");
        
        assertEquals("-39.081020377778", result.getLatitude());
        assertEquals("174.524286141667", result.getLongitude());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void rejectsMinutesOutsideValidRange() {
        OrigCoordInfoUtil.parseCoordinate("39 60 S", 'L');
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void rejectWrongHemisphere() {
        OrigCoordInfoUtil.parseCoordinate("39 4.5 E", 'L');
    }
}
