package nz.cri.gns.fred.data.test;

import junit.framework.TestCase;
import nz.cri.gns.fred.data.FRNumber;
import nz.cri.gns.fred.dataentry.DataInputException;

public class FRNumberTest extends TestCase {

	public void testParse() throws DataInputException {
		FRNumber frNum = FRNumber.parseFRNumber("I44/f1");
		assertEquals("I44/f0001", frNum.getFRNumber());
		frNum = FRNumber.parseFRNumber("I44/f1A");
		assertEquals("I44/f0001A", frNum.getFRNumber());
	}

}
