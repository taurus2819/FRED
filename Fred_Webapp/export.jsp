<%@page	extends="nz.cri.gns.jsp.FREDIPSysJspPage"
		import="nz.cri.gns.jsp.*, nz.cri.gns.db.*, java.sql.*, java.text.*, java.net.*, nz.cri.gns.auth.*, java.lang.*"
%><%!	public Authenticable[] getRequiredRights(HttpServletRequest request) { return new Authenticable[0]; }
%><%
	nz.cri.gns.intranet.DBConnection connection = JspUtils.createDatabaseConnection(session, CONNECTION, DB_NAME, application);
	Statement statement = connection.statement;
	ResultSet rs;
	String whereSQL, type;

	if (request.getParameter("WhereSQL") != null && request.getParameter("Type") != null) {
		whereSQL = request.getParameter("WhereSQL");
		type = request.getParameter("Type");

		if (type.equals("Loc")) {

			response.setHeader("Content-Disposition", "filename=\"FRED_Locality_download.txt\"");
			response.setContentType("application/x-octet-stream");

			out.println("FRNum\tLocality\n");
			rs = statement.executeQuery("SELECT DISTINCT FR_Number, Locality FROM Sample_View WHERE " + whereSQL);
			while (rs.next()) {
				out.println(rs.getString(1) + "\t" + rs.getString(2) + "\n");
			}
		}

		if (type.equals("Smp")) {

			response.setHeader("Content-Disposition", "filename=\"FRED_SampProp_download.txt\"");
			response.setContentType("application/x-octet-stream");

			out.println("FRNum\tCollection_Date\n");
			rs = statement.executeQuery("SELECT DISTINCT Sample_Name, Collection_Date FROM Sample_Property_View WHERE " + whereSQL);
			while (rs.next()) {
				out.println(rs.getString(1) + "\t" + rs.getString(2) + "\n");
			}
		}
		
	}
%>
