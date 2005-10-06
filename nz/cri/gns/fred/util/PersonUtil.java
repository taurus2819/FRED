package nz.cri.gns.fred.util;

import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.PersonDAO;
import nz.cri.gns.fred.dao.StorageAccessException;
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
			personDAO.save(person);
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
}
