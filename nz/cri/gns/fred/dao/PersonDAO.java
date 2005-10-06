package nz.cri.gns.fred.dao;

import nz.cri.gns.fred.model.Person;

public interface PersonDAO {

	public Person createNewPerson();

	public void save(Person person) throws StorageAccessException;

	/**
	 * If one exists in the database a person with the full name given. 
	 * @throws StorageAccessException 
	 */
	public Person getPerson(String name) throws StorageAccessException;

}
