package nz.cri.gns.fred.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nz.cri.gns.auth.domain.User;
import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.db.DBUtils;
import nz.cri.gns.fred.FREDHibernateServlet;
import nz.cri.gns.fred.Match;
import nz.cri.gns.fred.dao.FredDAO;
import nz.cri.gns.fred.de.DataInputException;
import nz.cri.gns.fred.hibernate.util.FredHibernate;
import nz.cri.gns.fred.model.Age;
import nz.cri.gns.fred.model.FREDConstants;
import nz.cri.gns.fred.model.Person;
import nz.cri.gns.fred.model.StratigraphicUnit;
import nz.cri.gns.fred.model.Taxon;
import nz.cri.gns.fred.model.TaxonomicGroup;
import nz.cri.gns.fred.util.FREDUtil;
import nz.cri.gns.fred.util.PersonUtil;
import nz.cri.gns.fred.util.StageUtil;
import nz.cri.gns.fred.util.TaxonomicUtil;
import nz.cri.gns.jsp.IPSysJspPage;
import nz.cri.gns.xss.SanitizeHttpServletRequest;

public class AJAXServlet extends FREDHibernateServlet {

    private static final Logger log = Logger.getLogger("nz.cri.gns.fred.servlet.AJAXServlet");

    public static class NamedId {

        private String id;
        private String name;

