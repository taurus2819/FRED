<%@page	extends="nz.cri.gns.fred.FREDIPSysJspPage"
%><%@page import="nz.cri.gns.fred.FREDUtils"
%><%@page import="nz.cri.gns.fred.model.Feature"
%><%@page import="nz.cri.gns.fred.model.Folder"
%><%@page import="nz.cri.gns.fred.model.UserFolder"
%><%@page import="nz.cri.gns.fred.model.FREDConstants"
%><%@page import="nz.cri.gns.fred.model.Sample"
%><%@page import="nz.cri.gns.fred.de.DataEntryForm"
%><%@page import="nz.cri.gns.fred.de.DataEntryFormFactory"
%><%@page import="nz.cri.gns.fred.util.FolderUtil"
%><%@page import="nz.cri.gns.fred.util.FeatureUtil"
%><%@page import="nz.cri.gns.fred.util.SampleUtil"
%><%@page import="nz.cri.gns.fred.util.FREDUtil"
%><%@page import="nz.cri.gns.fred.dao.DAOFactory"
%><%@page import="nz.cri.gns.fred.hibernate.util.HibernateUtil"
%><%@page import="nz.cri.gns.jsp.JspUtils"
%><%@page import="nz.cri.gns.jsp.PageState"
%><%@page import="nz.cri.gns.intranet.DBConnection"
%><%@page import="java.sql.ResultSet"
%><%@page import="java.sql.Statement"
%><%@page import="nz.cri.gns.auth.Authenticable"
%><%@page import="nz.cri.gns.auth.User"
%><%@page import="nz.cri.gns.fred.website.ContentProvider"
%><%@page import="java.util.Iterator"
%><%@page import="java.io.PrintWriter"
%><%@page import="java.io.File"
%><%!	
	public Authenticable[] getRequiredRights(HttpServletRequest request) {
		return new Authenticable[0];
	}
