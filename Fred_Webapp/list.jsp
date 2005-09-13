<%@page	extends="nz.cri.gns.fred.FREDIPSysJspPage"
		import="nz.cri.gns.fred.*, nz.cri.gns.fred.data.*, nz.cri.gns.fred.dataentry.*, nz.cri.gns.jsp.*, nz.cri.gns.util.map.*, nz.cri.gns.db.*, nz.cri.gns.intranet.*, nz.cri.gns.db.site.*, java.sql.*, java.text.*, java.net.*, nz.cri.gns.auth.*, java.lang.*, java.util.*, java.io.*"
%><%!	public Authenticable[] getRequiredRights(HttpServletRequest request) { return new Authenticable[0]; }
%><%

	String listName = request.getParameter("listName");
	
	out.println("<html>\n<head>\n</head>\n<body>");
	
	if (listName != null) {
	
		PageState state = new PageState(request, response, getServletContext());
		DBConnection connection = FREDUtils.getFREDConnection(state);
		Statement statement = connection.statement;
		
		ResultSet rs = null;
	
		out.println("<table>");
		
		if (listName.equals("folderContent")) {
			User user = null;
			try {
		   		user = new User(request.getParameter("user"), request.getParameter("pass"), JspUtils.createDatabaseConnection(state.getSession(), "nz.cri.gns.ip.connection", "ip", state.getContext()));
		   	} catch (Exception e) {
		   		out.println("<tr><td>Error: Invalid username/password</td></td>");
		   	}
		   	Folder folder = new Folder(Integer.parseInt(request.getParameter("folderID")), user, state);
		   	if (folder.isAllowedReadLocalities()) {
			   	if (request.getParameter("formType").equals("locality")) {
				   	try {
						for (Iterator i = folder.getAsVector(Folder.FEATURES).iterator(); i.hasNext(); ) {
							try {
								DataEntryForm dataEntryForm = DataEntryFormFactory.getLocalityDataEntryForm(((Integer) i.next()).intValue(), user, state);
								dataEntryForm.makeExcelImportHTML(new PrintWriter(out));
							} catch (Exception e) {}
						}
					} catch (Exception e) {
						out.println("<tr><td>No localities</td><td></td></tr>");
					}
				}
			} else {
				out.println("<tr><td>Error: Insufficient privileges</td></tr>");
			}
		} else if (listName.equals("document")) {
			User user = null;
			try {
		   		user = new User(request.getParameter("user"), request.getParameter("pass"), JspUtils.createDatabaseConnection(state.getSession(), "nz.cri.gns.ip.connection", "ip", state.getContext()));
		   	} catch (Exception e) {
		   		out.println("<tr><td>Error: Invalid username/password</td></td>");
		   	}
		   	DataEntryForm dataEntryForm = null;
		   	try {
		   		if (request.getParameter("docType").equals("locality")) {
					dataEntryForm = DataEntryFormFactory.getLocalityDataEntryForm(Integer.parseInt(request.getParameter("id")), user, state);
				} else if (request.getParameter("docType").equals("sample")) {
					dataEntryForm = DataEntryFormFactory.getSampleDataEntryForm(Integer.parseInt(request.getParameter("id")), user, state);
				}
			} catch (Exception e) {
				out.println("<tr><td>Error: Insufficient privileges</td></tr>");
			}
			if (dataEntryForm != null)
				dataEntryForm.makeExcelImportHTML(new PrintWriter(out));
		} else if (listName.equals("workingLocalityList")) {
			User user = null;
			try {
		   		user = new User(request.getParameter("user"), request.getParameter("pass"), JspUtils.createDatabaseConnection(state.getSession(), "nz.cri.gns.ip.connection", "ip", state.getContext()));
		   	} catch (Exception e) {
		   		out.println("<tr><td>Error: Invalid username/password</td></td>");
		   	}
		   	try {
			   	Folder folder = new Folder(Integer.parseInt(request.getParameter("folderID")), user, state);
		   		if (folder.get(Folder.FEATURES) != null) {
					for (Iterator i = folder.getAsVector(Folder.FEATURES).iterator(); i.hasNext(); ) {
						Feature feature = new Feature(((Integer)i.next()).intValue(), user, state);
						if (feature.getAsString(Feature.STATUS).equals(Audit.STATUS_WORKING) || feature.getAsString(Feature.STATUS).equals(Audit.STATUS_REJECTED))
							out.println("<tr><td>" + feature.getAsString(Feature.FEATURE_NAME) + "</td><td>" + feature.getFeatureID() + "</td></tr>");
					}
				} else {
					out.println("<tr><td>No features found</td><td></td></tr>");
				}
			} catch (Exception e) {
				out.println("<tr><td>Error</td><td></td></tr>");
			}
		} else if (listName.equals("blankWorkingLocalityList")) {
			out.println("<tr><td>No features defined</td><td></td></tr>");
		} else if (listName.equals("folderList")) {
			User user = null;
			try {
		   		user = new User(request.getParameter("user"), request.getParameter("pass"), JspUtils.createDatabaseConnection(state.getSession(), "nz.cri.gns.ip.connection", "ip", state.getContext()));
		   	} catch (Exception e) {
		   		out.println("<tr><td>Error: Invalid username/password</td></td>");
		   	}
		   	try {
			   	FolderList folderList = new FolderList(user, state);
		   		if (folderList.getPersonalFolderCount() > 0) {
					for (Iterator i = folderList.getPersonalFolders().iterator(); i.hasNext(); ) {
						KeyValueObject kv = (KeyValueObject) i.next();
						out.println("<tr><td>" + kv.getValue() + "</td><td>" + kv.getKey() + "</td></tr>");
					}
				} else {
					out.println("<tr><td>No folders found</td><td></td></tr>");
				}
			} catch (Exception e) {
				out.println("<tr><td>No folders defined</td><td></td></tr>");
			}
		} else if (listName.equals("blankFolderList")) {
			out.println("<tr><td>No folders defined</td><td></td></tr>");
		} else if (listName.equals("datum")) {
			out.println("<tr><td>New Zealand Map Grid</td><td>NZMG</td></tr>");
			out.println("<tr><td>NZMS260</td><td>NZMS260</td></tr>");
			out.println("<tr><td>NZ Yard Grid (Sth Isl)</td><td>NZYS</td></tr>");
			out.println("<tr><td>NZ Yard Grid (Nth Isl)</td><td>NZYN</td></tr>");
			out.println("<tr><td>NZMS1 (Sth Isl)</td><td>NZMS1S</td></tr>");
			out.println("<tr><td>NZMS1 (Nth Isl)</td><td>NZMS1N</td></tr>");
			out.println("<tr><td>Chatham Island Grid</td><td>CHAT</td></tr>");
			out.println("<tr><td>Auckland Island Grid</td><td>AUCK</td></tr>");
			out.println("<tr><td>Campbell Island Grid</td><td>CAMP</td></tr>");
			out.println("<tr><td>Lat/Long NZGD49</td><td>NZGD49</td></tr>");
			out.println("<tr><td>Lat/Long WGS84/NZGD2000</td><td>WGS84</td></tr>");
		} else if (listName.equals("localityType")) {
			out.println("<tr><td>Outcrop</td></tr>");
			out.println("<tr><td>Drillhole</td></tr>");
			out.println("<tr><td>Vertical Section</td></tr>");
		} else if (listName.equals("sedFeature")) {
			rs = statement.executeQuery("SELECT code, name FROM sedimentary_feature_type ORDER BY code");
			try {
				while (rs.next()) {
					out.println("<tr><td>" + rs.getString(1) + ": " + rs.getString(2).replaceAll(" ", "&nbsp;") + "</td><td>" + rs.getString(2) + "</td></tr>");
					out.println("<tr><td>" + rs.getString(1) + "*: " + rs.getString(2).replaceAll(" ", "&nbsp;") + " abundant</td><td>" + rs.getString(2) + "$</td></tr>");
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
					out.println("<tr><td>" + rs.getString(1).replaceAll(" ", "&nbsp;") + "</td></tr>");
				}
			} catch (Exception e) {}
		}
		
		out.println("</table>");
	}
	
	out.println("</body>\n</html>");
%>
