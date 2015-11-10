<%@page pageEncoding="utf-8"
%><%@page extends="nz.cri.gns.fred.FREDIPSysJspPage"
%><%@page import="nz.cri.gns.db.querybuilder.LogicManager"
%><%@page import="nz.cri.gns.db.querybuilder.OperatorManager"
%><%@page import="nz.cri.gns.db.querybuilder.QueryElement"
%><%@page import="nz.cri.gns.db.querybuilder.advanced.AdvancedQuery"
%><%@page import="nz.cri.gns.fred.query.FREDQuery"
%><%@page import="nz.cri.gns.fred.util.FREDUtil"
%><%@page import="nz.cri.gns.jsp.PageState"
%><%@page import="java.util.Date"
%><%@page import="java.util.Iterator"
%><%@page import="java.net.URLEncoder"
%><%@page import="org.springframework.security.core.GrantedAuthority"
%><%!
	public Authenticable[] getRequiredRights(HttpServletRequest request) { return new Authenticable[0]; }
%>
<!DOCTYPE html 
   PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" 
  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> 
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
 <head>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
  <title>FRED :: The Fossil Record Electronic Database</title>
  <link rel="stylesheet" href="/online/style/link_styles_internal.css" type="text/css" />
  <link rel="stylesheet" href="fred.css" type="text/css" />
  </head>
  <body>
    <div id="navBarWrap">
   <div class="left">

   <div id="navBarSpacer">
   </div>
   <div id="navPositioner">
   </div>
   </div>
  <div id="contentWrapInner"><%
	PageState state = new PageState(request, response, getServletContext());
	FREDQuery query = FREDUtil.getFREDQuery(state);
	if (request.getParameter(AdvancedQuery.LEFT_BRACKET) != null) try {
		query.decodeRequest(request);
		if (query.queryElementCount() == 1)
			query.getQueryElement(0).setLogic(null);
		//Reset the form
		%><script><!--
			parent.buildpanel.<%=AdvancedQuery.FORM_RESET_FUNCTION%>(parent.buildpanel.document.forms['<%=AdvancedQuery.FORM_NAME%>']);
		//--></script><%
	} catch (Exception e) {
		%><div style="color: red"><%=e.getMessage()%></div><%
	} else if (request.getParameter("spatial") != null) {
		%><script><!--
		parent.buildpanel.doSpatial(<%=request.getParameter("top")%>,<%=request.getParameter("right")%>,<%=request.getParameter("bottom")%>,<%=request.getParameter("left")%>);
		//--></script><%
	}
	
	try {
		%><script><!--
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
		
		<p><%
	
		startDETable(pageContext);
		%><table style="margin-left:20px; width:550px;" border="0"><tr><td>
		<p>Use the menus above to make to your query.  The query will build up below as you go.  You can add and remove lines of the query,
		but not modify them once they are in place.  When you have finished building the query click the Execute Query button to display the results or you can <a href="clearQuery.jsp" class="boldlink">clear</a> the query</p><%
		if (query.queryElementCount() > 0) {
			%><table><%
			int count = 0;
			for (Iterator it = query.queryElements(); it.hasNext(); count++) {
				%><%=((QueryElement)it.next()).getElementInTable()%>
				<td>&nbsp;<a href="removeElement.jsp?index=<%=count%>" class="boldlink">remove line</a></td></tr><%
			}
			%></table><%
		}
		%><p><input type="button" onClick="parent.location.href='result_list.jsp?Type=Adv&QueryURL=<%=URLEncoder.encode("buildframe.jsp", "ISO-8859-1")%>'" value="Execute query" alt="Execute query" /></p>
		</td></tr>
		</td></tr>
		<tr><td colspan="2" ></td></tr>
		</table><%
		endDETable(pageContext);
		%></p>
		
		<p>&nbsp;</p><%
	} catch (Exception e) {
		e.printStackTrace(new java.io.PrintWriter(out));
	}
	
	%></div>
	</body>
	</html>