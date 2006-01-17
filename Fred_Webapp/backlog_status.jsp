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
		return "FRED :: Backlog Processing Status";
	}
%><%
	DAOFactory factory = HibernateUtil.get().getDAOFactory();
	BacklogStatusUtil bsUtil = new BacklogStatusUtil(factory);

	ExtranetTemplate et = getExtranetTemplate();
	et.setUseNavigationColumn(false);
	//et.setDisplayLoadingMessage(true);

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

	%><p><%
	startDETable(pageContext);
	%><table border="0" width="160">
	<tr><td class="deHeading" colspan="3">Legend</td></tr>
	<tr><td>&nbsp;</td></tr>
	<tr><td style="background-color: #FF0000">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;</td><td class="heading">Processing</td></tr>
	<tr><td style="background-color: #00FF00"></td><td></td><td class="heading">Complete</td></tr>
	<tr><td style="background-color: #DDDDDD"></td><td></td><td class="heading">No locality</td></tr>
	</table><%
	endDETable(pageContext);
	%></p><%
	
	%></td><td><img src="images/blank.gif" width="30" height="1" /></td><td style="text-align: left"><%
	
	int masterfileId = -1;
	try {
		masterfileId = Integer.parseInt(request.getParameter("ID"));
	} catch (Exception e) {	}
	
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
				map.setLayerVisible(3, true);
				extent.setMinX(2370000);
				extent.setMinY(6230000);
				extent.setMaxX(2850000);
				extent.setMaxY(6810000);
				break;
			case 2:
				map.setLayerVisible(4, true);
				extent.setMinX(2550000);
				extent.setMinY(5960000);
				extent.setMaxX(3030000);
				extent.setMaxY(6540000);
				break;
			case 3:
				map.setLayerVisible(5, true);
				extent.setMinX(2470000);
				extent.setMinY(5765000);
				extent.setMaxX(2950000);
				extent.setMaxY(6345000);
				break;
			case 4:
				map.setLayerVisible(6, true);
				extent.setMinX(2270000);
				extent.setMinY(5760000);
				extent.setMaxX(2750000);
				extent.setMaxY(6300000);
				break;
			case 5:
				map.setLayerVisible(7, true);
				extent.setMinX(2170000);
				extent.setMinY(5480000);
				extent.setMaxX(2650000);
				extent.setMaxY(6060000);
				break;
			case 6:
				map.setLayerVisible(8, true);
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
		%><table border="0" width="460">
		<tr><td class="deHeading" colspan="5">Summary by Map Sheet</td></tr>
		<tr><th>Map</th><th>Status&nbsp;&nbsp;</th><th>Localities&nbsp;&nbsp;</th><th colspan="2">Percent Complete</th></tr><%
		for (Iterator i = bsUtil.getBacklogStatusInMasterfile(masterfileId).iterator(); i.hasNext();) {
			BacklogStatus bs = (BacklogStatus) i.next();
			%><tr><td class="heading"><%=bs.getMapNumber()%>&nbsp;&nbsp;</td><%
			if (bs.getStatus() != null) {
				String statusColour = "#FF0000";
				if (bs.getStatus().equals(FREDConstants.BACKLOG_COMPLETE))
					statusColour = "#00FF00";
				else if (bs.getStatus().equals(FREDConstants.BACKLOG_EMPTY))
					statusColour = "#DDDDDD";
				%><td style="color: <%=statusColour%>"><%=bs.getStatus()%>&nbsp;&nbsp;</td><%
			} else {
				%><td>not started&nbsp;&nbsp;</td><%
			}
			%><td><%=bs.getLocalityCount()%></td><%
			if (bs.getStatus() != null && !bs.getStatus().equals(FREDConstants.BACKLOG_EMPTY)) {
				if (bs.getStatus().equals(FREDConstants.BACKLOG_COMPLETE)) {
					%><td width="200">
					<table border="0" width="100%">
					<tr><td width="100%" style="background-color: #00FF00"><img src="images/blank.gif" height="5" width="200" alt="" /></td></tr>
					</table>
					</td>
					<td>100%</td><%
				} else {
					int totalCount = bs.getProcessingCount().intValue() + bs.getCompletedCount().intValue();
					int procPct = (bs.getProcessingCount().intValue() * 100) / totalCount;
					int procWidth = 2 * procPct;
					int comPct = (bs.getCompletedCount().intValue() * 100) / totalCount;
					int comWidth = 2 * comPct;
					%><td width="200">
					<table border="0" width="100%">
					<tr><td width="<%=comPct%>%" style="background-color: #00FF00"><img src="images/blank.gif" height="5" width="<%=comWidth%>" alt="" /></td>
					<td width="<%=procPct%>%" style="background-color: #FF0000"><img src="images/blank.gif" height="5" width="<%=procWidth%>" alt="" /></td></tr>
					</table>
					</td>
					<td><%=comPct%>%</td><%					
				}
			} else {
				%><td></td><td></td><%
			}
			%></tr><%
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