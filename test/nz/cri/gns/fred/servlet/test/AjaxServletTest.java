package nz.cri.gns.fred.servlet.test;

import nz.cri.gns.fred.servlet.AJAXServlet;
import junit.framework.TestCase;

public class AjaxServletTest extends TestCase {

	public void testEnumerations() {
		assertNotNull(AJAXServlet.Action.valueOf("Confirm"));
		assertNotNull(AJAXServlet.Action.valueOf("List"));
		assertNotNull(AJAXServlet.Action.valueOf("Add"));
	}
}
