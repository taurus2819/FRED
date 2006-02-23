package nz.cri.gns.fred.de.test;

import java.io.File;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.sql.SQLException;

import junit.framework.TestCase;
import nz.cri.gns.auth.InsufficientPrivelegesException;
import nz.cri.gns.auth.InvalidCredentialsException;
import nz.cri.gns.auth.User;
import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.FREDUtils;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.de.DataEntryForm;
import nz.cri.gns.fred.de.DataEntryFormFactory;
import nz.cri.gns.fred.de.DataInputException;
import nz.cri.gns.fred.de.TaxonomicListException;
import nz.cri.gns.fred.hibernate.util.HibernateUtil;
import nz.cri.gns.fred.model.FREDConstants;
import nz.cri.gns.fred.website.ContentProvider;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.test.TestingHttpServletRequest;
import nz.cri.gns.test.TestingPageState;

public class SampleTest extends TestCase {

	public void testSample() throws NotBoundException, IOException, InsufficientPrivelegesException, DataInputException, TaxonomicListException, SQLException, InvalidCredentialsException, StorageAccessException {
		TestingPageState state = new TestingPageState();
		DBConnection ipConn = FREDUtils.getIPConnection(state);
		User user = new User("test", "test", ipConn);
		DAOFactory factory = HibernateUtil.get().getDAOFactory();
		ContentProvider provider = new ContentProvider(new File(state.request.getSession().getServletContext().getRealPath("/content")));
		DataEntryForm form = DataEntryFormFactory.getSampleDataEntryForm(user, 1651, 13, factory, provider);

		TestingHttpServletRequest request = (TestingHttpServletRequest) state.getRequest();
		request.setParameter("KnwStageStart", "5");
		form.updateFromRequest(request, factory, false);
		form.save(FREDConstants.DATA_ORIGIN_ONLINE);
	}
	
}
