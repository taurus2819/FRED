<%@page pageEncoding="utf-8"%>
<%@page import="nz.cri.gns.fred.util.FeatureUtil"%>
<%@page import="nz.cri.gns.fred.util.FREDUtil"%>
<%@page import="nz.cri.gns.fred.hibernate.util.FredHibernate"%>
<%
    FeatureUtil featureUtil = new FeatureUtil(FredHibernate.get().getDAOFactory());
%>
<table width="550" style="width:550px;" border="0">
    <tr>
        <td colspan="2">
            <p>FRED is a computer database for the New Zealand Fossil Record File(FRF).  This is a recording scheme for fossil localities in NZ and nearby regions including SE Pacific Islands and the Ross Sea region of Antarctica, and is jointly managed by <a href="http://www.gsnz.org.nz" target="gsnz">Geoscience Society of New Zealand</a> and <a href="http://www.gns.cri.nz" target="gns">GNS Science</a>.  FRED is operated by GNS Science through the FRST National Paleontological Databases Programme, with the assistance of staff at Auckland, Victoria, Canterbury and Otago universities.</p>
        </td>
    </tr>
    <tr><td><img src="images/blank.gif" width="275" height="3"></td><td><img src="images/blank.gif" width="275" height="3"></td></tr>
    <tr>
        <td>
            <img src="images/graptolites.jpg" width="250" height="180" class="border" alt="graptolites"/>
        </td>
        <td>
            FRED primarily contains initial registration information about fossil localities:
            <ul>
                <li>geographic coordinates</li>
                <li>collecting details, including geological context</li>
                <li>initial repository of specimens</li>
            </ul>
        </td>
    </tr>
    <tr><td><img src="images/blank.gif" width="275" height="3"></td><td><img src="images/blank.gif" width="275" height="3"></td></tr>
    <tr>
        <td colspan="2">
            For some localities it also provides additional data deposited by government, university, industry, and amateur geoscientists:
            <ul>
                <li>taxonomic determinations</li>
                <li>paleontological interpretations of stratigraphic age and paleoenvironment</li>
                <li>geological opinions about stratigraphic age</li>
            </ul>
            <p>FRED contains all <%=featureUtil.getTotalFeatureCount()%> locality records registered at regional recording centres since 1946, and was last updated on <%=FREDUtil.formatDateForOutput(featureUtil.getLastFeatureApprovalDate())%>.</p>
            <p>Some samples collected from FRF localities are stored in the <a href="http://data.gns.cri.nz/npc">National Paleontological Collection (NPC)</a> at GNS Science.  Researchers may access these samples either through an on-site visit or by requesting a loan.</p>
            <p>Geographic coordinates can be queried and downloaded by anyone.  Access to detailed information, and capability to contribute new site records or other data requires a username and password - registered users agree to abide by accepted <a href="conditions.jsp">Conditions of Use</a>.</p>
            <!--p>Registered users of FRED can obtain a live template for uploading their bulk data directly into the database from their <a href="folder_list.jsp">Data Entry page</a>. For unregistered researchers, who would like to contribute to FRED with their data, or those who prefer our database managers upload the information, we've provided a <a href="FRED_entry_template.xls">static Excel upload template</a>, which can be filled out and e-mailed for upload to <a href="mailto:J.Simes@gns.cri.nz?cc=m.terezow@gns.cri.nz&subject=Date to upload to FRED please&body=Hi John please find included some data to upload to FRED.%0A%0A">John Simes</a> or <a href="mailto:m.terezow@gns.cri.nz?cc=J.Simes@gns.cri.nz&subject=Date to upload to FRED please&body=Hi Marianna please find included some data to upload to FRED.%0A%0A">Marianna Terezow<a>. The <a href="FRED_entry_template.xls">spreadsheet</a> provides options and examples of the type and format of data accepted.</p-->
            <p>If you are a new user please see the <a href="quick_start.jsp">Quick Start Guide</a>.</p>
            <p><span class="smalltext">Macintosh users: webmaps require Safari or Netscape 7+ browsers</span></p>
        </td>
    </tr>
    <tr>
        <td colspan="2">
            <p>Cite as:<br>
                GNS Science & Geoscience Society of New Zealand. (2003). New Zealand Fossil Record File [Data set]. GNS Science. <a href="https://doi.org/10.21420/JQQB-NK89">https://doi.org/10.21420/JQQB-NK89</a></p>
        </td>
    </tr>
</table>
