package nz.cri.gns.fred.util;

import java.util.List;

import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.Match;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.FredDAO;
import nz.cri.gns.fred.model.Person;

public class PersonUtil extends ModelUtil {

	private FredDAO fredDAO;
	
	public PersonUtil(DAOFactory factory) {
		super(factory);
		this.fredDAO = factory.getFredDAO();
	}
    
    /**
     * Returns the person with the given name and will create them if they don't exist
     * @throws StorageAccessException
     */
	public Person findOrCreatePerson(String name) throws StorageAccessException {
		Person person = fredDAO.getPerson(name);
		if (person == null) {
			//Insert them
			person = fredDAO.createNewPerson();
			person.setName(name);
			fredDAO.saveOrUpdate(person);
		}
		return person;
	}

    /**
     * Returns the person with the given name or null if they don't exist
     * @throws StorageAccessException
     */
	public Person findPerson(String name) throws StorageAccessException {
		return fredDAO.getPerson(name);
	}

	public List<Person> getMatchingPersons(String str, Match matchType, int maxMatches) throws StorageAccessException {
		return fredDAO.getMatchingPersons(str, matchType, maxMatches);
	}
	
	public List<Person> getPeople() throws StorageAccessException {
		return fredDAO.getList("FROM Person AS P", Person.class);
	}
	
}
