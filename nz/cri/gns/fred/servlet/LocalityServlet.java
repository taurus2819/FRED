package nz.cri.gns.fred.servlet;

import java.io.IOException;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.hibernate.util.HibernateUtil;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.FrNumber;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.util.FeatureUtil;

public class LocalityServlet extends HttpServlet {

	private static final long serialVersionUID = 20060714L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		redirect(request.getParameter("frNum"), "detail.jsp", request, response);
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String url = request.getRequestURI();
		String[] bits = url.split("/");
		String frNum = bits[bits.length-2] +  "/" + bits[bits.length-1];
		redirect(frNum, "../../detail.jsp", request, response);
	}
	
	private void redirect(String frNum, String url, HttpServletRequest request, HttpServletResponse response) throws IOException {
		DAOFactory factory = null;
		factory = HibernateUtil.get().getDAOFactory();
		FrNumber num = null;
		FrNumber yardNum = null;
		Vector<Feature> features = new Vector<Feature>();
		try {
			num = new FeatureUtil(factory).parseFrNumber(frNum, false);
		} catch (Exception e) {}
		try {
			yardNum = new FeatureUtil(factory).parseYardFrNumber(frNum, false);
		} catch (Exception e) {}
		
		try {
			features.addAll(num.getFeatures());
			features.addAll(yardNum.getFeatures());
			for (Sample sample : num.getSamples())
				features.add(sample.getFeature());
			for (Sample sample : yardNum.getSamples())
				features.add(sample.getFeature());
			if (features.size() == 1) {
				response.sendRedirect(url + "?FeatID=" + features.iterator().next().getFeatureId());
			} else if (features.size() == 0) {
				response.sendRedirect(url + "?FeatID=-1");
			} else {
				request.getSession().setAttribute("FRED.features", features);
				request.getSession().setAttribute("FRED.queryString", frNum);
				response.sendRedirect(url + "../result_list.jsp?Page=1");
			}
		} catch (Exception e) {
			response.sendRedirect(url + "?FeatID=-1");
		} finally {
			if (factory != null) try {
				factory.closeSession();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
