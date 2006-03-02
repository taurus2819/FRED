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
import nz.cri.gns.fred.hibernate.util.HibernateUtil;
import nz.cri.gns.fred.model.Person;
import nz.cri.gns.fred.util.PersonUtil;

public class AJAXServlet extends HttpServlet {

	public class NamedId {
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
		Person;
	};
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String type = request.getParameter("type");
		String start = request.getParameter("start");
		
		List<NamedId> values = new ArrayList<NamedId>();
		String what = null;
		switch (Type.valueOf(type)) {
			case Person:
				what = "person";
				PersonUtil util = new PersonUtil(HibernateUtil.get().getDAOFactory());
				try {
					List<Person> people = util.getMatchingPersons(start, Match.BEGINNING, 15);
					for (Person person : people) {
						values.add(new NamedId(person.getPersonId().toString(), person.getDisplayName()));
					}
				} catch (StorageAccessException e) {
				}
				break;
			default:
				throw new IllegalArgumentException("Unknown type");
		}
		
		response.setContentType("text/xml");
		PrintWriter out = response.getWriter();
		
		out.println("<?xml version=\"1.0\"?>");
		out.println("<" + what + "s>");
		for (NamedId value : values) {
			out.println("<" + what + ">");
			out.println("<name>" + value.getName() + "</name>");
			out.println("<id>" + value.getId() + "</id>");
			out.println("</" + what + ">");
		}
		out.println("</" + what + "s>");
	}

	
}
