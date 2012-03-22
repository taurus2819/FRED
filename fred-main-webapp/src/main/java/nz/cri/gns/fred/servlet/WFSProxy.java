package nz.cri.gns.fred.servlet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.naming.*;

public class WFSProxy extends HttpServlet	{


	private String TARGET_URL_WFS;
		
	
/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
	 */
	@Override
	public void init(ServletConfig arg0) throws ServletException {
		
		super.init(arg0);
		init();
	}

	//	 public methods
	public void init() throws ServletException
	{
		super.init();
		
		//initialize with parameters from web.xml	
		try {
			Context env = (Context)new InitialContext().lookup("java:comp/env");
			TARGET_URL_WFS = (String)env.lookup("WFS");
		} catch (NamingException e) {
			e.printStackTrace();
		}
	} // init	
		
	public void doPost(HttpServletRequest request, HttpServletResponse response)
    	throws IOException, ServletException	{
		
		URLConnection serverConnection = new URL(TARGET_URL_WFS).openConnection();
		serverConnection.setRequestProperty("CONTENT-TYPE", "text/xml");
		serverConnection.setDoInput(true);
		serverConnection.setDoOutput(true);
		
		//1. read from client, write to server
		int bytie;
		InputStream fromClient = request.getInputStream();
		OutputStream toServer = serverConnection.getOutputStream();
		
		while((bytie = fromClient.read()) != -1)	{
			toServer.write(bytie);
		}
		fromClient.close();
		toServer.close();
		
		InputStream fromServer = serverConnection.getInputStream();
		
		response.setContentType("text/xml");
		OutputStream toClient = response.getOutputStream();
		
		while((bytie = fromServer.read()) != -1)	{
			toClient.write(bytie);
		}
		fromServer.close();
		toClient.close();
    }
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException	
	{		
		String inParams = "?";
		
		for(Enumeration eno = request.getParameterNames(); eno.hasMoreElements();){
			String key = eno.nextElement().toString();
			inParams+= key+"="+URLEncoder.encode(request.getParameter(key),"UTF-8");
			if(eno.hasMoreElements())
				inParams+="&";
		}
		String targetURL = TARGET_URL_WFS;
		
		URLConnection serverConnection = new URL(targetURL+inParams).openConnection();
		//logger.info("Proxy calls "+targetURL+inParams);
		serverConnection.setDoInput(true);
		serverConnection.setDoOutput(false);
		
		BufferedReader fromServer = new BufferedReader(new InputStreamReader(serverConnection.getInputStream()));
		BufferedWriter toClient = new BufferedWriter(new OutputStreamWriter(response.getOutputStream()));

		response.setContentType("text/xml");
	
		String line;
		try {
			while((line = fromServer.readLine()) != null)	{
				line.trim();
				toClient.write(line);
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		}

		fromServer.close();
		toClient.flush();
		toClient.close();
	}	
}