%><%

	String listName = request.getParameter("listName");
	
	%><html><head></head><body><%
	
	if (listName != null) {
	
		PageState state = new PageState(request, response, getServletContext());
		DBConnection connection = FREDUtils.getFREDConnection(state);
		Statement statement = connection.statement;
		
		ResultSet rs = null;
	
		DAOFactory factory = HibernateUtil.get().getDAOFactory();
		FolderUtil folderUtil = new FolderUtil(factory);
		FeatureUtil featureUtil = new FeatureUtil(factory);
		SampleUtil sampleUtil = new SampleUtil(factory);
		
		%><table><%
		
		if (listName.equals("folderContent")) {
			User user = null;
			try {
		   		user = new User(request.getParameter("user"), request.getParameter("pass"), JspUtils.createDatabaseConnection(state.getSession(), "nz.cri.gns.ip.connection", "ip", state.getContext()));
		   	} catch (Exception e) {
		   		%><tr><td>Error: Invalid username/password</td></td><%
		   	}
		   	UserFolder userFolder = folderUtil.getUserFolder(Integer.parseInt(request.getParameter("folderID")), user);
		   	if (userFolder.isAllowedReadLocalities()) {
			   	if (request.getParameter("formType").equals("locality")) {
				   	try {
				   		ContentProvider provider = new ContentProvider(new File(request.getSession().getServletContext().getRealPath("/content")));
						for (Iterator i = userFolder.getFolder().getFeatures().iterator(); i.hasNext(); ) {
							Feature feature = (Feature) i.next();
							try {
								DataEntryForm dataEntryForm = DataEntryFormFactory.getLocalityDataEntryForm(feature.getFeatureId().intValue(), userFolder.getFolderId().intValue(), user, factory, provider);
								dataEntryForm.makeExcelImportHTML(new PrintWriter(out));
							} catch (Exception e) {}
						}
					} catch (Exception e) {
						%><tr><td>No localities</td><td></td></tr><%
					}
				}
			} else {
				%><tr><td>Error: Insufficient privileges</td></tr><%
			}
		} else if (listName.equals("document")) {
			User user = null;
			try {
		   		user = new User(request.getParameter("user"), request.getParameter("pass"), JspUtils.createDatabaseConnection(state.getSession(), "nz.cri.gns.ip.connection", "ip", state.getContext()));
		   	} catch (Exception e) {
		   		%><tr><td>Error: Invalid username/password</td></td><%
		   	}
		   	ContentProvider provider = new ContentProvider(new File(request.getSession().getServletContext().getRealPath("/content")));
		   	DataEntryForm dataEntryForm = null;
		   	try {
		   		int folderID = Integer.parseInt(request.getParameter("folderID"));
		   		if (request.getParameter("docType").equals("Locality")) {
		   			Feature feature = featureUtil.getFeature(Integer.parseInt(request.getParameter("id")));
		   			dataEntryForm = DataEntryFormFactory.getLocalityDataEntryForm(feature.getFeatureId().intValue(), folderID, user, factory, provider);
				} else if (request.getParameter("docType").equals("Sample")) {
					Sample sample = sampleUtil.getSample(Integer.parseInt(request.getParameter("id")));
					dataEntryForm = DataEntryFormFactory.getSampleDataEntryForm(sample.getSampleId().intValue(), folderID, user, factory, provider);
				}
			} catch (Exception e) {
				e.printStackTrace();
				%><tr><td>Error: <%=e%></td></tr><%
			}
			if (dataEntryForm != null)
				dataEntryForm.makeExcelImportHTML(new PrintWriter(out));
		} else if (listName.equals("sampleList")) {
			User user = null;
			try {
		   		user = new User(request.getParameter("user"), request.getParameter("pass"), JspUtils.createDatabaseConnection(state.getSession(), "nz.cri.gns.ip.connection", "ip", state.getContext()));
		   	} catch (Exception e) {
		   		%><tr><td>Error: Invalid username/password</td></td><%
		   	}
		   	try {
		   		Feature feature = featureUtil.getFeature(Integer.parseInt(request.getParameter("featureID")));
		   		if (!feature.getFeatureType().equals(FREDConstants.OUTCROP)) {
					for (Iterator i = feature.getSamples().iterator(); i.hasNext(); ) {
						Sample sample = (Sample) i.next();
						if (sampleUtil.isAllowedReadSample(user, sample)) {
							%><tr><td><%=SampleUtil.getDrillHoleDepthDescription(sample)%></td><td><%=sample.getSampleId()%></td></tr><%
						}
					}
				} else {
					%><tr><td>** Outcrop **</td><td></td></tr><%
				}
		   	} catch (Exception e) {
		   		%><tr><td>Error</td><td></td></tr><%
		   	}
		} else if (listName.equals("blankSampleList")) {
			%><tr><td>No samples defined</td><td>-1</td></tr><%
		} else if (listName.equals("localityList")) {
			User user = null;
			try {
		   		user = new User(request.getParameter("user"), request.getParameter("pass"), JspUtils.createDatabaseConnection(state.getSession(), "nz.cri.gns.ip.connection", "ip", state.getContext()));
		   	} catch (Exception e) {
		   		%><tr><td>Error: Invalid username/password</td></td><%
		   	}
		   	try {
			   	UserFolder userFolder = folderUtil.getUserFolder(Integer.parseInt(request.getParameter("folderID")), user);
		   		Feature[] features = featureUtil.getFeaturesInFolder(userFolder);
		   		if (features.length > 0) {
					for (int i = 0; i < features.length; i++) {
						if (featureUtil.isAllowedReadFeature(user, features[i])) {
							%><tr><td><%=FeatureUtil.getFeatureName(features[i])%></td><td><%=features[i].getFeatureId()%></td></tr><%
						}
					}
				} else {
					%><tr><td>No features found in folder</td><td></td></tr><%
				}
			} catch (Exception e) {
				%><tr><td>Error</td><td></td></tr><%
			}
		} else if (listName.equals("blankLocalityList")) {
			%><tr><td>No features defined</td><td>-1</td></tr><%
		} else if (listName.equals("folderList")) {
			User user = null;
			try {
		   		user = new User(request.getParameter("user"), request.getParameter("pass"), JspUtils.createDatabaseConnection(state.getSession(), "nz.cri.gns.ip.connection", "ip", state.getContext()));
		   	} catch (Exception e) {
		   		%><tr><td>Error: Invalid username/password</td></td><%
		   	}
		   	try {
			   	if (folderUtil.getPersonalFolders(user).size() > 0 || folderUtil.getBacklogFolders(user).size() > 0) {
					for (Iterator i = folderUtil.getPersonalFolders(user).iterator(); i.hasNext(); ) {
						UserFolder folder = (UserFolder) i.next();
						%><tr><td><%=folder.getFolderName()%></td><td><%=folder.getFolderId()%></td></tr><%
					}
					for (Iterator i = folderUtil.getBacklogFolders(user).iterator(); i.hasNext(); ) {
						UserFolder folder = (UserFolder) i.next();
						%><tr><td><%=folder.getFolderName()%></td><td><%=folder.getFolderId()%></td></tr><%
					}
				} else {
					%><tr><td>No folders found</td><td></td></tr><%
				}
			} catch (Exception e) {
				e.printStackTrace();
				%><tr><td>No folders defined</td><td></td></tr><%
			}
		} else if (listName.equals("blankFolderList")) {
			%><tr><td>No folders defined</td><td>-1</td></tr><%
		} else if (listName.equals("datum")) {
			%><tr><td>New Zealand Map Grid</td><td>NZMG</td></tr>
			<tr><td>NZMS260</td><td>NZMS260</td></tr>
			<tr><td>NZ Yard Grid (Sth Isl)</td><td>NZ Yard SthIsl</td></tr>
			<tr><td>NZ Yard Grid (Nth Isl)</td><td>NZ Yard NthIsl</td></tr>
			<tr><td>NZMS1 (Sth Isl)</td><td>NZMS1 SthIsl</td></tr>
			<tr><td>NZMS1 (Nth Isl)</td><td>NZMS1 NthIsl</td></tr>
			<tr><td>Chatham Island Grid</td><td>Chatham Island Grid</td></tr>
			<tr><td>Auckland Island Grid</td><td>Auckland Island Transverse Mercator</td></tr>
			<tr><td>Campbell Island Grid</td><td>Campbell Island Transverse Mercator</td></tr>
			<tr><td>Lat/Long NZGD49</td><td>NZGD49</td></tr>
			<tr><td>Lat/Long WGS84/NZGD2000</td><td>WGS84</td></tr><%
		} else if (listName.equals("localityType")) {
			%><tr><td>Outcrop</td></tr>
			<tr><td>Drillhole</td></tr>
			<tr><td>Vertical Section</td></tr><%
		} else if (listName.equals("sedFeature")) {
			rs = statement.executeQuery("SELECT code, name FROM sedimentary_feature_type ORDER BY code");
			try {
				while (rs.next()) {
					%><tr><td><%=rs.getString(1) + ": " + rs.getString(2).replaceAll(" ", "&nbsp;")%></td><td><%=rs.getString(2)%></td></tr><%
					%><tr><td><%=rs.getString(1) + "*: " + rs.getString(2).replaceAll(" ", "&nbsp;")%> abundant</td><td><%=rs.getString(2)%>$</td></tr><%
				}
			} catch (Exception e) {}
		} else {
			if (listName.equals("bedding")) {
				rs = statement.executeQuery("SELECT code || ': ' || name || '</td><td>' || bedding_id  FROM bedding ORDER BY code");
			} else if (listName.equals("carbonate")) {
				rs = statement.executeQuery("SELECT code || ': ' || name || '</td><td>' || carbonate_id  FROM carbonate ORDER BY code");
			} else if (listName.equals("colour")) {
				rs = statement.executeQuery("SELECT code || ': ' || name || '</td><td>' || colour_id  FROM rock_colour ORDER BY code");
			} else if (listName.equals("colourMod")) {
				rs = statement.executeQuery("SELECT code || ': ' || name || '</td><td>' || modifier_id  FROM colour_modifier ORDER BY code");
			} else if (listName.equals("country")) {
				rs = statement.executeQuery("SELECT country_name || '</td><td>' || country_code FROM mis.country ORDER BY country_name");
			} else if (listName.equals("drillType")) {
				rs = statement.executeQuery("SELECT name || '</td><td>' || drill_type_id FROM drill_type ORDER BY name");
			} else if (listName.equals("fossilGroup")) {
				rs = statement.executeQuery("SELECT name FROM fossil_group ORDER BY name");
			} else if (listName.equals("grainSize")) {
				rs = statement.executeQuery("SELECT code || ': ' || name || '</td><td>' || grain_size_id  FROM grain_size ORDER BY code");
			} else if (listName.equals("hardness")) {
				rs = statement.executeQuery("SELECT code || ': ' || name || '</td><td>' || hardness_id  FROM hardness ORDER BY code");
			} else if (listName.equals("lab")) {
				rs = statement.executeQuery("SELECT lab_name FROM sc.lab ORDER BY lab_name");
			} else if (listName.equals("locMethod")) {
				rs = statement.executeQuery("SELECT method || '</td><td>' || method_id || '</td><td>' || nom_accuracy_xy FROM sc.method WHERE nom_accuracy_xy IS NOT NULL ORDER BY nom_accuracy_xy");
			} else if (listName.equals("person")) {
				rs = statement.executeQuery("SELECT name FROM person_view ORDER BY family_name, given_name");
			} else if (listName.equals("regArea")) {
				rs = statement.executeQuery("SELECT name || '</td><td>' || reg_area_id FROM registration_area ORDER BY name");
			} else if (listName.equals("thickness")) {
				rs = statement.executeQuery("SELECT code || ': ' || name || '</td><td>' || thickness_id FROM bed_thickness ORDER BY code");
			} else if (listName.equals("stageName")) {
				rs = statement.executeQuery("SELECT ag_name || '</td><td>' || ag_id FROM age_view ORDER BY ag_name");
			} else if (listName.equals("stratName")) {
				rs = statement.executeQuery("SELECT su_name FROM sl.strat_unit ORDER BY su_name");
			} else if (listName.equals("weathering")) {
				rs = statement.executeQuery("SELECT code || ': ' || name || '</td><td>' || weathering_id FROM weathering ORDER BY code");
			}
			try {
				while (rs.next()) {
					%><tr><td><%=rs.getString(1).replaceAll(" ", "&nbsp;")%></td></tr><%
				}
			} catch (Exception e) {}
		}
		
		%></table><%
	}
	
	try {
		HibernateUtil.get().getDAOFactory().closeSession();
	} catch (Exception e) {
	}
	
	%></body></html>
