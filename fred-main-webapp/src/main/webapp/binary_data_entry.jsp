<%@page pageEncoding="utf-8"
        %><%@page extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
        %><%@page import="nz.cri.gns.fred.util.FolderUtil"
        %><%@page import="nz.cri.gns.fred.util.RecordUtil"
        %><%@page import="nz.cri.gns.fred.util.TaxonomicUtil"
        %><%@page import="nz.cri.gns.fred.dao.DAOFactory"
        %><%@page import="nz.cri.gns.fred.util.FREDUtil"
        %><%@page import="nz.cri.gns.fred.model.FREDConstants"
        %><%@page import="nz.cri.gns.fred.model.UserFolder"
        %><%@page import="nz.cri.gns.fred.model.PaleontologyListEntry"
        %><%@page import="nz.cri.gns.fred.website.WebsiteConstants"
        %><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
        %><%@page import="nz.cri.gns.jsp.PageState"
        %><%@page import="nz.cri.gns.auth.User"
        %><%@page import="nz.cri.gns.fred.hibernate.util.FredHibernate"
        %><%@page import="nz.cri.gns.db.metadata.DocumentAttacher"
        %><%@page import="nz.cri.gns.db.metadata.MetadataRecord"
        %><%@page import="nz.cri.gns.jsp.IconnedLink"
        %><%@page import="nz.cri.gns.html.select.SelectBox"
        %><%@page import="nz.cri.gns.html.Attributes"
        %><%@page import="java.io.PrintWriter"
        %><%@page import="nz.cri.gns.fred.model.Feature"
        %><%@page import="nz.cri.gns.fred.model.Sample"
        %><%@page import="nz.cri.gns.fred.model.Record"
        %><%@page import="nz.cri.gns.fred.util.FeatureUtil"
        %><%@page import="nz.cri.gns.fred.util.SampleUtil"
        %><%@page import="nz.cri.gns.fred.util.RecordUtil"
        %><%@page import="java.util.Vector"
        %><%@page import="nz.cri.gns.dataaccess.StorageAccessException"
%><%!
    public String getName(HttpServletRequest request) {
        return "FRED :: Add Image/File";
    }

    public class Item extends java.lang.Object {

        static final String FEATURE = "FEATURE";
        static final String SAMPLE = "SMP";
        static final String RECORD = "RECORD";
        static final String ADOPTION = FREDConstants.ADOPTION;
        static final String PALEONTOLOGICAL = FREDConstants.PALEONTOLOGICAL;
        String id;
        int intID;
        String type;
        String title;
        String subtype;
        String docType;
        String palListId;
        int loadID;
        Feature feature;
        Sample sample;
        Record record;
        PaleontologyListEntry palListEntry;
        DAOFactory factory = FredHibernate.get().getDAOFactory();
        FeatureUtil featureUtil = new FeatureUtil(factory);
        SampleUtil sampleUtil = new SampleUtil(factory);
        RecordUtil recordUtil = new RecordUtil(factory);

        public Item(String id, String type) throws StorageAccessException {
            this.id = id;
            this.type = type;
            this.intID = Integer.parseInt(this.id);
            this.loadID = this.intID;

            if (this.type.equals(Item.RECORD)) {
                docType = "RECORD";
                Record record = recordUtil.getRecord(intID);
                this.title = record.toString();
                this.subtype = recordUtil.getRecordType(record);
            } else if (this.type.equals("SMP")) {
                docType = "SAMPLE";
                Sample sample = sampleUtil.getSample(intID);
                this.title = sample.toString();
                this.subtype = "Sample";
            } else {
                docType = "FEATURE";
                Feature feature = featureUtil.getFeature(intID);
                this.title = feature.getFeatureName();
                this.subtype = feature.getFeatureType();
            }
        }

        public String getVariableName() {
            if (type.equals(FEATURE)) {
                return "FeatIDs";
            } else if (type.equals(SAMPLE)) {
                return "SampIDs";
            } else if (type.equals(RECORD)) {
                return "RecIDs";
            } else {
                return "";
            }
        }
    }

