package nz.cri.gns.fred.util;

import java.util.List;

import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.Match;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.PersonDAO;
import nz.cri.gns.fred.model.Person;

public class PersonUtil extends ModelUtil {

	private PersonDAO personDAO;
	
	public PersonUtil(DAOFactory factory) {
		super(factory);
		this.personDAO = factory.getPersonDAO();
	}
    
    /**
     * Returns the person with the given name and will create them if they don't exist
     * @throws StorageAccessException
     */
	public Person findOrCreatePerson(String name) throws StorageAccessException {
		Person person = personDAO.getPerson(name);
		if (person == null) {
			//Insert them
			person = personDAO.createNewPerson();
			person.setName(name);
			personDAO.saveOrUpdate(person);
		}
		return person;
	}

    /**
     * Returns the person with the given name or null if they don't exist
     * @throws StorageAccessException
     */
	public Person findPerson(String name) throws StorageAccessException {
		return personDAO.getPerson(name);
	}

	public List<Person> getMatchingPersons(String str, Match matchType, int maxMatches) throws StorageAccessException {
		return personDAO.getMatchingPersons(str, matchType, maxMatches);
	}
	
	public List<Person> getPeople() throws StorageAccessException {
		return personDAO.getList("FROM Person AS P", Person.class);
	}
	
}
