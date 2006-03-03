<%@page extends="nz.cri.gns.fred.FREDIPSysJspPage"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@page import="java.net.URL"
%><%@page import="java.util.Iterator"
%><%@page import="nz.cri.gns.auth.Authenticable"
%><%@page import="nz.cri.gns.fred.hibernate.util.HibernateUtil"
%><%@page import="nz.cri.gns.fred.dao.DAOFactory"
%><%@page import="nz.cri.gns.fred.model.BacklogStatus"
%><%@page import="nz.cri.gns.fred.model.Folder"
%><%@page import="nz.cri.gns.fred.model.FREDConstants"
%><%@page import="nz.cri.gns.fred.util.BacklogStatusUtil"
%><%@page import="nz.cri.gns.fred.util.FolderUtil"
%><%@page import="nz.cri.gns.gis.ims.IMSMap"
%><%@page import="com.esri.aims.mtier.model.envelope.Envelope"
%><%!	
	public Authenticable[] getRequiredRights(HttpServletRequest request) { 
		return new Authenticable[0]; 
	}
%><%!
	public String getName(HttpServletRequest request) {
		try {
			DAOFactory factory = HibernateUtil.get().getDAOFactory();
			FolderUtil folderUtil = new FolderUtil(factory);
			Folder folder = folderUtil.getFolder(Integer.parseInt(request.getParameter("ID")));
			return "FRED :: Backlog Processing Status for " + folder.getName() + " masterfile";
		} catch (Exception e) {
			return "FRED :: Backlog Processing Status";
		}
	}
%><%!
	public String getStatusColour(String status) {
		if (status.equals(FREDConstants.BACKLOG_PROCESSING))
			return STATUS_COLOUR[PROCESSING];
		if (status.equals(FREDConstants.BACKLOG_COMPLETE))
			return STATUS_COLOUR[COMPLETED];
		if (status.equals(FREDConstants.BACKLOG_EMPTY))
			return "#DDDDDD";
		return STATUS_COLOUR[NOT_STARTED];
}
%><%!
	public int[] getBarPct(int totalCount, int compCount, int procCount) {
		if (procCount == totalCount)
			return new int[] {0, 100, 0};
		if (compCount == totalCount)
			return new int[] {100, 0, 0};
		int procPct = (procCount * 100) / totalCount;
		if (procPct == 0 && procCount > 0)
			procPct = 1;
		if (procPct == 100)
			procPct = 99;
		int comPct;
		int nsPct;
		if (procCount + compCount == totalCount) {
			comPct = 100 - procPct;
			nsPct = 0;
		} else {
			comPct = (compCount * 100) / totalCount;
			if (comPct == 0 && compCount > 0)
				comPct = 1;
			nsPct = 100 - procPct - comPct;
		}
		return new int[] {comPct, procPct, nsPct};		
	}
%><%!
	public static final int COMPLETED = 0;
	public static final int PROCESSING = 1;
	public static final int NOT_STARTED = 2;
	public static final String[] STATUS_COLOUR = new String[] {"#00FF00", "#FF0000", "#000000"};
