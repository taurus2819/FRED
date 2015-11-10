<%@page pageEncoding="utf-8"
%><%@page	extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@page import="nz.cri.gns.jsp.IconnedLink"
%><%@page import="nz.cri.gns.db.DBUtils"
%><%@page import="java.util.Collections"
%><%@page import="java.util.List"
%><%@page import="java.util.Set"
%><%@page import="java.util.HashSet"
%><%@page import="java.util.Vector"
%><%@page import="java.util.Date"
%><%@page import="java.util.Calendar"
%><%@page import="java.util.GregorianCalendar"
%><%@page import="nz.cri.gns.fred.dao.DAOFactory"
%><%@page import="nz.cri.gns.fred.hibernate.util.FredHibernate"
%><%@page import="nz.cri.gns.fred.model.FREDConstants"
%><%@page import="nz.cri.gns.fred.model.Adoption"
%><%@page import="nz.cri.gns.fred.model.Paleontology"
%><%@page import="nz.cri.gns.fred.model.Sample"
%><%@page import="nz.cri.gns.fred.model.Record"
%><%@page import="nz.cri.gns.fred.query.FREDRecordQuery"
%><%@page import="nz.cri.gns.fred.util.AuditUtil"
%><%@page import="nz.cri.gns.fred.util.FeatureUtil"
%><%@page import="nz.cri.gns.fred.util.SampleUtil"
%><%@page import="nz.cri.gns.fred.util.RecordUtil"
%><%@page import="nz.cri.gns.fred.util.FREDUtil"
%><%@page import="nz.cri.gns.jsp.PageState"
%><%@page import="nz.cri.gns.auth.domain.User"
%><%!
	public String getName(HttpServletRequest request) {
		try {
			return "FRED :: Confidential Data List for " + ((User)getUser(request.getSession())).getFullName(); 
		} catch (Exception e) {
			return "FRED :: The Fossil Record Electronic Database";
		}
	}
	
