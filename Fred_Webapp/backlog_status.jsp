<%@page extends="nz.cri.gns.fred.FREDIPSysJspPage"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@page import="java.net.URL"
%><%@page import="java.util.Iterator"
%><%@page import="nz.cri.gns.auth.Authenticable"
%><%@page import="nz.cri.gns.fred.hibernate.util.HibernateUtil"
%><%@page import="nz.cri.gns.fred.dao.DAOFactory"
%><%@page import="nz.cri.gns.fred.model.BacklogStatus"
%><%@page import="nz.cri.gns.fred.model.FREDConstants"
%><%@page import="nz.cri.gns.fred.util.BacklogStatusUtil"
%><%@page import="nz.cri.gns.gis.ims.IMSMap"
%><%@page import="com.esri.aims.mtier.model.envelope.Envelope"
%><%!	
	public Authenticable[] getRequiredRights(HttpServletRequest request) { 
		return new Authenticable[0]; 
	}
%><%!
	public String getName(HttpServletRequest request) {
		return "FRED :: Backlog Status";
	}
%><%
	DAOFactory factory = HibernateUtil.get().getDAOFactory();
	BacklogStatusUtil bsUtil = new BacklogStatusUtil(factory);

	ExtranetTemplate et = getExtranetTemplate();
	et.setUseNavigationColumn(false);
	et.setDisplayLoadingMessage(true);

	drawTop(out, et, request, response);

	//List data
	%><table border="0">
	<tr><td><img src="images/blank.gif" width="10" height="10" /></td></tr>
	<tr><td></td><td><%

	%><p><%
	startDETable(pageContext);
	%><table border="0" width="160">
	<tr><td class="deHeading">View</td></tr>
	<tr><td class="heading"><a href="backlog_status.jsp?ID=-1">Overview</a></td></tr>
	<tr><td class="heading"><a href="backlog_status.jsp?ID=1">Northern North Island</a></td></tr>
	<tr><td class="heading"><a href="backlog_status.jsp?ID=2">Central North Island</a></td></tr>
	<tr><td class="heading"><a href="backlog_status.jsp?ID=3">Southern North Island</a></td></tr>
	<tr><td class="heading"><a href="backlog_status.jsp?ID=4">Nelson</a></td></tr>
	<tr><td class="heading"><a href="backlog_status.jsp?ID=5">Central South Island</a></td></tr>
	<tr><td class="heading"><a href="backlog_status.jsp?ID=6">Southern South Island</a></td></tr>
	</table><%
	endDETable(pageContext);
	%></p><%	
	
	%></td><td><img src="images/blank.gif" width="30" height="1" /></td><td style="text-align: left"><%
	
	int masterfileId = Integer.parseInt(request.getParameter("ID"));
	
	%><p><%
	try {
		
		URL imsService = new URL("http://maps.gns.cri.nz");
		String service = "fred_backlog";
		//URL imsService = new URL("http://" + DBUtils.getIMSServerFor(JspUtils.getInstance(application)));
		IMSMap map = new IMSMap(imsService, service, 480, 580);
		Envelope extent = new Envelope();
		switch (masterfileId) {
			case -1:
				extent.setMinX(1940000);
				extent.setMinY(5250000);
				extent.setMaxX(3050000);
				extent.setMaxY(6820000);
				break;
			case 1:
				extent.setMinX(2370000);
				extent.setMinY(6230000);
				extent.setMaxX(2850000);
				extent.setMaxY(6810000);
				break;
			case 2:
				extent.setMinX(2550000);
				extent.setMinY(5960000);
				extent.setMaxX(3030000);
				extent.setMaxY(6540000);
				break;
			case 3:
				extent.setMinX(2470000);
				extent.setMinY(5765000);
				extent.setMaxX(2950000);
				extent.setMaxY(6345000);
				break;
			case 4:
				extent.setMinX(2270000);
				extent.setMinY(5760000);
				extent.setMaxX(2750000);
				extent.setMaxY(6300000);
				break;
			case 5:
				extent.setMinX(2170000);
				extent.setMinY(5480000);
				extent.setMaxX(2650000);
				extent.setMaxY(6060000);
				break;
			case 6:
				extent.setMinX(1930000);
				extent.setMinY(5225000);
				extent.setMaxX(2410000);
				extent.setMaxY(5805000);
				break;
		}
		map.setExtent(extent);
		String imageURL = map.getURL();
		%><p><img src="<%=imageURL%>" width="480" height="580" alt="Backlog Status Map" border="0" /></p><%
	} catch (Exception e) {
		%><p>An error has occured while generating the map. Please try again</p><%
	}
	%></p><%
	
	if (masterfileId > 0) {
		%><p><%
		startDETable(pageContext);
		%><table border="0" width="480">
		<tr><td class="deHeading" colspan="5">Summary</td></tr>
		<tr><th>Map</th><th>Status&nbsp;&nbsp;</th><th>Number of Localities&nbsp;&nbsp;</th><th>Number Proccessing&nbsp;&nbsp;</th><th>Number Complete</th></tr><%
		for (Iterator i = bsUtil.getBacklogStatusInMasterfile(masterfileId).iterator(); i.hasNext();) {
			BacklogStatus bs = (BacklogStatus) i.next();
			%><tr><td class="heading"><%=bs.getMapNumber()%>&nbsp;&nbsp;</td><%
			if (bs.getStatus() != null) {
				%><td style="text-color: <%=(bs.getStatus().equals(FREDConstants.BACKLOG_PROCESSING) ? "#FF0000" : "#00FF00")%>">bs.getStatus()&nbsp;&nbsp;</td><%
			} else {
				%><td></td><%
			}
			%><td><%=bs.getLocalityCount()%></td>
			<td><%=bs.getProcessingCount()%></td>
			<td><%=bs.getCompletedCount()%></td></tr><%
		}
		
		%></table><%
		endDETable(pageContext);
		%></p><%		
		
	}
	
	%></td></tr></table><%

	
	drawBottom(out, et);
	
	try {
		HibernateUtil.get().getDAOFactory().closeSession();
	} catch (Exception e) {
	}
%>