<%@page extends="nz.cri.gns.fred.FREDIPSysJspPage"
%><%@page import="nz.cri.gns.fred.de.DataEntryForm"
%><%@page import="nz.cri.gns.fred.de.DataInputException"
%><%@page import="nz.cri.gns.fred.de.TaxonomicListException"
%><%@page import="nz.cri.gns.fred.model.Feature"
%><%@page import="nz.cri.gns.fred.model.UserFolder"
%><%@page import="nz.cri.gns.fred.model.PaleontologyListEntry"
%><%@page import="nz.cri.gns.fred.model.UnsavedListEntry"
%><%@page import="nz.cri.gns.fred.model.UnsavedTaxon"
%><%@page import="nz.cri.gns.jsp.JspUtils"
%><%@page import="nz.cri.gns.jsp.PageState"
%><%@page import="nz.cri.gns.fred.dao.DAOFactory"
%><%@page import="nz.cri.gns.fred.hibernate.util.HibernateUtil"
%><%@page import="nz.cri.gns.fred.util.FeatureUtil"
%><%@page import="nz.cri.gns.fred.util.FolderUtil"
%><%@page import="nz.cri.gns.fred.util.TaxonomicUtil"
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
					if (featID != null && featID.length() > 0) { //editing
						return DataEntryFormFactory.getLocalityDataEntryForm(Integer.parseInt(featID), Integer.parseInt(foldID), user, factory, provider);
					} else {
						return DataEntryFormFactory.getLocalityDataEntryForm(formType, user, Integer.parseInt(foldID), factory, provider);
					}
				} else if (formType.equals("Sample")) {
					String sampID = request.getParameter("SampID");
					if (sampID != null && sampID.length() > 0) { //editing
						return DataEntryFormFactory.getSampleDataEntryForm(Integer.parseInt(sampID), Integer.parseInt(foldID), user, factory, provider);
					} else {
						String featID = request.getParameter("FeatID");
			    		if (featID == null || featID.equals("")) {
			    			FeatureUtil featureUtil = new FeatureUtil(factory);
			    			FolderUtil folderUtil = new FolderUtil(factory);
			    			UserFolder folder = folderUtil.getUserFolder(Integer.parseInt(foldID), user);
			    			try {
			    				System.out.println("Feature Name = " + request.getParameter("featName"));
			    				System.out.println("Folder = " + folder);
				    			Feature feature = featureUtil.getFeatureWithName(request.getParameter("featName"), folder);
				    			featID = String.valueOf(feature.getFeatureId());
			    			} catch (Exception e) {
								throw new DataInputException("Locality Name", "Locality name not found");
			    			}
			    		}
						return DataEntryFormFactory.getSampleDataEntryForm(user, Integer.parseInt(featID), Integer.parseInt(foldID), factory, provider);
					}
				} else if (formType.equals(FREDConstants.PALEONTOLOGICAL) || formType.equals(FREDConstants.ADOPTION)) {
					String recID = request.getParameter("RecID");
					if (recID != null && !recID.equals("")) { //editing
						return DataEntryFormFactory.getRecordDataEntryForm(Integer.parseInt(recID), Integer.parseInt(foldID), user, factory, provider);
					} else {
						String sampID = request.getParameter("SampID");
						return DataEntryFormFactory.getRecordDataEntryForm(formType, user, Integer.parseInt(sampID), Integer.parseInt(foldID), factory, provider);
					}
				}
				return null;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
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
	    	if (type.equals("NewFold")) {
	    		new FolderUtil(HibernateUtil.get().getDAOFactory()).addFolder(request.getParameter("FoldName"), user);
	    		status = "Created OK";
	    		message = "";
	    	} else if (type.equals("Taxa")) {
	    		TaxonomicUtil taxaUtil = new TaxonomicUtil(HibernateUtil.get().getDAOFactory());
	    		UnsavedListEntry entry = new UnsavedListEntry();
				UnsavedTaxon taxon = new UnsavedTaxon();
				taxon.setTaxonomicGroup(taxaUtil.getTaxonomicGroup(request.getParameter("TaxaGroup")));
	    		taxon.setTaxonomicName(TaxonomicUtil.getCleanedName(request.getParameter("TaxaName")));
	    		taxon.setAuthor(request.getParameter("Author"));
	    		entry.setTaxon(taxon);
	    		entry.setTaxonomicGroup(taxaUtil.getTaxonomicGroup(request.getParameter("TaxaGroup")));
	    		entry.setTaxonomicName(request.getParameter("TaxaName"));
	    		taxaUtil.submitProvisional(user, entry);
	    		status = "Submitted OK";
	    	} else {
		    	DAOFactory factory = HibernateUtil.get().getDAOFactory();
		    	DataEntryForm dataEntryForm = getDataEntryFormImpl(request, user);
		    	if (dataEntryForm != null) {
			    	dataEntryForm.updateFromRequest(request, factory, true);
			    	String id;
			    	if (button.equals("save")) {
			    		id = String.valueOf(dataEntryForm.save(FREDConstants.DATA_ORIGIN_EXCEL));
			    		status = "Saved OK";
			    	} else {
			    		id = String.valueOf(dataEntryForm.submit(FREDConstants.DATA_ORIGIN_EXCEL));
			    		/*
			    		if (request.getParameter("FRNum") != null) {
			    			FRNumber frNum = FRNumber.parseFRNumber(request.getParameter("FRNum"), true);
			    			FolderUtils.approveLocality(id, frNum, null, user, state);
			    		} */
			    		status = "Submitted OK";
			    	}
					message = id;
		    	} else {
		    		status = "Error";
		    		message = "Not able to create data entry form";
		    	}
		    }
		} catch (InsufficientPrivelegesException e) {
			status = "AuthError";
			message = "User not authorised";
		} catch (TaxonomicListException e) {
			status = "TaxaListError";
			StringBuffer msg = new StringBuffer();
			for (PaleontologyListEntry t : e.getTaxaList())
				msg.append(t.getTaxonomicGroup().getName()).append("*").append(t.getTaxonomicName()).append("#");
			message = msg.toString();
	    } catch (DataInputException e) {
	    	status = "Error";
			String[] error = (String[])e.getError().firstElement();
			message = "Data Error: " + error[0] + " - " + error[1];
	    } catch (Exception e) {
	    	status = "Error";
			message = "Unspecified Error: " + e.toString();
			System.out.println("*** FRED Error: " + new java.util.Date() + " ***");
			e.printStackTrace();
	    }
	}

	try {
		HibernateUtil.get().getDAOFactory().closeSession();
	} catch (Exception e) {
	}
%>
<html>
<head></head>
<body>
<table><tr><td><%=status%></td><td><%=message%></td><td><%=(status.equals("AuthError")) ? "" : session.getId()%></td></tr></table>
</body>
</html>