<%@page pageEncoding="utf-8"%>
<%
	nz.cri.gns.fred.util.FREDUtil.setSessionLock(session);
%><html>
<frameset rows="275,*" border="0">
 <frame name="buildpanel" src="build_record.jsp?frameset=buildframe_record.jsp" scrolling="no" noresize="noresize" />
 <frame name="querydisp" src="adv_query_record.jsp" />
<noframes>
This page uses frames, but your browser does not support them
</noframes>
</frameset>
</html>
