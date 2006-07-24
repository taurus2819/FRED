package nz.cri.gns.fred.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.hibernate.util.HibernateUtil;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.FrNumber;
import nz.cri.gns.fred.util.FeatureUtil;

public class LocalityServlet extends HttpServlet {

	private static final long serialVersionUID = 20060714L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String url = request.getRequestURI();
		
		String[] bits = url.split("/");
		
		String frNum = bits[bits.length-2] +  "/" + bits[bits.length-1];
		
		DAOFactory factory = null;
		try {
			factory = HibernateUtil.get().getDAOFactory();
			FrNumber num = new FeatureUtil(factory).parseFrNumber(frNum, false);
			if (num != null) {
				Feature feature = num.getFeatures().iterator().next();
				response.sendRedirect("../../detail.jsp?FeatID=" + feature.getFeatureId());
			}
		} catch (Exception e) {
			//Don't log this by default or we'll get a record of every clown's mistake
			if (false)
				e.printStackTrace();
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		} finally {
			if (factory != null) try {
				factory.closeSession();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