%><%

	DAOFactory factory = HibernateUtil.get().getDAOFactory();
	BacklogStatusUtil bsUtil = new BacklogStatusUtil(factory);
	FolderUtil folderUtil = new FolderUtil(factory);

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
	<tr><td class="deHeading">Masterfile</td></tr>
	<tr><td class="heading"><a href="backlog_status.jsp?ID=-1">All</a></td></tr><%
	for (Iterator i = folderUtil.getAdminFolders().iterator(); i.hasNext();) {
		Folder folder = (Folder) i.next();
		%><tr><td class="heading"><a href="backlog_status.jsp?ID=<%=folder.getFolderId()%>"><%=folder.getName()%></a></td></tr><%
	}
	%></table><%
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
	
	%><p><%
	startDETable(pageContext);
	%><table border="0" width="480">
	<tr><td>The summary of map sheets completed in the backlog edit process is shown on the following map.  This map is dynamic, and is updated daily to show the current stage of completion, including those map sheets that are currently undergoing the backlog edit process. Ultimately each map sheet will become as they reach final completion.</td></tr>
	<tr><td>Note that records for Radiocarbon dating localities are only partially complete, at this stage lacking radiocarbon dating information. Tailored Radiocarbon dating forms are still to be developed to accommodate these details.</td></tr>
	</table><%
	endDETable(pageContext);
	%></p><%	
	
	int masterfileId = -1;
	try {
		masterfileId = Integer.parseInt(request.getParameter("ID"));
	} catch (Exception e) {	}

	%><p><%
	if (masterfileId <= 6) try {
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
				%><map name="FPMap0">
				<area href="backlog_status.jsp?ID=1" shape="polygon" coords="208, 11, 209, 23, 222, 21, 223, 34, 241, 35, 239, 66, 252, 67, 253, 86, 267, 88, 267, 109, 282, 110, 285, 143, 298, 144, 297, 199, 282, 199, 282, 209, 357, 208, 356, 109, 342, 109, 341, 88, 328, 89, 327, 77, 313, 76, 313, 65, 298, 67, 298, 45, 268, 46, 268, 35, 253, 34, 254, 24, 229, 23, 229, 12">
				<area href="backlog_status.jsp?ID=2" shape="polygon" coords="371, 277, 327, 277, 326, 244, 268, 243, 268, 212, 358, 211, 359, 155, 370, 155, 370, 144, 387, 144, 387, 156, 430, 156, 430, 200, 415, 199, 416, 232, 385, 232, 385, 254, 372, 254">
				<area href="backlog_status.jsp?ID=3" shape="polygon" coords="341, 321, 300, 321, 298, 277, 312, 278, 313, 265, 298, 265, 297, 256, 283, 255, 283, 245, 326, 245, 327, 277, 358, 277, 356, 300, 342, 300">
				<area href="backlog_status.jsp?ID=4" shape="polygon" coords="296, 333, 195, 333, 194, 321, 209, 321, 209, 278, 223, 279, 223, 265, 254, 266, 252, 289, 268, 288, 268, 277, 283, 277, 284, 289, 300, 289">
				<area href="backlog_status.jsp?ID=5" shape="polygon" coords="209, 442, 135, 442, 135, 385, 148, 387, 150, 376, 164, 376, 165, 366, 181, 365, 181, 342, 194, 342, 196, 331, 282, 334, 284, 354, 267, 355, 268, 375, 254, 375, 252, 387, 239, 386, 239, 399, 253, 398, 253, 422, 209, 422">
				<area href="backlog_status.jsp?ID=6" shape="polygon" coords="105, 566, 76, 565, 77, 544, 90, 543, 91, 520, 62, 519, 61, 509, 47, 509, 47, 487, 60, 487, 60, 453, 76, 454, 76, 444, 91, 443, 92, 422, 106, 421, 106, 411, 120, 411, 120, 398, 136, 399, 136, 443, 195, 441, 195, 499, 181, 498, 180, 510, 163, 511, 165, 532, 137, 531, 134, 544, 121, 544, 120, 556, 107, 555">
				</map><%
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
		%><p><img src="<%=imageURL%>" width="480" height="580" alt="Backlog Status Map" border="0" <%=((masterfileId == -1) ? "usemap=\"#FPMap0\"" : "")%>/></p><%
	} catch (Exception e) {
		%><p>An error has occured while generating the map. Please try again</p><%
	}
	%></p><%
	
	//Summary table
	%><p><%
	startDETable(pageContext);
	%><table border="0" width="480">
	<tr><td class="deHeading" colspan="4">Summary</td></tr>
	<tr><th>Status&nbsp;&nbsp;</th><th>Localities&nbsp;&nbsp;</th><th colspan="2">Percent Complete</th></tr><%
	if (masterfileId > 0) {
		Folder masterfile = folderUtil.getFolder(masterfileId);
		String status = bsUtil.getStatus(masterfile.getFolderId().intValue());
		int totalCount = bsUtil.getSumLocalityCount(masterfile.getFolderId().intValue()) - bsUtil.getSumNewCount(masterfile.getFolderId().intValue());
		%><tr>
		<td style="color: <%=getStatusColour(status)%>"><%=status%>&nbsp;&nbsp;</td>
		<td><%=totalCount%></td><%
		if (status.equals(FREDConstants.BACKLOG_COMPLETE) || status.equals(FREDConstants.BACKLOG_PROCESSING)) {
			int[] pct = getBarPct(totalCount, bsUtil.getSumCompletedCount(masterfile.getFolderId().intValue()), bsUtil.getSumProcessingCount(masterfile.getFolderId().intValue()));
			%><td width="200">
			<table border="0" width="100%" cellspacing="0" cellpadding="0">
			<tr><%
			for (int j = 0; j < 3; j++) {
				if (pct[j] > 0) {
					%><td width="<%=pct[j]%>%" style="background-color: <%=STATUS_COLOUR[j]%>"><img src="images/blank.gif" height="10" width="<%=pct[j] * 2%>" alt="" /></td><%
				}
			}
			%></tr>
			</table>
			</td>
			<td><%=pct[COMPLETED]%>%</td><%						
		} else {
			%><td></td><td></td><%
		}
		%></tr><%
	} else {
		String status = bsUtil.getStatus();
		int totalCount = bsUtil.getSumLocalityCount() - bsUtil.getSumNewCount();
		%><tr>
		<td style="color: <%=getStatusColour(status)%>"><%=status%>&nbsp;&nbsp;</td>
		<td><%=totalCount%></td><%
		if (status.equals(FREDConstants.BACKLOG_COMPLETE) || status.equals(FREDConstants.BACKLOG_PROCESSING)) {
			int[] pct = getBarPct(totalCount, bsUtil.getSumCompletedCount(), bsUtil.getSumProcessingCount());
			%><td width="200">
			<table border="0" width="100%" cellspacing="0" cellpadding="0">
			<tr><%
			for (int j = 0; j < 3; j++) {
				if (pct[j] > 0) {
					%><td width="<%=pct[j]%>%" style="background-color: <%=STATUS_COLOUR[j]%>"><img src="images/blank.gif" height="10" width="<%=pct[j] * 2%>" alt="" /></td><%
				}
			}
			%></tr>
			</table>
			</td>
			<td><%=pct[COMPLETED]%>%</td><%						
		} else {
			%><td></td><td></td><%
		}
		%></tr><%
	}
	%></table><%
	endDETable(pageContext);
	%></p><%
	
	//Detail table
	%><p><%
	startDETable(pageContext);
	%><table border="0" width="480">
	<tr><td class="deHeading" colspan="5">Detail</td></tr><%
	if (masterfileId > 0) {
		%><tr><th>Map</th><th>Status&nbsp;&nbsp;</th><th>Localities&nbsp;&nbsp;</th><th colspan="2">Percent Complete</th></tr><%
		for (Iterator i = bsUtil.getBacklogStatusInMasterfile(masterfileId).iterator(); i.hasNext();) {
			BacklogStatus bs = (BacklogStatus) i.next();
			int totalCount = bs.getLocalityCount().intValue() - bs.getNewCount().intValue();
			%><tr><td class="heading"><a href="backlog_status_sheet.jsp?Sheet=<%=bs.getMapNumber()%>"><%=bs.getMapNumber()%></a>&nbsp;&nbsp;</td>
			<td style="color: <%=getStatusColour(bs.getStatus())%>"><%=bs.getStatus()%>&nbsp;&nbsp;</td>
			<td><%=totalCount%></td><%
			if (bs.getStatus().equals(FREDConstants.BACKLOG_COMPLETE) || bs.getStatus().equals(FREDConstants.BACKLOG_PROCESSING)) {
				int[] pct = getBarPct(totalCount, bs.getCompletedCount().intValue(), bs.getProcessingCount().intValue());
				%><td width="200">
				<table border="0" width="100%" cellspacing="0" cellpadding="0">
				<tr><%
				for (int j = 0; j < 3; j++) {
					if (pct[j] > 0) {
						%><td width="<%=pct[j]%>%" style="background-color: <%=STATUS_COLOUR[j]%>"><img src="images/blank.gif" height="10" width="<%=pct[j] * 2%>" alt="" /></td><%
					}
				}
				%></tr>
				</table>
				</td>
				<td><%=pct[COMPLETED]%>%</td><%						
			} else {
				%><td></td><td></td><%
			}
			%></tr><%
		}
	} else {
		%><tr><th>Masterfile</th><th>Status&nbsp;&nbsp;</th><th>Localities&nbsp;&nbsp;</th><th colspan="2">Percent Complete</th></tr><%
		for (Iterator i = folderUtil.getAdminFolders().iterator(); i.hasNext();) {
			Folder masterfile = (Folder) i.next();
			String status = bsUtil.getStatus(masterfile.getFolderId().intValue());
			int totalCount = bsUtil.getSumLocalityCount(masterfile.getFolderId().intValue()) - bsUtil.getSumNewCount(masterfile.getFolderId().intValue());
			%><tr><td class="heading"><a href="backlog_status.jsp?ID=<%=masterfile.getFolderId()%>"><%=masterfile.getName()%></a>&nbsp;&nbsp;</td>
			<td style="color: <%=getStatusColour(status)%>"><%=status%>&nbsp;&nbsp;</td>
			<td><%=totalCount%></td><%
			if (status.equals(FREDConstants.BACKLOG_COMPLETE) || status.equals(FREDConstants.BACKLOG_PROCESSING)) {
				int[] pct = getBarPct(totalCount, bsUtil.getSumCompletedCount(masterfile.getFolderId().intValue()), bsUtil.getSumProcessingCount(masterfile.getFolderId().intValue()));
				%><td width="200">
				<table border="0" width="100%" cellspacing="0" cellpadding="0">
				<tr><%
				for (int j = 0; j < 3; j++) {
					if (pct[j] > 0) {
						%><td width="<%=pct[j]%>%" style="background-color: <%=STATUS_COLOUR[j]%>"><img src="images/blank.gif" height="10" width="<%=pct[j] * 2%>" alt="" /></td><%
					}
				}
				%></tr>
				</table>
				</td>
				<td><%=pct[COMPLETED]%>%</td><%						
			} else {
				%><td></td><td></td><%
			}
			%></tr><%
		}
	}
	%></table><%
	endDETable(pageContext);
	%></p><%	
	
	%></td></tr></table><%

	
	drawBottom(out, et);
	
	try {
		HibernateUtil.get().getDAOFactory().closeSession();
	} catch (Exception e) {
	}
%>