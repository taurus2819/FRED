package nz.cri.gns.fred.dao;

import nz.cri.gns.fred.model.Person;

public interface PersonDAO {

	/**
	 * Returns a person record for the given company.  A company
	 * is simply a person without a given name
	 * @throws StorageAccessException 
	 */
	public Person getCompany(String name) throws StorageAccessException;

	public Person createNewPerson();

	public void save(Person person) throws StorageAccessException;

}
