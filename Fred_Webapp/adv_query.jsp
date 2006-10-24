<%@page extends="nz.cri.gns.fred.FREDIPSysJspPage"
%><%@page import="nz.cri.gns.db.querybuilder.LogicManager"
%><%@page import="nz.cri.gns.db.querybuilder.OperatorManager"
%><%@page import="nz.cri.gns.db.querybuilder.QueryElement"
%><%@page import="nz.cri.gns.db.querybuilder.advanced.AdvancedQuery"
%><%@page import="nz.cri.gns.fred.query.FREDQuery"
%><%@page import="nz.cri.gns.fred.util.FREDUtil"
%><%@page import="nz.cri.gns.jsp.PageState"
%><%@page import="java.util.Date"
%><%@page import="java.util.Iterator"
%><%@page import="nz.cri.gns.auth.Authenticable"
%><%!
	public Authenticable[] getRequiredRights(HttpServletRequest request) { return new Authenticable[0]; }
%><html><head>
<link rel="StyleSheet" href="/online/style/extranet.css" type="text/css" />
</head>
<body><center><%
	PageState state = new PageState(request, response, getServletContext());
	FREDQuery query = FREDUtil.getFREDQuery(state);
	if (request.getParameter(AdvancedQuery.LEFT_BRACKET) != null) try {
		query.decodeRequest(request);

		if (query.queryElementCount() == 1)
			query.getQueryElement(0).setLogic(null);

		//Reset the form
		%><script><!--
parent.buildpanel.<%=AdvancedQuery.FORM_RESET_FUNCTION%>(parent.buildpanel.document.forms['<%=AdvancedQuery.FORM_NAME%>']);
//--></script>
<%
	} catch (Exception e) {
		%><div style="color: red"><%=e.getMessage()%></div><%
	} else if (request.getParameter("spatial") != null) {
		%><script><!--
parent.buildpanel.doSpatial(<%=request.getParameter("top")%>,<%=request.getParameter("right")%>,<%=request.getParameter("bottom")%>,<%=request.getParameter("left")%>);
//--></script>
<%
	}
try {
%>
<script><!--
function doStrt() {
	location.href="clearQuery.jsp";
}
function doSave() {
	window.open("<%=new java.text.SimpleDateFormat("yyyyMMdd").format(new Date())%>.gwq");
}
function doLoad() {
	window.open("load.jsp?show=yes");
}
//--></script>
<form name="newelement" action="adv_query.jsp" method="post">
 <input type="hidden" name="<%=AdvancedQuery.LEFT_BRACKET%>">
 <input type="hidden" name="<%=LogicManager.LOGIC%>">
 <input type="hidden" name="<%=AdvancedQuery.MASTER_FIELD%>">
 <input type="hidden" name="<%=AdvancedQuery.CHILD_FIELD%>">
 <input type="hidden" name="<%=OperatorManager.OPERATOR%>">
 <input type="hidden" name="<%=AdvancedQuery.FREE_VALUE%>">
 <input type="hidden" name="<%=AdvancedQuery.OPTION_VALUE%>">
 <input type="hidden" name="<%=AdvancedQuery.RIGHT_BRACKET%>">
 <input type="hidden" name="button">
</form>

  <table border="0" cellspacing="0" cellpadding="0" class="tall" width="100%" >
   <tr>
	<td height="100%" width="198" ></td>
	<td style="padding-top:20px;" >
	<table style='margin-left:20px; width:550px;' border='0'><tr><td>


<p>Use the menus above to make to your query.  The query will build up below as you go.  You can add and remove lines of the query,
but not modify them once they are in place.  When you have finishing building the query click the Execute Query button to display the results or you can <a href="clearQuery.jsp" class="boldlink">clear</a> the query</p>
<%
	if (query.queryElementCount() > 0) {
%>

<table>
<%
		int count = 0;
		for (Iterator it = query.queryElements(); it.hasNext(); count++) {
			%><%=((QueryElement)it.next()).getElementInTable()%>
<td>&nbsp;<a href="removeElement.jsp?index=<%=count%>" class="boldlink">remove line</a></td></tr>
<%
		}
		%></table>
<%
	}
} catch (Exception e) {
	e.printStackTrace(new java.io.PrintWriter(out));
}
%>

<p><input type="button" onClick="parent.location.href='result_list.jsp?Type=Adv'" value="Execute query" alt="Execute query" /></p>

	</td></tr>
	</td></tr>
   <tr><td colspan="2" ></td></tr>
  </table>
  </body>
</html>
