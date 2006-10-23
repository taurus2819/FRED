<%@page extends="nz.cri.gns.fred.FREDIPSysJspPage"
%><%@page import="nz.cri.gns.fred.query.FREDQuery"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%
	ExtranetTemplate et = getExtranetTemplate();
	//et.setDisplayLoadingMessage(true);

	drawTop(out, et, request, response, true);
%>
<script><!--
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
//--></script>

<table style='margin-left:20px; margin-top:20px; width:150px;' border='0'>
<tr><td><a href='query.jsp' target='_top'><img src='images/search.gif' height='20' width='20' border='0' alt='Simple Query'  /></a></td><td><a href='query.jsp' class='boldlink' target='_top'>Simple Query</a></td></tr>
<tr><td><a href='saved_query.jsp' target='_top'><img src='images/search.gif' height='20' width='20' border='0' alt='Saved Queries' /></a></td><td><a href='saved_query.jsp' class='boldlink' target='_top'>Saved Queries</a></td></tr>
<tr><td><a href='http://maps.gns.cri.nz/website/petlab'><img src='images/map.gif' height='20' width='20' border='0' alt='Maps' /></a></td><td><a href='http://maps.gns.cri.nz/website/petlab' class='boldlink'>Maps</a></td></tr>
</table>

<%	drawEndNavigation(out);
	FREDQuery query = new FREDQuery();
%>
<table style='margin-left:20px; width:550px;' border='0'>
<tr><td>
<table style="border: solid black 2pt; text-align: left">
<%
try {
	query.makeHTMLForQueryPanel(out, false);
} catch (Exception e) {
	e.printStackTrace(new java.io.PrintWriter(out));
}
%></table></td></tr>
<tr><td><input name="button" type="button" value="Add Line" onClick="doTransfer(this.form);" /></td></tr>
</table></form>
<script><!--
function onsub() {
	return false;
}
document.forms[0].onsubmit = onsub;
//--></script>
<%  drawBottom(out, et); %>