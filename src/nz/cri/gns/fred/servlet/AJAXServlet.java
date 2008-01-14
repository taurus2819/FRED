package nz.cri.gns.fred.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.Match;
import nz.cri.gns.fred.dao.StratLexDAO;
import nz.cri.gns.fred.hibernate.util.HibernateUtil;
import nz.cri.gns.fred.model.Person;
import nz.cri.gns.fred.model.StratigraphicUnit;
import nz.cri.gns.fred.model.Taxon;
import nz.cri.gns.fred.util.PersonUtil;
import nz.cri.gns.fred.util.TaxonomicUtil;

public class AJAXServlet extends HttpServlet {

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
		TaxonomicGroup,
		TaxonomicName;
	};
	
	public static enum Action {
		List() {
			public void process(Type type, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				String start = request.getParameter("start");
				List<NamedId> values = new ArrayList<NamedId>();
				switch (type) {
					case Person:
						PersonUtil util = new PersonUtil(HibernateUtil.get().getDAOFactory());
						try {
							List<Person> people = util.getMatchingPersons(start, Match.BEGINNING, 15);
							for (Person person : people) {
								values.add(new NamedId(person.getPersonId().toString(), person.getDisplayName()));
							}
						} catch (StorageAccessException e) {
						}
						break;
					case Strat:
						StratLexDAO dao = HibernateUtil.get().getDAOFactory().getStratLexDAO();
						try {
							List<StratigraphicUnit> units = dao.getMatchingUnitNames(start, Match.BEGINNING, 15);
							for (StratigraphicUnit unit : units) {
								values.add(new NamedId(unit.getId().toString(), unit.getName()));
							}
						} catch (StorageAccessException e) {
						}
						break;
					case TaxonomicGroup:
						break;
					case TaxonomicName:
						TaxonomicUtil taxaUtil = new TaxonomicUtil(HibernateUtil.get().getDAOFactory());
						try {
							List<Taxon> taxa = taxaUtil.getMatchingTaxa(start, null, Match.BEGINNING, 15);
							for (Taxon taxon : taxa) {
								values.add(new NamedId(taxon.getTaxaId().toString(), taxon.getTaxonomicGroup().getName() + ": " + taxon.getTaxonomicName()));
							}
						} catch (StorageAccessException e) {
						}
						break;
					default:
						throw new IllegalArgumentException("Unknown type");
				}
				
				PrintWriter out = response.getWriter();
				
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
			public void process(Type type, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				String name = request.getParameter("name");
				boolean confirmation = false;
				switch (type) {
					case Person:
						PersonUtil util = new PersonUtil(HibernateUtil.get().getDAOFactory());
						try {
							Person person = util.findPerson(name);
							confirmation = person != null;
						} catch (StorageAccessException e) {
						}
						break;
				}
				
				PrintWriter out = response.getWriter();
				
				String what = type.toString();
				
				out.println("<?xml version=\"1.0\"?>");
				out.println("<" + what + ">");
				out.println("<name><![CDATA[" + name + "]]></name>");
				out.println("<status>" + ((confirmation) ? "Exists" : "Doesn't exist") + "</status>");
				out.println("</" + what + ">");
			}
		},
		Add() {
			public void process(Type type, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				String name = request.getParameter("name");
				boolean confirmation = false;
				switch (type) {
					case Person:
						PersonUtil util = new PersonUtil(HibernateUtil.get().getDAOFactory());
						try {
							util.findOrCreatePerson(name);
							confirmation = true;
						} catch (StorageAccessException e) {
						}
						break;
				}
				
				PrintWriter out = response.getWriter();
				
				String what = type.toString();
				
				out.println("<?xml version=\"1.0\"?>");
				out.println("<" + what + ">");
				out.println("<name><![CDATA[" + name + "]]></name>");
				out.println("<status>" + ((confirmation) ? "Exists" : "Could not be created") + "</status>");
				out.println("</" + what + ">");
			}
		};
		
		public abstract void process(Type type, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//Set as XML output
		response.setContentType("text/xml");

		String action = request.getParameter("action");
		String type = request.getParameter("type");
		
		Action.valueOf(action).process(Type.valueOf(type), request, response);
	}

	
}
