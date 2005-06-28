<%@page	extends="nz.cri.gns.fred.FREDIPSysJspPage"
		import="nz.cri.gns.fred.*, nz.cri.gns.fred.data.*, nz.cri.gns.fred.dataentry.*, nz.cri.gns.jsp.*, nz.cri.gns.util.map.*, nz.cri.gns.db.*, nz.cri.gns.intranet.*, nz.cri.gns.db.site.*, java.sql.*, java.text.*, java.net.*, nz.cri.gns.auth.*, java.lang.*, java.util.*"
%><%!	public Authenticable[] getRequiredRights(HttpServletRequest request) { return new Authenticable[0]; }
%><%
	PageState state = new PageState(request, response, getServletContext());
	String status = "";
	String message = "";

    String userName = request.getParameter("user");
    String password = request.getParameter("pass");
    String foldID = request.getParameter("folderID");
    String button = request.getParameter("button");
    String formType = request.getParameter("form_type");
    if (button == null || button.equals(""))
    	button = "save";

	User user = null;
	try {
		user = new User(userName, password, JspUtils.createDatabaseConnection(state.getSession(), "nz.cri.gns.ip.connection", "ip", state.getContext()));
	} catch (Exception e) {
		status = "AuthError";
		message = "Invalid username/password";
	}
	
	if (formType != null && user != null) {
		DataEntryForm dataEntyForm;
	    try {
	    	if (formType.equals("Loc")) {
	    		String locType = request.getParameter("loc_type");
	    		dataEntryForm = DataEntryFormFactory.getLocalityDataEntryForm(locType, user, Integer.parseInt(foldID), state);
	    	}

			dataEntryForm.setTempField(DataEntryForm.FEATURE_NAME, request.getParameter("FeatName"));
			dataEntryForm.setTempField(DataEntryForm.REGISTRATION_AREA, request.getParameter("RegAreaID"));
			dataEntryForm.setTempField(DataEntryForm.WORKING_COMMENTS, request.getParameter("WorkComm"));
		/*	dataEntryForm.setTempField(DataEntryForm.SECURITY_TYPE, request.getParameter("SecType"));
			dataEntryForm.setTempField(DataEntryForm.GRID_REF, request.getParameter("GridRef"));
			dataEntryForm.setTempField(DataEntryForm.METHOD, request.getParameter("LocMethodID"));
			dataEntryForm.setTempField(DataEntryForm.ACCURACY, request.getParameter("Accuracy"));
			dataEntryForm.setTempField(DataEntryForm.LOCALITY_DESC, request.getParameter("Loc"));
			dataEntryForm.setTempField(DataEntryForm.RECOLLECTION, request.getParameter("Recoll"));
			dataEntryForm.setTempField(DataEntryForm.OPERATING_COMPANY, request.getParameter("Person"));
			dataEntryForm.setTempField(DataEntryForm.START_DATE, request.getParameter("StartDate"));
			dataEntryForm.setTempField(DataEntryForm.COMPLETION_DATE, request.getParameter("FinishDate"));
			dataEntryForm.setTempField(DataEntryForm.LICENCE_AREA, request.getParameter("LicArea"));
			dataEntryForm.setTempField(DataEntryForm.DATUM_TYPE, request.getParameter("DatumType"));
			dataEntryForm.setTempField(DataEntryForm.DATUM_ELEVATION, request.getParameter("DatumEl"));
			dataEntryForm.setTempField(DataEntryForm.KICK_OFF_DEPTH, request.getParameter("StartDepth"));
			dataEntryForm.setTempField(DataEntryForm.TERMINATION_DEPTH, request.getParameter("FinishDepth"));
*/
			dataEntryForm.setFieldsFromTemp();
			int id;
	    	if (button.equals("save")) {
	    		id = dataEntryForm.save();
	    	} else {
	    		id = dataEntryForm.submit();
	    	}
			status = "Loaded OK";
			message = String.valueOf(id);
		} catch (InvalidCredentialsException e) {
			status = "AuthError";
			message = "User not authorised";
	    } catch (BadDataException e) {
	    	status = "Error";
			message = "Data Error: " + e.getMessage();
	    } catch (Exception e) {
	    	status = "Error";
			message = "Unspecified Error: " + e.toString();
	    }
	}

%>
<html>
<head></head>
<body>
<table><tr><td><%=status%></td><td><%=message%></td></tr></table>
</body>
</html>