%><%
    PageState state = new PageState(request, response, getServletContext());
    DAOFactory factory = FredHibernate.get().getDAOFactory();
    User user = (User) getUser(session);
    FolderUtil folderUtil = new FolderUtil(factory);
    RecordUtil recordUtil = new RecordUtil(factory);
    TaxonomicUtil taxonomicUtil = new TaxonomicUtil(factory);

    ExtranetTemplate et = getExtranetTemplate();

    et.setDisplayLoadingMessage(true);
    et.setUseNavigationColumn(false);
    addButtons(et, new IconnedLink[]{
                new IconnedLink((String) session.getAttribute(WebsiteConstants.DATA_ENTRY_REDIRECT) + "&q=" + Math.random(), "images/back_arrow.gif", "Back")
            });

    drawTop(out, et, request, response);

    try {
        HttpServletRequest httpRequest = DocumentAttacher.decodeRequest(request);
        String foldID = httpRequest.getParameter("FoldID");
        UserFolder folder = folderUtil.getUserFolder(Integer.parseInt(foldID), user);

        if (folder.isAllowedEditLocalities()) {
            String[] featureIDs = httpRequest.getParameterValues("FeatIDs");
            String[] sampleIDs = httpRequest.getParameterValues("SampIDs");
            String[] recordIDs = httpRequest.getParameterValues("RecIDs");

            featureIDs = (featureIDs == null ? new String[]{} : featureIDs);
            sampleIDs = (sampleIDs == null ? new String[]{} : sampleIDs);
            recordIDs = (recordIDs == null ? new String[]{} : recordIDs);
            
            //PDB-67 attach images for inline table records
            String fid = httpRequest.getParameter("ID");
            if (fid!=null && !fid.isEmpty()) {
                // ensure its a feature not a folder
                String type = httpRequest.getParameter("RecType");
                if (type !=null && !type.isEmpty()) {
                    if ("Vertical SectionDrillholeOutcrop".indexOf(type) >-1) {
                        if (featureIDs.length==0){
                            featureIDs =new String[]{fid};
                        } else {
                            String[] tmp = featureIDs;
                            featureIDs = new String[tmp.length+1];
                            System.arraycopy(tmp, 0, featureIDs, 0, tmp.length);
                        }
                   }
               }
            }
            

            Vector<Item> items = new Vector<Item>();

            for (String id : featureIDs) {
                try {
                    items.add(new Item(id, Item.FEATURE));
                } catch (StorageAccessException sae1) {
                }
            }
            for (String id : sampleIDs) {
                try {
                    items.add(new Item(id, Item.SAMPLE));
                } catch (StorageAccessException sae2) {
                }
            }
            for (String id : recordIDs) {
                try {
                    Item item = new Item(id, Item.RECORD);
                    if (item.subtype == FREDConstants.PALEONTOLOGICAL) {
                        item.palListId = httpRequest.getParameter("PalListID" + item.id);
                        if ("-".equals(item.palListId) || "".equals(item.palListId)) {
                            item.palListId = null;
                        }
                        int loadId = item.intID;
                        if (item.palListId != null) {
                            item.docType = "PAL_LIST";
                            item.loadID = Integer.parseInt(item.palListId);
                            item.palListEntry = taxonomicUtil.getPaleontologyListEntry(item.loadID);
                        }
                    }
                    items.add(item);

                } catch (StorageAccessException sae3) {
                }
            }

            if (items != null && items.size() > 0) {
                startDETable(pageContext);
                out.println("    <form enctype=\"multipart/form-data\" method=\"post\" action=\"binary_data_entry.jsp\">");
                out.println("        <input type=\"hidden\" name=\"FoldID\" value=\"" + foldID + "\">");
                out.println("        <input type=\"hidden\" name=\"Action\" value=\"Insert\">");
                for (Item item : items) {
                    //int itemID = Integer.parseInt(item.id);
                    out.println("        <input type=\"hidden\" name=\"" + item.getVariableName() + "\" value=\"" + item.id + "\">");
                    if (item.subtype == FREDConstants.PALEONTOLOGICAL && item.palListId != null) {
                        out.println("        <input type=\"hidden\" name=\"PalListID" + item.id + "\" value=\"" + item.palListId + "\">");
                    }
                }
                out.println("<table border=\"0\" width=\"550\">");
                out.println("        <tr><td colspan=\"2\" class=\"deHeading\">Add Image/File</td></tr>");
                out.println("        <tr><td style=\"text-align:left\" class=\"heading\">File<br><span class=\"smalltext\">The following types of files can be loaded: images (JPEG, TIFF, GIF, BMP), text, Microsoft Word/Excel and PDF files</span></td><td style=\"text-align:left\"><input type=\"file\" name=\"Upload\"></td></tr>");
                out.println("        <tr><td style=\"text-align:left\" class=\"heading\">Name<br><span class=\"smalltext\">If different to filename</span></td><td style=\"text-align:left\"><input type=\"text\" name=\"Name\"></td></tr>");
                out.println("        <tr><td style=\"text-align:left\" class=\"heading\">Description</td><td style=\"text-align:left\"><input type=\"text\" name=\"Desc\"></td></tr>");
                out.println("        <tr><td style=\"text-align:left\"><input type=\"submit\" value=\"Upload\"></td></tr>");
                out.println("</table>");
                out.println("    </form>");
                endDETable(pageContext);
             
                int docID = 0;
                Item item = null;                
                DocumentAttacher attacher = null;
                for (int j=0; j < items.size(); j++) {               
                    item = items.get(j);                    
                    try {
                        attacher = FREDUtil.getDocumentAttacher(item.docType, state);
                        MetadataRecord[] mrs = attacher.getDocumentsForId(item.loadID);
                        String action = httpRequest.getParameter("Action");
                        if (action != null) {
                            try {
                                if (action.equals("Insert")) {                                    
                                    if (j == 0) {
                                        //Insert into META_CAT only for the first item's attachment.
                                        // For subsequent items with the same attachment, use only new attachment's metaID
                                        docID = attacher.insertDocument(item.loadID, httpRequest, "Upload");
                                        MetadataRecord mr = attacher.getDocumentForId(docID);
                                        if (httpRequest.getParameter("Name") != null) {
                                            attacher.setTitle(mr, httpRequest.getParameter("Name"));
                                        }
                                        if (httpRequest.getParameter("Desc") != null) {
                                            attacher.setNote(mr, httpRequest.getParameter("Desc"));
                                        } 
                                    } else {
                                        // Since same attachment is used for multiple items, use the same attachment metaID.
                                        // This stops duplicates of the same attachment inserted into META_CAT table. 
                                        // This is to avoid causing a tablespace problem in META_CAT table.
                                        MetadataRecord mr = attacher.getDocumentForId(docID);
                                        attacher.attachDocument(item.loadID, mr);
                                    }
                                } else if (action.equals("Remove")) {
                                    attacher.removeDocument(item.loadID, mrs[Integer.parseInt(httpRequest.getParameter("DeleteID"))]);
                                }
                                mrs = attacher.getDocumentsForId(item.loadID);
                            } catch (Exception e) {
                                System.out.println("********** FRED binary data entry error: " + new java.util.Date());
                                e.printStackTrace();
                                out.println("<script language=\"JavaScript\">alert(\"Your file can not be loaded: " + e + "\");</script>");
                            }
                        }                        

                        if (item.subtype.equals(FREDConstants.PALEONTOLOGICAL)) {
                            try {
                                startDETable(pageContext);
                                out.println("<table border='0' width='550'>");
                                out.println("    <form name=\"palIDForm" + item.id + "\" method=\"post\" action=\"binary_data_entry.jsp\">");
                                out.println("        <tr><td class=\"deHeading\">Select Taxon</td></tr>");
                                SelectBox<PaleontologyListEntry> selectBox = new SelectBox<PaleontologyListEntry>(recordUtil.getRecord(item.intID).getPaleontology().getListEntries());
                                Attributes attributes = Attributes.createNameOnlyAttributes("PalListID" + item.id);
                                attributes.setAttribute("onChange", "palIDForm" + item.id + ".submit();");
                                PaleontologyListEntry selectedPalListEntry = null;
                                if (item.palListId != null) {
                                    try {
                                        selectedPalListEntry = taxonomicUtil.getPaleontologyListEntry(item.loadID);
                                    } catch (Exception e) {
                                    }
                                }
                                out.println("<tr><td>&nbsp;</td></tr><tr><td style=\"text-align: left\">");
                                selectBox.writeBox(attributes, "Entire Paleo record", null, selectedPalListEntry, new PrintWriter(out));
                                out.println("</td></tr><input type=\"hidden\" name=\"ID\" value=\"" + item.intID + "\"><input type=\"hidden\" name=\"RecType\" value=\"" + item.subtype + "\">");
                                for (Item item1 : items) {
                                    out.println("        <input type=\"hidden\" name=\"" + item1.getVariableName() + "\" value=\"" + item1.id + "\">");
                                }
                                out.println("        <input type=\"hidden\" name=\"FoldID\" value=\"" + foldID + "\">");
                                out.println("</form></table>");
                                endDETable(pageContext);
                            } catch (Exception e) {
                                e.printStackTrace(new PrintWriter(out));
                            }
                        }

                        startDETable(pageContext);
                        out.println("<table border=\"0\" width=\"550\"><tr><td colspan=\"3\" class=\"deHeading\">Existing Images/Files for " + item.subtype + ": " + item.title + (item.palListEntry == null ? "" : " (" + item.palListEntry.getTaxonomicName() + ")") + "</td></tr>");
                        out.println("    <tr><td>&nbsp;</td></tr>");
                        if (mrs != null && mrs.length > 0) {
                            for (int i = 0; i < mrs.length; i++) {
                                out.println("<tr><td style=\"text-align:left\"><a href=\"binary_data_entry.jsp?" + item.getVariableName() + "=" + item.intID + "&FoldID=" + foldID + "&PalListID" + item.id + "=" + (item.palListId != null ? item.palListId : "") + "&RecType=" + item.subtype + "&Action=Remove&DeleteID=" + i + "\"><img src=\"images/cancel.gif\" width=\"20\" height=\"20\" border=\"0\" alt=\"Delete\" /></a></td>");
                                out.println("    <td style=\"text-align:left\"><a href=\"/online/DigitalDocument?src=" + mrs[i].getCode() + "\"><img border=\"0\" src=\"/online/Thumbnail?src=" + mrs[i].getCode() + "\" alt=\"FRED document\" /></a>&nbsp;&nbsp;</td>");
                                out.println("    <td style=\"text-align:left\">" + mrs[i].getTitle() + "</td></tr>");
                            }
                        } else {
                            out.println("<tr><td style=\"text-align:left\">None</td></tr>");
                        }
                        out.println("</table>");
                        endDETable(pageContext);

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } finally {
                        if (attacher != null) {
                            try {
                                FREDUtil.closeDocumentAttacherConnection();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            } else {
                out.println("<p><span class=\"subhead\">Access denied</span></p>No Items Provided.  Click <a href=\"index.jsp\" class=\"heading\">here</a> to return to the FRED home page.");
            }

            out.println("</td></tr></table>");
        }
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        drawBottom(out, et);
        et.setDisplayLoadingMessage(false);
        factory.closeSession();
    }
%>
