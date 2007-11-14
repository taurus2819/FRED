package nz.cri.gns.fred.dao;

import java.util.List;

import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.Match;
import nz.cri.gns.fred.model.Person;

public interface PersonDAO {

	public Person createNewPerson();

	/**
	 * If one exists in the database a person with the full name given. 
	 * @throws StorageAccessException 
	 */
	public Person getPerson(String name) throws StorageAccessException;

	public List<Person> getMatchingPersons(String str, Match matchType, int maxMatches) throws StorageAccessException;
	public void delete(Object object) throws StorageAccessException;
	public <T> T saveOrUpdate(T object) throws StorageAccessException;
	public <T extends Comparable<? super T>> List<T> getList(String query, Class<T> clazz, Object ... parameters) throws StorageAccessException;

}
