<%@page extends="nz.cri.gns.fred.FREDIPSysJspPage"
%><%@page import="nz.cri.gns.fred.de.DataEntryForm"
%><%@page import="nz.cri.gns.fred.de.DataInputException"
%><%@page import="nz.cri.gns.fred.model.Feature"
%><%@page import="nz.cri.gns.jsp.JspUtils"
%><%@page import="nz.cri.gns.jsp.PageState"
%><%@page import="nz.cri.gns.fred.dao.DAOFactory"
%><%@page import="nz.cri.gns.fred.hibernate.util.HibernateUtil"
%><%@page import="nz.cri.gns.fred.util.FeatureUtil"
%><%@page import="nz.cri.gns.auth.User"
%><%@page import="nz.cri.gns.auth.Authenticable"
%><%@page import="nz.cri.gns.auth.InsufficientPrivelegesException"
%><%@page import="nz.cri.gns.fred.de.DataEntryFormFactory"
%><%@page import="nz.cri.gns.fred.model.FREDConstants"
%><%@page import="nz.cri.gns.fred.website.ContentProvider"
%><%@page import="java.io.File"
%><%!	
	public Authenticable[] getRequiredRights(HttpServletRequest request) {
		return new Authenticable[0];
	}
%><%!	
	private DataEntryForm getDataEntryFormImpl(HttpServletRequest request, User user) {
		DAOFactory factory = HibernateUtil.get().getDAOFactory();
			try {
				String formType = request.getParameter("Type");
				String foldID = request.getParameter("FoldID");
				ContentProvider provider = new ContentProvider(new File(request.getSession().getServletContext().getRealPath("/content")));
				if (formType.equals(FREDConstants.OUTCROP) || formType.equals(FREDConstants.DRILLHOLE) || formType.equals(FREDConstants.VERTICAL_SECTION)) {
					String featID = request.getParameter("FeatID");
					if (featID != null) { //editing
						return DataEntryFormFactory.getLocalityDataEntryForm(Integer.parseInt(featID), Integer.parseInt(foldID), user, factory, provider);
					} else {
						return DataEntryFormFactory.getLocalityDataEntryForm(formType, user, Integer.parseInt(foldID), factory, provider);
					}
				} else if (formType.equals("Sample")) {
					String sampID = request.getParameter("SampID");
					if (sampID != null) { //editing
						return DataEntryFormFactory.getSampleDataEntryForm(Integer.parseInt(sampID), Integer.parseInt(foldID), user, factory, provider);
					} else {
						String featID = request.getParameter("FeatID");
			    		if (featID == null || featID.equals("")) {
			    			FeatureUtil featureUtil = new FeatureUtil(factory);
			    			try {
				    			Feature feature = featureUtil.getFeatureWithIdentifyingName(request.getParameter("featName"));
				    			featID = String.valueOf(feature.getFeatureId());
			    			} catch (Exception e) {
								throw new DataInputException("Locality Name", "Locality name not found");
			    			}
			    		}
						return DataEntryFormFactory.getSampleDataEntryForm(user, Integer.parseInt(featID), Integer.parseInt(foldID), factory, provider);
					}
				} else {
					String recID = request.getParameter("RecID");
					if (recID != null) { //editing
						return DataEntryFormFactory.getRecordDataEntryForm(Integer.parseInt(recID), Integer.parseInt(foldID), user, factory, provider);
					} else {
						String sampID = request.getParameter("SampID");
						return DataEntryFormFactory.getRecordDataEntryForm(formType, user, Integer.parseInt(sampID), Integer.parseInt(foldID), factory, provider);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				return null; //!
			}
		}		
%><%
	PageState state = new PageState(request, response, getServletContext());
	String status = "";
	String message = "";

    String userName = request.getParameter("user");
    String password = request.getParameter("pass");
    String button = request.getParameter("button");
    String type = request.getParameter("Type");
    if (button == null || button.equals(""))
    	button = "save";

	User user = null;
	try {
		user = new User(userName, password, JspUtils.createDatabaseConnection(state.getSession(), "nz.cri.gns.ip.connection", "ip", state.getContext()));
	} catch (Exception e) {
		status = "AuthError";
		message = "Invalid username/password";
	}
	
	if (type != null && !type.equals("") && user != null) {
	    try {
	    	DAOFactory factory = HibernateUtil.get().getDAOFactory();
	    	DataEntryForm dataEntryForm = getDataEntryFormImpl(request, user);
	    	dataEntryForm.updateFromRequest(request, factory);
	    	String id;
	    	if (button.equals("save")) {
	    		id = String.valueOf(dataEntryForm.save());
	    		status = "Saved OK";
	    	} else {
	    		id = String.valueOf(dataEntryForm.submit());
	    		/*
	    		if (request.getParameter("FRNum") != null) {
	    			FRNumber frNum = FRNumber.parseFRNumber(request.getParameter("FRNum"));
	    			FolderUtils.approveLocality(id, frNum, null, user, state);
	    		} */
	    		status = "Submitted OK";
	    	}
			message = id;
		} catch (InsufficientPrivelegesException e) {
			status = "AuthError";
			message = "User not authorised";
	    } catch (DataInputException e) {
	    	status = "Error";
			String[] error = (String[])e.getError().firstElement();
			message = "Data Error: " + error[0] + " - " + error[1];
	    } catch (Exception e) {
	    	status = "Error";
			message = "Unspecified Error: " + e.toString();
			e.printStackTrace();
	    }
	}

%>
<html>
<head></head>
<body>
<table><tr><td><%=status%></td><td><%=message%></td></tr></table>
</body>
</html>