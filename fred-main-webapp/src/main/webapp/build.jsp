<%@page pageEncoding="utf-8"
%><%@page extends="nz.cri.gns.fred.FREDIPSysJspPage"
%><%@page import="nz.cri.gns.fred.query.FREDQuery"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@page import="nz.cri.gns.fred.util.FREDUtil"
%><%@page import="nz.cri.gns.jsp.PageState"
%><%!
	public String getName(HttpServletRequest request) {
		return "FRED :: Advanced Query Builder";
	}
%><%
	ExtranetTemplate et = getExtranetTemplate(request.getSession());
	//et.setDisplayLoadingMessage(true);
	
	drawTop(out, et, request, response, true);

	%><script><!--
	function doTransfer(frm) {
		var otherForm = parent.querydisp.document.forms['newelement'];
		for (var i=0; i<frm.elements.length; i++) {
			var element = frm.elements[i];
			if (element.type == "select-one") {
				otherForm.elements[element.name].value = element.options[element.selectedIndex].value;
			} else if (element.type == "checkbox") {
				otherForm.elements[element.name].value = (element.checked) ? "Yes" : "No";
			} else {
				otherForm.elements[element.name].value = element.value;
			}
	
		}
		otherForm.submit();
	}
	//--></script><%

	PageState state = new PageState(request, response, getServletContext());
	FREDQuery query = FREDUtil.getFREDQuery(state);
	%><p><%
	startDETable(pageContext);
	%><table style="margin-left:20px; width:550px;" border="0">
	<tr><td class="deHeading">Query Builder</td></tr>
	<tr><td>
	<table style="border: solid black 2pt; text-align: left"><%
	try {
		query.makeHTMLForQueryPanel(out, false);
	} catch (Exception e) {
		e.printStackTrace();
	}
	%></table></td></tr>
	<tr><td><input name="button" type="button" value="Add Line" onClick="doTransfer(this.form);" /></td></tr>
	</table><%
	endDETable(pageContext);
	%></p>
	<script><!--
		function onsub() {
			return false;
		}
		document.forms[0].onsubmit = onsub;
	//--></script><%
	drawBottom(out, et); %>