%><%
	DAOFactory factory = FredHibernate.get().getDAOFactory();
	User user = (User)getUser(session);
	Integer userId = new Integer(user.getId());
	AuditUtil auditUtil = new AuditUtil(factory);
	RecordUtil recordUtil = new RecordUtil(factory);
	
	ExtranetTemplate et = getExtranetTemplate();
	et.setDisplayLoadingMessage(true);
	addButtons(et, new IconnedLink[] {
			new IconnedLink("buildframe_record.jsp", "images/search.gif", "Search for Confidential Data"),
			new IconnedLink("confid_list.jsp?q=" + Math.random(), "images/lock.gif", "Data Soon to be Open-File"),
			new IconnedLink("confid_list.jsp?Type=All&q=" + Math.random(), "images/lock.gif", "All Data")
		});

	if (request.getParameter("ActionType") != null && request.getParameterValues("AuditIDs") != null) {
		if ("EditLapseDate".equals(request.getParameter("ActionType"))) {
			if (!"-".equals(request.getParameter("ConfidPeriod")))
			auditUtil.updateLapseDates(request.getParameterValues("AuditIDs"), new Double(request.getParameter("ConfidPeriod")));
		} else if ("ClearConfid".equals(request.getParameter("ActionType"))) {
			auditUtil.clearConfidentialites(request.getParameterValues("AuditIDs"));
		}
	}
	
	drawTop(out, et, request, response);
	try {
		List<Sample> confidSamples = null;
		List<Adoption> confidAdoptions = null;
		List<Paleontology> confidPaleontologies = null;
		List<Paleontology> confidPalLists = null;
		if ("All".equals(request.getParameter("Type"))) {
			confidSamples = auditUtil.getConfidentialSamples(user);
			confidAdoptions = auditUtil.getConfidentialAdoptionRecords(user);
			confidPaleontologies = auditUtil.getConfidentialPaleontologyRecords(user);
			confidPalLists = auditUtil.getConfidentialPalLists(user);
		} else if ("Query".equals(request.getParameter("Type"))) {
			PageState state = new PageState(request, response, getServletContext());
			FREDRecordQuery query = FREDUtil.getFREDRecordQuery(state);
			String whereSQL = query.getHQLQuery();
			//System.out.println(whereSQL);
			List<Record> records = recordUtil.getListFromQueryBuilder(whereSQL);
			confidSamples = new Vector<Sample>();
			Set<Sample> sampleSet = new HashSet<Sample>();
			confidAdoptions = new Vector<Adoption>();
			confidPaleontologies = new Vector<Paleontology>();
			confidPalLists = new Vector<Paleontology>();
			for (Record record : records) {
				sampleSet.add(record.getSample());
				if (record.getAudit().getConfidentialFlag() && record.getAudit().getCreatedById().equals(userId)) {
					if (RecordUtil.getRecordType(record).equals(FREDConstants.ADOPTION))
						confidAdoptions.add(record.getAdoption());
					else
						confidPaleontologies.add(record.getPaleontology());	
				}
				if (record.getPalListAudit() != null && record.getPalListAudit().getConfidentialFlag() && record.getPalListAudit().getCreatedById().equals(userId))
					confidPalLists.add(record.getPaleontology());
			}
			for (Sample sample : sampleSet) {
				if (!FREDConstants.OUTCROP.equals(sample.getFeature().getFeatureType()) && sample.getAudit().getConfidentialFlag() && sample.getAudit().getCreatedById().equals(userId))
					confidSamples.add(sample);
			}
			Collections.sort(confidSamples);
			Collections.sort(confidAdoptions);
			Collections.sort(confidPaleontologies);
			Collections.sort(confidPalLists);
		} else {
			GregorianCalendar cal = new GregorianCalendar();
			cal.add(Calendar.MONTH, 7);
			Date lapseDate = cal.getTime();
			confidSamples = auditUtil.getConfidentialSamples(user, lapseDate);
			confidAdoptions = auditUtil.getConfidentialAdoptionRecords(user, lapseDate);
			confidPaleontologies = auditUtil.getConfidentialPaleontologyRecords(user, lapseDate);
			confidPalLists = auditUtil.getConfidentialPalLists(user, lapseDate);
		}
	
		%><script><!--
			function selectAll(type) {
				if (document.confidForm.AuditIDs.length) {
					for (var i=0; i<document.confidForm.AuditIDs.length; i++) {
						if (document.confidForm.AuditIDs[i].id.indexOf(type) >= 0) {
							document.confidForm.AuditIDs[i].checked = true;
						}
					}
				} else {
					if (document.confidForm.AuditIDs.id.indexOf(type) >= 0) {
						document.confidForm.AuditIDs.checked = true;
					}
				}
			}
			function unselectAll(type) {
				if (document.confidForm.AuditIDs.length) {
					for (var i=0; i<document.confidForm.AuditIDs.length; i++) {
						if (document.confidForm.AuditIDs[i].id.indexOf(type) >= 0) {
							document.confidForm.AuditIDs[i].checked = false;
						}
					}
				} else {
					if (document.confidForm.AuditIDs.id.indexOf(type) >= 0) {
						document.confidForm.AuditIDs.checked = false;
					}
				}
			}
			//--></script>
			<form name="confidForm" method="get" action="confid_list.jsp"><%
		
		//List samples
		if (confidSamples != null && confidSamples.size() > 0) {
			%><p><%
			startDETable(pageContext);
			%><table border="0" cellspacing="0" cellpadding="2" width="550">
			<tr><td colspan="6" class="deHeading">Confidential Drillhole/Vertical Section Samples</td></tr>
			<tr><th style="text-align: left" colspan="2">Locality&nbsp;&nbsp;</th><th style="text-align: left">Sample&nbsp;&nbsp;</th><th style="text-align: left">Lapse Date&nbsp;&nbsp;</th><th style="text-align: left">Access List</th></tr><%
			for (Sample sample : confidSamples) {
				%><tr>
				<td style="text-align: left"><input type="checkbox" name="AuditIDs" id="Samp<%=sample.getAudit().getAuditId()%>" value="<%=sample.getAudit().getAuditId()%>" /></td>
				<td style="text-align: left"><a href="detail.jsp?FeatID=<%=sample.getFeature().getFeatureId()%>"><%=FeatureUtil.getFeatureIdentifyingName(sample.getFeature())%></a>&nbsp;&nbsp;</td>
				<td style="text-align: left"><a href="detail.jsp?ID=<%=sample.getSampleId()%>"><%=SampleUtil.getDrillHoleDepthDescription(sample)%></a>&nbsp;&nbsp;</td>
				<td style="text-align: left"><%=FREDUtil.formatDateForOutput(sample.getAudit().getConfidLapseDate())%></td>
				<td style="text-align: left"><%=auditUtil.getConfidAccessListDescription(sample.getAudit())%></td>
				<td style="text-align: left"><a href="set_confidentiality.jsp?SampIDs=<%=sample.getSampleId()%>"><img src="images/lock.gif" border="0" height="20" width="20" alt="Edit Confidentiality" title="Edit Confidentiality" /></a></td>
				<td style="text-align: left"><a href="confid_list.jsp?ActionType=ClearConfid<%=(request.getParameter("Type") != null) ? "&Type=" + request.getParameter("Type") : ""%>&AuditIDs=<%=sample.getAudit().getAuditId()%>&q=<%=Math.random()%>"><img src="images/lock_cancel.gif" border="0" height="20" width="20" alt="Clear Confidentiality" /></a></td>
				</tr><%
			}
			%><tr><td colspan="5" style="text-align: left"><a href="javascript:selectAll('Samp')">Select All</a>&nbsp;&nbsp;<a href="javascript:unselectAll('Samp')">Unselect All</a></td></tr>
			</table><%
			endDETable(pageContext);
			%></p><%
		}
	
		//List adoptions
		if (confidAdoptions != null && confidAdoptions.size() > 0) {
			%><p><%
			startDETable(pageContext);
			%><table border="0" cellspacing="0" cellpadding="2" width="550">
			<tr><td colspan="6" class="deHeading">Confidential Adoption Records</td></tr>
			<tr><th style="text-align: left" colspan="2">Locality&nbsp;&nbsp;</th><th style="text-align: left">Adoption Record&nbsp;&nbsp;</th><th style="text-align: left">Lapse Date&nbsp;&nbsp;</th><th style="text-align: left">Access List</th></tr><%
			for (Adoption adoption : confidAdoptions) {
				Record record = adoption.getRecord();
				%><tr>
				<td style="text-align: left"><input type="checkbox" name="AuditIDs" id="Ado<%=record.getAudit().getAuditId()%>" value="<%=record.getAudit().getAuditId()%>" /></td>
				<td style="text-align: left"><a href="detail.jsp?ID=<%=record.getSample().getSampleId()%>"><%=FeatureUtil.getFeatureIdentifyingName(record.getSample().getFeature())%><%=(SampleUtil.getDrillHoleDepthDescription(record.getSample()) != null) ? "<br />" + SampleUtil.getDrillHoleDepthDescription(record.getSample()) : ""%></a>&nbsp;&nbsp;</td>
				<td style="text-align: left"><a href="detail.jsp?ID=<%=record.getSample().getSampleId()%>"><%=RecordUtil.getRecordName(record)%></a>&nbsp;&nbsp;</td>
				<td style="text-align: left"><%=FREDUtil.formatDateForOutput(record.getAudit().getConfidLapseDate())%></td>
				<td style="text-align: left"><%=auditUtil.getConfidAccessListDescription(record.getAudit())%></td>
				<td style="text-align: left"><a href="set_confidentiality.jsp?RecIDs=<%=record.getRecordId()%>"><img src="images/lock.gif" border="0" height="20" width="20" alt="Edit Confidentiality" title="Edit Confidentiality" /></a></td>
				<td style="text-align: left"><a href="confid_list.jsp?ActionType=ClearConfid<%=(request.getParameter("Type") != null) ? "&Type=" + request.getParameter("Type") : ""%>&AuditIDs=<%=record.getAudit().getAuditId()%>&q=<%=Math.random()%>"><img src="images/lock_cancel.gif" border="0" height="20" width="20" alt="Clear Confidentiality" /></a></td>
				</tr><%
			}
			%><tr><td colspan="5" style="text-align: left"><a href="javascript:selectAll('Ado')">Select All</a>&nbsp;&nbsp;<a href="javascript:unselectAll('Ado')">Unselect All</a></td></tr>
			</table><%
			endDETable(pageContext);
			%></p><%
		}
			
		//List paleontologies
		if (confidPaleontologies != null && confidPaleontologies.size() > 0) {
			%><p><%
			startDETable(pageContext);
			%><table border="0" cellspacing="0" cellpadding="2" width="550">
			<tr><td colspan="7" class="deHeading">Confidential Paleontology Records</td></tr>
			<tr><th style="text-align: left" colspan="2">Locality&nbsp;&nbsp;</th><th style="text-align: left">Paleontology Record&nbsp;&nbsp;</th><th style="text-align: left">Lapse Date&nbsp;&nbsp;</th><th style="text-align: left">Access List</th></tr><%
			for (Paleontology paleontology : confidPaleontologies) {
				Record record = paleontology.getRecord();
				%><tr>
				<td style="text-align: left"><input type="checkbox" name="AuditIDs" id="Pal<%=record.getAudit().getAuditId()%>" value="<%=record.getAudit().getAuditId()%>" /></td>
				<td style="text-align: left"><a href="detail.jsp?ID=<%=record.getSample().getSampleId()%>"><%=FeatureUtil.getFeatureIdentifyingName(record.getSample().getFeature())%><%=(SampleUtil.getDrillHoleDepthDescription(record.getSample()) != null) ? "<br />" + SampleUtil.getDrillHoleDepthDescription(record.getSample()) : ""%></a>&nbsp;&nbsp;</td>
				<td style="text-align: left"><a href="detail.jsp?ID=<%=record.getSample().getSampleId()%>"><%=RecordUtil.getRecordName(record)%></a>&nbsp;&nbsp;</td>
				<td style="text-align: left"><%=FREDUtil.formatDateForOutput(record.getAudit().getConfidLapseDate())%></td>
				<td style="text-align: left"><%=auditUtil.getConfidAccessListDescription(record.getAudit())%></td>
				<td style="text-align: left"><a href="set_confidentiality.jsp?RecIDs=<%=record.getRecordId()%>"><img src="images/lock.gif" border="0" height="20" width="20" alt="Edit Confidentiality" title="Edit Confidentiality"/></a></td>
				<td style="text-align: left"><a href="confid_list.jsp?ActionType=ClearConfid<%=(request.getParameter("Type") != null) ? "&Type=" + request.getParameter("Type") : ""%>&AuditIDs=<%=record.getAudit().getAuditId()%>&q=<%=Math.random()%>"><img src="images/lock_cancel.gif" border="0" height="20" width="20" alt="Clear Confidentiality" /></a></td>
				</tr><%
			}
			%>
			<tr><td colspan="5" style="text-align: left"><a href="javascript:selectAll('Pal')">Select All</a>&nbsp;&nbsp;<a href="javascript:unselectAll('Pal')">Unselect All</a></td></tr>
			</table><%
			endDETable(pageContext);
			%></p><%
		}
			
		//List pal lists
		if (confidPalLists != null && confidPalLists.size() > 0) {
			%><p><%
			startDETable(pageContext);
			%><table border="0" cellspacing="0" cellpadding="2" width="550">
			<tr><td colspan="7" class="deHeading">Confidential Paleontology Taxonomic Lists</td></tr>
			<tr><th style="text-align: left" colspan="2">Locality&nbsp;&nbsp;</th><th style="text-align: left">Paleontology Record&nbsp;&nbsp;</th><th style="text-align: left">Lapse Date&nbsp;&nbsp;</th><th style="text-align: left">Access List</th></tr><%
			for (Paleontology paleontology : confidPalLists) {
				Record record = paleontology.getRecord();
				%><tr>
				<td style="text-align: left"><input type="checkbox" name="AuditIDs" id="PList<%=record.getPalListAudit().getAuditId()%>" value="<%=record.getPalListAudit().getAuditId()%>" /></td>
				<td style="text-align: left"><a href="detail.jsp?ID=<%=record.getSample().getSampleId()%>"><%=FeatureUtil.getFeatureIdentifyingName(record.getSample().getFeature())%><%=(SampleUtil.getDrillHoleDepthDescription(record.getSample()) != null) ? "<br />" + SampleUtil.getDrillHoleDepthDescription(record.getSample()) : ""%></a>&nbsp;&nbsp;</td>
				<td style="text-align: left"><a href="detail.jsp?ID=<%=record.getSample().getSampleId()%>"><%=RecordUtil.getRecordName(record)%>&nbsp;&nbsp;</a></td>
				<td style="text-align: left"><%=FREDUtil.formatDateForOutput(record.getPalListAudit().getConfidLapseDate())%></td>
				<td style="text-align: left"><%=auditUtil.getConfidAccessListDescription(record.getPalListAudit())%></td>
				<td style="text-align: left"><a href="set_confidentiality.jsp?RecIDs=<%=record.getRecordId()%>"><img src="images/lock.gif" border="0" height="20" width="20" alt="Edit Confidentiality" title="Edit Confidentiality"/></a></td>
				<td style="text-align: left"><a href="confid_list.jsp?ActionType=ClearConfid<%=(request.getParameter("Type") != null) ? "&Type=" + request.getParameter("Type") : ""%>&AuditIDs=<%=record.getPalListAudit().getAuditId()%>&q=<%=Math.random()%>"><img src="images/lock_cancel.gif" border="0" height="20" width="20" alt="Clear Confidentiality" /></a></td>
				</tr><%
			}
			%><tr><td colspan="5" style="text-align: left"><a href="javascript:selectAll('PList')">Select All</a>&nbsp;&nbsp;<a href="javascript:unselectAll('PList')">Unselect All</a></td></tr>
			</table><%
			endDETable(pageContext);
			%></p><%
		}
		
		%><p><%
		//Selected Actions box
		startDETable(pageContext);
		%><table border="0" width="550">
		<tr><td colspan="11" class="deHeading">Selected Actions</td></tr>
		<tr>
		<td><a href="javascript:document.confidForm.ActionType.value='EditLapseDate';document.confidForm.submit();"><img src="images/lock.gif" border="0" height="20" width="20" alt="Edit Lapse Date" title="Edit Lapse Date"/></a></td>
		<td class="heading" style="text-align: left"><a href="javascript:document.confidForm.ActionType.value='EditLapseDate';document.confidForm.submit();">Edit Lapse Date</a>&nbsp;
		<select name="ConfidPeriod"><option value="-">-- Choose --</option>
		<option value="0.5">6 months</option>
		<option value="1">1 year</option>
		<option value="2">2 years</option>
		<option value="5">5 years</option>
		</select>
		</td>
		</tr>
		<tr>
		<td><a href="javascript:document.confidForm.ActionType.value='ClearConfid';document.confidForm.submit();"><img src="images/lock_cancel.gif" border="0" height="20" width="20" alt="Clear Confidentiality" /></a></td>
		<td class="heading" style="text-align: left"><a href="javascript:document.confidForm.ActionType.value='ClearConfid';document.confidForm.submit();">Clear Confidentiality</a></td>
		</tr>
		</table><%		
		endDETable(pageContext);
		%></p><%
		
		if (request.getParameter("Type") != null) {
			%><input type="hidden" name="Type" value="<%=request.getParameter("Type")%>" /><%
		}
		%><input type="hidden" name="ActionType" value="" />
		<input type="hidden" name="q" value="<%=Math.random()%>" />
		</form><%

	} catch (Exception e) {
		e.printStackTrace();
	}
	drawBottom(out, et);

	
	try {
		FredHibernate.get().getDAOFactory().closeSession();
	} catch (Exception e) {
	}

%>


