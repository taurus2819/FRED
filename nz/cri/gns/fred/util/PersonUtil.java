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
	public Person findOrCreateCompany(String companyName) throws StorageAccessException {
		Person person = personDAO.getCompany(companyName);
		if (person == null) {
			//Insert them
			person = personDAO.createNewPerson();
			person.setFamilyName(companyName);
			personDAO.save(person);
		}
		return person;
	}
	public Person findOrCreatePerson(String givenName, String familyName) throws StorageAccessException {
		Person person = personDAO.getPerson(givenName, familyName);
		if (person == null) {
			//Insert them
			person = personDAO.createNewPerson();
			person.setFamilyName(familyName);
			person.setGivenName(givenName);
			personDAO.save(person);
		}
		return person;
	}
	public Person findPerson(String name) throws StorageAccessException {
		return personDAO.findPerson(name);
	}
}