        public NamedId(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
    private static final long serialVersionUID = 20050818L;

    public static enum Type {

        Person,
        Strat,
        Age,
        TaxonomicGroup,
        TaxonomicName;
    };

    public static enum Action {

        List() {

            @Override
            public void process(Type type, HttpServletRequest request, PrintWriter out) throws ServletException, IOException, StorageAccessException, DataInputException {
                String start = request.getParameter("start");
                List<NamedId> values = new ArrayList<NamedId>();
                switch (type) {
                    case Person:
                        PersonUtil personUtil = new PersonUtil(FredHibernate.get().getDAOFactory());
                        List<Person> people = personUtil.getMatchingPersons(start, Match.BEGINNING, 15);
                        for (Person person : people) {
                            values.add(new NamedId(person.getPersonId().toString(), person.getDisplayName()));
                        }
                        break;
                    case Strat:
                        FredDAO dao = FredHibernate.get().getDAOFactory().getFredDAO();
                        List<StratigraphicUnit> units = dao.getMatchingUnitNames(start, Match.BEGINNING, 15);
                        for (StratigraphicUnit unit : units) {
                            values.add(new NamedId(unit.getId().toString(), unit.getName()));
                        }
                        break;
                    case Age:
                        StageUtil stageUtil = new StageUtil(FredHibernate.get().getDAOFactory());
                        List<Age> ages = stageUtil.getMatchingAges(start, 15);
                        for (Age age : ages) {
                            values.add(new NamedId(age.getAgeId().toString(), age.getName() + " (" + age.getCode() + ")"));
                        }
                        break;
                    case TaxonomicGroup:
                        List<TaxonomicGroup> groups = new TaxonomicUtil(FredHibernate.get().getDAOFactory()).getMatchingTaxonomicGroups(start, Match.BEGINNING, 5);
                        for (TaxonomicGroup group : groups) {
                            values.add(new NamedId(group.getGroupId().toString(), group.getName()));
                        }
                        break;
                    case TaxonomicName:
                        TaxonomicUtil taxaUtil = new TaxonomicUtil(FredHibernate.get().getDAOFactory());
                        String cleanName = null;
                        TaxonomicGroup group = null;
                        if (request.getParameter("group") != null) {
                            group = taxaUtil.getTaxonomicGroup(request.getParameter("group"));
                        }
                        cleanName = TaxonomicUtil.normaliseTaxonomicName(start);
                        List<Taxon> taxa = taxaUtil.getMatchingTaxa(cleanName, group, Match.ANYWHERE, 30);
                        for (Taxon taxon : taxa) {
                            values.add(new NamedId(taxon.getTaxaId().toString(), ((group == null) ? taxon.getTaxonomicGroup().getName() + ": " : "") + taxon.getTaxonomicName()));
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown type");
                }

                String what = type.toString();

                out.println("<?xml version=\"1.0\"?>");
                out.println("<" + what + "s>");
                for (NamedId value : values) {
                    out.println("<" + what + ">");
                    out.println("<name><![CDATA[" + value.getName() + "]]></name>");
                    out.println("<id>" + value.getId() + "</id>");
                    out.println("</" + what + ">");
                }
                out.println("</" + what + "s>");

            }
        },
        Confirm() {

            @Override
            public void process(Type type, HttpServletRequest request, PrintWriter out) throws ServletException, IOException, StorageAccessException, DataInputException {
                SanitizeHttpServletRequest sanitizeHttpRequest = new SanitizeHttpServletRequest();
                String name = sanitizeHttpRequest.stripAllScripts(request.getParameter("name").trim());
                String status = "";
                String moreData = null;
                switch (type) {
                    case Person:
                        PersonUtil util = new PersonUtil(FredHibernate.get().getDAOFactory());
                        Person person = util.findPerson(name);
                        status = ((person != null) ? "Exists" : "Doesn't exist");
                        break;
                    case TaxonomicName:
                        TaxonomicUtil taxaUtil = new TaxonomicUtil(FredHibernate.get().getDAOFactory());
                        TaxonomicGroup group = null;
                        if (request.getParameter("TaxonomicGroup") != null) {
                            group = taxaUtil.getTaxonomicGroup(request.getParameter("TaxonomicGroup").trim());
                        }
                        String cleanName = TaxonomicUtil.normaliseTaxonomicName(name);
                        if (cleanName == null || cleanName.length() == 0) {
                            if (group != null) {
                                status = FREDConstants.APPROVED;
                            } else {
                                status = "invalid";
                            }
                        } else {
                            Taxon taxon = taxaUtil.getTaxon(group, cleanName, null);
                            if (taxon != null) {
                                status = taxon.getStatus();
                                moreData = "<taxa-id>" + taxon.getTaxaId() + "</taxa-id>";
                                moreData += "<clean-name>" + cleanName + "</clean-name>";
                                moreData += "<taxonomic-group>" + taxon.getTaxonomicGroup().getName() + "</taxonomic-group>";
                                moreData += "<author><![CDATA[" + DBUtils.nvl(taxon.getAuthor()) + "]]></author>";
                                if (status.equals(FREDConstants.PROVISIONAL)) {
                                    moreData += "<submitted-by><![CDATA[" + taxon.getSubmittedBy().getFullName() + "]]></submitted-by>"
                                            + "<submitted-date>" + FREDUtil.formatDateForOutput(taxon.getSubmittedDate()) + "</submitted-date>";
                                } else if (status.equals(FREDConstants.REJECTED)) {
                                    moreData += "<rejected-by><![CDATA[" + taxon.getApprovedBy().getFullName() + "]]></rejected-by>"
                                            + "<rejected-date>" + FREDUtil.formatDateForOutput(taxon.getApprovedDate()) + "</rejected-date>"
                                            + "<rejected-comments><![CDATA[" + taxon.getPanelistComments() + "]]></rejected-comments>";
                                }
                            } else {
                                status = "new";
                                moreData = "<clean-name>" + cleanName + "</clean-name>";
                            }
                        }
                        break;
                }

                String what = type.toString();

                out.println("<?xml version=\"1.0\"?>");
                out.println("<" + what + ">");
                out.println("<name><![CDATA[" + name + "]]></name>");
                out.println("<status>" + status + "</status>");
                if (moreData != null) {
                    out.println(moreData);
                }
                out.println("</" + what + ">");
            }
        },
        Add() {

            @Override
            public void process(Type type, HttpServletRequest request, PrintWriter out) throws ServletException, IOException, StorageAccessException, DataInputException {
                SanitizeHttpServletRequest sanitizeHttpRequest = new SanitizeHttpServletRequest();
                String name = sanitizeHttpRequest.stripAllScripts(request.getParameter("name"));
                boolean confirmation = false;
                String moreData = null;
                switch (type) {
                    case Person:
                        PersonUtil util = new PersonUtil(FredHibernate.get().getDAOFactory());
                        util.findOrCreatePerson(name);
                        confirmation = true;
                        break;
                    case TaxonomicName:
                        TaxonomicUtil taxaUtil = new TaxonomicUtil(FredHibernate.get().getDAOFactory());
                        TaxonomicGroup group = taxaUtil.getTaxonomicGroup(sanitizeHttpRequest.stripAllScripts(request.getParameter("TaxonomicGroup").trim()));
                        String cleanName = TaxonomicUtil.normaliseTaxonomicName(name);
                        String author = sanitizeHttpRequest.stripAllScripts(request.getParameter("Author"));
                        Taxon taxon = taxaUtil.getTaxon(group, cleanName, null);
                        if (taxon == null) {
                            taxon = taxaUtil.createTaxon();
                            taxon.setTaxonomicGroup(group);
                            taxon.setTaxonomicName(cleanName);
                            taxon.setAuthor(author);
                            taxaUtil.submitProvisional((User) IPSysJspPage.getUser(request.getSession()), taxon);
                            confirmation = true;
                        } else {
                            confirmation = true;
                        }
                        moreData = "<taxa-id>" + taxon.getTaxaId() + "</taxa-id>";
                }

                String what = type.toString();

                out.println("<?xml version=\"1.0\"?>");
                out.println("<" + what + ">");
                out.println("<name><![CDATA[" + name + "]]></name>");
                out.println("<status>" + ((confirmation) ? "Exists" : "Could not be created") + "</status>");
                if (moreData != null) {
                    out.println(moreData);
                }
                out.println("</" + what + ">");
            }
        };

        public abstract void process(Type type, HttpServletRequest request, PrintWriter out) throws ServletException, IOException, StorageAccessException, DataInputException;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //Set as XML output
        response.setContentType("text/xml");

        String action = request.getParameter("action");
        String type = request.getParameter("type");
        PrintWriter out = response.getWriter();

        try {
            Action.valueOf(action).process(Type.valueOf(type), request, out);
        } catch (StorageAccessException | ServletException e) {
            log.log(Level.SEVERE, null, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writeError(out, e.getMessage());
        } catch (DataInputException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            writeError(out, e.getMessage());
        } catch (IOException e) {
            // Client closed the connection. We let it be.
        }
    }

    private void writeError(PrintWriter out, String message) {
        // client closed the connection. We let it be.
        out.print("<?xml version=\"1.0\"?>");
        out.print("<error>");
        out.print(message);
        out.println("</error>");
    }
}
