package nz.cri.gns.fred.util;

import java.util.Collections;
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
     * Returns the person with the given name and will create them if they don't
     * exist
     */
    public Person findOrCreatePerson(String name) throws StorageAccessException {
        Person person = fredDAO.getFirst(
            "FROM Person p WHERE p.name = ?1",
            Person.class,
            name
        );
        if (person == null) {
            person = fredDAO.createNewPerson();
            person.setName(name);
            fredDAO.saveOrUpdate(person);
        }
        return person;
    }

    /**
     * Returns the person with the given name or null if they don't exist
     */
    public Person findPerson(String name) throws StorageAccessException {
        return fredDAO.getFirst(
            "FROM Person p WHERE p.name = ?1",
            Person.class,
            name
        );
    }

    public List<Person> getMatchingPersons(String str, Match matchType, Integer maxMatches)
            throws StorageAccessException {

        if (str == null || str.isBlank()) {
            return Collections.emptyList();
        }

        // Build LIKE pattern based on match type
        String pattern;
        switch (matchType) {
            case ANYWHERE:
                pattern = "%" + str + "%";
                break;
            case BEGINNING:
                pattern = str + "%";
                break;
            case END:
                pattern = "%" + str;
                break;
            default:
                // Fallback: behave like ANYWHERE
                pattern = "%" + str + "%";
        }

        // Case-insensitive match using lower(...)
        String hql = "FROM Person p " +
                     "WHERE lower(p.name) LIKE lower(?1) " +
                     "ORDER BY p.name";

        if (maxMatches == null) {
            return fredDAO.getList(hql, Person.class, pattern);
        } else {
            return fredDAO.getList(hql, maxMatches, Person.class, pattern);
        }
    }

    public List<Person> getPeople() throws StorageAccessException {
        return fredDAO.getList("FROM Person p", Person.class);
    }
}
