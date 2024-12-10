<%@page pageEncoding="utf-8"%>
<%@page import="nz.cri.gns.fred.util.FeatureUtil"%>
<%@page import="nz.cri.gns.fred.hibernate.util.FredHibernate"%>
<%@page import="nz.cri.gns.core.Environment"%>
<%--<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>--%>
<%
    FeatureUtil featureUtil = new FeatureUtil(FredHibernate.get().getDAOFactory());
%>
<table width="550" style="width:550px;" border="0">
<tr><td>
<p><span class="bigheading">About the New Zealand Fossil Record File</span><br/>

<p>The New Zealand Fossil Record File (FRF) is a file of fossil localities, primarily from New Zealand and the Ross Sea region of Antarctica. Originally paper-based, the FRF has now been digitised and made available through a web site operated by <a href="https://www.gns.cri.nz/">GNS Science</a> and governed jointly by the <a href="https://gsnz.org.nz/">Geoscience Society of New Zealand</a> and <a href="https://www.gns.cri.nz/">GNS Science</a>.</p>

<p>To date, the FRF contains <%=featureUtil.getTotalFeatureCount()%> locality records registered at regional recording centres since 1946. Operation of this online version of the FRF is funded by the Ministry of Business, Innovation and Employment through the <a href="https://www.mbie.govt.nz/science-and-technology/science-and-innovation/funding-information-and-opportunities/investment-funds/strategic-science-investment-fund/funded-infrastructure/nationally-significant-collections-and-databases/">Nationally Significant Collections and Databases programme</a>, with the assistance of staff from Auckland, Victoria, Canterbury, and Otago universities.</p>

<p>The FRF includes locality coordinates, collection details, stratigraphic position, relevant sedimentary data, and fossil identifications with paleontological opinion on geological age and paleoenvironment.</p>

<p>New Zealand mainland data are organised according to 1:50,000 scale New Zealand map sheets (map series NZMS260), with fossil localities within each map sheet allocated a unique number.</p>

<p>Some samples collected from FRF localities are stored in the <a href="<%=Environment.getDataUrl()%>/npc/catalogue/index.jsp">National Paleontological Collection</a> at GNS Science. Researchers may access these samples either through an on-site visit or by requesting a loan.</p>

<p><a href="<%=Environment.getDataUrl()%>/staff/email.jsp?id=frf&subject=FRF%20General%20Enquiry">Contact administrator.</a></p>
</td></tr>
</table>