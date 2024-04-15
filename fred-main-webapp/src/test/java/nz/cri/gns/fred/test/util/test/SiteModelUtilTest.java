package nz.cri.gns.fred.util.test;

import static junit.framework.Assert.*;
import nz.cri.gns.fred.util.SiteModelUtil;
import org.junit.Test;

public class SiteModelUtilTest {

    @Test
    public void testGetMainLandMasterfile() {
        // sanity checks, not testing edge cases
        assertEquals(SiteModelUtil.MASTERFILE_NTH_NI, SiteModelUtil.getMainLandMasterfile(-38.316323, 175.700239, false));
        assertEquals(SiteModelUtil.MASTERFILE_CEN_NI, SiteModelUtil.getMainLandMasterfile(-39.57814594093618, 176.57848983373788, false));
        assertEquals(SiteModelUtil.MASTERFILE_STH_NI, SiteModelUtil.getMainLandMasterfile(-41.296639102347555, 174.77742963346662, false));
        assertEquals(SiteModelUtil.MASTERFILE_NELSON, SiteModelUtil.getMainLandMasterfile(-41.36092465784367, 173.20595471407788, false));
        assertEquals(SiteModelUtil.MASTERFILE_CEN_SI, SiteModelUtil.getMainLandMasterfile(-43.524855592069095, 172.48085459229347, false));
        assertEquals(SiteModelUtil.MASTERFILE_STH_SI, SiteModelUtil.getMainLandMasterfile(-46.446185701164524, 168.53024147704588, false));
        assertEquals(SiteModelUtil.MASTERFILE_OFFSHORE, SiteModelUtil.getMainLandMasterfile(-49.08839853321715, 167.98943105820982, false));
    }
}
