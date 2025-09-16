package nz.cri.gns.fred.servlet;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import nz.cri.gns.fred.FREDHibernateServlet;

import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.hibernate.util.hibernate6.FredHibernate;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.FrNumber;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.util.FREDUtil;
import nz.cri.gns.fred.util.FeatureUtil;

public class LocalityServlet extends FREDHibernateServlet {

    private static final long serialVersionUID = 20060714L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        redirect(request.getParameter("frNum"), "", request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String url = request.getRequestURI();
        String[] bits = url.split("/");
        String frNum = bits[bits.length - 2] + "/" + bits[bits.length - 1];
        redirect(frNum, "../../", request, response);
    }

    private void redirect(String frNum, String baseUrl, HttpServletRequest request, HttpServletResponse response) throws IOException {
        DAOFactory factory = null;
        factory = FredHibernate.get().getDAOFactory();
        List<FrNumber> nums = null;
        HashSet<Feature> features = new HashSet<Feature>();
        try {
            nums = new FeatureUtil(factory).getFrNumbersByString(frNum);
        } catch (Exception e) {
        }

        try {
            if (nums != null && nums.size() > 0) {
                for (FrNumber num : nums) {
                    if (!FREDUtil.isEmpty(num.getFeatures())) {
                        features.addAll(num.getFeatures());
                    }
                    for (Sample sample : num.getSamples()) {
                        features.add(sample.getFeature());
                    }
                    if (!FREDUtil.isEmpty(num.getFeaturesByYard())) {
                        features.addAll(num.getFeaturesByYard());
                    }
                    for (Sample sample : num.getSamplesByYard()) {
                        features.add(sample.getFeature());
                    }
                }
            }
            List<Feature> featureList = FREDUtil.getSortedList(features);
            if (featureList.size() == 1) {
                response.sendRedirect(baseUrl + "detail.jsp?FeatID=" + featureList.get(0).getFeatureId());
            } else if (featureList.size() == 0) {
                response.sendRedirect(baseUrl + "detail.jsp?FeatID=-1");
            } else {
                request.getSession().setAttribute("FRED.features", featureList);
                request.getSession().setAttribute("FRED.samples", null);
                request.getSession().setAttribute("FRED.queryString", frNum);
                response.sendRedirect(baseUrl + "result_list.jsp?Page=1");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(baseUrl + "detail.jsp?FeatID=-1");
        }
    }

}
