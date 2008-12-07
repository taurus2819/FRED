<%@page	extends="nz.cri.gns.fred.FREDIPSysJspPage"
%><%@page import="nz.cri.gns.fred.model.Feature"
%><%@page import="nz.cri.gns.fred.model.UserFolder"
%><%@page import="nz.cri.gns.fred.model.FREDConstants"
%><%@page import="nz.cri.gns.fred.model.Sample"
%><%@page import="nz.cri.gns.fred.model.Record"
%><%@page import="nz.cri.gns.fred.model.Paleontology"
%><%@page import="nz.cri.gns.fred.de.DataEntryForm"
%><%@page import="nz.cri.gns.fred.de.DataEntryFormFactory"
%><%@page import="nz.cri.gns.fred.de.DataInputException"
%><%@page import="nz.cri.gns.fred.util.FREDUtil"
%><%@page import="nz.cri.gns.fred.util.FolderUtil"
%><%@page import="nz.cri.gns.fred.util.FeatureUtil"
%><%@page import="nz.cri.gns.fred.util.SampleUtil"
%><%@page import="nz.cri.gns.fred.util.RecordUtil"
%><%@page import="nz.cri.gns.fred.dao.DAOFactory"
%><%@page import="nz.cri.gns.fred.hibernate.util.FredHibernate"
%><%@page import="nz.cri.gns.jsp.JspUtils"
%><%@page import="nz.cri.gns.jsp.PageState"
%><%@page import="java.sql.Connection"
%><%@page import="java.sql.ResultSet"
%><%@page import="java.sql.Statement"
%><%@page import="java.util.Collections"
%><%@page import="java.util.List"
%><%@page import="java.util.Vector"
%><%@page import="nz.cri.gns.auth.Authenticable"
%><%@page import="nz.cri.gns.auth.User"
%><%@page import="nz.cri.gns.auth.InsufficientPrivelegesException"
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
	
		DAOFactory factory = FredHibernate.get().getDAOFactory();
		FolderUtil folderUtil = new FolderUtil(factory);
		FeatureUtil featureUtil = new FeatureUtil(factory);
		SampleUtil sampleUtil = new SampleUtil(factory);
		RecordUtil recordUtil = new RecordUtil(factory);
		
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
				} else if (request.getParameter("docType").equals(FREDConstants.PALEONTOLOGICAL) || request.getParameter("docType").equals(FREDConstants.ADOPTION)) {
					Record record = recordUtil.getRecord(Integer.parseInt(request.getParameter("id")));
					dataEntryForm = DataEntryFormFactory.getRecordDataEntryForm(record.getRecordId().intValue(), folderID, user, factory, provider);
				}
			} catch (Exception e) {
				e.printStackTrace();
				%><tr><td>Error: <%=e%></td></tr><%
			}
			if (dataEntryForm != null)
				dataEntryForm.makeExcelImportHTML(new PrintWriter(out));
		} else if (listName.equals("sampleId")) {
			User user = null;
			try {
		   		user = new User(request.getParameter("user"), request.getParameter("pass"), JspUtils.createDatabaseConnection(state.getSession(), "nz.cri.gns.ip.connection", "ip", state.getContext()));
		   	} catch (Exception e) {
		   		%><tr><td>Error: Invalid username/password</td></td><%
		   	}
		   	if (user != null) {
		   		Sample sample = null;
		   		try {
					sample = sampleUtil.findOrCreateSample(request.getParameter("localityName"), user);
		   		} catch (DataInputException e) {
		   			String[] error = (String[])e.getError().firstElement();
			    	%><tr><td>Error:</td><td><%=error[0]%> - <%=error[1]%></td></tr><%
		   		} catch (InsufficientPrivelegesException e) {
			    	%><tr><td>Error:</td><td><%=e.getMessage()%></td></tr><%
		   		}
				if (sample != null) {
					if (sampleUtil.isAllowedReadSample(user, sample)) {
						%><tr><td><%=sample.getSampleId()%></td></tr><%
					} else {
						%><tr><td>Error: not allowed to read sample</td></tr><%
					}
				} else {
					%><tr><td>Error: no locality found</td></tr><%	
				}
		   	}
		} else if (listName.equals("palList")) {
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
						List<Paleontology> palRecords = recordUtil.getPaleontologyRecords(features[i]);
						for (Paleontology palRecord : palRecords) {
							if (recordUtil.isAllowedReadRecord(user, palRecord.getRecord())) {
								Sample sample = palRecord.getRecord().getSample();
								String recName = FeatureUtil.getFeatureIdentifyingName(features[i]) + ": "
									+ ((!FREDConstants.OUTCROP.equals(features[i].getFeatureType())) ? SampleUtil.getDrillHoleDepthDescription(sample) + ": " : "")
									+ RecordUtil.getRecordName(palRecord);
								%><tr><td><%=recName%></td><td><%=palRecord.getRecordId()%></td></tr><%
							}
						}
					}
				} else {
					%><tr><td>No records found in folder</td><td></td></tr><%
				}
			} catch (Exception e) {
				%><tr><td>Error</td><td></td></tr><%
			}
		} else if (listName.equals("blankPalList")) {
			%><tr><td>No records defined</td><td>-1</td></tr><%
		} else if (listName.equals("locSampleList")) {
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
							List<Sample> samples = new Vector<Sample>();
							samples.addAll(features[i].getSamples());
							Collections.sort(samples);
							for (Sample sample : samples) {
								%><tr><td><%=FeatureUtil.getFeatureIdentifyingName(features[i])
									+ ((!FREDConstants.OUTCROP.equals(features[i].getFeatureType())) ? ": " + SampleUtil.getDrillHoleDepthDescription(sample) : "")%></td><td><%=sample.getSampleId()%></td></tr><%
							}
						}
					}
				} else {
					%><tr><td>No features found in folder</td><td></td></tr><%
				}
			} catch (Exception e) {
				%><tr><td>Error</td><td></td></tr><%
			}
		} else if (listName.equals("blankLocSampleList")) {
			%><tr><td>No samples defined</td><td>-1</td></tr><%
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
					for (Sample sample : FeatureUtil.getSortedSamples(feature)) {
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
							%><tr><td><%=FeatureUtil.getFeatureIdentifyingName(features[i])%></td><td><%=features[i].getFeatureId()%></td></tr><%
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
			<tr><td>NZMS260 - 3-digit</td><td>NZMS260</td></tr>
			<tr><td>NZMS260 - 4-digit</td><td>NZMS260</td></tr>
			<tr><td>NZTM</td><td>NZTM</td></tr>
			<tr><td>NZTopo50 - 3-digit</td><td>NZTopo50</td></tr>
			<tr><td>NZTopo50 - 4-digit</td><td>NZTopo50</td></tr>
			<tr><td>NZ Yard Grid (Sth Isl)</td><td>NZ Yard SthIsl</td></tr>
			<tr><td>NZ Yard Grid (Nth Isl)</td><td>NZ Yard NthIsl</td></tr>
			<tr><td>NZMS1 (Sth Isl) - 3-digit</td><td>NZMS1 SthIsl</td></tr>
			<tr><td>NZMS1 (Sth Isl) - 4-digit</td><td>NZMS1 SthIsl</td></tr>
			<tr><td>NZMS1 (Nth Isl) - 3-digit</td><td>NZMS1 NthIsl</td></tr>
			<tr><td>NZMS1 (Nth Isl) - 4-digit</td><td>NZMS1 NthIsl</td></tr>
			<tr><td>Chatham Island Grid</td><td>Chatham Island Grid</td></tr>
			<tr><td>Auckland Island Grid</td><td>Auckland Island Transverse Mercator</td></tr>
			<tr><td>Campbell Island Grid</td><td>Campbell Island Transverse Mercator</td></tr>
			<tr><td>Lat/Long NZGD49</td><td>NZGD49</td></tr>
			<tr><td>Lat/Long Chatham Isl</td><td>Chatham Island Datum</td></tr>
			<tr><td>Lat/Long NZGD2000</td><td>NZGD2000</td></tr>
			<tr><td>Lat/Long WGS84</td><td>WGS84</td></tr><%
		} else if (listName.equals("localityType")) {
			%><tr><td>Outcrop</td></tr>
			<tr><td>Drillhole</td></tr>
			<tr><td>Vertical Section</td></tr><%
		} else {
			Connection conn = null;
			try {
				conn = FREDUtil.getConnection();
				Statement statement = conn.createStatement();
				ResultSet rs = null;
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
					rs = statement.executeQuery("SELECT name FROM person ORDER BY name");
				} else if (listName.equals("regArea")) {
					rs = statement.executeQuery("SELECT name || '</td><td>' || reg_area_id FROM registration_area ORDER BY name");
				} else if (listName.equals("thickness")) {
					rs = statement.executeQuery("SELECT code || ': ' || name || '</td><td>' || thickness_id FROM bed_thickness ORDER BY code");
				} else if (listName.equals("sedFeature")) {
					rs = statement.executeQuery("SELECT name FROM sedimentary_feature_type ORDER BY code");
				} else if (listName.equals("stageName")) {
					rs = statement.executeQuery("SELECT ag_name || '</td><td>' || ag_id FROM age_view ORDER BY ag_name");
				} else if (listName.equals("stratName")) {
					rs = statement.executeQuery("SELECT su_name FROM sl.strat_unit ORDER BY su_name");
				} else if (listName.equals("weathering")) {
					rs = statement.executeQuery("SELECT code || ': ' || name || '</td><td>' || weathering_id FROM weathering ORDER BY code");
				} else if (listName.equals("labSection")) {
					rs = statement.executeQuery("SELECT l.lab_name || DECODE(ls.code, NULL, NULL, ': ' || ls.code) || '</td><td>' || ls.lab_section_id FROM lab_section ls, sc.lab l WHERE ls.lab_id = l.lab_id ORDER BY l.lab_name, ls.code"); 
				} else if (listName.equals("taxaGroup")) {
					rs = statement.executeQuery("SELECT name FROM taxonomic_group ORDER BY group_id"); 
				}
				while (rs.next()) {
					%><tr><td><%=rs.getString(1).replaceAll(" ", "&nbsp;")%></td></tr><%
				}
				rs.close();
				statement.close();
				conn.close();
			} finally {
				if (conn != null) try {
					conn.close();
				} catch (Exception _e) {
				}
			}
		}
		
		%></table><%
	}
	
	try {
		FredHibernate.get().getDAOFactory().closeSession();
	} catch (Exception e) {
	}
	
	%></body></html>
