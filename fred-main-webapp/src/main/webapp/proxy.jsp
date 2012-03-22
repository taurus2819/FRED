<%@page import="java.net.URL"
%><%@page import="java.net.HttpURLConnection"
%><%@page import="java.io.InputStream"
%><%@page import="java.io.OutputStreamWriter"
%><%@page import="java.io.BufferedInputStream"
%><%@page import="java.io.BufferedReader"
%><%@page import="java.io.ByteArrayOutputStream"
%><%
	boolean okFlag = false;
	
	String rawUrl = request.getParameter("url");
	URL url = new URL(rawUrl.replace("/gwc/service",""));
	
	System.out.println(url.getHost());
	if (url.getHost().equals("maps-dev.gns.cri.nz") || url.getHost().equals("maps.gns.cri.nz")) 
		okFlag = true;

	if (okFlag) {
		BufferedReader reader = request.getReader();
	    String s;
	    StringBuffer postContent = new StringBuffer();
	    while((s = reader.readLine()) != null)
	    	postContent.append(s);
		
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		if (postContent.length() > 0) {
			connection.setUseCaches(false);
			connection.setRequestMethod("POST");
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Type", "application/xml");
	
			OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
			wr.write(postContent.toString());
			wr.flush();
			wr.close();
		}
		
		String contentType = connection.getContentType();
		if ("application/vnd.ogc.wms_xml".equals(contentType))
			contentType = "text/xml";
		response.setContentType(contentType);
		InputStream input = connection.getInputStream();
		BufferedInputStream bis = new BufferedInputStream(input);
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
	
		int result = bis.read();
		while (result != -1) {
			byte b = (byte)result;
			buf.write(b);
			result = bis.read();
		}
		%><%=buf.toString()%><%
		System.out.println(buf);

		} else {
		%>Access to <%=url.getHost()%> prohibited<%
	}
%>