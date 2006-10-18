package nz.cri.gns.fred.servlet;

import java.io.IOException;
import java.util.HashSet;
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
import nz.cri.gns.fred.util.FREDUtil;
import nz.cri.gns.fred.util.FeatureUtil;

public class LocalityServlet extends HttpServlet {

	private static final long serialVersionUID = 20060714L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		redirect(request.getParameter("frNum"), "", request, response);
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String url = request.getRequestURI();
		String[] bits = url.split("/");
		String frNum = bits[bits.length-2] +  "/" + bits[bits.length-1];
		redirect(frNum, "../../", request, response);
	}
	
	private void redirect(String frNum, String baseUrl, HttpServletRequest request, HttpServletResponse response) throws IOException {
		DAOFactory factory = null;
		factory = HibernateUtil.get().getDAOFactory();
		FrNumber num = null;
		FrNumber yardNum = null;
		HashSet<Feature> features = new HashSet<Feature>();
		try {
			num = new FeatureUtil(factory).parseFrNumber(frNum, false);
		} catch (Exception e) {}
		try {
			yardNum = new FeatureUtil(factory).parseYardFrNumber(frNum, false);
		} catch (Exception e) {
		}
		
		try {
			System.out.println("Adding features");
			if (num != null) {
				if (!FREDUtil.isEmpty(num.getFeatures()))
					features.addAll(num.getFeatures());
				System.out.println("Count after metric features = " + features.size());
				for (Sample sample : num.getSamples())
					features.add(sample.getFeature());
				System.out.println("Count after metric samples = " + features.size());
			}
			if (yardNum != null) {
				if (!FREDUtil.isEmpty(yardNum.getFeaturesByYard()))
					features.addAll(yardNum.getFeatures());
				System.out.println("Count after yard features = " + features.size());
				for (Sample sample : yardNum.getSamplesByYard())
					features.add(sample.getFeature());
				System.out.println("Count after yard samples = " + features.size());
			}
			System.out.println("Total feature count = " + features.size());
			if (features.size() == 1) {
				response.sendRedirect(baseUrl + "detail.jsp?FeatID=" + features.iterator().next().getFeatureId());
			} else if (features.size() == 0) {
				response.sendRedirect(baseUrl + "detail.jsp?FeatID=-1");
			} else {
				request.getSession().setAttribute("FRED.features", features);
				request.getSession().setAttribute("FRED.queryString", frNum);
				response.sendRedirect(baseUrl + "result_list.jsp?Page=1");
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.sendRedirect(baseUrl + "detail.jsp?FeatID=-1");
		} finally {
			if (factory != null) try {
				factory.closeSession();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
